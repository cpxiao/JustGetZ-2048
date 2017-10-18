package com.cpxiao.gamelib.mode.common.imps;

/**
 * @author cpxiao on 2017/09/08.
 *         Velocity速度；Speed速率
 *         日常生活中，速度和速率几乎是同义的。
 *         然而在物理学中，速度和速率是两个不同的概念。
 *         速度是矢量，具有大小和方向；速率则纯粹指物体运动的快慢，是标量，没有方向。
 */

public interface Move {

    void setSpeedX(float speedX);

    float getSpeedX();

    void setSpeedY(float speedY);

    float getSpeedY();

    public void moveBy(float offsetX, float offsetY);

    public void moveTo(float x, float y);

    public void centerTo(float centerX, float centerY);

}
