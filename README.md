# GMS 助手

**GMS Helper**

Google 登录异常？microG 不知道装对没有？ChatGPT / Chrome 认证有问题？

免费检测 Android / 华为 HarmonyOS 的 Google、GMS、microG 环境，找到问题并给出修复方案。

[**Download APK**](https://github.com/teainsky/android-gms-helper/releases/latest) · **Free & Open Source** · **No Root** · **No Bootloader Unlock** · **No ROM Modification**

> Unofficial community tool. Not affiliated with Google or microG.

## 功能 / Features

- 检测 GMS / Google Play Services
- 检测 microG 安装状态
- Google 环境诊断
- Chrome / ChatGPT 登录问题诊断
- 已验证设备提供安全配置引导

GMS Helper is a free and open-source Android app for checking Google, GMS, and microG environments on Android and Huawei HarmonyOS devices. It helps identify Google sign-in and Chrome/ChatGPT authentication problems and provides safe guidance. Automatic configuration is available only for exactly verified device and system profiles.

## 免费版支持范围 / Free MVP scope

免费 MVP 为主流 Android 设备提供检测和诊断，并为未知品牌保留安全的基础检测。品牌本身不决定兼容结论；相同品牌在不同系统、市场版本和 Google 环境下可能得到不同结果。

The Free MVP provides detection and diagnostics for mainstream Android devices. Unknown Android brands receive safe basic diagnostics rather than an unsupported verdict. Device brand alone never determines a repair workflow.

| 设备/环境 | 检测 | Google 诊断 | 已验证配置 |
| --- | --- | --- | --- |
| Huawei Pura 70 Pro+ / HBN-AL80 / HarmonyOS 4.2 | ✅ | ✅ | ✅ |
| Huawei HarmonyOS 4.x 其他型号 | ✅ | ✅ | ❌ |
| Honor、Xiaomi/Redmi/POCO、OPPO、vivo/iQOO、OnePlus | ✅ | ✅ | ❌ |
| Samsung、Pixel | ✅ | ✅ | 仅在健康证据充分时显示无需处理 |
| Motorola、TECNO/Infinix/itel、Nothing | ✅ | ✅ | ❌ |
| 未知 Android 品牌 | 基础检测 | 基础诊断 | ❌ |
| HarmonyOS 5/6 原生分支或第三方兼容环境 | ✅ | ✅ | 不进入旧鸿蒙流程 |

未验证设备欢迎反馈，但“可检测”或“可诊断”不代表已经支持自动配置。

Unverified devices are welcome in compatibility reports, but detection or diagnostics do not mean that automatic configuration is supported.

## 当前深度验证 / Currently verified device

- Huawei Pura 70 Pro+
- 型号 / Model: `HBN-AL80`
- 系统 / OS: HarmonyOS `4.2.0`
- Android API: `31`
- 组件 / Components: microG Huawei `v0.3.16.252432`

该验证只绑定以上精确设备与系统画像，不自动扩展到 HarmonyOS 4.3、5/6、其他 Pura 或相似 Huawei 型号。

This verification applies only to the exact device and system profile above. It does not extend to HarmonyOS 4.3, HarmonyOS 5/6, other Pura models, or similar Huawei devices.

## 使用流程 / How to use

1. 下载并安装最新 APK。
2. 打开 GMS 助手，点击“开始检测”。
3. 查看“可检测 / 可诊断 / 已验证可配置”能力标签和 Google 环境说明。
4. 按实际情况完成 Google、ChatGPT、Chrome 的真实使用确认。
5. 只有精确验证过的设备和系统才会显示安全配置入口。

Download the latest APK, tap **Start detection**, review the Google environment diagnosis, and record actual Google/ChatGPT/Chrome results. Configuration remains locked unless the exact device and system profile has trusted verification.

## 安全与隐私 / Safety and privacy

- 不 Root，不解锁 Bootloader，不修改 ROM。
- 不静默安装，不绕过 Android 系统确认，不自动卸载已有组件。
- 不读取 Google 账号内容、IMEI、手机号、Android ID、序列号、MAC、联系人或 SIM 标识。
- 默认在本机完成检测和诊断，不上传检测结果。
- 不使用第三方 APK 镜像；组件或规则证据校验失败时安全停止。

No root, bootloader unlock, or ROM modification is required. Detection is local by default. The app does not read account contents or device-unique identifiers, silently install packages, or fall back to third-party APK mirrors.

## 已知限制 / Known limitations

- 当前只有一条精确实机验证记录；其他主流 Android 设备以检测和诊断为主。
- “组件完整”不等于来源可信、功能健康或通过 Play 认证。
- HarmonyOS 5/6 与第三方兼容运行环境目前仅诊断，不进入 HarmonyOS 1–4 配置流程。
- TikTok 不在首版支持范围。
- 不保证所有手机可配置，也不保证所有 Google 应用可用。

Only one exact device profile is deeply verified today. HarmonyOS 5/6 and third-party compatibility runtimes are diagnostic-only. The project does not promise configuration for every phone or compatibility with every Google app. TikTok is outside the current scope.

## 设备兼容反馈 / Report an issue

[**提交设备兼容反馈 / Report device compatibility**](https://github.com/teainsky/android-gms-helper/issues/new?template=compatibility-report.yml)

请勿提交账号信息、设备唯一标识或未脱敏的私人数据。未验证设备欢迎反馈，不代表已经支持。

Do not include account data, unique device identifiers, or unredacted personal information. Reports for unverified devices are welcome but do not mean those devices are supported.

## 常见问题 / FAQ

### 为什么我的手机只能诊断，不能配置？

真实配置只对精确设备、系统、可信组件和签名规则均通过验证的画像开放。品牌相同不代表可以继承验证结论。

### 检测到 Google 包，为什么没有显示“无需处理”？

包存在只说明组件集合状态。应用还需要可信来源、功能健康或精确设备验证证据，才会给出“无需处理”。

### HarmonyOS 5/6 可以使用旧鸿蒙方案吗？

不可以。HarmonyOS 5/6 原生分支和无法确认版本的 Harmony 分支与 HarmonyOS 1–4 流程严格隔离。

## 技术说明 / Technical details

应用把组件存在、组件可信度、功能健康和 Play Certification 分开评估。运行时规则来自已验证签名的 Catalog snapshot；自动配置只接受精确 `DEVICE_VERIFIED` 记录，并在下载和执行前再次核对设备、系统、版本、SHA-256 与签名证书。证据不足、规则不适用或校验失败时均 fail closed。

The runtime evaluates component presence, component trust, functional health, and Play Certification independently. Configuration requires an exact signed `DEVICE_VERIFIED` record and revalidates the device profile, catalog snapshot, package versions, SHA-256 hashes, and signer certificates before execution.

## 文档 / Documentation

- [正式隐私说明](PRIVACY.md)
- [Apache License 2.0](LICENSE)
- [第三方许可证边界](THIRD_PARTY_NOTICES.md)
- [真机冒烟测试清单](docs/RELEASE-SMOKE-TEST.md)
- [Release signing 配置](docs/RELEASE_SIGNING.md)

## 开发验证 / Development verification

```powershell
.\.toolchains\gradle-8.9\bin\gradle.bat assembleDebug
.\.toolchains\gradle-8.9\bin\gradle.bat assembleRelease
.\.toolchains\gradle-8.9\bin\gradle.bat testDebugUnitTest
.\.toolchains\gradle-8.9\bin\gradle.bat lintDebug
.\.toolchains\gradle-8.9\bin\gradle.bat verifyReleaseSigningHygiene
git diff --check
```
