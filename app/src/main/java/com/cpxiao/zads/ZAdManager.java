package com.cpxiao.zads;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.cpxiao.zads.ads.AdMobBannerAd;
import com.cpxiao.zads.ads.AdMobNativeExpressAd;
import com.cpxiao.zads.ads.FbBannerAd;
import com.cpxiao.zads.ads.FbNativeAd;
import com.cpxiao.zads.ads.core.Advertisement;
import com.cpxiao.zads.core.AdConfigBean;
import com.cpxiao.zads.core.ZAdDefaultConfig;
import com.cpxiao.zads.core.ZAdListener;
import com.cpxiao.zads.core.ZAdPosition;
import com.cpxiao.zads.core.ZAdType;
import com.cpxiao.zads.utils.UMeng;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * @author cpxiao on 2017/08/10.
 */
public class ZAdManager {

    private static final boolean DEBUG = ZAdsConfig.DEBUG;
    private static final String TAG = ZAdManager.class.getSimpleName();

    //广告配置数据
    private ArrayMap<Integer, List<AdConfigBean>> mAdConfigArrayMap = null;

    //成功获取的广告
    private ArrayMap<Integer, Advertisement> mLoadedAdViewArrayMap = new ArrayMap<>();

    //单例模式
    private static volatile ZAdManager mInstance = null;

    private ZAdManager() {

    }

