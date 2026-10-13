package com.example.globalcompat.diagnostics

enum class DiagnosticStatus {
    NORMAL,
    NEEDS_ATTENTION,
    NOT_TESTED,
}

enum class DiagnosticIssueCode {
    MICROG_SERVICES_MISSING,
    MICROG_SERVICES_DISABLED,
    MICROG_SERVICES_VERSION_UNKNOWN,
    MICROG_COMPANION_MISSING,
    MICROG_COMPANION_DISABLED,
    MICROG_COMPANION_VERSION_UNKNOWN,
    PLAY_STORE_MISSING,
    PLAY_STORE_DISABLED,
    PLAY_STORE_IMPLEMENTATION_UNKNOWN,
    DEFAULT_BROWSER_MISSING,
    CHROME_MISSING,
    CHROME_DISABLED,
    WEBVIEW_MISSING,
    CUSTOM_TABS_MISSING,
    STORAGE_LOW,
    APP_CACHE_LARGE,
    STORAGE_UNREADABLE,
}

data class PackageDiagnostic(
    val packageName: String,
    val installed: Boolean,
    val enabled: Boolean?,
    val versionName: String?,
    val versionCode: Long?,
    val applicationLabel: String?,
    val reportedSignerSha256: List<String>,
)

enum class PlayStoreImplementation {
    OFFICIAL_PLAY_STORE,
    MICROG_COMPANION,
    UNKNOWN,
    NOT_INSTALLED,
}

data class MicrogHealthDiagnostic(
    val status: DiagnosticStatus,
    val detected: Boolean,
    val services: PackageDiagnostic,
    val companion: PackageDiagnostic,
    val selfCheckAvailable: Boolean,
    val issues: List<DiagnosticIssueCode>,
)

data class GooglePlayConflictDiagnostic(
    val status: DiagnosticStatus,
    val implementation: PlayStoreImplementation,
    val packageInfo: PackageDiagnostic,
    val issues: List<DiagnosticIssueCode>,
)

data class LoginAuthenticationDiagnostic(
    val status: DiagnosticStatus,
    val defaultBrowserPackage: String?,
    val chrome: PackageDiagnostic,
    val webViewPackage: String?,
    val customTabsAvailable: Boolean?,
    val googleEnvironmentEstablished: Boolean,
    val issues: List<DiagnosticIssueCode>,
)

data class SystemStatusDiagnostic(
    val status: DiagnosticStatus,
    val availableStorageBytes: Long?,
    val appCacheBytes: Long?,
    val issues: List<DiagnosticIssueCode>,
)

data class HealthDiagnosticsReport(
    val microg: MicrogHealthDiagnostic,
    val googlePlay: GooglePlayConflictDiagnostic,
    val loginAuthentication: LoginAuthenticationDiagnostic,
    val systemStatus: SystemStatusDiagnostic,
)

data class HealthDiagnosticsProbe(
    val packages: Map<String, PackageDiagnostic>,
    val catalogMatchedMicrogPackages: Set<String>,
    val microgExpected: Boolean,
    val defaultBrowserPackage: String?,
    val webViewPackage: String?,
    val customTabsAvailable: Boolean?,
    val availableStorageBytes: Long?,
    val appCacheBytes: Long?,
    val googleEnvironmentEstablished: Boolean,
)
