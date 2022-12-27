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

# 自定义View
- 自定义录制按钮（RecordView.java）

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

# 播放器
- 播放器Target 面向接口编程（IPlayTarget.java）
- 播放器的编写（PageListPlay.java）
- 播放列表播放检测类、监听RV的滚动已处理播放逻辑（PageListPlayDetector.java）
- 播放列表页面管理器，维护各个页面的播放器实例（PageListPlayManager.java）
- 播放区域点击事件接管

# CameraX录制、拍照、文件存储
- 自定义View之录制按钮（RecordView.java）
- 权限获取（CaptureActivity.java）
- 拍摄/录制Activity（CaptureActivity.java）
- 预览Activity（PreviewActivity.java）

# LiveDataBus事件总线
- 正常时间：先监听，后发送
- 粘性事件：先发送，后监听
- 编写LiveDataBus实现正常事件与粘性事件的分发（LiveDataBus.java）

# 沙发
- TabLayout的使用（fragment_sofa.xml）
- ViewPager2的使用（fragment_sofa.xml、SofaFragment.java）
- TabLayoutMediator的使用使得TabLayout和ViewPager2相结合（SofaFragment.java）
- 解决Fragment复用造成的视频播放重复播放的问题（SofaFragment.java）

# 图文详情页、视频详情页
- 顶部HeaderXML组件代码编写（layout_feed_detail_author_info.xml）
- 底部FooterXML组件代码编写（layout_feed_detail_bottom_inateraction.xml）
- 可添加HeaderView和FooterView的抽象AbsPagedListAdapter组件代码编写（AbsPagedListAdapter.java）
- 评论列表Adapter代码编写、继承自AbsPageListAdapter（FeedCommentAdapter.java）
- 底部评论框CommentDialogFragment编写、评论之后PagedList增删功能、软键盘的控制相关（CommentDialog.java、ViewHandler.java）

## ViewHandler抽象类编写、持有图文详情和视频详情公共组件（ViewHandler.java）
- 持有ViewModel类、视图类
- 监听数据变化，设置数据、监听点击事件等等
- 接口事件接入、关注、收藏、点赞（InteractionPresenter.java）
## 图文详情ImageViewHandler逻辑处理代码（ImageViewHandler.java）
- 监听RV的滑动，处理头部的隐藏与现实工作
## 图文详情页代码编写 （FeedDetailActivity.java）
- 持有ImageViewHandler，并给其设置数据
- 视频详情页图文评论、视频加文字评论、多线程上传处理、Activity+DialogFragment+Activity跳转传值处理
- （CaptureActivity.java、PreviewActivity.java、CommentDialog.java）
- 视频评论点击可预览（FeedCommentAdapter.java）

# 我的

# 6.0以上沉浸式布局以及问题修复
- 沉浸式布局文件（StatusBar.java）
- 白底黑字、黑底白字（StatusBar.java）
- 问题修改（WindowInsetsFrameLayout.java、WindowInsetsNavHostFragment.java、activity_main.xml）

# 启动白屏问题修复
- 入口页面主题（styles.xml）
- 应用（AndroidManifest.xml）
- 进入页面处理（MainActivity.java）
