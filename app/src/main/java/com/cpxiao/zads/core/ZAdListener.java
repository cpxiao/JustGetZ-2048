package com.cpxiao.zads.core;

import com.cpxiao.zads.ads.core.Advertisement;

import java.util.Queue;

/**
 * @author cpxiao on 2017/08/10.
 */
public interface ZAdListener {

    void onLoadSuccess(Advertisement ad);

    void onLoadFailed(Advertisement ad, String message, Queue<Advertisement> next);

    void onAdClick(Advertisement ad);

}
