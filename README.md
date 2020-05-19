# eros-plugin-filecache-manager

####  当前工具类支持--基础依赖包wxframework <= 1.0.9 的版本

####  如果您的基础依赖包wxframework的版本 > 1.0.9 , 为了使该工具类继续可以使用， 可前往: [Eros-plugin-android-extend](https://github.com/heynchy/eros-plugin-android-extend)
      
#### 基于Eros框架下的相关功能（weex 与 Android 的交互）：
 1. Android端文件的查看，下载，预览基本功能
 2. 本地缓存大小的获取，清除功能
 3. 跳转至应用市场评分页（支持三星市场）
 4. 获取软键盘的高度
 5. 获取屏幕的高度（不包含虚拟按键），版本 0.0.9
 6. 为下载添加token参数，版本 0.1
 7. 修改打开Txt文档崩溃的BUG  ， 版本0.1.1
 8. 修改下载文件的进度值的返回值问题， 版本0.1.2
 9. 为了提高与JS端交互时下载后打开文件的灵活性，隐藏了下载完成后主动打开的功能，JS端可使用previewFile(params)主动打开， 版本0.1.3
 10. 增加在当前app中打开其他app的方法；
     增加Android端的本地存储工具类，等同于JS端的storage存储方式
     本地存储和JS端的存储可通用， 即Android端可根据key值获取JS存储的数据；
     JS端可根据key值获取Android端存储的数据；      版本0.1.4
 11. 增加本地选择文件并上传的功能（上传的代码逻辑类比Eros中的图片上传） 版本0.1.5
 12. 增加打开xlsx文件的方式      版本0.1.6
 13. 增加获取apk包的MD5值的方法， 用以完整性校验 版本0.1.9
 14. 增加强制退出app的功能（kill进程）          版本0.2
 15. 处理MD5值首位为0时自动消除0的BUG                     版本0.2.1
 16. 增加断点下载的功能downloadBreakPoint(),             版本0.2.4
 17. 文件上传时添加打开指定文件夹的功能                   版本0.2.5
## Usage
###  Add dependency
```groovy
	dependencies {
	        implementation 'com.github.heynchy:eros-plugin-filecache-manager:0.2.5'
	}

```
### 添加混淆（如果需要断点下载功能）
```groovy
 # 断点下载的混淆
-keepnames class com.liulishuo.okdownload.core.connection.DownloadOkHttp3Connection
# ------- end com.liulishuo.okdownload:okhttp proguard rules ----
-keep class com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite {
        public com.liulishuo.okdownload.core.breakpoint.DownloadStore createRemitSelf();
        public com.liulishuo.okdownload.core.breakpoint.BreakpointStoreOnSQLite(android.content.Context);
}
```

### 文件操作Module
#### 1.	Module名称： FileModule
#### 2.	相关方法：
    2.1 文件下载
```java
        
   /**
     * 下载文件的方法，带有进度的
     *
     * @param params   必须传递（Json 格式）,相关参数包含url, fileId, fileName，token(需要权限的传递该参数)
     * @param success  下载成功的回调
     * @param failure  下载失败的回调
     * @param progress 下载进度的回调
     */
    @JSMethod(uiThread = true)
    public void downloadFile(String params, final JSCallback success, final JSCallback failure, final JSCallback progress)    
``` 
    2.1.1 文件断点下载（参数，返参跟downloadFile一样，就是方法名称不一样而已）
```java
        
   /**
     * 下载文件的方法，带有进度的
     *
     * @param params   必须传递（Json 格式）,相关参数包含url, fileId, fileName，token(需要权限的传递该参数)
     * @param success  下载成功的回调
     * @param failure  下载失败的回调
     * @param progress 下载进度的回调
     */
    @JSMethod(uiThread = true)
    public void downloadBreakPoint(String params, final JSCallback success, final JSCallback failure, final JSCallback progress)    
``` 
    2.2 判断文件是否存在（是否已下载）
