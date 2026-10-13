package com.example.globalcompat.diagnostics

class HealthDiagnosticsEvaluator {
    fun evaluate(probe: HealthDiagnosticsProbe): HealthDiagnosticsReport {
        val services = probe.packageOrMissing(GMS_PACKAGE)
        val companion = probe.packageOrMissing(VENDING_PACKAGE)
        val chrome = probe.packageOrMissing(CHROME_PACKAGE)
        val servicesLooksLikeMicrog = services.looksLikeMicrog(probe.catalogMatchedMicrogPackages)
        val companionLooksLikeMicrog = companion.looksLikeMicrog(probe.catalogMatchedMicrogPackages)
        val microgDetected = servicesLooksLikeMicrog || companionLooksLikeMicrog

        val microgIssues = buildList {
            if (probe.microgExpected || microgDetected) {
                addComponentIssues(
                    component = services,
                    recognized = servicesLooksLikeMicrog,
                    missing = DiagnosticIssueCode.MICROG_SERVICES_MISSING,
                    disabled = DiagnosticIssueCode.MICROG_SERVICES_DISABLED,
                    versionUnknown = DiagnosticIssueCode.MICROG_SERVICES_VERSION_UNKNOWN,
                )
                addComponentIssues(
                    component = companion,
                    recognized = companionLooksLikeMicrog,
                    missing = DiagnosticIssueCode.MICROG_COMPANION_MISSING,
                    disabled = DiagnosticIssueCode.MICROG_COMPANION_DISABLED,
                    versionUnknown = DiagnosticIssueCode.MICROG_COMPANION_VERSION_UNKNOWN,
                )
            }
        }
        val microgStatus = when {
            !probe.microgExpected && !microgDetected -> DiagnosticStatus.NOT_TESTED
            microgIssues.isEmpty() -> DiagnosticStatus.NORMAL
            else -> DiagnosticStatus.NEEDS_ATTENTION
        }

        val playImplementation = when {
            !companion.installed -> PlayStoreImplementation.NOT_INSTALLED
            companionLooksLikeMicrog -> PlayStoreImplementation.MICROG_COMPANION
            companion.looksLikeOfficialPlayStore() -> PlayStoreImplementation.OFFICIAL_PLAY_STORE
            else -> PlayStoreImplementation.UNKNOWN
        }
        val playIssues = buildList {
            if (!companion.installed) add(DiagnosticIssueCode.PLAY_STORE_MISSING)
            if (companion.installed && companion.enabled == false) {
                add(DiagnosticIssueCode.PLAY_STORE_DISABLED)
            }
            if (playImplementation == PlayStoreImplementation.UNKNOWN) {
                add(DiagnosticIssueCode.PLAY_STORE_IMPLEMENTATION_UNKNOWN)
            }
        }
        val playStatus = if (playIssues.isEmpty()) {
            DiagnosticStatus.NORMAL
        } else {
            DiagnosticStatus.NEEDS_ATTENTION
        }

        val loginIssues = buildList {
            if (probe.defaultBrowserPackage.isNullOrBlank()) {
                add(DiagnosticIssueCode.DEFAULT_BROWSER_MISSING)
            }
            if (!chrome.installed) {
                add(DiagnosticIssueCode.CHROME_MISSING)
            } else if (chrome.enabled == false) {
                add(DiagnosticIssueCode.CHROME_DISABLED)
            }
            if (probe.webViewPackage.isNullOrBlank()) {
                add(DiagnosticIssueCode.WEBVIEW_MISSING)
            }
            if (probe.customTabsAvailable == false) {
                add(DiagnosticIssueCode.CUSTOM_TABS_MISSING)
            }
        }
        val loginStatus = when {
            probe.customTabsAvailable == null -> DiagnosticStatus.NOT_TESTED
            loginIssues.isEmpty() -> DiagnosticStatus.NORMAL
            else -> DiagnosticStatus.NEEDS_ATTENTION
        }

        val systemIssues = buildList {
            val available = probe.availableStorageBytes
            val cache = probe.appCacheBytes
            if (available == null || cache == null) {
                add(DiagnosticIssueCode.STORAGE_UNREADABLE)
            }
            if (available != null && available < LOW_STORAGE_BYTES) {
                add(DiagnosticIssueCode.STORAGE_LOW)
            }
            if (cache != null && cache >= LARGE_CACHE_BYTES) {
                add(DiagnosticIssueCode.APP_CACHE_LARGE)
            }
        }
        val systemStatus = when {
            DiagnosticIssueCode.STORAGE_UNREADABLE in systemIssues -> DiagnosticStatus.NOT_TESTED
            systemIssues.isEmpty() -> DiagnosticStatus.NORMAL
            else -> DiagnosticStatus.NEEDS_ATTENTION
        }

        return HealthDiagnosticsReport(
            microg = MicrogHealthDiagnostic(
                status = microgStatus,
                detected = microgDetected,
                services = services,
                companion = companion,
                selfCheckAvailable = servicesLooksLikeMicrog && services.enabled != false,
                issues = microgIssues,
            ),
            googlePlay = GooglePlayConflictDiagnostic(
                status = playStatus,
                implementation = playImplementation,
                packageInfo = companion,
                issues = playIssues,
            ),
            loginAuthentication = LoginAuthenticationDiagnostic(
                status = loginStatus,
                defaultBrowserPackage = probe.defaultBrowserPackage,
                chrome = chrome,
                webViewPackage = probe.webViewPackage,
                customTabsAvailable = probe.customTabsAvailable,
                googleEnvironmentEstablished = probe.googleEnvironmentEstablished,
                issues = loginIssues,
            ),
            systemStatus = SystemStatusDiagnostic(
                status = systemStatus,
                availableStorageBytes = probe.availableStorageBytes,
                appCacheBytes = probe.appCacheBytes,
                issues = systemIssues,
            ),
        )
    }

