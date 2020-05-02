package  com.fhl.test;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * @ProjectName: CarLauncherSeparated
 * @Package: com.pvt.launcher.module.home.ui.view
 * @ClassName: HorzTextProgressView
 * @Description: 水平进度条
 * @Author: fenghl
 * @CreateDate: 2019/10/8 19:56
 * @UpdateUser: 更新者：
 * @UpdateDate: 2019/10/8 19:56
 * @UpdateRemark: 更新说明：
 * @Version: 1.0
 */
public class HorzTextProgressView1 extends View {

    private double mMaxNum = Integer.MAX_VALUE; //最大值
    private double mCurProgress = 0; //当前的值
    private int mTextMarginStart = 0; //当前进度时间文字左边距
    private String mStartText = "0:00"; //当前进度时间文字
    private String mEndText = "5:00"; //总时间文字

    private int mTextSize;  //字体大小
    private float mTextMargin;  //字体与进度条间距

    private int mTextColor = 0; //字体颜色
    private int mInLineColor = 0; //内线颜色
    private int mOutLineColor = 0; //外线颜色
    private int mBgColor = Color.parseColor("#4c000000"); //背景颜色

    private int mInLineSize; //外线 大小 单位sp
    private int mOutLineSize; //内线 大小 单位sp
    private int mPointSize; //内线 大小 单位sp

    private int mWidth; //宽
    private int mHeight; //高
    private int mDefaultWidth = 300; //默认宽，单位sp
    private int mDefaultHeight = 20; //默认高，单位sp

    int boxWidth = 0; //文字框 宽 单位sp
    int mTextH = 0;
    float mEndW;//总文字宽度
    //画笔
    private Paint mTextPaint;
    private Paint mInPaint;
    private Paint mOutPaint;
    private Paint mBoxPaint;
    private Paint mBgPaint;
    private Paint mGradientPaint;
    //长方形进度点宽高
    private int mRectW;
    private int mRectH;
    int mDy = 0;
    float mRectR = 0.0f;
    float mBgRectH = 0;

    //进度点X位置
    float mBoxPointX = 0;
    //进度条的总宽度
    float mProgressWidth = 0;

    public HorzTextProgressView1(Context context) {
        this(context, null);
    }

