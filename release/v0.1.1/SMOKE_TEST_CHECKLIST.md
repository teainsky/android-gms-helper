# GMS 助手 v0.1.1 真机冒烟测试清单

> 本清单用于 RC 真机验收。每项需在对应真实设备上记录 PASS / FAIL 和必要备注；生成清单本身不代表已经完成真机测试。

## Huawei Pura 70 Pro+ / HBN-AL80 / HarmonyOS 4.2

- [ ] microG Services 与 Companion 两个组件识别正确。
- [ ] 检测到 microG 后，Self-Check / 设置入口可以打开。
- [ ] microG Companion 不被误认成官方 Google Play Store，并显示避免直接覆盖的提示。
- [ ] Chrome、Android System WebView 与 Custom Tabs 认证链路诊断可以运行并给出状态。
- [ ] GMS 助手自身缓存可以安全清理，不扫描或删除其他私人文件。
- [ ] 既有精确验证结果不回归：`DEVICE_VERIFIED`，已正确安装组件时保持 `NO_ACTION_REQUIRED`。

## Huawei Pura / HarmonyOS 6.1 + 卓易通

- [ ] 仍然只允许设备检测与基础诊断。
- [ ] 正确识别为第三方兼容运行环境，并明确检测结果不代表手机原生系统。
- [ ] 不显示 microG 自动配置入口，不允许下载旧鸿蒙组件或进入安装流程。

## 通用检查

- [ ] 从 v0.1.0 覆盖安装后可以正常启动，用户数据和既有安全状态未异常丢失。
- [ ] “怎么修”仅执行安全的本应用操作，其他修复只进入 Android 官方设置页。
- [ ] 中文与 English 界面关键状态、说明和入口可读。
