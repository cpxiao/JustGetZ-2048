package com.cpxiao.zads.ads.core;

import android.content.Context;
import android.view.View;

import com.cpxiao.zads.core.ZAdListener;

import java.util.Queue;

/**
 * @author cpxiao on 2017/08/10.
 */
public interface Advertisement {

    /**
     * 加载广告
     *
     * @param c    Application Context
     * @param next 下一个广告商
     */
    void load(Context c, Queue<Advertisement> next);

    /**
     * 设置广告回调
     *
     * @param listener ZAdListener
     */
    void setListener(ZAdListener listener);

    /**
     * 获得最后的广告View，原生广告要将ad和view关联
     *
     * @return View
     */
    View getLastAdView();

    /**
     * 清空所有广告，包含缓存广告
     * 注意: 原生广告需要取消关联，则必须重写此方法
     */
    void destroyAllView(Context context);

}
