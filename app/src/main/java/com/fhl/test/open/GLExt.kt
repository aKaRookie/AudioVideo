package com.fhl.test.open

import android.app.ActivityManager
import android.content.Context
import android.content.res.Resources
import android.opengl.GLES20
import android.os.Build
import android.util.Log
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.regex.Matcher


/**
 *
 * @ProjectName:    TestApplication
 * @Package:        com.fhl.test.fragment
 * @ClassName:      GLExt
 * @Description:    java类作用描述
 * @Author:         fenghl
 * @CreateDate:     2020/5/1 18:18
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/5/1 18:18
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */

/**
 * 对ShaderCode进行编译
 *
 * @param type       shader的type
 * @param shaderCode 进行编译的Shader代码
 * @return shaderObjectId
 */
const val TAG = "GLExt"

fun compileShaderCode(type: Int, shaderCode: String): Int {
    //得到一个着色器的ID。主要是对ID进行操作
    val shaderObjId = GLES20.glCreateShader(type)
    //如果着色器的id不为0，则表示是可以用的
    if (shaderObjId != 0) {
        GLES20.glShaderSource(shaderObjId, shaderCode)
        //1.编译代码.根据刚刚和代码绑定的ShaderObjectId进行编译
        GLES20.glCompileShader(shaderObjId)
        //2.查询编译的状态
        val status = intArrayOf(0)
        //调用getShaderIv ，传入GL_COMPILE_STATUS进行查询
        GLES20.glGetShaderiv(shaderObjId, GLES20.GL_COMPILE_STATUS, status, 0)
        //等于0。则表示失败
        if (status[0] == 0) {
            //失败的话，需要释放资源，就是删除这个引用
            GLES20.glDeleteShader(shaderObjId)
            Log.w(TAG, "compile failed!")
            return 0
        }
    }
    return shaderObjId
}

fun readGLSL(fileName: String, res: Resources): String? {
    val builder = StringBuilder()
    var input: InputStream? = null
    var br: BufferedReader? = null
    try {
        input = res.assets.open(fileName)
        br = input.bufferedReader()

        var size: Int = 0
        val buffer = ByteArray(1024)
        var line: String?
        while (br.readLine().also { line = it } != null) {
            if (!line.isNullOrEmpty()) {
                if (!line!!.startsWith("#") && !line!!.contains("/")&&!line!!.contains("*")) {
                    builder.append(line).append("\n")
                }
            }
        }

        /*  while (input.read(buffer).also { size = it } != -1) {
              builder.append(String(buffer, 0, size))
          }*/

    } catch (e: Exception) {
        Log.d(TAG, Log.getStackTraceString(e))
        return null
    } finally {
        input?.close()
        br?.close()
    }
    return builder.toString().replace("\\r\\n".toRegex(), "\n")
        .also { Log.d(TAG, "readGLSL string= $it") }


}

const val GLES_VERSION_2 = 2
/**
 * 判断是否支持es2.0
 *
 * @param context
 * @return
 */
fun isSupportEs2(context: Context): Boolean { //检查是否支持2.0
    val activityManager =
        context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    return if (activityManager != null) {
        val deviceConfigurationInfo =
            activityManager.deviceConfigurationInfo
        val reqGlEsVersion = deviceConfigurationInfo.reqGlEsVersion
        reqGlEsVersion >= GLES_VERSION_2 || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1
                && (Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK build for x86")))
    } else {
        false
    }
}
