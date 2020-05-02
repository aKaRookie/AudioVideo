package com.fhl.test

import android.content.Context
import android.os.CountDownTimer
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.ImageView

import java.lang.ref.WeakReference
import kotlin.concurrent.timerTask

/**
 * @ProjectName: MyKotlinApplication
 * @Package: com.example.mykotlin
 * @ClassName: HandlerTest
 * @Description: java类作用描述
 * @Author: fenghl
 * @CreateDate: 2020/1/10 10:43
 * @UpdateUser: 更新者：
 * @UpdateDate: 2020/1/10 10:43
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
class HandlerTest(private val mContext: Context) {

    private val handler: MyHandler
    private val imageView: ImageView? = null

    private  class MyHandler internal constructor(context: HandlerTest) : Handler() {
        internal var mContext: WeakReference<HandlerTest>? = null

        init {
            mContext = WeakReference(context)

        }

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            val context = mContext!!.get()
            if (context == null) {
                Log.w("fhl", "window context 被回收!")
                return
            }
        }
    }



    init {
        handler = MyHandler(this)
        handler.sendEmptyMessage(1)
    }
}
