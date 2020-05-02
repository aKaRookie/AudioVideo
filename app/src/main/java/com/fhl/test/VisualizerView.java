package  com.fhl.test;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.media.audiofx.Visualizer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.Random;


/**
 * 音阶视图控件,需要申请录音权限
 * <uses-permission android:name="android.permission.RECORD_AUDIO"/>
 *
 * @author donghongyu
 * @update fenghl
 */
public class VisualizerView extends View {
    private static final int DEFAULT_NUM_COLUMNS = 50;
    private byte[] mBytes;
    private Paint mForePaint = new Paint();
    private int mNumColumns = 55;
    private int mBaseY;
    private float mColumnWidth;
    private float mSpaceMax = 7;
    private float mSpaceMin = 2;
    private int mRenderColor;

    private Visualizer mVisualizer;

    public VisualizerView(Context context) {
        this(context, null);
    }

    public VisualizerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray args = context.obtainStyledAttributes(attrs, R.styleable.VisualizerView);
        mNumColumns = args.getInteger(R.styleable.VisualizerView_numColumns, DEFAULT_NUM_COLUMNS);
        mSpaceMax = args.getInteger(R.styleable.VisualizerView_spaceMax, DEFAULT_NUM_COLUMNS);
        mSpaceMin = args.getInteger(R.styleable.VisualizerView_spaceMax, DEFAULT_NUM_COLUMNS);
        mRenderColor = args.getColor(R.styleable.VisualizerView_renderColor, Color.WHITE);
        args.recycle();
        mBytes = null;
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(mRenderColor);
        int start = Color.parseColor("#1DD4B4");
        int end = Color.parseColor("#0C99E1");

