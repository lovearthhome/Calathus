package com.lovearthstudio.calathus.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.lovearthstudio.calathus.R;


public class ScratchCardView extends TextView {

    private int widget, height;

    private Context mContext;
    private Paint mPaint;
    private Canvas tempCanvas;
    private Bitmap mBitmap;
    private float x, y, ox, oy;
    private Path mPath;
    Handler mHandler;
    MyThread mThread;

    int messageCount;

    int[] pixels;

    int color = 0xFFD6D6D6;

    public ScratchCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);
    }

    /**
     * 再一次抽奖
     */
    public void againScratch(String str) {
        messageCount = 0;
        tempCanvas.drawColor(color);
        setText(str);
    }

    private void init(AttributeSet attrs) {
        // 获取控件大小值
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.lotter);
        widget = (int) a.getDimension(R.styleable.lotter_lotter_widget, 300);
        height = (int) a.getDimension(R.styleable.lotter_lotter_height, 100);
        a.recycle();

        // 初始化路径
        mPath = new Path();

        // 初始化画笔
        mPaint = new Paint();
        mPaint.setColor(mContext.getResources().getColor(R.color.view_color));
        mPaint.setAlpha(0);
        mPaint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Style.STROKE);
        mPaint.setStrokeWidth(50);//画笔宽度

        // 初始化Bitmap并且锁定到临时画布上
        mBitmap = Bitmap.createBitmap(widget, height, Bitmap.Config.ARGB_4444);
        tempCanvas = new Canvas();
        tempCanvas.setBitmap(mBitmap);
        againScratch("");

        // 在字线程中创建Handler接收像素消息
        mThread = new MyThread();
        mThread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 将处理过的bitmap画上去
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                touchDown(event);
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                break;
        }
        return true;
    }

    /**
     * 移动的时候
     *
     * @param event
     */
    private void touchMove(MotionEvent event) {
        x = event.getX();
        y = event.getY();
        // 二次贝塞尔，实现平滑曲线；oX, oY为操作点 x,y为终点
        mPath.quadTo((x + ox) / 2, (y + oy) / 2, x, y);
        tempCanvas.drawPath(mPath, mPaint);
        ox = x;
        oy = y;
        invalidate();
        computeScale();
    }

    /**
     * 第一次按下来
     *
     * @param event
     */
    private void touchDown(MotionEvent event) {
        ox = x = event.getX();
        oy = y = event.getY();
        mPath.reset();
        mPath.moveTo(ox, oy);
    }

    /**
     * 计算百分比
     */
    private void computeScale() {
        Message msg = mHandler.obtainMessage(0);
        msg.obj = ++messageCount;
        mHandler.sendMessage(msg);
    }

    /**
     * 异步线程，作用是创建handler接收处理消息。
     *
     * @author Administrator
     */
    class MyThread extends Thread {

        public MyThread() {
        }

        @Override
        public void run() {
            super.run();
            /*
             * 创建 handler前先初始化Looper.
			 */
            Looper.prepare();

            mHandler = new Handler() {
                @Override
                public void dispatchMessage(Message msg) {
                    super.dispatchMessage(msg);
                    // 只处理最后一次的百分比
                    if ((Integer) (msg.obj) != messageCount) {
                        return;
                    }
                    // 取出像素点
                    synchronized (mBitmap) {
                        if (pixels == null) {
                            pixels = new int[mBitmap.getWidth()
                                    * mBitmap.getHeight()];
                        }
                        mBitmap.getPixels(pixels, 0, widget, 0, 0, widget,
                                height);
                    }

                    int sum = pixels.length;
                    int num = 0;
                    for (int i = 0; i < sum; i++) {
                        if (pixels[i] == 0) {
                            num++;
                        }
                    }
                    double scratchPercentage=num / (double) sum * 100;
                    //System.out.println("百分比:" + scratchPercentage);
                    //大于55%则刮开
                    if (scratchPercentage> 55) {
                        messageCount = 0;
                        mPaint.clearShadowLayer();
                        tempCanvas.drawPaint(mPaint);
                    }
                }
            };
			/*
			 * 启动该线程的消息队列
			 */
            Looper.loop();
        }
    }
}
