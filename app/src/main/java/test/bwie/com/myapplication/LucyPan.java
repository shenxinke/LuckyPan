package test.bwie.com.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import static test.bwie.com.myapplication.R.mipmap.f;

public class LucyPan extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mholder;
    private Canvas mCanvas;
    private Thread t;
    //线程的控制开关
    private  Boolean isRunning;
    //盘块的奖项
    private String[] mStrs = new String[]{"单反相机","ipad","恭喜发财","iphone","服装一套","恭喜发财"};
    //盘块的图片
    private int[] mImgs = new int[]{R.mipmap.b,R.mipmap.c, f,R.mipmap.d,R.mipmap.e, f};
    //板块的颜色
    private int[] mColors = new int[]
            {0xFFFc300,0XFFF17E01,0xFFFc300,0XFFF17E01,0xFFFc300,0XFFF17E01};
    //与图片对应的bitmap数组
    private Bitmap[] mImgsBitmap;
    private Bitmap mBgBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.bg2);
    //盘快的数量
    private int mItemCount = 6;
    //绘制盘块的画笔
    private Paint mArcPaint;
    //绘制文本的画笔
    private Paint mTextPaint;
    private float mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,20,getResources().getDisplayMetrics());
    //整个盘块的范围
    private RectF mRange = new RectF();
    //整个盘块的直径
    private int mRadius;
    //转盘的中心位置
    private int mCenter;
    //这里我们的pandding直接以paddingLeft为准
    private int mPadding;
    //滚动的速度
    private double mSpeed=0;
    private  volatile  float mStarAngle = 0;
    //判断是否点击停止按钮
    private  boolean isShouldEnd;

    public LucyPan(Context context) {
        this(context,null);
    }
    public LucyPan(Context context, AttributeSet attrs) {
        super(context, attrs);
        mholder = getHolder();
        mholder.addCallback(this);
        //设置焦点
        setFocusable(true);
        setFocusableInTouchMode(true);
        //设置常量
        setKeepScreenOn(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = Math.min(getMeasuredWidth(),getMeasuredHeight());
        mPadding = getPaddingLeft();
        //直径
        mRadius = width - mPadding*2;
        //中心点
        mCenter = width/2;
        setMeasuredDimension(width,width);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        //初始化绘制盘块的画笔
        mArcPaint = new Paint();
        mArcPaint.setAntiAlias(true);
        mArcPaint.setDither(true);
        //初始化绘制盘块的画笔
        mTextPaint = new Paint();
        mTextPaint.setColor(0xffffffff);
        mTextPaint.setTextSize(mTextSize);
        //初始化盘块绘制的范围
        mRange = new RectF(mPadding,mPadding,mPadding+mRadius,mItemCount+mRadius);
        //初始化图片
        mImgsBitmap  = new Bitmap[mItemCount];
        for (int i = 0; i <mItemCount; i++) {
            mImgsBitmap[i] = BitmapFactory.decodeResource(getResources(),mImgs[i]);
        }
        isRunning = true;
        t = new Thread(this);
        t.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        isRunning = false;

    }

    @Override
    public void run() {
        //不断进行绘制
        while (isRunning){
            long start = System.currentTimeMillis();
            draw();
            long end = System.currentTimeMillis ();
            if(end-start<50){
                try {
                    Thread.sleep(50-(end-start));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void draw() {
        try {
            mCanvas = mholder.lockCanvas();
            if(mCanvas!=null){
                //绘制背景
                drawBg();
                //绘制盘块
                float tmpAngle = mStarAngle;
                float sweepAngle = 360/mItemCount;
                for (int i = 0; i <mItemCount; i++) {
                    mArcPaint.setColor(mColors[i]);
                    //绘制盘块
                    mCanvas.drawArc(mRange,tmpAngle,sweepAngle,true,mArcPaint);
                    //绘制文本
                    drawText(tmpAngle,sweepAngle,mStrs[i]);
                    //绘制Icon
                    drawIcon(tmpAngle,mImgsBitmap[i]);
                    tmpAngle+=sweepAngle;
                }
                mStarAngle +=mSpeed;
                //点击停止按钮
                if(isShouldEnd){
                    mSpeed-=1;
                }
                if (mSpeed<=0){
                    mSpeed=0;
                    isShouldEnd = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if(mCanvas!=null){
                mholder.unlockCanvasAndPost(mCanvas);
            }
        }
    }
    //点击旋转按钮
    public void luckyStart(int index){
        //计算每一项的角度
        float angle = 360/mItemCount;
        //计算每一项的中奖范围（当前index）
        //0:210~270
        //1:->150~210

        float from = 300-(index+1)*angle;
        float end = from+angle;
        //停下来需要旋转的距离
        float targetFrom = 4*360+from;
        float targetEnd  = 4*360+end;
        //设置停下来的速度
        float v1 = (float) (-1+Math.sqrt(1+8*targetFrom)/2);
        float v2 = (float) (-1+Math.sqrt(1+8*targetEnd)/2);
        mSpeed = v1+Math.random()*(v2-v1);
        isShouldEnd = false;
    }
    //点击停止按钮
    public void luckyEnd(){
        mStarAngle=0;
        isShouldEnd = true;
    }
    //转盘是否在旋转
    public boolean isStart() {
        return mSpeed!=0;
    }

    public boolean isShouldEnd(){
        mStarAngle=0;
        return isShouldEnd;
    }


    private void drawIcon(float tmpAngle, Bitmap bitmap) {
        //设置图片的宽度为直径8/1
        int imgWidth = mRadius/8;
        //Math.PI/180
        float angle = (float) ((tmpAngle+360/mItemCount/2)*Math.PI/180);

        int x = (int) (mCenter+mRadius/2/2*Math.cos(angle));
        int y = (int) (mCenter+mRadius/2/2*Math.sin(angle));
        //确定这个图片的位置
        Rect rect = new Rect(x-imgWidth/2,y-imgWidth/2,x+imgWidth/2,y+imgWidth/2);
        mCanvas.drawBitmap(bitmap,null,rect,null);
    }

    private void drawText(float tmpAngle, float sweepAngle, String mStr) {
        Path path = new Path();
        path.addArc(mRange,tmpAngle,sweepAngle);
        float textWidth = mTextPaint.measureText(mStr);
        int hOffset = (int) (mRadius*Math.PI/mItemCount/2-textWidth/2);
        int vOffset = mRadius/2/6;
        mCanvas.drawTextOnPath(mStr,path,hOffset,vOffset,mTextPaint);
    }
    //绘制背景
    private void drawBg() {
        mCanvas.drawColor(0xffffffff);
        mCanvas.drawBitmap(mBgBitmap,null,new Rect(mPadding/2,mPadding/5,getMeasuredWidth()-mPadding/2,getMeasuredHeight()-mPadding),null);
    }
}
