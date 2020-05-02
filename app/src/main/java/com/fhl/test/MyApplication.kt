package  com.fhl.test

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import com.danikula.videocache.HttpProxyCacheServer
import info.guardianproject.netcipher.client.TlsOnlySocketFactory
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext

/**
 *
 * @ProjectName:    MyKotlinApplication
 * @Package:        com.example.mykotlin
 * @ClassName:      MyApplication
 * @Description:    java类作用描述
 * @Author:         作者名
 * @CreateDate:     2019/11/18 15:46
 * @UpdateUser:     更新者：
 * @UpdateDate:     2019/11/18 15:46
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
class MyApplication : Application() {

    companion object {
        lateinit var sContext: Context

    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this);
    }
    override fun onCreate() {
        super.onCreate()
        sContext = applicationContext
        configSSL()
    }

    private var proxy: HttpProxyCacheServer? = null
    private fun configSSL(){

        val sslContext = SSLContext.getInstance("TLS")
        sslContext.init(null, null, null)
        val noSSLv3Factory = TlsOnlySocketFactory(sslContext.socketFactory)
        HttpsURLConnection.setDefaultSSLSocketFactory(noSSLv3Factory)
    }
}