```java
   /**
     * 判断文件是否存在
     *
     * @param params  Json格式，包含fileId 和 fileName
     * @param resultCallback  结果回调（true: 文件存在 false: 文件不存在）
     */
    @JSMethod(uiThread = true)
    public void isFileExist(String params, JSCallback resultCallback)
```
     2.3 预览文件（查看文件）
```java
     /**
     * 预览文件
     *
     * @param params  Json 格式，包含fileId 和fileName
     */
    @JSMethod(uiThread = true)
    public void previewFile(String params)
```
    2.4 打开应用市场的评分页（应用详情页）
```java
    /**
     * 跳转至应用市场的评价界面
     */
    @JSMethod(uiThread = true)
    public void marketComment()
```
    2.5 选择本地文件并上传（JS端的调用实现方式）
```java
        weex.requireModule('FileModule').pickAndUpload({
            url: '',      // 上传的接口地址
            header: {     // 上传的相关头文件信息
                'Content-Type':'multipart/form-data',  // 必传参数
                'Authorization':data                   // token (如果没有可以不传)
            }，
	    params:{
	         // 其他参数的信息（可参考Eros图片上传时该处的取值格式进行设置）
	    }，
	    fileFolderPath:"test"  // 路径以手机根目录为基础，传参只传根目录下的文件夹路径。 例：/storage/emulated/0/test， 只传test即可
        }, success =>{
            // 上传成功的回调
        }, failure => {
            // 上传失败的回调
        }，progress => {
	   // 进度值的回调（0--100）
	});
```
### 缓存数据操作Module
#### 1.	Module 名称： CacheModule
#### 2.	相关方法
    2.1	获取缓存文件的大小
```java
   /**
    * 获取应用缓存的大小
    * 
    * @param callback  结果回调（返回缓存大小的字符串, 例如"2.37MB")
    */
    @JSMethod(uiThread = true)
    public void getCachesSize(JSCallback callback) 
```
    2.2	清除缓存
```java
   /**
     * 清除应用缓存
     *
     * @param callback  结果回调（如果清除成功则返回true, 否则返回false)
     */
    @JSMethod(uiThread = true)
    public void clearCaches(JSCallback callback)
```
### Android相关功能的Module
#### 1. Module 名称： UtilModule
#### 2. 相关方法
     2.1 获取Android手机软件盘的高度
```java
    /**
     * 获取Android手机软键盘的高度，返回值包括pxHeight（以px为单位）和dpHeight（以dp为单位）-----非监听形式
     *
     * @param callback           软键盘弹出的回调
     * @param callbackInvisible  软键盘隐藏的回调
     */
    @JSMethod(uiThread = true)
    public void getSoftKeyInfo(final JSCallback callback, final JSCallback callbackInvisible) 
    
   /**
     * 获取Android手机软键盘的高度----监听形式
     *
     * @param callback          软键盘弹出的回调
     * @param callbackInvisible 软键盘隐藏的回调
     */
    @JSMethod(uiThread = true)
    public void getSoftKeyInfoAlive(final JSCallback callback, final JSCallback callbackInvisible)
```
    2.1.1 JS端使用说明
```java
    weex.requireModule('UtilModule').getSoftKeyInfo(visible => {
                   // 软键盘弹出后的相关操作
                    var date = JSON.parse(visible);
                    console.log("heyn_OtherNormalModule1: "+ date.pxHeight);
                    console.log("heyn_OtherNormalModule2: "+ date.dpHeight);
                }, invisible =>{
		   // 软键盘隐藏后的相关操作
                    var date = JSON.parse(invisible);
                    console.log("heyn_OtherNormalModule3: "+ date.pxHeight);
                    console.log("heyn_OtherNormalModule4: "+ date.dpHeight);
                });
```
    2.2 获取Android手机的屏幕高度（不包含虚拟按键）
