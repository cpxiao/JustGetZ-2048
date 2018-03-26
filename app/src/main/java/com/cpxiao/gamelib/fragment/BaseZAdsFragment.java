package com.cpxiao.gamelib.fragment;

import com.cpxiao.gamelib.activity.BaseZAdsActivity;

/**
 * @author cpxiao on 2017/09/05.
 */

public abstract class BaseZAdsFragment extends BaseFragment {

    protected void loadZAds(int position) {
        if (getHoldingActivity() instanceof BaseZAdsActivity) {
            BaseZAdsActivity activity = (BaseZAdsActivity) getHoldingActivity();
            activity.loadZAds(position);
        }
    }
}
