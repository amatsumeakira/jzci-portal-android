# JZCI Portal Android

晋中信息学院统一身份认证 Android 应用，基于 Kotlin + Jetpack Compose 构建。

## 功能特性

- CAS 统一身份认证登录
- 学生基本信息查看
- 学籍信息查看
- 宿舍信息查看
- 辅导员信息查看
- 家庭成员信息查看

## 技术栈

- **语言**: Kotlin
- **UI 框架**: Jetpack Compose
- **最低 SDK**: 26 (Android 8.0)
- **目标 SDK**: 34 (Android 14)
- **架构**: MVVM
- **网络**: HttpURLConnection + Gson

## 项目结构

```
app/src/main/java/com/jzci/portal/
├── data/
│   ├── api/          # API 服务
│   └── model/        # 数据模型
├── ui/
│   ├── screens/      # 页面
│   └── theme/        # 主题
├── JZCIPortalApp.kt  # Application 类
└── MainActivity.kt   # 主入口
```

## 构建

```bash
# Debug 构建
./gradlew assembleDebug

# Release 构建
./gradlew assembleRelease
```

## CI/CD

项目配置了 GitHub Actions，每次 push 到 main 分支时会自动构建 debug APK。

构建产物位于:
- `app/build/outputs/apk/debug/app-debug.apk`

## License

Private
