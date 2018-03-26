package com.cpxiao.gamelib.activity.core;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.cpxiao.R;
import com.cpxiao.gamelib.fragment.BaseFragment;

/**
 * @author cpxiao on 2017/08/31.
 */

public abstract class BaseAppActivity extends BaseActivity {

    //获取第一个fragment
    protected abstract BaseFragment getFirstFragment();

    //获取Intent
    protected void handleIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //no title. requestFeature() must be called before adding content
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //隐藏状态栏部分（电池电量、时间等部分）
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        setContentView(getContentViewId());
        if (null != getIntent()) {
            handleIntent(getIntent());
        }

        //避免重复添加Fragment
        if (null == savedInstanceState) {
            BaseFragment firstFragment = getFirstFragment();
            if (null != firstFragment) {
                addFragment(firstFragment);
            }
        }

    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected int getFragmentContainerId() {
        return R.id.fragment_container;
    }

}
