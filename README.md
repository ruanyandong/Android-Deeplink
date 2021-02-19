## DeepLink 介绍

DeepLink，即为深度链接技术，主要应用场景是通过Web页面直接调用Android原生app，并且把需要的参数通过Uri的形式，直接传递给app，节省用户的注册成本。

DeepLink 通常运用于App社交分享、App广告引流、App裂变活动、Web to App、分享效果统计、沉默用户唤醒等场景，对广告引流、活动推广、新闻类、电商类、游戏类、视频直播类App的引流推广和转化都有着奇效。

常见使用场景如下：

    电商类：在分享商品链接中点击，进入 App 内对应店铺或购物页面
    资讯类：在分享新闻链接中点击，进入 App 内对应内容页面
    游戏类：在分享邀请组队的链接中点击，进入 App 内对应的游戏房间或战队队伍中
    广告：在社交平台点击相关广告，进入 App 内对应内容页面
    拉新活动：例如老带新邀请、福利抽奖等 H5 页面活动，参与者可以点击进入 App 内对应活动参与页面

App 间的自由跳转，解决的不仅仅是用户体验问题，更是拓展 App 的应用宽度问题，有了深度链接后，App 之间不再是独立的个体平台，开发者可以在移动端再现网页端的自由跳转，将广告、活动营销、裂变拉新、用户唤醒等业务结合其中，创造一个更加完整、精简的转化链，能给 App 的运营和推广带来更多想象空间。
## Android DeepLink 原理

Android的DeepLink实现首先需要在Web页面调起Android App，这块的基础实现，我们在之前整理的 Android 从浏览器启动应用 里面已经讲述了。

这里我们再进行更多的扩展和说明。

移动端深度链接，本质上是使用URI的schema，移动操作系统提供解析schema的能力，判断schema属于哪个app，唤起并将参数传递给App。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210219190610777.png)


```javascript
URI实例：myapp://test/spec?param1=p1&param2=p2
```

其中需要注意的是：

    App1必须支持，如微信屏蔽了很多schema，一般手机浏览器不会屏蔽shcema；
    APP2必须支持，APP也需要开发,让系统知道其对应的schema，并解析参数定位到具体位置。

Android 深度链接唤起App顺序：

    打开用户指定的首选APP（如果用户指定了URI对应的APP）；
    打开处理URI的唯一APP；
    对话框中选择相应的APP（URI对应多个APP的情况）

如何进行URI Schema的配置，可以参照  Android 从浏览器启动应用，即在Activity的配置文件中添加如下：


```java
<intent-filter>
   <data android:scheme="***" /> /* URI Schema 在此进行配置 */
   <action android:name="android.intent.action.VIEW" />
   <category android:name="android.intent.category.DEFAULT" />
   <category android:name="android.intent.category.BROWSABLE" />
</intent-filter>
```



data可配置的内容如下：


```java
<data
     android:scheme="xxxx"
     android:host="xxxx"
     android:port="xxxx"
     android:path="xxxx"
     android:pathPattern="xxxx"
     android:pathPrefix="xxxx"
     android:mimeType="xxxx"/>
```

