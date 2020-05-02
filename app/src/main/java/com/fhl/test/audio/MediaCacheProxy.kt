package com.fhl.test.audio

import android.util.Log
import com.danikula.videocache.HttpProxyCacheServer
import com.fhl.test.MyApplication
import java.io.File

class MediaCacheProxy {

    private var proxy: HttpProxyCacheServer
    val path = "${MyApplication.sContext.getExternalFilesDir(null)}${File.separator}mogufm"

    private constructor() {

        val fileDir = File(path)
        if (!fileDir.exists()) {
            val flag = fileDir.mkdirs()
            Log.i(TAG,"创建文件夹: $flag")
        }
        proxy = HttpProxyCacheServer.Builder(MyApplication.sContext)
            .cacheDirectory(File(path))
//            .fileNameGenerator(MyFileNameGenerator())
            .maxCacheSize(1024 * 1024 * 1024)//最大缓存大小1G
            .maxCacheFilesCount(200)
            .build()
    }

    companion object {
        val TAG = "MediaCacheProxy"

        @Volatile
        private var instance: MediaCacheProxy? = null

        fun getInstance(): MediaCacheProxy {
            if (instance == null) {
                synchronized(MediaCacheProxy::class) {
                    if (instance == null) {
                        instance = MediaCacheProxy()
                    }
                }
            }
            return instance!!
        }
    }


    fun getCachePatch(url: String): String {
        return proxy.getProxyUrl(url)
    }

    fun isCacheExist(url: String): Boolean {
        return proxy.isCached(url)
    }

    fun delete(url: String) {

    }
}