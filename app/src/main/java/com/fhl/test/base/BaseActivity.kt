package com.fhl.test.base

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.app.AppCompatActivity

/**
 *
 * @ProjectName:    MyKotlinApplication
 * @Package:        com.example.mykotlin.base
 * @ClassName:      BaseActivity
 * @Description:    java类作用描述
 * @Author:         fenghl
 * @CreateDate:     2020/4/30 16:48
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/4/30 16:48
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onPreInit()
        setContentView(getResLayoutId())
        init()
    }

    open fun onPreInit() {}
    abstract fun getResLayoutId(): Int
    abstract fun init()
}