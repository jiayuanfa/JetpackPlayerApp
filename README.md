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

# 使用QQ登录实现三方登录、结合Navigator框架实现登录拦截
- 我的页面登录拦截（MainActivity.java）
- QQ登录实现（LoginActivity.java）
- 登陆成功过上报至服务器登录接口、缓存用户登录信息（LoginActivity.java、UserManager.java）
- 解决LiveData数据倒灌造成一直回调登录成功的问题（UserManager.java）

# 首页
- 自定义图片View之PPImageView，支持宽大于高、高大于宽，动态左边距的ImageView（PPImageView.java）
- 自定义视频播放器ListPlayerView，支持宽大于高、高大于宽，动态展示的播放器View（ListPlayerView.java）
- 首页视频图片流列表Item UI 编写 （layout_feed_type_image.xml、layout_feed_type_video.xml）
- 首页视频图片流列表Model编写 （Feed.java）
- 通用的带下拉刷新、上拉加载、空视图的RecyclerView布局列表xml文件编写 （layout_refresh_view.xml）
- 通用的抽象的ListFragment封装 （AbsListFragment.java）
- 通用的抽象的viewModel的封装、结合Paging框架PagedList的使用 （AbsViewModel.java）
- PagedListAdapter的使用（FeedAdapter.java）
- SmartRefreshLayout的使用 （HomeFragment.java）
- ViewModel的使用、Paging数据源框架ItemKeyedDataSource的使用、初次加载数据、加载更多等数据逻辑处理（HomeViewModel.java）
- 数据callBack的数据是Paging的数据源框架ItemKeyedDataSource等内部处理的
- 缓存加载更多的的处理（HomeFragment.java）
- LiveData的使用（HomeFragment.java）
- 缓存处理代码、发射、监听 (HomeViewModel.java、HomeFragment.java)
- 点赞、踩接口处理、以及点赞之后页面的刷新、绑定、（使用阿里巴巴的JSONObject解析import com.alibaba.fastjson.JSONObject;
  、layout_feed_interaction.xml、InteractionPresenter.java）
- 首页数据接口加上用户ID，保证用户操作之后下拉刷新能够使用到用户行为之后的新数据（HomeFragment.java）

# 分享
- 自定义View可以给传入的View设置任意的圆角（CornerFrameLayout.java、ViewHelper.java）
- 自定义分享弹窗 （ShareDialog.java）
- 获取系统中可分享的APP并调用分享（ShareDialog.java）
- DataBinding中的Context的获取以及子的Layout文件的Content获取，通过传递LifeCycleOwner强转Context实现

# 沙发
# 我的