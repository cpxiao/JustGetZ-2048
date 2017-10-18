package com.cpxiao.gamelib.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

/**
 * BaseSurfaceViewFPS
 *
 * @author cpxiao on 2016/8/23
 * @version 2017/3/21
 *          2017/8/17 修改默认fps
 */
public abstract class BaseSurfaceViewFPS extends BaseSurfaceView implements Runnable {

    /**
     * 线程消亡的标志位
     */
    protected boolean isRunning = false;

    /**
     * 设置FPS，默认为60
     */
    protected int mFPS = 60;

    protected long mFrame = 0;

    public BaseSurfaceViewFPS(Context context) {
        super(context);
    }

    public BaseSurfaceViewFPS(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseSurfaceViewFPS(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        super.surfaceCreated(holder);
        isRunning = true;

        /* 创建一个线程 */
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);
        isRunning = false;
    }

    @Override
    public void run() {
        while (isRunning) {
            synchronized (BaseSurfaceViewFPS.class.getSimpleName()) {
                int deltaTime = 1000 / mFPS;

                long start = System.currentTimeMillis();
                mFrame++;
                myDraw();
                timingLogic();
                long end = System.currentTimeMillis();
                try {
                    long useTime = end - start;
                    if (useTime < deltaTime) {
                        Thread.sleep(deltaTime - useTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 定时逻辑
     */
    protected abstract void timingLogic();


}