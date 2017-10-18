package com.cpxiao.zads.views;//package com.cpxiao.zads.views;
//
//import android.content.Context;
//import android.content.res.Resources;
//import android.graphics.Color;
//import android.text.TextUtils;
//import android.util.Log;
//import android.view.Gravity;
//import android.widget.FrameLayout;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.cpxiao.AppConfig;
//
///**
// * @author cpxiao on 2017/08/10.
// */
//public class ZBannerView50 extends FrameLayout {
//    private static final boolean DEBUG = AppConfig.DEBUG;
//    private static final String TAG = ZBannerView50.class.getSimpleName();
//
//    private ImageView mIcon;
//    private TextView mTitle;
//    private TextView mDescription;
//
//    public ZBannerView50(Context context) {
//        super(context);
//        init(context);
//    }
//
//    private void init(Context c) {
//        setBackgroundColor(Color.WHITE);
//        mIcon = new ImageView(c);
//        mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//
//        mTitle = new TextView(c);
//        mTitle.setTextColor(Color.BLACK);
//        mTitle.setSingleLine();
//        mTitle.setMaxLines(1);
//        mTitle.setEllipsize(TextUtils.TruncateAt.END);
//
//        mDescription = new TextView(c);
//        mDescription.setTextColor(Color.GRAY);
//        mDescription.setMaxLines(1);
//        mDescription.setSingleLine();
//        mDescription.setEllipsize(TextUtils.TruncateAt.END);
//
//
//        //        int mScreenWidth = getResources().getDisplayMetrics().widthPixels;
//        //        int iconW = (int) (mScreenWidth * 0.138);
//        int iconW = dip2px(50);
//        LayoutParams paramsIcon = new LayoutParams(iconW, iconW);
//        paramsIcon.gravity = Gravity.CENTER_VERTICAL;
//        addView(mIcon, paramsIcon);
//
//        LayoutParams paramsTitle = new LayoutParams(-1, -1);
//        paramsTitle.setMargins(dip2px(68.0f), 0, 0, 0);
//        addView(mTitle, paramsTitle);
//
//        LayoutParams paramsDesc = new LayoutParams(-1, -1);
//        paramsDesc.setMargins(dip2px(68.0f), dip2px(28.0f), 0, 0);
//        addView(mDescription, paramsDesc);
//
//        //尽量与CommonRankItem的图标及文字对齐
//        //        setPadding(dip2px(c, 18), dip2px(c, 10), dip2px(c, 18), dip2px(c, 10));
//        setPadding(dip2px(18), 0, dip2px(18), 0);
//    }
//
//    public void bindData(Context context, String icon, String title, String desc) {
//        if (DEBUG) {
//            Log.d(TAG, "bindData: icon = " + icon);
//        }
//        if (!TextUtils.isEmpty(icon)) {
//            Glide.with(context).load(icon).into(mIcon);
//        } else {
//            if (DEBUG) {
//                Log.d(TAG, "bindData: icon is empty");
//                throw new IllegalArgumentException("icon is empty");
//            }
//        }
//        mTitle.setText(title);
//        mDescription.setText(desc);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//
//    public int dip2px(float dipValue) {
//        return (int) (dipValue * Resources.getSystem().getDisplayMetrics().density + 0.5f);
//    }
//
//    public int px2dip(float pxValue) {
//        return (int) (pxValue / Resources.getSystem().getDisplayMetrics().density + 0.5f);
//    }
//}