        initPaints(mNumColumns, start, end);
        initVisualizer();
    }

    /**
     * 颜色渐变
     *
     * @param cl1   颜色1
     * @param cl2   颜色2
     * @param index 索引
     * @param num   总个数
     * @return 计算渐变后的颜色
     */
    public int getColorChanges(int cl1, int cl2, float index, int num) {
        if (num <= 0) {
            num = 1;
        }
        if (index < 0) {
            index = 0;
        } else if (index > num) {
            index = num;
        }

        float r1, g1, b1, r2, g2, b2;
        r1 = Color.red(cl1);
        g1 = Color.green(cl1);
        b1 = Color.blue(cl1);
        r2 = Color.red(cl2);
        g2 = Color.green(cl2);
        b2 = Color.blue(cl2);
        r1 += ((r2 - r1) / num) * index;
        g1 += ((g2 - g1) / num) * index;
        b1 += ((b2 - b1) / num) * index;
        return Color.rgb((int) r1, (int) g1, (int) b1);
    }

    private Paint[] mPaints = null;

    private void initPaints(int n, int startColor, int endColor) {
        int c;
        mPaints = new Paint[n];
        for (int i = 0; i < mNumColumns; i++) {
            mPaints[i] = new Paint();
            mPaints[i].setAntiAlias(true);
            c = getColorChanges(startColor, endColor, i, mNumColumns);
            mPaints[i].setColor(c);
        }
    }

    public void initVisualizer() {
        Log.i("Visualizer", "=====音律控件初始化initVisualizer=====");
        // Create the Visualizer object and attach it to our media player.
        try {
            // 这里录制的是系统的声音
            mVisualizer = new Visualizer(0);
            boolean flag = mVisualizer.getEnabled();
            if (!flag) {
                mVisualizer.setCaptureSize(256);
            }
            mVisualizer.setEnabled(false);
            // Pass through Visualizer data to VisualizerView
            Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
                                                  int samplingRate) {
                    //Log.i("Visualizer","===onWaveFormDataCapture===" + Arrays.toString(bytes));
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                                             int samplingRate) {
                    updateVisualizer(bytes);
                    //Log.i("Visualizer","===onFftDataCapture===" + Arrays.toString(bytes));
                }
            };

            /*mVisualizer.setDataCaptureListener(
                    captureListener,
                    Visualizer.getMaxCaptureRate(),
                    true,
                    true);*/

            // Enabled Visualizer and disable when we're done with the stream
            mVisualizer.setEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        Log.i("Visualizer", "=====音律控件释放onDetachedFromWindow=====");
        release();
        super.onDetachedFromWindow();
    }

    /**
     * Call to release the resources used by VisualizerView. Like with the
     * MediaPlayer it is good practice to call this method
     */
    public void release() {
        if (mVisualizer != null) {
            Log.i("Visualizer", "=====音律控件释放release=====");
            mVisualizer.setEnabled(false);
            mVisualizer.release();
            mVisualizer = null;
        }
    }

    public void updateVisualizer(byte[] fft) {
        byte[] model = new byte[fft.length / 2 + 1];

        model[0] = (byte) Math.abs(fft[0]);
        for (int i = 2, j = 1; j < mNumColumns; ) {
            byte rfk = fft[i];
            byte ifk = fft[i + 1];
            float magnitude = (rfk * rfk + ifk * ifk);
            int dbValue = (int) (20 * Math.log10(magnitude));

            model[j] = (byte) dbValue;
            i += 2;
            j++;
        }
        mBytes = model;

        invalidate();

    }

    Random random = new Random(100);

    private float random(float min,float max) {
        //取值0.5~1.0
        return (random.nextFloat() * (max-min)+min);
    }

    private synchronized void drawRects(Canvas canvas) {
        if (mNumColumns > getWidth()) {
            mNumColumns = DEFAULT_NUM_COLUMNS;
        }
        mColumnWidth = (float) getWidth() / (float) mNumColumns -mSpaceMax;
        //mSpaceMax = mColumnWidth / 4;

        mBaseY = getHeight();
        //每一条的高度
        float dh = 30.0f;
        for (int i = 0; i < mNumColumns; i++) {
            float x = random(0.3f,0.9f);
            float height = dh * x;

            // 柱状图的左右坐标
            float left = i* (mColumnWidth+mSpaceMax);
            float right =left + mColumnWidth ;

            RectF rect = createRectF(left, right, height);
//            canvas.drawRect(rect, mPaints[i]);
            canvas.drawRoundRect(rect, 0, 0, mPaints[i]);
        }
    }

    private synchronized void drawClearRects(Canvas canvas) {
        for (int i = 0; i < mNumColumns; i++) {
            canvas.drawRect(new RectF(0, 0, 0, 0), mPaints[i]);
        }
    }

    private void drawRectsByVolum(Canvas canvas) {

        //mBytes就是采集来的数据 这里是个大小为1024的数组，里面的数据都是byts类型，所以大小为-127到128
        if (mBytes == null) {
            return;
        }

        if (mNumColumns > getWidth()) {
            mNumColumns = DEFAULT_NUM_COLUMNS;
        }
        mColumnWidth = (float) getWidth() / (float) mNumColumns;
        mSpaceMax = mColumnWidth / 4;

        mBaseY = getHeight();
        RectF rect;
        for (int i = 0; i < mNumColumns; i++) {
            float height = mBytes[i];

            // 柱状图的左右坐标
            float left = i * mColumnWidth + mSpaceMax;
            float right = (i + 1) * mColumnWidth - mSpaceMax;

            rect = createRectF(left, right, height);
            canvas.drawRect(rect, mForePaint);
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void play() {
        isRunning = true;
        invalidate();
    }

    private volatile boolean isRunning = false;
    public void pause() {
        isRunning = false;
    }
    public void stop() {
        isRunning = false;
        invalidate();
    }

    void clearDraw(Canvas canvas) {
        /*对画布进行清屏*/
        canvas.save();

        Paint paint = new Paint();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawPaint(paint);
        //canvas.drawColor(Color.BLUE);

        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isRunning) {
            drawRects(canvas);
            postInvalidateDelayed(200);
        } else {
            drawClearRects(canvas);
            //  clearDraw(canvas);
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
    private RectF createRectF(float left, float right, float height) {
        return new RectF(left, (mBaseY - height) / 2, right, (mBaseY + height) / 2.0f);
    }
}
