package com.cpxiao.zads.ads;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.cpxiao.AppConfig;
import com.cpxiao.zads.ads.core.Advertisement;
import com.cpxiao.zads.ads.core.BaseZAd;
import com.cpxiao.zads.core.AdConfigBean;
import com.cpxiao.zads.core.ZAdSize;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdListener;
import com.facebook.ads.AdSettings;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;


/**
 * @author cpxiao on 2017/08/10.
 *         备注：fb的广告的laod可能会长时间卡住，什么回调都不会执行。
 */
public class FbBannerAd extends BaseZAd {
    private static final String TAG = "FbBannerAd";
    private static final String TEST_DEVICE = AppConfig.TEST_DEVICE_FB;

    private AdView mAdManager;

    public FbBannerAd(AdConfigBean advertiser) {
        super(advertiser);
    }


    @Override
    public void loadAd(final Context c, final Queue<Advertisement> next) {
        //参数校验
        if (TextUtils.isEmpty(mPlaceId)) {
            if (DEBUG) {
                String msg = "loadAd: param error!";
                Log.d(TAG, msg);
                throw new IllegalArgumentException(msg);
            }
            return;
        }

        if (DEBUG) {
            Log.d(TAG, "loadAd: mPublishId = " + mPublishId + ", mPlaceId = " + mPlaceId);
        }
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }
        mAdManager = new AdView(c, mPlaceId, getAdSize(mAdSize));
        mAdManager.setAdListener(new AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {
                String msg = "getErrorCode = " + adError.getErrorCode() + ", getErrorMessage = " + adError.getErrorMessage();
                if (DEBUG) {
                    Log.d(TAG, "onError: mPlaceId = " + mPlaceId + ", msg = " + msg);
                }
                onLoadZAdFail(get(), msg, next);
            }

            @Override
            public void onAdLoaded(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdLoaded: mPlaceId = " + mPlaceId);
                }
                mLastAdView = generateView(c, mAdManager, mAdSize);
                onLoadZAdSuccess(get());
            }

            @Override
            public void onAdClicked(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onAdClicked: ");
                }
                if (mZAdListener != null) {
                    mZAdListener.onAdClick(get());
                }
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                if (DEBUG) {
                    Log.d(TAG, "onLoggingImpression: ");
                }
            }
        });
        if (DEBUG) {
            //            AdSettings.addTestDevice("e6298923190b4e7e7119e0f14c44f097");

            // 如果想要添加多台测试设备，只需创建一个字符串列表，添加到加载广告前的位置：
            List<String> testDevices = new ArrayList<>();
            testDevices.add(TEST_DEVICE);
            AdSettings.addTestDevices(testDevices);
        }
        mAdManager.loadAd();
    }


    @Override
    public void destroyAllView(Context context) {
        super.destroyAllView(context);

        removeFromParent(mAdManager);
        if (mAdManager != null) {
            mAdManager.destroy();
            mAdManager = null;
        }
    }

    private View generateView(Context c, AdView bannerView, int size) {
        if (c == null || bannerView == null) {
            return null;
        }
        removeFromParent(bannerView);

//        LinearLayout view = new LinearLayout(c);
//        view.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(size));
//        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
//        view.addView(bannerView, params);
        //        view.addView(bannerView);
        //        return view;
        bannerView.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        return bannerView;
    }

    private AdSize getAdSize(int size) {
        if (size == ZAdSize.BANNER_320X50) {
            return AdSize.BANNER_HEIGHT_50;
        } else if (size == ZAdSize.BANNER_320X90) {
            return AdSize.BANNER_HEIGHT_90;
        } else if (size == ZAdSize.BANNER_320X100) {
            return AdSize.BANNER_HEIGHT_90;
        } else if (size == ZAdSize.BANNER_300X250) {
            return AdSize.RECTANGLE_HEIGHT_250;
        } else {
            if (DEBUG) {
                throw new IllegalArgumentException("No Size found in " + TAG);
            }
            return AdSize.BANNER_HEIGHT_50;
        }
    }

    @Override
    public String toString() {
        return TAG;
    }

}
