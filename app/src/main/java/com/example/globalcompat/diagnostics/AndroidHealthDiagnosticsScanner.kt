package com.example.globalcompat.diagnostics

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.StatFs
import android.webkit.WebView
import androidx.core.content.pm.PackageInfoCompat
import com.example.globalcompat.baseline.OfficialComponentComparison
import com.example.globalcompat.baseline.OfficialComponentMatchStatus
import com.example.globalcompat.data.CompatibilityDecisionStatus
import com.example.globalcompat.data.CompatibilityPlanId
import com.example.globalcompat.data.EnvironmentReport
import java.io.File
import java.security.MessageDigest

class AndroidHealthDiagnosticsScanner(
    private val context: Context,
    private val evaluator: HealthDiagnosticsEvaluator = HealthDiagnosticsEvaluator(),
) {
    private val packageManager = context.packageManager

    fun scan(
        environment: EnvironmentReport,
        comparisons: List<OfficialComponentComparison>,
    ): HealthDiagnosticsReport {
        val packages = TARGET_PACKAGES.associateWith(::readPackage)
        val catalogMatches = comparisons.filter { comparison ->
            comparison.status == OfficialComponentMatchStatus.VERSION_MATCH &&
                comparison.componentId?.startsWith("microg_") == true
        }.mapTo(mutableSetOf()) { it.fingerprint.packageName }

        return evaluator.evaluate(
            HealthDiagnosticsProbe(
                packages = packages,
                catalogMatchedMicrogPackages = catalogMatches,
                microgExpected = environment.compatibilityPlan.planId ==
                    CompatibilityPlanId.HUAWEI_MICROG_COMPAT_PLAN,
                defaultBrowserPackage = readDefaultBrowserPackage(),
                webViewPackage = readWebViewPackage(),
                customTabsAvailable = readCustomTabsAvailability(),
                availableStorageBytes = runCatching {
                    StatFs(context.filesDir.absolutePath).availableBytes
                }.getOrNull(),
                appCacheBytes = runCatching { directorySize(context.cacheDir) }.getOrNull(),
                googleEnvironmentEstablished =
                    environment.compatibilityDecision.decisionStatus ==
                    CompatibilityDecisionStatus.NO_ACTION_REQUIRED,
            ),
        )
    }

    private fun readPackage(packageName: String): PackageDiagnostic {
        val packageInfo = try {
            getPackageInfo(packageName)
        } catch (_: PackageManager.NameNotFoundException) {
            return missingPackage(packageName)
        } catch (_: RuntimeException) {
            return missingPackage(packageName)
        }
        return PackageDiagnostic(
            packageName = packageName,
            installed = true,
            enabled = packageInfo.applicationInfo?.enabled,
            versionName = packageInfo.versionName,
            versionCode = PackageInfoCompat.getLongVersionCode(packageInfo),
            applicationLabel = packageInfo.applicationInfo?.let { info ->
                runCatching { packageManager.getApplicationLabel(info).toString() }.getOrNull()
            },
            reportedSignerSha256 = currentSignatures(packageInfo)
                .map { signature -> sha256(signature.toByteArray()) }
                .distinct()
                .sorted(),
        )
    }

    private fun getPackageInfo(packageName: String): PackageInfo =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(
                    PackageManager.GET_SIGNING_CERTIFICATES.toLong(),
                ),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(
                packageName,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    PackageManager.GET_SIGNING_CERTIFICATES
                } else {
                    PackageManager.GET_SIGNATURES
                },
            )
        }

    private fun currentSignatures(packageInfo: PackageInfo) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            @Suppress("DEPRECATION")
            packageInfo.signatures ?: emptyArray()
        }

    private fun readDefaultBrowserPackage(): String? = runCatching {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://accounts.google.com")).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)
        }
        resolveActivity(intent)?.activityInfo?.packageName
            ?.takeUnless { it == "android" }
    }.getOrNull()

    private fun readCustomTabsAvailability(): Boolean? = runCatching {
        val intent = Intent(CUSTOM_TABS_SERVICE_ACTION)
        queryIntentServices(intent).isNotEmpty()
    }.getOrNull()

    private fun readWebViewPackage(): String? = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WebView.getCurrentWebViewPackage()?.packageName
        } else {
            null
        }
    }.getOrNull()

    private fun resolveActivity(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.resolveActivity(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()),
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }

    private fun queryIntentServices(intent: Intent) =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentServices(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentServices(intent, 0)
        }

    private fun directorySize(root: File): Long =
        root.walkTopDown().filter(File::isFile).sumOf(File::length)

    private fun missingPackage(packageName: String) = PackageDiagnostic(
        packageName = packageName,
        installed = false,
        enabled = null,
        versionName = null,
        versionCode = null,
        applicationLabel = null,
        reportedSignerSha256 = emptyList(),
    )

    private fun sha256(bytes: ByteArray): String = MessageDigest.getInstance("SHA-256")
        .digest(bytes)
        .joinToString("") { byte -> "%02x".format(byte) }

    private companion object {
        const val CUSTOM_TABS_SERVICE_ACTION =
            "android.support.customtabs.action.CustomTabsService"
        val TARGET_PACKAGES = listOf(
            HealthDiagnosticsEvaluator.GMS_PACKAGE,
            HealthDiagnosticsEvaluator.VENDING_PACKAGE,
            HealthDiagnosticsEvaluator.CHROME_PACKAGE,
        )
    }
}
