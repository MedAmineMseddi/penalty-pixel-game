package com.mygame.client;

import javafx.scene.image.Image;

/**
 * Simple player model with animation placeholder.
 * IMPROVEMENT: Added state tracking for power and direction selection AND animation control.
 */
public class Player {

    private final String name;
    private final int playerId;
    private boolean isStriker;
    private int score = 0;

    // Position in screen space
    private double x;
    private double y;

    // === ANIMATION CONTROL FIELDS ===
    
    // Animation Constants based on your 8-frame wide sprite sheets
    public static final int ANIM_IDLE_FRAMES = 2;          // Frames 0-1 for Striker/Keeper Idle
    public static final int ANIM_KICK_START_INDEX = 8;     // Start of Striker's Kick sequence (Row 1, Frame 0)
    public static final int ANIM_KICK_FRAMES = 8;          // Full kick sequence (Frames 8-15)
    public static final int ANIM_DIVE_START_INDEX = 3;     // Start of Keeper's dive sequence (Ready Crouch)
    public static final int ANIM_DIVE_FRAMES = 5;          // Frames 3-7 (Ready, Dive 1, 2, 3, 4)
    public static final int ANIM_DIVE_RECOVER_INDEX = 25;  // Index for a recovery frame
    
    // Simple animation index (index within the current animation sequence, 0 to maxAnimFrames-1)
    private int animationIndex = 0;
    private long animTicker = 0;
    private int maxAnimFrames = ANIM_IDLE_FRAMES; // Default to idle length
    
    // The starting index in the full sprite sheet array for the current animation
    private int animationStartOffset = 0; 

    // === UX/GAMEPLAY FIELDS ===
    private Direction directionSelection = Direction.NONE; 
    private double currentPower = 0.5; 
    private boolean isPowerIncreasing = true;
    private Direction chosenDirection = Direction.NONE;

    public Player(String name, int id, boolean striker) {
        this.name = name;
        this.playerId = id;
        this.isStriker = striker;

        if (striker) {
            x = GameWindow.WIDTH / 2.0;
            y = GameWindow.HEIGHT - 120;
        } else {
            x = GameWindow.WIDTH / 2.0;
            y = 160;
        }
    }

    public void tick() {
        animTicker++;
        // Cycle every 20 ticks (slower idle animation)
        if (animTicker % 20 == 0) { 
            animationIndex = (animationIndex + 1) % maxAnimFrames;
        }
        
        // Power Bar Animation (Only for Striker when awaiting input)
        if (isStriker && chosenDirection == Direction.NONE) {
            if (isPowerIncreasing) {
                currentPower += 0.02;
                if (currentPower >= 1.0) {
                    currentPower = 1.0;
                    isPowerIncreasing = false;
                }
            } else {
                currentPower -= 0.02;
                if (currentPower <= 0.0) {
                    currentPower = 0.0;
                    isPowerIncreasing = true;
                }
            }
        }
    }

    public void resetTurn() {
        this.chosenDirection = Direction.NONE;
        this.directionSelection = Direction.NONE;
        this.currentPower = 0.5;
        this.isPowerIncreasing = true;
        // Reset player to idle animation state
        setIdle();
    }

    // === ANIMATION CONTROL METHODS ===

    /**
     * Calculates the final frame index in the full sprite sheet array.
     * Index = (Starting Offset) + (Current Frame Index)
     */
    public int getAnimationIndex() { 
        return animationStartOffset + (animationIndex % maxAnimFrames); 
    }
    
    /**
     * Sets the player's animation back to the default idle state.
     */
    public void setIdle() {
        startAnimation(0, ANIM_IDLE_FRAMES);
    }

    /**
     * Starts a new animation sequence (e.g., kick or dive).
     * @param startIndex The index in the sprite sheet where the animation begins.
     * @param frameCount The number of frames in the sequence.
     */
    public void startAnimation(int startIndex, int frameCount) {
        this.animationStartOffset = startIndex;
        this.maxAnimFrames = frameCount;
        this.animationIndex = 0; // Always start new animation from frame 0
    }
    
    // Method to change the animation being played (Deprecated: Use startAnimation instead)
    public void setMaxAnimFrames(int count) { 
         this.maxAnimFrames = count; 
         this.animationIndex = 0; // Reset index when changing animation
    }

    // === UX/GAMEPLAY GETTERS/SETTERS ===
    public Direction getDirectionSelection() { return directionSelection; }
    public void setDirectionSelection(Direction dir) { this.directionSelection = dir; }
    public double getCurrentPower() { return currentPower; }
    public void setCurrentPower(double power) { this.currentPower = power; }
    public String getName() { return name; }
    public int getScore() { return score; }
    public void addScore() { score++; }
    public boolean isStriker() { return isStriker; }
    public void setStriker(boolean s) { 
        isStriker = s;
        // Reset position based on role
        if (s) {
             y = GameWindow.HEIGHT - 120;
        } else {
             y = 160;
        }
        setIdle(); // Ensure animation resets when role changes
    }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getPlayerId() { return playerId; }
    public Direction getChosenDirection() { return chosenDirection; }
    public void setChosenDirection(Direction dir) { this.chosenDirection = dir; }

    // Removed unused getCurrentFrame()
}