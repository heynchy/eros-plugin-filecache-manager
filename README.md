# eros-plugin-filecache-manager
基于Eros框架下的Android端文件的查看，下载，预览基本功能以及本地缓存大小的获取，清除功能的基本实现
## Usage
###  Add dependency
```groovy
	dependencies {
	        implementation 'com.github.heynchy:eros-plugin-filecache-manager:0.0.3'
	}

```

### 文件操作Module
#### 1.	Module名称： FileModule
#### 2.	相关方法：
    2.1	文件下载
```java
        
   /**
     * 下载文件的方法，带有进度的
     *
     * @param params   必须传递（Json 格式）,相关参数包含url, fileId, fileName
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
