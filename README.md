# eros-plugin-filecache-manager
         -----当前支持wxframework <= 1.0.9的版本
基于Eros框架下的相关功能（weex 与 Android 的交互）：
 1. Android端文件的查看，下载，预览基本功能
 2. 本地缓存大小的获取，清除功能
 3. 跳转至应用市场评分页（支持三星市场）
 4. 获取软键盘的高度
 5. 获取屏幕的高度（不包含虚拟按键），版本 0.0.9
 6. 为下载添加token参数，版本 0.1
 7. 修改打开Txt文档崩溃的BUG  ， 版本0.1.1
## Usage
###  Add dependency
```groovy
	dependencies {
	        implementation 'com.github.heynchy:eros-plugin-filecache-manager:0.1'
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

License
-------
    Copyright 2018 heynchy

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
