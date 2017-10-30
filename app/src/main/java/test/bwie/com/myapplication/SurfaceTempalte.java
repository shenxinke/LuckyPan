package test.bwie.com.myapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by xinke on 2017/10/28 0028.
 */

public class SurfaceTempalte extends SurfaceView implements SurfaceHolder.Callback,Runnable {
    private SurfaceHolder mholder;
    private Canvas mCanvas;
    private Thread t;
    /*
    * 线程的控制开关
    * */
    private  Boolean isRunning;

    public SurfaceTempalte(Context context) {
        this(context,null);
    }
    public SurfaceTempalte(Context context, AttributeSet attrs) {
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
    public void surfaceCreated(SurfaceHolder surfaceHolder) {

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
            draw();
        }
    }
    private void draw() {
        try {
            mCanvas = mholder.lockCanvas();
            if(mCanvas!=null){
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
}
