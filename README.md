# JetpackPlayerApp

# 一款基于Jetpack技术架构实现的短视频APP 

# 技术要点
- 使用注解生成器生成Fragment、Activity路由json，适合大型项目

# 环境配置
- Android Studio Chipmunk
- Jdk 1.8.0_291
- NDK 21.4.7075529
- Gradle 5.4.1
- Gradle Plugin 3.5.3
- compileSdk 29
- targetSdk 29
- minSdk 29
- Kotlin 1.3.72
- AndroidX true
- DataBinding true

## Jetpack
- Navigation
- DataBinding
- ViewPager2
- Paging
- WorkManager
- Room
- LiveData

# 架构代码编写
- 使用Navigator模式创建项目
- 创建libnavannotation注解模块
- 创建libnavcompiler模块，用于给Fragment、Activity生成路由json文件
- 给HomeFragment添加路由注解生成器


# 首页
# 沙发
# 我的