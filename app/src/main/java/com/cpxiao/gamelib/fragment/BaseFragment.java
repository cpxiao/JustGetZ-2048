package com.cpxiao.gamelib.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cpxiao.AppConfig;
import com.cpxiao.gamelib.activity.core.BaseActivity;

/**
 * @author cpxiao on 2017/08/31.
 */

public abstract class BaseFragment extends Fragment {
    protected static final boolean DEBUG = AppConfig.DEBUG;
    //        protected String TAG = BaseFragment.class.getSimpleName();//这样只能获得"BaseFragment"
    protected String TAG;

    protected BaseActivity mActivity;

    //获取布局文件ID
    protected abstract int getLayoutId();

    protected abstract void initView(View view, Bundle savedInstanceState);

    //获取宿主Activity
    protected BaseActivity getHoldingActivity() {
        return mActivity;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
        this.mActivity = (BaseActivity) context;
    }

    //    @Override
    //    public void onAttach(Activity activity) {
    //        super.onAttach(activity);
    //        this.mActivity = (BaseActivity) activity;
    //    }

    //添加fragment
    protected void addFragment(BaseFragment fragment) {
        if (null != fragment) {
            getHoldingActivity().addFragment(fragment);
        }
    }

    //移除fragment
    protected void removeFragment() {
        getHoldingActivity().removeFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        initView(view, savedInstanceState);
        return view;
    }
}
