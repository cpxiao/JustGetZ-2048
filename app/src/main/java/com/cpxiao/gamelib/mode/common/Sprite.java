package com.cpxiao.gamelib.mode.common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.cpxiao.AppConfig;
import com.cpxiao.gamelib.mode.common.imps.Base;
import com.cpxiao.gamelib.mode.common.imps.Life;
import com.cpxiao.gamelib.mode.common.imps.Move;

/**
 * @author cpxiao on 2017/7/16.
 * @version cpxiao on 2017/9/8.删除无用方法
 */

public class Sprite implements Base, Move, Life {

    protected boolean DEBUG = AppConfig.DEBUG;
    protected String TAG = getClass().getSimpleName();

    /**
     * 坐标
     */
    private float x, y;

    /**
     * 宽高
     */
    private float width, height;
    /**
     * 速度
     */
    private float mSpeedX;//横向速度，向右为正。
    private float mSpeedY;//纵向速度，向下为正。

    //精灵可移动的范围矩形，根据精灵矩形判断边界
    private RectF mMovingRangeRectF = null;

    /**
     * 生命
     */
    private int mLife;

    private Bitmap bitmap = null;

    /**
     * 精灵矩形
     */
    private RectF mSpriteRectF = new RectF();

    /**
     * 碰撞矩形，根据精灵矩形的百分比计算
     */
    private float mCollideRectFPercentW = 1, mCollideRectFPercentH = 1;
    private RectF mCollideRectF = new RectF();


    private boolean visible = true;//是否可见
    private boolean destroyed = false;//是否已销毁
    private long mFrame = 0;//绘制的次数

    private Sprite() {
    }

    protected Sprite(Build build) {
        x = build.x;
        y = build.y;
        width = build.w;
        height = build.h;
        bitmap = build.bitmap;
        mSpeedX = build.speedX;
        mSpeedY = build.speedY;
        mLife = build.life;
        mMovingRangeRectF = build.movingRangeRectF;
    }