    public static ZAdManager getInstance() {
        if (mInstance == null) {
            synchronized (ZAdManager.class) {
                if (mInstance == null) {
                    mInstance = new ZAdManager();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     *
     * @param context context
     */
    public void init(final Context context) {
        mAdConfigArrayMap = ZAdDefaultConfig.getDefaultConfig(context);
    }

    /**
     * 清空所有广告，包含缓存广告
     */
    public void destroyAllPosition(Context context) {
        destroy(context, ZAdPosition.POSITION_MAIN);
        destroy(context, ZAdPosition.POSITION_HOME);
        destroy(context, ZAdPosition.POSITION_GAME);
        destroy(context, ZAdPosition.POSITION_SETTINGS);
        destroy(context, ZAdPosition.POSITION_LEVEL_LIST);
        destroy(context, ZAdPosition.POSITION_BEST_SCORE);
        destroy(context, ZAdPosition.POSITION_TEST);

        System.gc();
    }

    /**
     * 清空所有广告，包含缓存广告
     *
     * @param position 广告位
     */
    public void destroy(Context context, int position) {
        if (DEBUG) {
            Log.d(TAG, "destroy: position = " + position);
        }
        if (mLoadedAdViewArrayMap == null) {
            if (DEBUG) {
                String msg = "destroy: param error! mLoadedAdViewArrayMap == null";
                Log.e(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
            return;
        }
        Advertisement advertisement = mLoadedAdViewArrayMap.get(position);
        if (advertisement == null) {
            if (DEBUG) {
                Log.d(TAG, "destroy: 此广告位无广告View！advertisement == null, position = " + position);
            }
            return;
        }
        advertisement.destroyAllView(context);
    }

    public void loadAd(final Context appCxt, final int position, final LinearLayout layout) {
        if (layout == null) {
            if (DEBUG) {
                String msg = "loadAd: param error! layout == null";
                Log.e(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
            return;
        }
        if (appCxt == null || mAdConfigArrayMap == null) {
            if (DEBUG) {
                String msg = "loadAd: param error! 广告未进行初始化配置!";
                Log.e(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
            return;
        }

        List<AdConfigBean> list = mAdConfigArrayMap.get(position);
        if (list == null || list.isEmpty()) {
            if (DEBUG) {
                String msg = "loadAd: error! 此广告位的配置信息为空！position = " + position;
                Log.d(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
            return;
        }
        //先排队
        Queue<Advertisement> queue = new ArrayBlockingQueue<>(list.size());
        for (AdConfigBean advertiser : list) {
            Advertisement advertisement = getAdvertisement(appCxt, advertiser);
            if (advertisement != null) {
                queue.add(advertisement);
            }
        }
        if (DEBUG) {
            Log.d(TAG, "loadAd: queue.size() = " + queue.size());
        }
        //请求第一个,如果失败请求下一个
        final Advertisement advertisement = queue.poll();
        //注意回调的时候要在主线程中，否则有些广告会crash！
        advertisement.setListener(new ZAdListener() {
            @Override
            public void onLoadSuccess(Advertisement ad) {
                if (DEBUG) {
                    Log.d(TAG, "onLoadSuccess: " + ad.toString() + ", position = " + position);
                }
                // TODO 统计：广告请求成功
                UMeng.postStat(appCxt, UMeng.SDK_AD_SUCCESS, ad.toString(), position);

                mLoadedAdViewArrayMap.put(position, ad);

                layout.removeAllViews();
                View adView = ad.getLastAdView();
                if (adView != null) {
                    layout.addView(adView);
                    // TODO 统计：广告展示
                    UMeng.postStat(appCxt, UMeng.SDK_AD_IMPRESSION, ad.toString(), position);
                }
            }

            @Override
            public void onLoadFailed(Advertisement ad, String message, Queue<Advertisement> next) {
                if (DEBUG) {
                    Log.d(TAG, "onLoadFailed: " + ad.toString() + ", position = " + position + ", msg = " + message);
                }
                // TODO 统计：广告请求失败
                UMeng.postStat(appCxt, UMeng.SDK_AD_FAIL, ad.toString(), position);

                //获取下一个广告商并加载
                Advertisement advertisementNext = next.poll();
                if (advertisementNext == null) {
                    if (DEBUG) {
                        Log.d(TAG, "当前广告位请求全部失败, position = " + position);
                    }
                    return;
                }
                advertisementNext.setListener(this);
                advertisementNext.load(appCxt, next);
                // TODO 统计：广告请求开始
                UMeng.postStat(appCxt, UMeng.SDK_AD_LOAD, ad.toString(), position);

                if (DEBUG) {
                    Log.d(TAG, "请求下一个广告商: " + advertisementNext.toString() + ", position = " + position);
                }
            }

            @Override
            public void onAdClick(Advertisement ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdClick: " + ad.toString() + ", position = " + position);
                }
                // TODO 统计：广告点击
                UMeng.postStat(appCxt, UMeng.SDK_AD_CLICK, ad.toString(), position);
            }
        });
        advertisement.load(appCxt, queue);
        // TODO 统计：广告请求开始
        UMeng.postStat(appCxt, UMeng.SDK_AD_LOAD, advertisement.toString(), position);

        if (DEBUG) {
            Log.d(TAG, "请求广告商: " + advertisement.toString() + ", position = " + position);
        }
    }

    private Advertisement getAdvertisement(Context context, AdConfigBean adConfigBean) {
        if (context == null || adConfigBean == null) {
            if (DEBUG) {
                String msg = "getAdvertisement: param error!  context == null || adConfigBean == null";
                Log.d(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
            return null;
        }
        switch (adConfigBean.adType) {
            case ZAdType.AD_ADMOB_NATIVE: {
                return new AdMobNativeExpressAd(adConfigBean);
            }
            case ZAdType.AD_ADMOB_BANNER: {
                return new AdMobBannerAd(adConfigBean);
            }
            case ZAdType.AD_FB_NATIVE: {
                return new FbNativeAd(adConfigBean);
            }
            case ZAdType.AD_FB_BANNER: {
                return new FbBannerAd(adConfigBean);
            }
            default: {
                if (DEBUG) {
                    String msg = "getAdvertisement: ZAdType error!  adConfigBean.adType = " + adConfigBean.adType;
                    Log.d(TAG, msg);
                    throw new IllegalArgumentException(msg);
                }
                return null;
            }
        }
    }

}
