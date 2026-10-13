package com.example.globalcompat.diagnostics

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HealthDiagnosticsEvaluatorTest {
    private val evaluator = HealthDiagnosticsEvaluator()

    @Test
    fun `catalog matched Huawei pair is healthy microg and Companion`() {
        val report = evaluator.evaluate(
            probe(
                packages = mapOf(
                    GMS to installed(GMS, "0.3.16.252432-hw", 252432032),
                    VENDING to installed(VENDING, "0.3.16.40226-hw", 84022632),
                    CHROME to installed(CHROME, "130.0", 130),
                ),
                catalogMatches = setOf(GMS, VENDING),
                microgExpected = true,
            ),
        )

        assertEquals(DiagnosticStatus.NORMAL, report.microg.status)
        assertTrue(report.microg.selfCheckAvailable)
        assertEquals(PlayStoreImplementation.MICROG_COMPANION, report.googlePlay.implementation)
        assertEquals(DiagnosticStatus.NORMAL, report.googlePlay.status)
    }

    @Test
    fun `missing Companion requires attention and never becomes official Play Store`() {
        val report = evaluator.evaluate(
            probe(
                packages = mapOf(GMS to installed(GMS, "0.3.16.252432-hw", 252432032)),
                catalogMatches = setOf(GMS),
                microgExpected = true,
            ),
        )

        assertEquals(DiagnosticStatus.NEEDS_ATTENTION, report.microg.status)
        assertTrue(DiagnosticIssueCode.MICROG_COMPANION_MISSING in report.microg.issues)
        assertEquals(PlayStoreImplementation.NOT_INSTALLED, report.googlePlay.implementation)
    }

    @Test
    fun `Google labelled and signed package is classified as official Play Store`() {
        val playStore = installed(VENDING, "43.8.26-31", 84382631).copy(
            applicationLabel = "Google Play Store",
            reportedSignerSha256 = listOf(HealthDiagnosticsEvaluator.GOOGLE_PRIVILEGED_SIGNER_SHA256),
        )
        val report = evaluator.evaluate(probe(packages = mapOf(VENDING to playStore)))

        assertEquals(PlayStoreImplementation.OFFICIAL_PLAY_STORE, report.googlePlay.implementation)
        assertEquals(DiagnosticStatus.NORMAL, report.googlePlay.status)
    }

    @Test
    fun `ambiguous vending implementation fails closed as unknown`() {
        val report = evaluator.evaluate(
            probe(packages = mapOf(VENDING to installed(VENDING, "unknown", 7))),
        )

        assertEquals(PlayStoreImplementation.UNKNOWN, report.googlePlay.implementation)
        assertEquals(DiagnosticStatus.NEEDS_ATTENTION, report.googlePlay.status)
        assertTrue(
            DiagnosticIssueCode.PLAY_STORE_IMPLEMENTATION_UNKNOWN in report.googlePlay.issues,
        )
    }

    @Test
    fun `browser chain reports attention even when Google environment is established`() {
        val report = evaluator.evaluate(
            probe(
                packages = mapOf(CHROME to installed(CHROME, "130.0", 130).copy(enabled = false)),
                defaultBrowser = null,
                webView = null,
                customTabs = false,
                googleEnvironmentEstablished = true,
            ),
        )

        assertEquals(DiagnosticStatus.NEEDS_ATTENTION, report.loginAuthentication.status)
        assertTrue(report.loginAuthentication.googleEnvironmentEstablished)
        assertTrue(DiagnosticIssueCode.CHROME_DISABLED in report.loginAuthentication.issues)
        assertTrue(DiagnosticIssueCode.CUSTOM_TABS_MISSING in report.loginAuthentication.issues)
    }

    @Test
    fun `unknown Custom Tabs result remains not tested`() {
        val report = evaluator.evaluate(probe(customTabs = null))

        assertEquals(DiagnosticStatus.NOT_TESTED, report.loginAuthentication.status)
    }

    @Test
    fun `low storage and large app cache require attention`() {
        val report = evaluator.evaluate(
            probe(
                availableStorage = HealthDiagnosticsEvaluator.LOW_STORAGE_BYTES - 1,
                appCache = HealthDiagnosticsEvaluator.LARGE_CACHE_BYTES,
            ),
        )

        assertEquals(DiagnosticStatus.NEEDS_ATTENTION, report.systemStatus.status)
        assertTrue(DiagnosticIssueCode.STORAGE_LOW in report.systemStatus.issues)
        assertTrue(DiagnosticIssueCode.APP_CACHE_LARGE in report.systemStatus.issues)
    }

    @Test
    fun `device without microg evidence is not tested rather than failed`() {
        val report = evaluator.evaluate(probe())

        assertEquals(DiagnosticStatus.NOT_TESTED, report.microg.status)
        assertFalse(report.microg.detected)
        assertTrue(report.microg.issues.isEmpty())
    }

    private fun probe(
        packages: Map<String, PackageDiagnostic> = mapOf(
            CHROME to installed(CHROME, "130.0", 130),
        ),
        catalogMatches: Set<String> = emptySet(),
        microgExpected: Boolean = false,
        defaultBrowser: String? = CHROME,
        webView: String? = "com.google.android.webview",
        customTabs: Boolean? = true,
        availableStorage: Long? = 8L * 1024 * 1024 * 1024,
        appCache: Long? = 16L * 1024 * 1024,
        googleEnvironmentEstablished: Boolean = false,
    ) = HealthDiagnosticsProbe(
        packages = packages,
        catalogMatchedMicrogPackages = catalogMatches,
        microgExpected = microgExpected,
        defaultBrowserPackage = defaultBrowser,
        webViewPackage = webView,
        customTabsAvailable = customTabs,
        availableStorageBytes = availableStorage,
        appCacheBytes = appCache,
        googleEnvironmentEstablished = googleEnvironmentEstablished,
    )

    private fun installed(packageName: String, versionName: String, versionCode: Long) =
        PackageDiagnostic(
            packageName = packageName,
            installed = true,
            enabled = true,
            versionName = versionName,
            versionCode = versionCode,
            applicationLabel = null,
            reportedSignerSha256 = emptyList(),
        )

    private companion object {
        const val GMS = HealthDiagnosticsEvaluator.GMS_PACKAGE
        const val VENDING = HealthDiagnosticsEvaluator.VENDING_PACKAGE
        const val CHROME = HealthDiagnosticsEvaluator.CHROME_PACKAGE
    }
}