这里我们解释一个每个字段所代表得意思：

    scheme：协议类型，我们可以自定义，一般是项目或公司缩写，String
    host：域名地址，String
    port：端口，int。
    path：访问的路径，String
    pathPrefix：访问的路径的前缀，String
    pathPattern：访问路径的匹配格式，相对于path和pathPrefix更为灵活，String
    mimeType：资源类型，例如常见的：video/*, image/png, text/plain。

通过这几个配置项，我们可以知道data实际上为当前的页面绑定了一个Uri地址，通过Uri直接打开这个Activity。

Uri的结构如下：

```java
<scheme> :// <host> : <port> / [ <path> | <pathPrefix> | <pathPattern> ]
```

scheme和host不可缺省，否则配置无效；path，pathPrefix，pathPattern一般指定一个就可以了，pathPattern与host不可同时使用；mimeType可以不设置，如果设置了，跳转的时候必须加上mimeType，否则不能匹配到Activity。

 ## Android deeplink和AppLink区别

deeplink，也包括Android6.0之后的AppLink。AppLink就是特殊的deeplink，只不过它多了一种类似于验证机制，如果验证通过，就设置默认打开，如果验证不过，则退化为deeplink，如果单从APP端来看，区别主要在Manifest文件中的`android:autoVerify="true"`，如下，

APPLINK只是在安装时候多了一个验证，其他跟之前deeplink一样，如果没联网，验证失败，那就跟之前的deeplink表现一样

**deeplink配置（不限http/https）**

```java
<intent-filter>
    <data android:scheme="https" android:host="test.example.com"  />
    <category android:name="android.intent.category.DEFAULT" />
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.BROWSABLE" />
</intent-filter>

 （不限http/https）
 <intent-filter>
        <data android:scheme="example" />
        <!-- 下面这几行也必须得设置 -->
        <category android:name="android.intent.category.DEFAULT" />
        <action android:name="android.intent.action.VIEW" />
        <category android:name="android.intent.category.BROWSABLE" />
</intent-filter>
```

**applink配置（只能http/https）**

```java
<intent-filter android:autoVerify="true">
    <data android:scheme="https" android:host="test.example.com"  />
    <category android:name="android.intent.category.DEFAULT" />
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.BROWSABLE" />
</intent-filter>
```

果如果APPLink验证失败，APPLink会完全退化成deepLink，这也是为什么说APPLINK是一种特殊的deepLink，所以先分析下deepLink，deepLink理解了，APPLink就很容易理解。

## 延迟深度链接

延迟深度链接，也称为Deferred Deep Linking，也就是深度链接的延迟版，实际上延迟深度链接是对深度链接功能的一个细分，是指在用户点击深度链接中打开APP的按钮时，如果手机没有安装APP能够自动跳转到APP的下载页面。

延迟深度链接对于APP而言最大的作用在于缩短了下载路径，能够有效地提升APP的下载量。我们回想一下没有延迟深度链接的场景，当用户收到一个好友发来的或者在Web上浏览的一个推广链接，比如是某电商APP的一件商品，如果他的手机没有安装该APP那么他的操作路径是“退出链接-打开应用商店-搜索APP-安装APP”，如果使用了延迟深度链接用户的操作路径就会优化成“点击链接内按钮-安装APP”。在这种优化之中，用户的主动操作只留下了最开始的按钮触发，既是对用户操作的一种体验优化，更减少了到达下载路径之前用户的主观思考环境，让用户在改变主意之前完成了下载。

## DeepLink实现Demo
###### 首先创建Android工程。
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210219191422186.PNG?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0NjgxNTgw,size_16,color_FFFFFF,t_70#pic_center)
###### 新建HTML网页
![在这里插入图片描述](https://img-blog.csdnimg.cn/20210219191752127.PNG?x-oss-process=image/watermark,type_ZmFuZ3poZW5naGVpdGk,shadow_10,text_aHR0cHM6Ly9ibG9nLmNzZG4ubmV0L3FxXzM0NjgxNTgw,size_16,color_FFFFFF,t_70#pic_center)协议请自行定义，我这里使用`ryd://deeplink/main`
###### 新建WebActivity加载网页
代码如下：

```java
public class WebActivity extends AppCompatActivity {
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        webView = findViewById(R.id.web);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (TextUtils.isEmpty(url)) {
                    return false;
                }
                try {
                    // 用于DeepLink测试
                    if (url.startsWith("ryd://")) {
                        Uri uri = Uri.parse(url);
                    }
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri uri = Uri.parse(url);
                    intent.setData(uri);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        });
        webView.loadUrl("file:///android_asset/web/index.html");
    }
}
```
###### 新建MainActivity,	并编辑Manifest文件
代码如下：

```java
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WebActivity.class));
            }
        });

        getDataFromBrowser();
    }

    /**
     * 从deep link中获取数据
     * 'will://share/传过来的数据'
     */
    private void getDataFromBrowser() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        Uri data = getIntent().getData();
        if (data == null) {
            return;
        }
        try {
            String scheme = data.getScheme();
            String host = data.getHost();
            Log.d("ryd", "getDataFromBrowser: " + scheme + " " + host);
//            List<String> params = data.getPathSegments();
//            // 从网页传过来的数据
//            String testId = params.get(0);
//            String text = "Scheme: " + scheme + "\n" + "host: " + host + "\n" + "params: " + testId;
//            Log.e("ScrollingActivity", text);
//            textView.setText(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
清单文件：

```java
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.ai.deeplink">
    <uses-permission android:name="android.permission.INTERNET"/>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">

        <activity android:name=".WebActivity"/>

        <activity android:name=".MainActivity">

            <intent-filter>
                <action android:name="android.intent.action.MAIN" />

                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>

            <intent-filter>
                <action android:name="android.intent.action.VIEW" />
                <category android:name="android.intent.category.DEFAULT" />
                <category android:name="android.intent.category.BROWSABLE" />
                <data
                    android:scheme="ryd"
                    android:host="deeplink"
                    android:path="/main"
                    />
            </intent-filter>

        </activity>
    </application>

</manifest>
```

   **指定 ACTION_VIEW intent 操作，以便能够从 Google 搜索中访问此 intent 过滤器。**

   添加一个或多个 <data> 标记，每个标记都代表一个解析为 Activity 的 URI 格式。<data> 标记必须至少包含 android:scheme 属性。

   您可以添加更多属性，以进一步细化 Activity 接受的 URI 类型。例如，您可能拥有多个接受相似 URI 的 Activity，这些 URI 只是路径名称有所不同。在这种情况下，请使用 android:path 属性或其 pathPattern 或 pathPrefix 变体区分系统应针对不同 URI 路径打开哪个 Activity。

   **包含 BROWSABLE 类别。如果要从网络浏览器中访问 Intent 过滤器，就必须提供该类别。否则，在浏览器中点击链接便无法解析为您的应用。**

   **此外，还要包含 DEFAULT 类别。这样您的应用才可以响应隐式 intent。否则，只有在 intent 指定您的应用组件名称时，Activity 才能启动。**

以下 XML 代码段展示了如何在清单中为深层链接指定 intent 过滤器。URI “example://gizmos” 和 “http://www.example.com/gizmos” 都会解析到此 Activity。

    <activity
        android:name="com.example.android.GizmosActivity"
        android:label="@string/title_gizmos" >
        <intent-filter android:label="@string/filter_view_http_gizmos">
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <!-- Accepts URIs that begin with "http://www.example.com/gizmos” -->
            <data android:scheme="http"
                  android:host="www.example.com"
                  android:pathPrefix="/gizmos" />
            <!-- note that the leading "/" is required for pathPrefix-->
        </intent-filter>
        <intent-filter android:label="@string/filter_view_example_gizmos">
            <action android:name="android.intent.action.VIEW" />
            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />
            <!-- Accepts URIs that begin with "example://gizmos” -->
            <data android:scheme="example"
                  android:host="gizmos" />
        </intent-filter>
    </activity>
    

请注意，<data> 元素是这两个 intent 过滤器的唯一区别。虽然同一过滤器可以包含多个 <data> 元素，但如果您想要声明唯一网址（例如特定的 scheme 和 host 组合），则创建单独的过滤器很重要，因为同一 intent 过滤器中的多个 <data> 元素实际上会合并在一起以涵盖合并后属性的所有变体。例如，请参考以下示例：

    <intent-filter>
      ...
      <data android:scheme="https" android:host="www.example.com" />
      <data android:scheme="app" android:host="open.my.app" />
    </intent-filter>
    

**看起来这似乎仅支持 https://www.example.com 和 app://open.my.app。但是，实际上除了这两种之外，它还支持 app://www.example.com 和 https://open.my.app。**

当您向应用清单添加包含 Activity 内容 URI 的 intent 过滤器后，Android 可以在运行时将所有包含匹配 URI 的 Intent 转发到您的应用。

如需详细了解如何定义 intent 过滤器，请参阅允许其他应用启动您的 Activity。
读取传入 intent 中的数据

在系统通过 intent 过滤器启动您的 Activity 后，您可以根据 Intent 提供的数据确定需要呈现的内容。调用 getData() 和 getAction() 方法检索与传入 Intent 相关联的数据和操作。您可以在 Activity 生命周期的任何时间执行此 Activity，但您通常应在早期回调时（比如，onCreate() 或 onStart()）执行。

以下代码段展示了如何检索 Intent 中的数据：

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
    }
    

