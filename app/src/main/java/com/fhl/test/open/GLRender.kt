package com.fhl.test.open

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.util.Log
import com.fhl.test.common.BYTE_FOR_FLOAT
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.properties.Delegates

/**
 *
 * @ProjectName:    TestApplication
 * @Package:        com.fhl.test.fragment
 * @ClassName:      GLRender
 * @Description:    java类作用描述
 * @Author:         fenghl
 * @CreateDate:     2020/5/1 18:03
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/5/1 18:03
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
class GLRender constructor(context: Context) : GLSurfaceView.Renderer {
    private val TAG: String = "GLRender"
    private val VERTEX_SHADER_FILE = "VShader.glsl"
    private val FRAGMENT_SHADER_FILE = "PShader.glsl"
    private val A_POSITION = "vPosition"
    private val A_COLOR = "vColor"
    private val mContext = context
    private val TRIANGEL_COORDS =
        floatArrayOf(
            0.5f, 1.0f, 0f,
            -0.5f, -0.5f, 0f,
            0.5f, -0.5f, 0f
        )
    private val TRIANGLE_COLOR = floatArrayOf(0f, 1.0f, 1.0f, 1.0f)

    //在数组中，一个顶点需要3个来描述其位置，需要3个偏移量
    private val COORDS_PER_VERTEX = 3
    private val COORDS_PER_COLOR = 0
    private val TOTAL_COMPONENT_COUNT = COORDS_PER_COLOR + COORDS_PER_VERTEX
    private val VERTEX_COUNT = TRIANGEL_COORDS.size / TOTAL_COMPONENT_COUNT
    //一个点需要的byte偏移量。
    private val STRIDE = TOTAL_COMPONENT_COUNT * BYTE_FOR_FLOAT

    private var mProgramId by Delegates.notNull<Int>()
    private lateinit var vBuffer: FloatBuffer

    init {
        /*
        1. 使用nio中的ByteBuffer来创建内存区域。
        2. ByteOrder.nativeOrder()来保证，同一个平台使用相同的顺序
        3. 然后可以通过put方法，将内存复制过去。
        因为这里是Float，所以就使用floatBuffer
         */
        vBuffer = ByteBuffer.allocateDirect(TRIANGEL_COORDS.size * BYTE_FOR_FLOAT)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(TRIANGEL_COORDS)
        //因为是从第一个点开始，就表示三角形的，所以将position移动到0
         vBuffer.position(0)
    }

    override fun onDrawFrame(gl: GL10?) {
        Log.d(TAG, "onDrawFrame")
        //0.glClear（）的唯一参数表示需要被清除的缓冲区。当前可写的颜色缓冲
         GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        //0.先使用这个program?这一步应该可以放到onCreate中进行
        GLES20.glUseProgram(mProgramId)
        //1.根据我们定义的取出定义的位置
        val vPosition = GLES20.glGetAttribLocation(mProgramId, A_POSITION)
        //2.开始启用我们的position
        GLES20.glEnableVertexAttribArray(vPosition)
        //3.将坐标数据放入
        GLES20.glVertexAttribPointer(
            vPosition,//上面得到的id
            COORDS_PER_VERTEX,//告诉他用几个偏移量来描述一个顶点
            GLES20.GL_FLOAT,
            false,
            STRIDE,//一个顶点需要多少个字节的偏移量
            vBuffer
        )
        val aColor = GLES20.glGetUniformLocation(mProgramId, A_COLOR)
        //开始绘制
        //绘制三角形的颜色
        GLES20.glUniform4fv(aColor, 1, TRIANGLE_COLOR, 0)
        //绘制三角形.
        //draw arrays的几种方式 GL_TRIANGLES三角形
        //GL_TRIANGLE_STRIP三角形带的方式(开始的3个点描述一个三角形，后面每多一个点，多一个三角形)
        //GL_TRIANGLE_FAN扇形(可以描述圆形)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, VERTEX_COUNT)
        //禁止顶点数组的句柄
        GLES20.glDisableVertexAttribArray(vPosition)

    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        Log.d(TAG, "onSurfaceChanged")
        //在窗口改变的时候调用
        GLES20.glViewport(0, 0, width, height)
        /*val ratio: Float = if (width > height) {
            width.toFloat() / height.toFloat()
        } else {
            height.toFloat() / width.toFloat()
        }
        if (width > height) {
            //横屏。需要设置的就是左右。
            Matrix.orthoM(TRIANGLE_COLOR, 0, -ratio, ratio, -1f, 1f, -1f, 1f)
        } else {
            //竖屏。需要设置的就是上下
            Matrix.orthoM(TRIANGLE_COLOR, 0, -1f, 1f, -ratio, ratio, -1f, 1f)
        }*/
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        Log.d(TAG, "onSurfaceCreated")
        //0.简单的给窗口填充一种颜色
        GLES20.glClearColor(1f,1f,0f, 1f)
        /*vBuffer = ByteBuffer.allocateDirect(TRIANGEL_COORDS.size * BYTE_FOR_FLOAT)
            .order(ByteOrder.nativeOrder()).asFloatBuffer().put(TRIANGEL_COORDS)
        vBuffer.position(0)*/
        //在创建的时候，去创建这些着色器
        //先从Asset中得到着色器的代码
        val vShaderCode = readGLSL(VERTEX_SHADER_FILE, mContext.resources)
        val pShaderCode = readGLSL(FRAGMENT_SHADER_FILE, mContext.resources)
        //3.继续套路。取得到program
        var vShaderId: Int = 0
        var pShaderId: Int = 0
        vShaderCode?.let {
            vShaderId = compileShaderCode(GLES20.GL_VERTEX_SHADER, it)
        }
        pShaderCode?.let {
            pShaderId = compileShaderCode(GLES20.GL_FRAGMENT_SHADER, it)
        }
        mProgramId = GLES20.glCreateProgram()
        GLES20.glAttachShader(mProgramId, vShaderId)
        GLES20.glAttachShader(mProgramId, pShaderId)
        //4.最后，启动GL link program
        GLES20.glLinkProgram(mProgramId)
    }
}