    public HorzTextProgressView1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorzTextProgressView1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        //默认值
        int defaultTextSize = 10;  //默认字体大小 单位sp
        String defaultTextColor = "#FFFFFF"; //默认字体颜色
        String defaultInColor = "#EDEDED"; //默认内颜色
        String defaultOutColor = "#CCBD00"; //默认外颜色
        int defaultLineSize = 10; //默认线的大小 单位sp
        int defaultTextMargin = 10; //默认文字与进度条间距 单位px
        int defaultPointSize = 10; //默认文字与进度条间距 单位px
        int defaultRectW = 0; //默认文字与进度条间距 单位px
        int defaultRectH = 0; //默认文字与进度条间距 单位px
        int defaultDy = 0; //默认时间与进度条Y间距 单位px


        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.HorzTextProgressView);

        int curTime = typedArray.getInt(R.styleable.HorzTextProgressView_progress_horz_text_start, 0);
        mTextMarginStart = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_text_margin_start, 0);
        mStartText = calculateTime(curTime);
        int maxTime = typedArray.getInt(R.styleable.HorzTextProgressView_progress_horz_text_end, 0);
        mEndText = calculateTime(maxTime);
        mTextColor = typedArray.getColor(R.styleable.HorzTextProgressView_progress_horz_textColor, Color.parseColor(defaultTextColor));
        mTextMargin = typedArray.getDimension(R.styleable.HorzTextProgressView_progress_horz_text_margin, defaultTextMargin);
        mInLineColor = typedArray.getColor(R.styleable.HorzTextProgressView_progress_horz_inLineColor, Color.parseColor(defaultInColor));
        mOutLineColor = typedArray.getColor(R.styleable.HorzTextProgressView_progress_horz_outLineColor, Color.parseColor(defaultOutColor));

        mTextSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_textSize, sp2px(defaultTextSize));
        mInLineSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_inLineSize, sp2px(defaultLineSize));
        mOutLineSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_outLineSize, sp2px(defaultLineSize));
        mPointSize = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_pointSize, sp2px(defaultPointSize));
        mRectW = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_rect_w, sp2px(defaultRectW));
        mRectH = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_rect_h, sp2px(defaultRectH));

        mDy = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_dy, sp2px(defaultDy));
        mBgRectH = typedArray.getDimensionPixelSize(R.styleable.HorzTextProgressView_progress_horz_bg_rect_h, 0);


        typedArray.recycle();
        setGradientPaint();
        setTextPaint();
        setInPaint();
        setOutPaint();
        setBoxPaint();
        setBgPaint();
        if (mStartText == null) {
            mStartText = "0:00";
        }
        boxWidth = sp2px(boxWidth);
        mTextPaint.getTextBounds(mStartText, 0, mStartText.length(), boundStart); //获取文字区域大小
        mTextPaint.getTextBounds(mEndText, 0, mEndText.length(), boundEnd); //获取文字区域大小

        mEndW = mTextPaint.measureText(mEndText);
        Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        mTextH = (metrics.bottom - metrics.top);
    }

    /**
     * 背景画笔
     */
    private void setBgPaint() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(mBgColor);
        mBgPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_ATOP));
    }

    /**
     * 方框画笔
     */
    private void setBoxPaint() {
        mBoxPaint = new Paint();
        mBoxPaint.setAntiAlias(true);
        mBoxPaint.setColor(mOutLineColor);
    }

    /**
     * 文字画笔
     */
    private void setTextPaint() {
        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
    }

    /**
     * 内线画笔
     */
    private void setGradientPaint() {
        mGradientPaint = new Paint();
        mGradientPaint.setAntiAlias(true);

    }

    /**
     * 内线画笔
     */
    private void setInPaint() {
        mInPaint = new Paint();
        mInPaint.setAntiAlias(true);
        mInPaint.setColor(mInLineColor);
        mInPaint.setStrokeWidth(mInLineSize); //大小
        mInPaint.setStrokeCap(Paint.Cap.ROUND); // 结束位置圆角
    }

    /**
     * 外线画笔
     */
    private void setOutPaint() {
        mOutPaint = new Paint();
        mOutPaint.setAntiAlias(true);
        mOutPaint.setColor(mOutLineColor);
        mOutPaint.setStrokeWidth(mOutLineSize); //大小
        mOutPaint.setStrokeCap(Paint.Cap.ROUND); // 结束位置圆角
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //取默认值
        mWidth = sp2px(mDefaultWidth);
        mHeight = sp2px(mDefaultHeight);
        //1. 获取宽
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        //2.获取高
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.EXACTLY) { //具体值
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
        }
        int realWidth = mWidth;
        int realHeight = mHeight;
        if (isVisible) {
            //realWidth = (int) (mWidth + mTextMargin * 2 + mEndW * 2);
            // realHeight = mHeight + mTextH + +mDy+mRectH/2;
        }

        //2. 确定宽高
        setMeasuredDimension(realWidth, realHeight);
    }

    //开始时间文字宽高
    Rect boundStart = new Rect();
    //开始时间文字宽高
    Rect boundEnd = new Rect();

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //0.画背景
        float bgRaduis = getHeight() / 2.0f;
        float rectY = (getHeight() - mBgRectH) / 2.0f;
        drawBgRect(canvas, bgRaduis, rectY, getWidth() - bgRaduis, mBgRectH);
        drawBgCircle(canvas, bgRaduis);
        //1. 获取当前进度
        mProgressWidth = mWidth - mTextMarginStart - mEndW * 2 - mTextMargin * 3;
        float outWidth = (int) (mCurProgress / mMaxNum * mProgressWidth); //计算当前进度距离
        if (outWidth >= mProgressWidth) {
            outWidth = mProgressWidth;
        }
        //2. 画文字
        Paint.FontMetricsInt metrics = mTextPaint.getFontMetricsInt();
        int dy = (metrics.bottom - metrics.top) / 2 - metrics.bottom;


        //文字变化的时候 为了保证文字居中 所以需要知道文字区域大小


        // canvas.drawText(mStartText, outWidth + (boxWidth / 2 - bound.width() / 2), baseLine, mTextPaint);
        int leftMargin = 0;
        float outRight = 0;
        if (isVisible) {
            int baseLine = mHeight / 2 + dy; //基线
            canvas.drawText(mStartText, mTextMarginStart, baseLine, mTextPaint);
            canvas.drawText(mEndText, mWidth - boundEnd.width() - mTextMargin, baseLine, mTextPaint);
            leftMargin = (boundStart.width() + (int) mTextMargin);
            outRight = leftMargin + outWidth;
        } else {
            outRight = outWidth;
        }
        //3. 画进度条
        int inTop = (mHeight - mInLineSize) / 2;
        int outTop = (mHeight - mOutLineSize) / 2;
        //画内线
        //drawHLine(canvas, leftMargin+mTextMarginStart, inTop, leftMargin+mWidth, mHeight - inTop, mInPaint);
        //画外线
        //drawHLine(canvas, leftMargin+mTextMarginStart, outTop, outRight+ sp2px(2), mHeight - outTop, mOutPaint);

        //4. 画进度点
        //drawPoint(canvas, outWidth + leftMargin, outTop, mPointSize / 2);
        float y = (getHeight() - mRectH) / 2.0f;
        mBoxPointX = mTextMarginStart + outWidth + leftMargin;
        drawBox(canvas, y, mBoxPointX, mRectW, mRectH);
        drawGradient(canvas, rectY, leftMargin + mTextMarginStart, outWidth, (int) mBgRectH);

    }


    public void drawBgRect(Canvas canvas, float x, float y, float width, float height) {
        // 设置个新的长方形
        if (x >= getWidth() - mRectW) {
            x = getWidth() - mRectW;
        }
        RectF rectF = new RectF(x, y, width + x, height + y);
        //第二个参数是x半径，第三个参数是y半径
        canvas.drawRoundRect(rectF, mRectR, mRectR, mBgPaint);
    }

    public void drawBgCircle(Canvas canvas, float r) {

        canvas.drawCircle(r, r, r, mBgPaint);
    }

    /**
     * @param canvas
     * @param left   左边距离
     * @param width  矩形 宽
     * @param height 矩形 高
     */
    public void drawBox(Canvas canvas, float y, float left, float width, float height) {
        // 设置个新的长方形
        /*if (left>=getWidth()-mRectW){
            left=getWidth()-mRectW;
        }*/
        RectF rectF = new RectF(left, y, width + left, height + y);
        //第二个参数是x半径，第三个参数是y半径
        canvas.drawRoundRect(rectF, height / 2, height / 2, mBoxPaint);
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

        float a1, r1, g1, b1, a2, r2, g2, b2;
        a1 = Color.alpha(cl1);
        r1 = Color.red(cl1);
        g1 = Color.green(cl1);
        b1 = Color.blue(cl1);
        a2 = Color.alpha(cl2);
        r2 = Color.red(cl2);
        g2 = Color.green(cl2);
        b2 = Color.blue(cl2);
        a1 += ((a2 - a1) / num) * index;
        r1 += ((r2 - r1) / num) * index;
        g1 += ((g2 - g1) / num) * index;
        b1 += ((b2 - b1) / num) * index;

        return Color.argb((int) a1,(int) r1, (int) g1, (int) b1);
    }

    public void drawGradient(Canvas canvas, float y, float left, float width, float height) {
        mGradientPaint.setAlpha(173);
        int colorStart = Color.parseColor("#00000000");
        int color = getColorChanges(colorStart, Color.parseColor("#ff00CAFF"), (int) mCurProgress, (int) mMaxNum);
        LinearGradient backGradient =
                new LinearGradient(0, 0, width, 0, new int[]{colorStart, color}, new float[]{0, 1}, Shader.TileMode.CLAMP);
        mGradientPaint.setShader(backGradient);
        canvas.drawRect(left, y, width + left, height + y, mGradientPaint);
    }

    /**
     * 画进度点
     *
     * @param canvas
     * @param left   左边距
     * @param height 高度
     * @param radius 半径
     */
    public void drawPoint(Canvas canvas, int left, int height, int radius) {
        canvas.drawCircle(left, height / 2, radius, mBoxPaint);
    }

    /**
     * 水平进度条(前进方向平的) 通用
     *
     * @param canvas
     * @param left
     * @param top
     * @param right
     * @param bottom
     * @param paint
     */
    public void drawHLine(Canvas canvas, int left, int top, int right, int bottom, Paint paint) {
        int height = bottom - top; //高度
        int r = 0;
        if (isVisible) {
            r = height / 2; //半径
        }

        int cFirstX = left + r; //第一个分割点x坐标
        int cSecondX = right - left - r - (int) mTextMargin; //第二个分割点x坐标
        int cy = top + r; //圆心y坐标

        //1. 绘制第一个圆
      /*  canvas.save();
        canvas.clipRect(new RectF(left, top, right, bottom));
        canvas.drawCircle(left + r, cy, r, paint);
        canvas.restore();*/

        //2. 绘制中间矩形
        if (right >= cFirstX) {
            canvas.save();
            int currentRight = right;
            if (right > cSecondX) {
                //  currentRight = cSecondX;
            }
            //canvas.drawRect(new RectF(left + r, top, currentRight, bottom), paint);
            canvas.drawRect(new RectF(left + r, top, currentRight, bottom), paint);
            canvas.restore();
        }

        //3. 绘制最后的圆
        /*if (right >= cSecondX) {
            canvas.save();
            canvas.clipRect(new RectF(cSecondX, top, right+r, bottom));
            canvas.drawCircle(cSecondX, cy, r, paint);
            canvas.restore();
        }*/
    }

    private int sp2px(int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp,
                getResources().getDisplayMetrics());
    }

    private static String calculateTime(int duration) {
        if (duration < 0) {
            return "00:00";
        }
        String time = "";
        long minute = duration / 60000;
        long seconds = duration % 60000;
        long second = (long) Math.floor((double) seconds / 1000);
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (second < 10) {
            time += "0";
        }
        time += second;
        return time;
    }

    public void setCurrentProgress(double curProgress) {
        this.mCurProgress = curProgress;
        if (mCurProgress > mMaxNum) {
            mCurProgress = mMaxNum;
        }
        mStartText = calculateTime((int) curProgress);
        invalidate();
    }

    public void setMaxProgress(double maxProgress) {
        this.mMaxNum = maxProgress;
        mEndText = calculateTime((int) maxProgress);
        invalidate();
    }

    public double getCurrentProgress(double currentNum) {
        return mCurProgress;
    }

    public void setMaxNum(double max) {
        this.mMaxNum = max;
        invalidate();
    }

    public double getMaxNum() {
        return this.mMaxNum;

    }

    boolean isVisible = true;

    public void setTextVisible(int visible) {
        isVisible = visible == View.VISIBLE;
        invalidate();
    }

    float fistX, fistY;
    //触摸有效区域
    float[] touchArea = {-10, 10};

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                fistX = event.getX();
                fistY = event.getY();
                if (mBoxPointX >= fistX + touchArea[0] && mBoxPointX <= touchArea[1] + fistX) {
                    Log.d("@fhl", "touch有效!");
                    if (mProgressChangeListener!=null) {
                        mProgressChangeListener.onStartTracking();
                    }
                    return true;
                }
                return false;
            case MotionEvent.ACTION_MOVE:
                float lastX = event.getX(), lastY = event.getY();

                //Log.d("@fhl", String.format("滑动进度条: first x=%f, y=%f, last x=%f,y=%f, scroll: x=%f", fistX, fistY, lastX, lastY, lastX - fistX));

                break;
            case MotionEvent.ACTION_UP:
               double dxWidth =  (event.getX()-fistX)/ mProgressWidth*mMaxNum;
                mCurProgress =mCurProgress+(dxWidth);
                Log.d("@fhl", String.format("进度条更新:%f",mCurProgress));
                if (mCurProgress>=mMaxNum) {
                    mCurProgress = mMaxNum;
                }else if (mCurProgress<0){
                    mCurProgress = 0;
                }
                if (mProgressChangeListener != null) {
                    mProgressChangeListener.onStopTracking(mCurProgress);
                }
                //invalidate();
                //setCurrentProgress(mCurProgress);
                break;
            default:
        }
        return true;
    }
    public void setOnProgressChangedListener( OnProgressChangeListener l){
        mProgressChangeListener = l;
    }
    private  OnProgressChangeListener mProgressChangeListener;
    interface OnProgressChangeListener{
        /**
         * 开始跟踪进度
         */
        void onStartTracking();

        /**
         * 停止跟踪进度
         * @param progress 当前进度
         */
        void onStopTracking(double progress);
    }
}
