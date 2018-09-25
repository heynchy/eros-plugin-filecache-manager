# eros-plugin-filecache-manager
基于Eros框架下的Android端文件的查看，下载，预览基本功能以及本地缓存大小的获取，清除功能的基本实现

### 文件操作
  #### 1.	Module名称： FileModule
  #### 2.	相关方法：
   2.1	文件下载
        方法名称：
```java
        
         /**
     * 下载文件的方法，带有进度的
     *
     * @param params   相关参数包含url, fileId, fileName
     * @param success  下载成功的回调
     * @param failure  下载失败的回调
     * @param progress 下载进度的回调
     */
    @JSMethod(uiThread = true)
    public void downloadFile(String params, final JSCallback success, final JSCallback failure,
                             final JSCallback progress)    
```

   2.2 判断文件是否存在（是否已下载）
       方法名称：isFileExist ( String params, JSCallback resultCallback)
       参    数：params: Json格式，包含fileId 和 fileName
              resultCallback: 结果回调（true: 文件存在 false: 文件不存在）

   2.3	预览文件（查看文件）
        方法名称：previewFile (String params)
        参    数：params: Json 格式，包含fileId 和fileName

## 缓存数据操作
  1.	Module 名称： CacheModule
  2.	相关方法
    2.1	获取缓存文件的大小
        方法名称： getCacheSize  ( JSCallback  callback)
        参    数： callback: 结果回调（返回缓存大小的字符串, 例如“2.37MB“）
    2.2	清除缓存
        方法名称：clearCaches ( JSCallback callback)
        参   数 ： callback: 结果回调（如果清除成功则返回true, 否则返回false)
