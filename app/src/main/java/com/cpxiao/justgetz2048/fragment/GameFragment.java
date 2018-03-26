package com.cpxiao.justgetz2048.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.cpxiao.R;
import com.cpxiao.justgetz2048.mode.Tile;
import com.cpxiao.justgetz2048.mode.extra.Extra;
import com.cpxiao.justgetz2048.views.GameView;
import com.cpxiao.androidutils.library.utils.PreferencesUtils;
import com.cpxiao.gamelib.fragment.BaseZAdsFragment;

/**
 * @author cpxiao on 2017/9/27.
 */

public class GameFragment extends BaseZAdsFragment {
    private GameView mGameView;

    private int mSquareCountX = Extra.Key.SQUARE_COUNT_X_DEFAULT;
    private int mSquareCountY = Extra.Key.SQUARE_COUNT_Y_DEFAULT;

    public static GameFragment newInstance(Bundle bundle) {
        GameFragment fragment = new GameFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        Context context = getHoldingActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mSquareCountX = bundle.getInt(Extra.Key.SQUARE_COUNT_X);
            mSquareCountY = bundle.getInt(Extra.Key.SQUARE_COUNT_Y);
        }

        mGameView = new GameView(context, mSquareCountX, mSquareCountY);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.layout_game_view);
        layout.removeAllViews();
        layout.addView(mGameView);

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(Extra.Key.HAS_STATE)) {
                load();
            }
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_game;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(Extra.Key.HAS_STATE, true);
        save();
    }

    @Override
    public void onPause() {
        super.onPause();
        save();
    }

    private void save() {
        Context context = getHoldingActivity();
        Tile[][] field = mGameView.game.grid.field;
        Tile[][] undoField = mGameView.game.grid.undoField;

        PreferencesUtils.putInt(context, Extra.Key.WIDTH, field.length);
        PreferencesUtils.putInt(context, Extra.Key.HEIGHT, field.length);
        for (int xx = 0; xx < field.length; xx++) {
            for (int yy = 0; yy < field[0].length; yy++) {
                Tile tile = field[xx][yy];
                String tileKey = getTileKey(xx, yy);
                if (tile != null) {
                    PreferencesUtils.putInt(context, tileKey, tile.getValue());
                } else {
                    PreferencesUtils.putInt(context, tileKey, 0);
                }
                Tile undoTile = undoField[xx][yy];
                String undoTileKey = getUndoTileKey(xx, yy);
                if (undoTile != null) {
                    PreferencesUtils.putInt(context, undoTileKey, undoTile.getValue());
                } else {
                    PreferencesUtils.putInt(context, undoTileKey, 0);
                }
            }
        }
        PreferencesUtils.putLong(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.SCORE), mGameView.game.score);
        PreferencesUtils.putLong(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.BEST_SCORE), mGameView.game.bestScore);
        PreferencesUtils.putLong(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.UNDO_SCORE), mGameView.game.lastScore);
        PreferencesUtils.putBoolean(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.CAN_UNDO), mGameView.game.canUndo);
        PreferencesUtils.putInt(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.GAME_STATE), mGameView.game.gameState);
        PreferencesUtils.putInt(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.UNDO_GAME_STATE), mGameView.game.lastGameState);

    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    private void load() {
        //Stopping all animations
        mGameView.game.aGrid.cancelAnimations();

        Context context = getHoldingActivity();
        for (int xx = 0; xx < mGameView.game.grid.field.length; xx++) {
            for (int yy = 0; yy < mGameView.game.grid.field[0].length; yy++) {
                String tileKey = getTileKey(xx, yy);
                int value = PreferencesUtils.getInt(context, tileKey, -1);

                if (value > 0) {
                    mGameView.game.grid.field[xx][yy] = new Tile(xx, yy, value);
                } else if (value == 0) {
                    mGameView.game.grid.field[xx][yy] = null;
                }

                String undoTileKey = getUndoTileKey(xx, yy);
                int undoValue = PreferencesUtils.getInt(context, undoTileKey, -1);
                if (undoValue > 0) {
                    mGameView.game.grid.undoField[xx][yy] = new Tile(xx, yy, undoValue);
                } else if (undoValue == 0) {
                    mGameView.game.grid.undoField[xx][yy] = null;
                }
            }
        }

        mGameView.game.score = PreferencesUtils.getLong(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.SCORE), mGameView.game.score);
        mGameView.game.bestScore = PreferencesUtils.getLong(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.BEST_SCORE), mGameView.game.bestScore);
        mGameView.game.lastScore = PreferencesUtils.getLong(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.UNDO_SCORE), mGameView.game.lastScore);
        mGameView.game.canUndo = PreferencesUtils.getBoolean(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.CAN_UNDO), mGameView.game.canUndo);
        mGameView.game.gameState = PreferencesUtils.getInt(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.GAME_STATE), mGameView.game.gameState);
        mGameView.game.lastGameState = PreferencesUtils.getInt(context, Extra.Key.getKey(mSquareCountX, mSquareCountY, Extra.Key.UNDO_GAME_STATE), mGameView.game.lastGameState);

    }

    private String getTileKey(int xx, int yy) {
        return mSquareCountX + "_" + mSquareCountY + "_" + xx + "_" + yy;
    }

    private String getUndoTileKey(int xx, int yy) {
        return Extra.Key.UNDO_GRID + "_" + mSquareCountX + "_" + mSquareCountY + "_" + xx + "_" + yy;
    }
}
