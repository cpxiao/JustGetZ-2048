package com.cpxiao.aa2048.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;

import com.cpxiao.R;
import com.cpxiao.aa2048.mode.extra.Extra;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;

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
        Button easy = (Button) view.findViewById(R.id.easy);
        Button normal = (Button) view.findViewById(R.id.normal);
        Button hard = (Button) view.findViewById(R.id.hard);
        Button insane = (Button) view.findViewById(R.id.insane);
        Button impossible = (Button) view.findViewById(R.id.impossible);
        Button bestScore = (Button) view.findViewById(R.id.best_score);
        Button settings = (Button) view.findViewById(R.id.settings);
        Button quit = (Button) view.findViewById(R.id.quit);

        easy.setOnClickListener(this);
        normal.setOnClickListener(this);
        hard.setOnClickListener(this);
        insane.setOnClickListener(this);
        impossible.setOnClickListener(this);
        bestScore.setOnClickListener(this);
        settings.setOnClickListener(this);
        quit.setOnClickListener(this);

        settings.setVisibility(View.GONE);
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
        if (id == R.id.easy) {
            bundle.putInt(Extra.Key.SQUARE_COUNT_X, 4);
            bundle.putInt(Extra.Key.SQUARE_COUNT_Y, 4);
            addFragment(GameFragment.newInstance(bundle));
        } else if (id == R.id.normal) {
            bundle.putInt(Extra.Key.SQUARE_COUNT_X, 5);
            bundle.putInt(Extra.Key.SQUARE_COUNT_Y, 5);
            addFragment(GameFragment.newInstance(bundle));
        } else if (id == R.id.hard) {
            bundle.putInt(Extra.Key.SQUARE_COUNT_X, 6);
            bundle.putInt(Extra.Key.SQUARE_COUNT_Y, 6);
            addFragment(GameFragment.newInstance(bundle));
        } else if (id == R.id.insane) {
            bundle.putInt(Extra.Key.SQUARE_COUNT_X, 7);
            bundle.putInt(Extra.Key.SQUARE_COUNT_Y, 7);
            addFragment(GameFragment.newInstance(bundle));
        } else if (id == R.id.impossible) {
            bundle.putInt(Extra.Key.SQUARE_COUNT_X, 8);
            bundle.putInt(Extra.Key.SQUARE_COUNT_Y, 8);
            addFragment(GameFragment.newInstance(bundle));
        } else if (id == R.id.best_score) {
            showBestScoreDialog(context);
        } else if (id == R.id.settings) {

        } else if (id == R.id.quit) {
            removeFragment();
        }
    }

    private void showBestScoreDialog(Context context) {
        long bestScoreEasy = PreferencesUtils.getLong(context, Extra.Key.getKey(4, 4, Extra.Key.BEST_SCORE), 0);
        long bestScoreNormal = PreferencesUtils.getLong(context, Extra.Key.getKey(5, 5, Extra.Key.BEST_SCORE), 0);
        long bestScoreHard = PreferencesUtils.getLong(context, Extra.Key.getKey(6, 6, Extra.Key.BEST_SCORE), 0);
        long bestScoreInsane = PreferencesUtils.getLong(context, Extra.Key.getKey(7, 7, Extra.Key.BEST_SCORE), 0);
        long bestScoreImpossible = PreferencesUtils.getLong(context, Extra.Key.getKey(8, 8, Extra.Key.BEST_SCORE), 0);

        String msg = getString(R.string.easy) + ": " + bestScoreEasy + "\n"
                + getString(R.string.normal) + ": " + bestScoreNormal + "\n"
                + getString(R.string.hard) + ": " + bestScoreHard + "\n"
                + getString(R.string.insane) + ": " + bestScoreInsane + "\n"
                + getString(R.string.impossible) + ": " + bestScoreImpossible + "\n";

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
