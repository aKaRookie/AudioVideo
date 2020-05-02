package  com.fhl.test

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.media.audiofx.Visualizer
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import java.nio.ByteBuffer
import java.nio.FloatBuffer
import java.util.*
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.LinkedBlockingQueue

/**
 * 录音进度视图
 * @ProjectName:    录音进度视图
 * @Package:        com.example.mykotlin
 * @ClassName:      RecordWaveView
 * @Description: 音阶视图控件, 需要申请录音权限<uses-permission android:name="android.permission.RECORD_AUDIO"></uses-permission>
 * @Author:         fenghualong
 * @CreateDate:     2020/1/7 16:27
 * @UpdateUser:     更新者：
 * @UpdateDate:     2020/1/7 16:27
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 */
class RecordWaveView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private val TAG = javaClass.simpleName
    private val mWavePaint by lazy { Paint() }


    private var random = Random(100)
    private var mHeight: Float = 0f
    private var mWidth: Float = 0f
    @Volatile
    private var isRunning = true
    private var maxHeight = 0f
    private var minHeight = 0f
    private var space = 0f
    private var mLineWidth = 0f
    private var mLineColor = 0
    private var mLineStartColor = 0
    private var mLineEndColor = 0
    private val MAX_VALUE: Int = 7

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RecordWaveView)
        maxHeight = typedArray.getDimension(R.styleable.RecordWaveView_maxHeight, 0f)
        minHeight = typedArray.getDimension(R.styleable.RecordWaveView_minHeight, 0f)
        space = typedArray.getDimension(R.styleable.RecordWaveView_space, 0f)
        mLineWidth = typedArray.getDimension(R.styleable.RecordWaveView_lineWidth, 0f)
        mLineColor = typedArray.getColor(R.styleable.RecordWaveView_lineColor, Color.WHITE)
        mLineStartColor =
            typedArray.getColor(R.styleable.RecordWaveView_lineColor_start, Color.WHITE)
        mLineEndColor = typedArray.getColor(R.styleable.RecordWaveView_lineColor_end, Color.WHITE)

        typedArray.recycle()
        initPaints()
    }

    private fun initPaints() {
        mWavePaint.isAntiAlias = true
        mWavePaint.strokeWidth = mLineWidth
        mWavePaint.strokeCap = Paint.Cap.SQUARE
    }

    /**
     * 设置wave line的颜色,渐变色方向270度↓
     * @param h line高度
     */
    private fun setLineColor(h: Int) {
        if (mLineStartColor != Color.WHITE || mLineEndColor != Color.WHITE) {
            val y0 = (h - maxHeight) / 2
            val y1 = (h + maxHeight) / 2
            val linearGradient = LinearGradient(
                0f, y0, 0f, y1,
                mLineStartColor,
                mLineEndColor,
                Shader.TileMode.CLAMP
            )
            mWavePaint.shader = linearGradient
        } else {
            mWavePaint.color = mLineColor
        }
    }


    private fun random(min: Float, max: Float): Float {
        //取值0.5~1.0
        return random.nextFloat() * (max - min) + min
    }


    @Synchronized
    private fun drawClearRects(canvas: Canvas) {
        canvas.drawRect(RectF(0f, 0f, 0f, 0f), mWavePaint)

    }

    fun play() {
        isRunning = true
        invalidate()
    }

    fun pause() {
        isRunning = false
    }

    fun stop() {
        isRunning = false
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mWidth = w.toFloat()
        mHeight = h.toFloat()
        setLineColor(h)
    }

    private val queue by lazy { LinkedBlockingQueue<Float>() }

    @Synchronized
    fun addLine(value: Int) {
        if ((queue.size + 1) * (space + mLineWidth) > mWidth) {
            //如果下一次添加line的时候大于view宽度,return
            queue.poll()
           // return
        }
        val ratio = value / MAX_VALUE.toFloat()

        var lHeight = maxHeight * ratio

        if (lHeight < minHeight) {
            lHeight = minHeight
        }

        Log.d(TAG, "wave height=$lHeight, value=$value")
        queue.offer(lHeight)
        postInvalidate()
    }


    @Synchronized
    private fun drawWaveLine(canvas: Canvas) {

        val lineArray = queue.toFloatArray()
        var startX = mWidth
        val size = lineArray.size
        for ((i, height) in lineArray.withIndex()) {
            val j = (size - i)
            startX = mWidth - j * (mLineWidth + space)
            /*startX = if (startX < 0) {
                mWidth + mLineWidth
            } else {
                mWidth - j * (mLineWidth + space)
            }*/

            canvas.drawLine(
                startX,
                (mHeight - height) / 2,
                startX,
                (height + mHeight) / 2,
                mWavePaint
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (isRunning) {
            drawWaveLine(canvas)
        } else {
            drawClearRects(canvas)
        }
    }

    /**
     * 创建矩形
     *
     * @param left   左边的坐标点
     * @param right  右边的左边点
     * @param height 高度
     * @return 矩形
     */
    private fun createRectF(left: Float, right: Float, height: Float): RectF {
        return RectF(left, (getHeight() - height) / 2, right, (getHeight() + height) / 2.0f)
    }

    override fun onDetachedFromWindow() {
        Log.i("Visualizer", "=====音律控件释放onDetachedFromWindow=====")
        super.onDetachedFromWindow()
    }
}
