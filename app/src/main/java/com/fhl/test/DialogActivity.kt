package com.fhl.test

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import com.jakewharton.disklrucache.DiskLruCache
import java.util.concurrent.locks.Condition

/**
 *
 * @ProjectName:    MyKotlinApplication
 * @Package:        com.example.mykotlin
 * @ClassName:      DialogActivity
 * @Description:    java类作用描述
 * @Author:         fenghl
 * @CreateDate:     2019/12/26 16:03
 * @UpdateUser:     更新者：
 * @UpdateDate:     2019/12/26 16:03
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
open class DialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()

    }

    private var mDialogView: View? = null
    fun getDialogLayout(): View? {
        if (mDialogView == null) {
            mDialogView = LayoutInflater.from(this).inflate(getDialogRes(), null)
        }
        return mDialogView
    }

    open fun getDialogRes(): Int {

        return 0
    }

    var dialog: AlertDialog? = null
    private fun initDialog() {
        if (dialog == null) {
            val layout = getDialogLayout()
            layout?.let {
                dialog =
                    AlertDialog.Builder(this)
                        .setView(it)
                        .setCancelable(false)
                        .create()
            }


        }
    }

    fun showDialog() {
        dialog?.show()
    }

    fun dismissDialog() {
        dialog?.run {
            if (isShowing) {
                dismiss()
            }

        }
    }

    fun destoryDialog() {
        dismissDialog()
        dialog = null
    }

}