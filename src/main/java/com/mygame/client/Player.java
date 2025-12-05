package com.mygame.client;

import javafx.scene.image.Image;

/**
 * Simple player model with animation placeholder.
 * IMPROVEMENT: Added state tracking for power and direction selection.
 */
public class Player {

    private final String name;
    private final int playerId;
    private boolean isStriker;
    private int score = 0;

    // Position in screen space
    private double x;
    private double y;

    // Simple animation index (index into RenderSystem frames)
    private int animationIndex = 0;
    private long animTicker = 0;

    // === NEW UX/GAMEPLAY FIELDS ===
    private Direction directionSelection = Direction.NONE; // Direction currently being hovered/selected
    private double currentPower = 0.5; // Power from 0.0 to 1.0
    private boolean isPowerIncreasing = true;
    // =============================

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
        if (animTicker % 10 == 0) {
            animationIndex = (animationIndex + 1) % 4;
        }
        
        // Power Bar Animation (Only for Striker when awaiting input)
        if (isStriker && chosenDirection != Direction.NONE) {
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
    }

    public Image getCurrentFrame() {
        return null;
    }

    // === NEW UX/GAMEPLAY GETTERS/SETTERS ===
    public Direction getDirectionSelection() { return directionSelection; }
    public void setDirectionSelection(Direction dir) { this.directionSelection = dir; }

    public double getCurrentPower() { return currentPower; }
    public void setCurrentPower(double power) { this.currentPower = power; }

    // ======================================

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
    }

    public double getX() { return x; }
    public double getY() { return y; }

    public int getPlayerId() { return playerId; }

    // Inside Player.java, add this getter:
    public int getAnimationIndex() { return animationIndex; }

    public Direction getChosenDirection() { return chosenDirection; }
    public void setChosenDirection(Direction dir) { this.chosenDirection = dir; }

}