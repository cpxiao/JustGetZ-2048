package com.cpxiao.zads.ads.core;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.cpxiao.zads.ZAdsConfig;
import com.cpxiao.zads.core.AdConfigBean;
import com.cpxiao.zads.core.ZAdListener;
import com.cpxiao.zads.utils.ThreadUtils;

import java.util.Queue;


/**
 * @author cpxiao on 2017/08/10.
 */
public abstract class BaseZAd implements Advertisement {

    protected static final boolean DEBUG = ZAdsConfig.DEBUG;

    /**
     * ZAdListener
     */
    protected ZAdListener mZAdListener;

    /**
     * 广告相关配置参数
     */
    protected String mPublishId;
    protected String mPlaceId;
    protected int mAdSize;

    /**
     * 最后使用的广告View
     */
    protected View mLastAdView;

    /**
     * 最后一次请求开始的时间，用于判断请求超时防止某些广告如fb长时间无广告回调
     */
    private long mLastLoadStart;

    /**
     * 请求超时时间,单位：毫秒
     */
    private static final long TIME_FROM_LAST_LOAD_START = 30000;

    private BaseZAd() {

    }

    public BaseZAd(AdConfigBean advertiser) {
        if (advertiser == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("BaseZAd: param error!  advertiser == null");
            }
            return;
        }
        if (!TextUtils.isEmpty(advertiser.publishId)) {
            //校验publishId前后是否有空格
            if (advertiser.publishId.trim().length() != advertiser.publishId.length()) {
                if (DEBUG) {
                    throw new IllegalArgumentException("BaseZAd: param error!  publishId = " + advertiser.publishId + ".");
                }
            }
        }
        //校验placeId前后是否有空格
        if (!TextUtils.isEmpty(advertiser.placeId)) {
            if (advertiser.placeId.trim().length() != advertiser.placeId.length()) {
                if (DEBUG) {
                    throw new IllegalArgumentException("BaseZAd: param error!  placeId = " + advertiser.placeId + ".");
                }
            }
        }
        mPublishId = advertiser.publishId;
        mPlaceId = advertiser.placeId;
        mAdSize = advertiser.adSize;
    }

    @Override
    public void load(Context c, Queue<Advertisement> next) {
        //参数校验
        if (c == null) {
            if (DEBUG) {
                throw new IllegalArgumentException("load: param error!  c == null");
            }
            return;
        }

        if (Math.abs(System.currentTimeMillis() - mLastLoadStart) < TIME_FROM_LAST_LOAD_START) {
            return;
        }
        mLastLoadStart = System.currentTimeMillis();
        loadAd(c, next);
    }

    protected abstract void loadAd(Context c, Queue<Advertisement> next);


    @Override
    public void setListener(ZAdListener listener) {
        mZAdListener = listener;
    }

    @Override
    public void destroyAllView(Context context) {
        removeFromParent(mLastAdView);
        mLastAdView = null;
    }

    public View getLastAdView() {
        return mLastAdView;
    }

    /**
     * 加载成功
     *
     * @param advertisement ad
     */
    protected void onLoadZAdSuccess(final Advertisement advertisement) {
        //回调必须在主线程中,否则next的请求可能会崩溃
        ThreadUtils.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mZAdListener != null) {
                    mZAdListener.onLoadSuccess(advertisement);
                }
            }
        });
    }

    /**
     * 加载失败
     *
     * @param advertisement ad
     * @param msg           message
     * @param next          next ad
     */
    protected void onLoadZAdFail(final Advertisement advertisement, final String msg, final Queue<Advertisement> next) {
        //回调必须在主线程中,否则next的请求可能会崩溃
        ThreadUtils.getInstance().runOnMainThread(new Runnable() {
            @Override
            public void run() {
                if (mZAdListener != null) {
                    mZAdListener.onLoadFailed(advertisement, msg, next);
                }
            }
        });
    }


    /**
     * 从父容器中移除
     *
     * @param view View
     */
    protected void removeFromParent(View view) {
        if (view == null) {
            return;
        }
        ViewGroup parent = (ViewGroup) view.getParent();
        if (parent != null) {
            parent.removeView(view);
        }
    }

    protected Advertisement get() {
        return this;
    }

    public int dip2px(float dipValue) {
        return (int) (dipValue * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    public int px2dip(float pxValue) {
        return (int) (pxValue / Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }
}
