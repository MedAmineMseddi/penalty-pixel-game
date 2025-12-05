package com.mygame.client;

import javafx.scene.image.Image;

/**
 * Simple player model with animation placeholder.
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

    public Player(String name, int id, boolean striker) {
        this.name = name;
        this.playerId = id;
        this.isStriker = striker;

        // default positions (striker near bottom, keeper near goal)
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
    }

    public Image getCurrentFrame() {
        // For simplicity return null (RenderSystem will draw placeholder) or use striker frames directly
        // Hook: later connect to RenderSystem to fetch appropriate frame
        return null;
    }

    public String getName() { return name; }
    public int getScore() { return score; }
    public void addScore() { score++; }
    public boolean isStriker() { return isStriker; }
    public void setStriker(boolean s) { isStriker = s; }

    public double getX() { return x; }
    public double getY() { return y; }

    public int getPlayerId() { return playerId; }

    private Direction chosenDirection = Direction.NONE;

    public Direction getChosenDirection() { return chosenDirection; }
    public void setChosenDirection(Direction dir) { this.chosenDirection = dir; }

}
