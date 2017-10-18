package com.cpxiao.zads.utils;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

/**
 * @author cpxiao on 2017/08/10.
 */
public class ThreadUtils {

    /**
     * 工作线程
     */
    private static final HandlerThread sWorkerThread;
    /**
     * 工作线程Handler
     */
    private static final Handler sWorker;
    /**
     * UI线程Handler
     */
    private static Handler sMainHandler;
    private static ThreadUtils sInstance;

    static {
        sWorkerThread = new HandlerThread("ThreadUtils.Loader");
        sWorkerThread.start();
        sWorker = new Handler(sWorkerThread.getLooper());
    }

    protected final String TAG = getClass().getSimpleName();

    private ThreadUtils() {

    }

    public static ThreadUtils getInstance() {
        synchronized (ThreadUtils.class) {

            if (sInstance == null) {
                sInstance = new ThreadUtils();
            }
            return sInstance;
        }
    }

    /**
     * 获得UI线程Handler
     *
     * @return
     */
    private static Handler getUIHandler() {
        synchronized (ThreadUtils.class) {
            if (sMainHandler == null) {
                sMainHandler = new Handler(Looper.getMainLooper());
            }
            return sMainHandler;
        }
    }

    /**
     * 判断当前执行的线程是否为UI线程
     *
     * @return
     */
    public boolean isCurrentUIThread() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }

    /**
     * 在UI线程上执行
     *
     * @param runnable
     */
    public void runOnMainThread(Runnable runnable) {
        if (isCurrentUIThread()) {
            runnable.run();
        } else {
            getUIHandler().post(runnable);
        }
    }

    /**
     * 延迟执行
     *
     * @param runnable
     * @param delayMillis
     */
    public void runOnMainThreadDelayed(Runnable runnable, long delayMillis) {
        getUIHandler().postDelayed(runnable, delayMillis);
    }

    /**
     * 在工作线程上执行
     *
     * @param runnable
     */
    public void runOnWorkThread(Runnable runnable) {
        if (isCurrentUIThread()) {
            sWorker.post(runnable);
        } else {
            runnable.run();
        }
    }

}
