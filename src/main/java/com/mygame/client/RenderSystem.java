package com.mygame.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.InputStream;

/**
 * Responsible for loading sprite sheets, slicing frames and rendering objects.
 */
public class RenderSystem {

    private final GraphicsContext gc;

    private Image strikerSheet;
    private Image keeperSheet;
    private Image[] strikerFrames;
    private Image[] keeperFrames;

    public static final int FRAME_W = 48;
    public static final int FRAME_H = 48;

    public RenderSystem(GraphicsContext gc) {
        this.gc = gc;
        loadSprites();
    }

    private void loadSprites() {
        try {
            InputStream s1 = getClass().getResourceAsStream("/sprites/striker_sheet.png");
            InputStream s2 = getClass().getResourceAsStream("/sprites/keeper_sheet.png");
            strikerSheet = (s1 != null) ? new Image(s1) : null;
            keeperSheet = (s2 != null) ? new Image(s2) : null;
        } catch (Exception e) {
            strikerSheet = null;
            keeperSheet = null;
        }
        strikerFrames = slice(strikerSheet);
        keeperFrames = slice(keeperSheet);
    }

    private Image[] slice(Image sheet) {
        if (sheet == null) return new Image[0];
        int cols = (int) (sheet.getWidth() / FRAME_W);
        int rows = (int) (sheet.getHeight() / FRAME_H);
        Image[] frames = new Image[cols * rows];
        int i = 0;
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < cols; x++) {
                frames[i++] = new WritableImageView(sheet, x * FRAME_W, y * FRAME_H, FRAME_W, FRAME_H).toImage();
            }
        }
        return frames;
    }

    // Rendering helpers (draw background, players, ball, UI)
    public void renderBackground() {
        // simple green field background
        gc.setFill(Color.web("#2a7a2a"));
        gc.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
        // penalty area lines
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(GameWindow.WIDTH / 2 - 220, GameWindow.HEIGHT - 420, 440, 300);
    }

    public void render(GameState state) {
        // clear & draw
        renderBackground();

        // Draw ball
        Ball ball = state.getBall();
        if (ball != null) {
            gc.drawImage(ball.getCurrentFrame(), ball.getX() - Ball.BALL_SIZE / 2, ball.getY() - Ball.BALL_SIZE / 2,
                    Ball.BALL_SIZE, Ball.BALL_SIZE);
        }

        // Draw players
        Player p1 = state.getPlayer1();
        Player p2 = state.getPlayer2();
        if (p1 != null) drawPlayer(p1);
        if (p2 != null) drawPlayer(p2);

        // Draw scores / HUD
        gc.setFill(Color.WHITE);
        gc.fillText(p1.getName() + ": " + p1.getScore(), 20, 30);
        gc.fillText(p2.getName() + ": " + p2.getScore(), GameWindow.WIDTH - 120, 30);
    }

    private void drawPlayer(Player p) {
        Image frame = p.getCurrentFrame();
        double x = p.getX();
        double y = p.getY();
        if (frame != null) {
            gc.drawImage(frame, x - FRAME_W / 2, y - FRAME_H / 2, FRAME_W * 2, FRAME_H * 2);
        } else {
            // placeholder rectangle
            gc.setFill(Color.DARKGRAY);
            gc.fillRect(x - 24, y - 48, 48, 96);
        }
    }
}