    @Override
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public void setY(float y) {
        this.y = y;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public void setWidth(float width) {
        this.width = width;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getHeight() {
        return height;
    }


    @Override
    public void setSpeedX(float speedX) {
        mSpeedX = speedX;
    }

    @Override
    public float getSpeedX() {
        return mSpeedX;
    }

    @Override
    public void setSpeedY(float speedY) {
        mSpeedY = speedY;
    }


    @Override
    public float getSpeedY() {
        return mSpeedY;
    }

    @Override
    public void moveBy(float offsetX, float offsetY) {
        x += offsetX;
        y += offsetY;
    }

    @Override
    public void moveTo(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void centerTo(float centerX, float centerY) {
        float w = getWidth();
        float h = getHeight();
        x = centerX - 0.5F * w;
        y = centerY - 0.5F * h;
    }

    @Override
    public void setLife(int life) {
        this.mLife = life;
    }

    @Override
    public int getLife() {
        return mLife;
    }

    @Override
    public void addLife(int life) {
        this.mLife += life;
    }

    @Override
    public void deleteLife(int life) {
        this.mLife -= life;
    }

    public void destroy() {
        destroyed = true;
    }

    public boolean isDestroyed() {
        return destroyed;
    }


    public void setFrame(long frame) {
        mFrame = frame;
    }

    public long getFrame() {
        return mFrame;
    }

    public void setVisibility(boolean visible) {
        this.visible = visible;
    }

    public boolean getVisibility() {
        return visible;
    }

    public float getCenterX() {
        return x + 0.5F * width;
    }

    public float getCenterY() {
        return y + 0.5F * height;
    }

    public RectF getSpriteRectF() {
        mSpriteRectF.left = x;
        mSpriteRectF.top = y;
        mSpriteRectF.right = mSpriteRectF.left + getWidth();
        mSpriteRectF.bottom = mSpriteRectF.top + getHeight();
        return mSpriteRectF;
    }

    public void setCollideRectFPercent(float percent) {
        mCollideRectFPercentW = percent;
        mCollideRectFPercentH = percent;
    }

    public void setCollideRectFPercent(float percentW, float percentH) {
        mCollideRectFPercentW = percentW;
        mCollideRectFPercentH = percentH;
    }

    public RectF getCollideRectF() {
        mCollideRectF.left = getCenterX() - 0.5F * mCollideRectFPercentW * getWidth();
        mCollideRectF.top = getCenterY() - 0.5F * mCollideRectFPercentH * getHeight();
        mCollideRectF.right = getCenterX() + 0.5F * mCollideRectFPercentW * getWidth();
        mCollideRectF.bottom = getCenterY() + 0.5F * mCollideRectFPercentH * getHeight();
        return mCollideRectF;
    }

    public void setMovingRangeRectFL(float l) {
        if (mMovingRangeRectF != null) {
            mMovingRangeRectF.left = l;
        }
    }

    public void setMovingRangeRectFR(float r) {
        if (mMovingRangeRectF != null) {
            mMovingRangeRectF.right = r;
        }
    }

    public void setMovingRangeRectFT(float t) {
        if (mMovingRangeRectF != null) {
            mMovingRangeRectF.top = t;
        }
    }

    public void setMovingRangeRectFB(float b) {
        if (mMovingRangeRectF != null) {
            mMovingRangeRectF.bottom = b;
        }
    }

    public RectF getMovingRangeRectF() {
        return mMovingRangeRectF;
    }

    public void draw(Canvas canvas, Paint paint) {
        mFrame++;
        beforeDraw(canvas, paint);
        onDraw(canvas, paint);
        afterDraw(canvas, paint);
    }

    protected void beforeDraw(Canvas canvas, Paint paint) {
        if (mLife <= 0) {
            destroy();
        }
        if (!isDestroyed()) {
            //移动speed像素
            moveBy(mSpeedX, mSpeedY);

            //判断移动范围
            if (mMovingRangeRectF != null) {
                RectF rectF = getSpriteRectF();
                if (rectF.left <= mMovingRangeRectF.left) {
                    setX(mMovingRangeRectF.left);
                }
                if (rectF.right >= mMovingRangeRectF.right) {
                    setX(mMovingRangeRectF.right - rectF.width());
                }
                if (rectF.top <= mMovingRangeRectF.top) {
                    setY(mMovingRangeRectF.top);
                }
                if (rectF.bottom >= mMovingRangeRectF.bottom) {
                    setY(mMovingRangeRectF.bottom - rectF.height());
                }
            }
        }
    }

    public void onDraw(Canvas canvas, Paint paint) {

    }

    protected void afterDraw(Canvas canvas, Paint paint) {

    }


    public static class Build {
        private Bitmap bitmap = null;
        private float x;
        private float y;
        private float w;
        private float h;

        private float speedX = 0, speedY = 0;

        private int life = 1;
        private RectF movingRangeRectF = null;


        public Build setBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
            return this;
        }

        public Build setX(float x) {
            this.x = x;
            return this;
        }

        public Build setY(float y) {
            this.y = y;
            return this;
        }

        public Build setW(float w) {
            this.w = w;
            return this;
        }

        public Build setH(float h) {
            this.h = h;
            return this;
        }

        public Build centerTo(float centerX, float centerY) {
            x = centerX - 0.5F * w;
            y = centerY - 0.5F * h;
            return this;
        }

        public Build setSpeedX(float speedX) {
            this.speedX = speedX;
            return this;
        }

        public Build setSpeedY(float speedY) {
            this.speedY = speedY;
            return this;
        }

        public Build setLife(int life) {
            this.life = life;
            return this;
        }

        public Build setMovingRangeRectF(RectF rectF) {
            this.movingRangeRectF = rectF;
            return this;
        }


        public Sprite build() {
            return new Sprite(this);
        }
    }

}
