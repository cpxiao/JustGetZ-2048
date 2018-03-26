package com.cpxiao.justgetz2048.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.cpxiao.gamelib.mode.common.Sprite;
import com.cpxiao.gamelib.views.BaseSurfaceViewFPS;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author cpxiao on 2017/10/9.
 */

public abstract class BaseGameView extends BaseSurfaceViewFPS {

    protected ConcurrentLinkedQueue<Sprite> mSpriteQueue = new ConcurrentLinkedQueue<>();

    public BaseGameView(Context context) {
        super(context);
    }

    public BaseGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BaseGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void drawCache() {
        drawSpriteQueue(mCanvasCache, mPaint);
    }

    private void drawSpriteQueue(Canvas canvas, Paint paint) {
        for (Sprite sprite : mSpriteQueue) {
            sprite.draw(canvas, paint);
        }
    }

    @Override
    protected void timingLogic() {
        removeDestroyedSprite();
    }

    private void removeDestroyedSprite() {
        for (Sprite sprite : mSpriteQueue) {
            if (sprite != null && sprite.isDestroyed()) {
                mSpriteQueue.remove(sprite);
            }
        }
    }
}