```java
    /**
     * 获取Android屏幕尺寸，但是不包括虚拟键的高度
     *
     * @param callback 返回值的回调（已转换为JS端可用数据）
     */
    @JSMethod(uiThread = true)
    public void getNoHasVirtualKey(final JSCallback callback)
```
    2.3 在当前app打开另外一个app
```java
    /**
     * 打开另外一个APP
     *
     * @param params             相关参数配置（JSON格式，包括如下参数）
     *                              ----- packageName: 包名（APPLICATION_ID）--->必须提供
     *                              ----- activityName: 页面的路径名（例如com.benmu.wx.activity.SplashActivity）--->可选
     *                              ----- key: 需要传参时的key--->可选(key 和 params，要么都为空，要么都不为空)
     *                              ----- params: Json格式，所传的参数--->可选(key 和 params，要么都为空，要么都不为空)
     * @param resultCallback     结果回调（true: 打开成功， false: 打开失败）
     * @param installed          安装回调（如果未安装，会响应---该回调）
     */
    @JSMethod(uiThread = true)
    public void openOtherApp(String params, JSCallback resultCallback, JSCallback installed)
```
     2.3.1 JS端的使用方式----打开另一个APP
```java
           weex.requireModule('UtilModule').openOtherApp({
                packageName:'com.test.heynchy',
                activityName:'com.benmu.wx.activity.SplashActivity',
                key:'TEST',
                params:'{"token":"mytoken","usename":"heynchy","useId":"helloID"}'
            }, result =>{
	        // 打开成功返回true, 打开失败返回false
                console.log("heyn","result: "+result)
            }, inststall =>{
	       // 如果检测到为未安装则执行该函数，
	       // TODO 下载需要打开的app
                console.log("heyn","inststall: "+inststall)
            });
```

    2.3.2 注意事项 
       1. activityName:  如果该值不为null, 即指定了打开的页面，则需要在AndroidManifest.xml 中的指定的页面activity
                         中添加  android:exported="true" ； 例如上面示例中，需要添加在
			  <activity
                           android:name=".activity.SplashActivity"
                           android:theme="@style/FullscreenTheme"
                           android:exported="true"
			   ......
      2.  acitivityName如果为null, 则打开后默认跳转至app的启动页
      3.  如果要在两个app之间传递参数，则key值需保持一致，才能接收到 getIntent().getStringExtra(key);
    2.4 获取应用包（当前APP的APK包）的MD5
       weex.requireModule('UtilModule').getAPKMD5Code(success =>{
            console.log("chy1234", "success===="+success);
            this.$notice.alert({
                message:'success====='+success
            });
        }, failure=>{
            this.$notice.alert({
                message:'failure====='+failure
            });
        });
     2.5 强制退出APP
        weex.requireModule('UtilModule').exitAPP();
			 
### Android 原生方法工具类
####  存储工具类 ErosStorageUtil
##### 相关方法：
      1. 本地数据保存
          ErosStorageUtil.put(Context context, String key, String value);
      2. 存储数据移除
          ErosStorageUtil.remove(Context context, String key)
      3. 存储数据获取
          ErosStorageUtil.get(Context context, String key)，返回值String类型
### JS 调用方式----举例——清除缓存的使用
```java
<template>
  <div>
    <text onclick="click">testMyModule</text>
  </div>
</template>

<script>
  module.exports = {
    methods: {
      click: function() {
        weex.requireModule('CacheModule').clearCaches(params => {
           if (params){
               //  清除成功
           } else {
              //  清除失败
           }
        });
      }
    }
  }
</script>
```
#### 注意事项
    1. 涉及到相关权限问题，需要手动添加至自己工程的AndroidManifest.xml中（针对上架的权限审核问题）
       1.1 文件操作权限包括：
           <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
           <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /
       1.2 网络权限：
            <uses-permission android:name="android.permission.INTERNET" /> 
       1.3 Android 8.0以上版本的APk安装时，需要安装权限：
            <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES" />
	   

License
-------
    Copyright 2018-2020 heynchy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