请遵循以下最佳做法以改进用户体验：

    深层链接应该将用户直接转到相应内容，而不会出现任何提示、转到任何插页式网页或登录页。请确保用户即使之前从未打开过相应应用，也可以查看应用内容。您可以在后续互动中或在用户从启动器中打开应用时进行提示。
    遵循使用“返回”和“向上”导航中所述的设计指南，确保您的应用能达到用户通过深层链接进入您的应用后对向后导航体验的预期。

测试深层链接

您可以将 Android 调试桥与 Activity 管理器 (am) 工具结合使用，以测试您为深层链接指定的 intent 过滤器 URI 是否可以解析为正确的应用 Activity。您可以针对设备或模拟器运行 adb 命令。

使用 adb 测试 intent 过滤器 URI 的一般语法为：

    $ adb shell am start
            -W -a android.intent.action.VIEW
            -d <URI> <PACKAGE>
    

例如，以下命令试图查看与指定 URI 相关的目标应用 Activity。

    $ adb shell am start
            -W -a android.intent.action.VIEW
            -d "example://gizmos" com.example.android
    

## 参考文档
[Android DeepLink介绍与使用](https://blog.csdn.net/jingbin_/article/details/84228877)
[Android deeplink和AppLink原理](https://www.cnblogs.com/mingfeng002/p/10579771.html)
[Android DeepLink 深度链接技术实现](https://www.cnblogs.com/renhui/p/13188538.html)
[使用DeepLink技术唤醒 App](https://www.jianshu.com/p/a606d8898cea)
[Google官方DeepLink文档](https://developer.android.com/training/app-links/deep-linking)





