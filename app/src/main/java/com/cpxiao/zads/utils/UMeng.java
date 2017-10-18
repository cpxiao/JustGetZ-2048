package com.cpxiao.zads.utils;

import android.content.Context;
import android.text.TextUtils;

import com.cpxiao.zads.ZAdsConfig;
import com.umeng.analytics.MobclickAgent;

/**
 * @author cpxiao on 2017/09/19.
 */

public class UMeng {
    private static final boolean DEBUG = ZAdsConfig.DEBUG;

    /**
     * 广告
     */
    public static final String SDK_AD_LOAD = "A0";
    public static final String SDK_AD_SUCCESS = "A1";
    public static final String SDK_AD_FAIL = "A2";
    public static final String SDK_AD_IMPRESSION = "A3";
    public static final String SDK_AD_CLICK = "A4";


    /**
     * @param context       context
     * @param event         event
     * @param advertisement 广告商
     * @param position      广告位置
     */
    public static void postStat(Context context, String event, String advertisement, int position) {
        if (context == null || TextUtils.isEmpty(event) || TextUtils.isEmpty(advertisement)) {
            if (DEBUG) {
                throw new IllegalArgumentException("param error!");
            }
        }
        //单个广告商单个广告位
        String value = advertisement + "_P" + position;
        MobclickAgent.onEvent(context, event, value);

        //单个广告商所有广告位
        String value1 = advertisement + "_PAll";
        MobclickAgent.onEvent(context, event, value1);

        //所有广告商单个广告位
        String value2 = "ADSAll_P" + position;
        MobclickAgent.onEvent(context, event, value2);

        //所有广告商所有广告位
        String value3 = "ADSAll_PAll";
        MobclickAgent.onEvent(context, event, value3);

    }
}
