package com.mygame.client;

import javafx.scene.image.Image;

/**
 * Basic ball model with trivial animation handling.
 * IMPROVEMENT: Unified movement start into the 'shoot' method.
 */
public class Ball {

    // Overall speed multiplier
    public static final double BASE_SPEED = 15.0; 

    // Angle parameters (in degrees, used for the left/right deviation)
    // The amount of horizontal deviation for a 'Left' or 'Right' shot.
    public static final double MAX_DEVIATION_DEGREES = 20.0; // Angle from the center line (e.g., 20 degrees)

    public static final int BALL_SIZE = 100; // Size of the ball when rendered

    private double x, y;
    private double vx, vy;
    
    // --- FIX 1: Renamed 'moving' to 'isMoving' to match the shoot method ---
    private boolean isMoving = false; 
    
    private int maxAnimFrames = 8; // Assuming 8 frames for ball spin
    
    private int animIndex = 0;
    private long ticker = 0;

    public Ball(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public void tick() {
        if (!isMoving) { // Use the consistent 'isMoving' field
            vx = vy = 0; // Ensure speed is zero when not moving
            return;
        }
        
        x += vx;
        y += vy;
        
        // simple friction
        vx *= 0.995;
        vy *= 0.995;

        // stop condition (off bounds)
        if (y < 50 || y > GameWindow.HEIGHT + 200 || x < -200 || x > GameWindow.WIDTH + 200) {
            isMoving = false; // Use the consistent 'isMoving' field
            vx = vy = 0;
        }
        
        // animation (Ball spins faster when moving)
        ticker++;
        if (ticker % 2 == 0) { // Cycle frames every 2 ticks for fast spin
            animIndex = (animIndex + 1) % maxAnimFrames;
        }
    }

    /**
     * Calculates the ball's X and Y velocity based on player input 
     * using the standard geometric angle convention (90 deg = straight up).
     * @param chosenDirection The direction of the shot (LEFT, RIGHT, CENTER).
     * @param power The power (speed) of the shot (0.0 to 1.0).
     */
    public void shoot(Direction chosenDirection, double power) {
        
        // 1. Calculate final speed based on power
        double speed = BASE_SPEED * power;
        
        // 2. Define the Base Angle: 90 degrees (Straight up)
        double angleDegrees = 90.0;
        
        // 3. Determine Deviation from the 90-degree line
        switch (chosenDirection) {
            case LEFT:
                // 90 + 20 = 110 degrees (Moves left)
                angleDegrees += MAX_DEVIATION_DEGREES; 
                break;
            case RIGHT:
                // 90 - 20 = 70 degrees (Moves right)
                angleDegrees -= MAX_DEVIATION_DEGREES;
                break;
            case CENTER:
            case NONE:
                // angleDegrees remains 90.0
                break;
        }
        
        // Convert degrees to radians
        double angleRadians = Math.toRadians(angleDegrees);
        
        // 4. Calculate velocity components using standard formulas
        
        // X-Velocity (Horizontal component): V * cos(theta) for standard angle
        this.vx = speed * Math.cos(angleRadians); 
        
        // Y-Velocity (Vertical component): V * sin(theta) for standard angle
        // CRITICAL FIX: NEGATE Y-VELOCITY to move UP the screen (lower Y values).
        this.vy = -(speed * Math.sin(angleRadians)); 
        
        // Kick-start animation and state change
        this.isMoving = true;
    }
    
    // --- Getters and Setters ---
    public double getX() { return x; }
    public double getY() { return y; }

    public void setPosition(double x, double y) { 
        this.x = x; 
        this.y = y; 
    }
    
    public boolean isMoving() { return isMoving; }
    public void setMoving(boolean moving) { this.isMoving = moving; }
    
    public int getAnimIndex() { return animIndex; }
}