    private fun MutableList<DiagnosticIssueCode>.addComponentIssues(
        component: PackageDiagnostic,
        recognized: Boolean,
        missing: DiagnosticIssueCode,
        disabled: DiagnosticIssueCode,
        versionUnknown: DiagnosticIssueCode,
    ) {
        if (!component.installed || !recognized) {
            add(missing)
            return
        }
        if (component.enabled == false) add(disabled)
        if (!component.hasReasonableVersion()) add(versionUnknown)
    }

    private fun HealthDiagnosticsProbe.packageOrMissing(packageName: String) =
        packages[packageName] ?: PackageDiagnostic(
            packageName = packageName,
            installed = false,
            enabled = null,
            versionName = null,
            versionCode = null,
            applicationLabel = null,
            reportedSignerSha256 = emptyList(),
        )

    private fun PackageDiagnostic.looksLikeMicrog(catalogMatches: Set<String>): Boolean =
        installed && (
            packageName in catalogMatches ||
                applicationLabel.orEmpty().contains("microg", ignoreCase = true) ||
                versionName.orEmpty().contains("microg", ignoreCase = true) ||
                versionName.orEmpty().endsWith("-hw", ignoreCase = true)
            )

    private fun PackageDiagnostic.looksLikeOfficialPlayStore(): Boolean {
        if (!installed || versionName.orEmpty().endsWith("-hw", ignoreCase = true)) return false
        val labelMatches = applicationLabel.orEmpty().contains("google play", ignoreCase = true)
        val signerMatches = reportedSignerSha256.any {
            it.equals(GOOGLE_PRIVILEGED_SIGNER_SHA256, ignoreCase = true)
        }
        return labelMatches && signerMatches
    }

    private fun PackageDiagnostic.hasReasonableVersion(): Boolean =
        !versionName.isNullOrBlank() && (versionCode ?: 0L) > 0L

    companion object {
        const val GMS_PACKAGE = "com.google.android.gms"
        const val VENDING_PACKAGE = "com.android.vending"
        const val CHROME_PACKAGE = "com.android.chrome"
        const val LOW_STORAGE_BYTES = 1024L * 1024L * 1024L
        const val LARGE_CACHE_BYTES = 256L * 1024L * 1024L
        const val GOOGLE_PRIVILEGED_SIGNER_SHA256 =
            "f0fd6c5b410f25cb25c3b53346c8972fae30f8ee7411df910480ad6b2d60db83"
    }
}
