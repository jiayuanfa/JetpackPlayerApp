# JetpackPlayerApp

# 一款基于Jetpack技术架构实现的短视频APP 

# 技术要点
- 通过反射获取全局的Application实例（AppGlobals）
- 使用注解生成器生成Fragment、Activity路由json，适合大型项目
- 通过json文件动态灵活底部导航（好处：可以根据登录权限等配置底部导航栏的个数）
- 定制FragmentNavigator，实现底部Tab切换的时候，使用hide()/show()，而不是replace()。解决生命周期重启的问题

# 环境配置
- Android Studio Chipmunk
- Jdk 1.8.0_291
- NDK 21.4.7075529
- Gradle 5.4.1
- Gradle Plugin 3.5.3
- compileSdk 29
- targetSdk 29
- minSdk 21
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
- 创建libnavannotation注解Java模块
- 创建libnavcompiler Java模块，用于给Fragment、Activity生成路由json文件
- 给HomeFragment、SofaFragment、PublishActivity、FindFragment、MyFragment添加路由注解生成器
- 创建libcommon 安卓模块，编写整个项目所依赖的Jetpack库
- 通过反射全局获取Application实例（libcommon、AppGlobals）
- 获取json配置文件并解析为model，以供自定义NavGraph、自定义的AppBottomBar使用（AppConfig）
- 自定义NavGraphBuilder，实现路由自由配置、可拦截（NavGraphBuilder）
- 自定义AppBottomBar，灵活控制显示逻辑（AppBottomBar）
- MainActivity使用自定义的AppBottomBar、自定义NavGraphBuilder（MainActivity）

# 网络库与缓存
- Okhttp证书信任问题（ApiService）
- 使用FastJson封装JsonConvert转换类（JsonConvert）
- 封装Request抽象类
- 封装GetRequest、PostRequest请求类
- MainActivity请求测试
- Room数据库的创建（CacheDatabase）
- Cache数据表的增删改查（Cache、CacheDao）
- Cache存取封装（CacheManager）

# 首页
# 沙发
# 我的