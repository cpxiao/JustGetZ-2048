package com.cpxiao.gamelib.mode.common;

import android.graphics.Point;
import android.graphics.RectF;

/**
 * @author cpxiao on 2017/09/08.
 */

public final class SpriteControl {
    /**
     * @param x x
     * @param y y
     * @return 是否点中精灵
     */
    public static boolean isClicked(Sprite sprite, float x, float y) {
        RectF rectF = sprite.getCollideRectF();
        return x >= rectF.left && x <= rectF.right && y >= rectF.top && y <= rectF.bottom;
    }

    /**
     * @param sprite1 精灵
     * @param sprite2 精灵
     * @return 碰撞点
     */
    public static Point getCollidePointByTwoRectFSprite(Sprite sprite1, Sprite sprite2) {
        Point point = null;
        RectF rectF1 = sprite1.getSpriteRectF();
        RectF rectF2 = sprite2.getSpriteRectF();
        RectF rectF = new RectF();
        boolean isIntersect = rectF.setIntersect(rectF1, rectF2);
        if (isIntersect) {
            point = new Point(Math.round(rectF.centerX()), Math.round(rectF.centerY()));
        }
        return point;
    }


    /**
     * @param sprite1 精灵
     * @param sprite2 精灵
     * @return 两个矩形精灵是否发生碰撞
     */
    public static boolean isTwoRectFSpriteCollided(Sprite sprite1, Sprite sprite2) {
        if (sprite1 == null || sprite2 == null) {
            return false;
        }
        RectF rectF1 = sprite1.getCollideRectF();
        RectF rectF2 = sprite2.getCollideRectF();
        RectF rectF = new RectF();
        return rectF.setIntersect(rectF1, rectF2);
    }

    /**
     * @param sprite1 精灵
     * @param sprite2 精灵
     * @return 两个圆形精灵是否发生碰撞
     */
    public static float getTwoCircleSpriteDistance(Sprite sprite1, Sprite sprite2) {
        if (sprite1 == null || sprite2 == null) {
            return -1;
        }
        float cX1 = sprite1.getCenterX();
        float cY1 = sprite1.getCenterY();

        float cX2 = sprite2.getCenterX();
        float cY2 = sprite2.getCenterY();

        return (float) Math.sqrt(Math.pow(cX1 - cX2, 2) + Math.pow(cY1 - cY2, 2));
    }

    /**
     * @param sprite1 精灵
     * @param sprite2 精灵
     * @return 两个圆形精灵是否发生碰撞
     */
    public static boolean isTwoCircleSpriteCollided(Sprite sprite1, Sprite sprite2) {
        float distance = getTwoCircleSpriteDistance(sprite1, sprite2);
        float r1 = 0.5F * sprite1.getWidth();
        float r2 = 0.5F * sprite2.getWidth();

        return distance >= 0 && distance <= (r1 + r2);
    }

    /**
     * 根据向量计算角度
     *
     * @param vectorX
     * @param vectorY
     * @return 返回0到360
     */
    public static float getAngleByVector(float vectorX, float vectorY) {
        double angle = Math.atan2(vectorY, vectorX);//范围为(-π, π)
        if (angle < 0) {
            angle += Math.PI * 2;
        }
        //angle 范围为(0, 2π), 将弧度转化为角度
        return (float) Math.toDegrees(angle);
    }

}
