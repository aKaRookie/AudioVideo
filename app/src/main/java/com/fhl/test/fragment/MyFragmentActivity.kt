package com.fhl.test.fragment

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.util.Log
import com.fhl.test.R
import com.fhl.test.base.BaseActivity
import com.fhl.test.open.GLRender
import kotlinx.android.synthetic.main.activity_root.*
import org.opencv.android.OpenCVLoader

/**
 *
 * @ProjectName:    MyKotlinApplication
 * @Package:        com.example.mykotlin
 * @ClassName:      MyFragmentActivity
 * @Description:    java类作用描述
 * @Author:         fenghl
 * @CreateDate:     2020/4/30 16:44
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/4/30 16:44
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
class MyFragmentActivity : BaseActivity() {

    override fun getResLayoutId(): Int = R.layout.activity_root
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onPreInit() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        val success = OpenCVLoader.initDebug();
        if (success) {
            Log.i("open cv","OpenCV Lib is loaded...")
        }else{
            Log.w("open cv","OpenCV Lib is not loaded...")
        }
    }


    override fun init() {
        intFragment()
    }

    fun intFragment() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_1, AFragment.newInstance(), AFragment.TAG)
            .commit()
        supportFragmentManager.beginTransaction()
            .add(R.id.fl_2, BFragment.newInstance(), BFragment.TAG)
            .commit()
    }

}