# JetpackPlayerApp

# 一款基于Jetpack技术架构实现的短视频APP 

# 技术要点
- 使用注解生成器生成Fragment、Activity路由json，适合大型项目

# 环境
- Android Studio Chipmunk
- Jdk 1.8.0_291
- Gradle 7.3.3
- Gradle Plugin 7.2.0
- compileSdk 32
- targetSdk 32
- minSdk 21

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