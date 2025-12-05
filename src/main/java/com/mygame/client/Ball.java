package com.mygame.client;

import javafx.scene.image.Image;

/**
 * Basic ball model with trivial animation handling.
 */
public class Ball {

    public static final int BALL_SIZE = 32;

    private double x, y;
    private double vx, vy;
    private boolean moving = false;

    private Image[] frames; // optional

    private int animIndex = 0;
    private long ticker = 0;

    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void kick(double power, double angleDegrees) {
        // Basic ballistic for arcade feel
        double speed = 6 + power * 8;
        double rad = Math.toRadians(angleDegrees - 90); // adjust coordinate system
        vx = Math.cos(rad) * speed;
        vy = Math.sin(rad) * speed * -1; // screen Y up negative in game coordinates
        moving = true;
    }

    public void tick() {
        if (!moving) return;
        x += vx;
        y += vy;
        // simple friction
        vx *= 0.995;
        vy *= 0.995;

        // stop condition (off bounds)
        if (y < 50 || y > GameWindow.HEIGHT + 200 || x < -200 || x > GameWindow.WIDTH + 200) {
            moving = false;
            vx = vy = 0;
        }
        // animation
        ticker++;
        if (ticker % 5 == 0) animIndex = (animIndex + 1) % 8;
    }

    public Image getCurrentFrame() {
        // if frames exist, return frames[animIndex], else null (render draws placeholder circle)
        if (frames != null && frames.length > 0) return frames[animIndex % frames.length];
        return null;
    }

    public double getX() { return x; }
    public double getY() { return y; }

    // Inside Ball.java, add this getter:
    public int getAnimIndex() { return animIndex; }

    public void setPosition(double x, double y) { this.x = x; this.y = y; }
    public boolean isMoving() { return moving; }
    
    // === FIX: ADDED MISSING SETTER ===
    public void setMoving(boolean moving) { this.moving = moving; }
    // =================================
}