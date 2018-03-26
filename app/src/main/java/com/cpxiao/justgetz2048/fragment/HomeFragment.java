package com.cpxiao.justgetz2048.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageButton;

import com.cpxiao.R;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.androidutils.library.utils.RateAppUtils;
import com.cpxiao.androidutils.library.utils.ShareAppUtils;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;
import com.cpxiao.justgetz2048.mode.extra.Extra;

/**
 * @author cpxiao on 2017/9/27.
 */

public class HomeFragment extends BaseZAdsFragment implements View.OnClickListener {

    public static HomeFragment newInstance(Bundle bundle) {
        HomeFragment fragment = new HomeFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        ImageButton play = (ImageButton) view.findViewById(R.id.play);
        ImageButton rateApp = (ImageButton) view.findViewById(R.id.rate_app);
        ImageButton share = (ImageButton) view.findViewById(R.id.share);
        ImageButton bestScore = (ImageButton) view.findViewById(R.id.best_score);

        play.setOnClickListener(this);
        rateApp.setOnClickListener(this);
        share.setOnClickListener(this);
        bestScore.setOnClickListener(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Context context = getHoldingActivity();
        Bundle bundle = new Bundle();
        if (id == R.id.play) {
            bundle.putInt(Extra.Key.SQUARE_COUNT_X, 6);
            bundle.putInt(Extra.Key.SQUARE_COUNT_Y, 6);
            addFragment(GameFragment.newInstance(bundle));
        } else if (id == R.id.rate_app) {
            RateAppUtils.rate(context);
        } else if (id == R.id.share) {
            String msg = getString(R.string.share_msg) + "\n" +
                    getString(R.string.app_name) + "\n" +
                    "https://play.google.com/store/apps/details?id=" + context.getPackageName();
            ShareAppUtils.share(context, getString(R.string.share), msg);
        } else if (id == R.id.best_score) {
            showBestScoreDialog(context);
        }
    }

    private void showBestScoreDialog(Context context) {
        long bestScoreHard = PreferencesUtils.getLong(context, Extra.Key.getKey(6, 6, Extra.Key.BEST_SCORE), 0);

        String msg = bestScoreHard + "\n";

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle(R.string.best_score)
                .setMessage(msg)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
}
