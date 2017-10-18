package com.cpxiao.zads.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cpxiao.AppConfig;

/**
 * @author cpxiao on 2017/8/19.
 */
public class ZBannerView extends FrameLayout {
    private static final boolean DEBUG = AppConfig.DEBUG;
    private static final String TAG = ZBannerView.class.getSimpleName();

    private int mHeightDip = 50;
    private ImageView mIcon;
    private TextView mTitle;
    private TextView mDescription;

    public ZBannerView(Context context, int heightDip) {
        super(context);
        mHeightDip = heightDip;
        init(context);
    }

    public ZBannerView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context c) {
        setBackgroundColor(Color.WHITE);
        mIcon = new ImageView(c);
        if (DEBUG) {
            mIcon.setBackgroundColor(Color.RED);
        }
        mIcon.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        int padding = (int) (0.1F * dip2px(mHeightDip));
        mIcon.setPadding(padding, padding, padding, padding);


        mTitle = new TextView(c);
        if (DEBUG) {
            mTitle.setBackgroundColor(Color.BLUE);
        }
        mTitle.setTextColor(Color.BLACK);
        mTitle.setSingleLine();
        mTitle.setMaxLines(1);
        mTitle.setEllipsize(TextUtils.TruncateAt.END);
        mTitle.setGravity(Gravity.CENTER_VERTICAL);
        mTitle.setTextSize(dip2px(0.08F * mHeightDip));

        mDescription = new TextView(c);
        if (DEBUG) {
            mDescription.setBackgroundColor(Color.YELLOW);
        }
        mDescription.setTextColor(Color.GRAY);
        mDescription.setMaxLines(1);
        mDescription.setSingleLine();
        mDescription.setEllipsize(TextUtils.TruncateAt.END);

        mDescription.setTextSize(dip2px(0.06F * mHeightDip));
        mDescription.setGravity(Gravity.CENTER_VERTICAL);

        int iconW = dip2px(mHeightDip);
        LayoutParams paramsIcon = new LayoutParams(iconW, iconW);
        paramsIcon.gravity = Gravity.CENTER_VERTICAL;
        addView(mIcon, paramsIcon);


        LayoutParams paramsTitle = new LayoutParams(-1, dip2px(0.5F * mHeightDip));
        paramsTitle.setMargins(dip2px(mHeightDip), 0, 0, 0);
        addView(mTitle, paramsTitle);


        LayoutParams paramsDesc = new LayoutParams(-1, dip2px(0.5F * mHeightDip));
        paramsDesc.setMargins(dip2px(mHeightDip), dip2px(0.5F * mHeightDip), 0, 0);

        addView(mDescription, paramsDesc);


        int paddingLR = dip2px(10);
        int paddingTB = dip2px(0);
        setPadding(paddingLR, paddingTB, paddingLR, paddingTB);
    }

    public void bindData(Context context, String icon, String title, String desc) {
        if (DEBUG) {
            Log.d(TAG, "bindData: icon = " + icon);
        }
        if (!TextUtils.isEmpty(icon)) {
            Glide.with(context).load(icon).into(mIcon);
        } else {
            if (DEBUG) {
                Log.d(TAG, "bindData: icon is empty");
                throw new IllegalArgumentException("icon is empty");
            }
        }
        if (!TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        if (!TextUtils.isEmpty(desc)) {
            mDescription.setText(desc);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private int dip2px(float dipValue) {
        return (int) (dipValue * Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }

    private int px2dip(float pxValue) {
        return (int) (pxValue / Resources.getSystem().getDisplayMetrics().density + 0.5f);
    }
}
