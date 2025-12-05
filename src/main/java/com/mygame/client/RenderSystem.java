package com.mygame.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView; // Added for slicing
import javafx.scene.image.WritableImage; // Added for slicing
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class RenderSystem {

    private final GraphicsContext gc;

    private Image fieldBackground; // NEW: Field Background
    private Image ballSheet;       // NEW: Ball Sheet

    private Image strikerSheet;
    private Image keeperSheet;
    
    // Arrays of sliced frames
    private Image[] strikerFrames;
    private Image[] keeperFrames;
    private Image[] ballFrames;    // NEW: Ball Frames

    // Assumed frame dimensions (adjust if your sprites are different)
    public static final int FRAME_W = 48;
    public static final int FRAME_H = 48;
    public static final int BALL_FRAME_W = 32;
    public static final int BALL_FRAME_H = 32;


    public RenderSystem(GraphicsContext gc) {
        this.gc = gc;
        loadSprites();
    }

    private void loadSprites() {
        try {
            // Load all sprite sheets
            InputStream isField = getClass().getResourceAsStream("/Field.png");
            InputStream isBall = getClass().getResourceAsStream("/Ball.png");
            InputStream isStriker = getClass().getResourceAsStream("/keeper_sheet.png");
            InputStream isKeeper = getClass().getResourceAsStream("/stricker_sheet.png"); // Check if these two names are swapped in your files

            fieldBackground = (isField != null) ? new Image(isField) : null;
            ballSheet = (isBall != null) ? new Image(isBall) : null;
            strikerSheet = (isStriker != null) ? new Image(isStriker) : null;
            keeperSheet = (isKeeper != null) ? new Image(isKeeper) : null;

        } catch (Exception e) {
            System.err.println("Error loading sprites: " + e.getMessage());
            // Set all to null if loading fails
            fieldBackground = strikerSheet = keeperSheet = ballSheet = null;
        }
        
        // Slice the sprite sheets into frame arrays
        strikerFrames = slice(strikerSheet, FRAME_W, FRAME_H);
        keeperFrames = slice(keeperSheet, FRAME_W, FRAME_H);
        ballFrames = slice(ballSheet, BALL_FRAME_W, BALL_FRAME_H); // Assuming ball frames are 32x32
    }

    /**
     * Slices a sprite sheet into individual Image frames.
     */
    private Image[] slice(Image sheet, int frameW, int frameH) {
        if (sheet == null) return new Image[0];
        
        int sheetWidth = (int) sheet.getWidth();
        int sheetHeight = (int) sheet.getHeight();
        
        // Calculate the number of columns and rows
        int cols = sheetWidth / frameW;
        int rows = sheetHeight / frameH;
        
        Image[] frames = new Image[cols * rows];
        int i = 0;
        
        for (int y = 0; y < rows; y++) {
             for (int x = 0; x < cols; x++) {
                 // Create a WritableImage by copying a section of the original sheet
                 WritableImage writableImage = new WritableImage(sheet.getPixelReader(), 
                                                                 x * frameW, y * frameH, 
                                                                 frameW, frameH);
                 frames[i++] = writableImage;
             }
        }
        return frames;
    }

    // Paste this method into RenderSystem.java
    private void drawInputIndicators(GameState state) {
        Player striker = (state.getCurrentKickerId() == 1) ? state.getPlayer1() : state.getPlayer2();
        Player keeper = (state.getCurrentKickerId() == 1) ? state.getPlayer2() : state.getPlayer1();
        
        // 1. Striker Direction Indicator (While selecting)
        if (striker.getChosenDirection() == Direction.NONE && striker.getDirectionSelection() != Direction.NONE) {
            drawDirectionIndicator(striker, Color.YELLOW, striker.getDirectionSelection());
        }
        
        // 2. Keeper Direction Indicator (While selecting)
        if (keeper.getChosenDirection() == Direction.NONE && keeper.getDirectionSelection() != Direction.NONE) {
            drawDirectionIndicator(keeper, Color.CYAN, keeper.getDirectionSelection());
        }
        
        // 3. Striker Power Bar (After direction is chosen)
        if (state.isAwaitingPowerConfirmation()) {
            drawPowerBar(striker);
        }
    }

    // Paste this method into RenderSystem.java
    private void drawDirectionIndicator(Player p, Color color, Direction dir) {
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        gc.setFill(color);
        
        String text = switch (dir) {
            case LEFT -> "<< LEFT";
            case CENTER -> "CENTER >>";
            case RIGHT -> "RIGHT >>";
            case NONE -> "";
        };
        
        double x = p.getX();
        double y = p.getY();
        
        // Place indicator above player
        // NOTE: This calculation is rough; adjust the offset (-100) if needed.
        gc.fillText(text, x - text.length() * 4, y - 100);
    }

    // Paste this method into RenderSystem.java
    private void drawPowerBar(Player striker) {
        double barX = striker.getX() - 100;
        double barY = striker.getY() + 120;
        double barWidth = 200;
        double barHeight = 20;
        
        double power = striker.getCurrentPower();
        double fillWidth = barWidth * power;
        
        // Background
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeRect(barX, barY, barWidth, barHeight);
        
        // Fill (Green to Red based on power)
        Color fill = Color.hsb(power * 120, 1.0, 1.0); // Hue shift for color change
        gc.setFill(fill);
        gc.fillRect(barX, barY, fillWidth, barHeight);
        
        // Label
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.NORMAL, 14));
        gc.fillText("POWER (" + (int)(power * 100) + "%)", barX + 5, barY - 5);
    }


    // Paste this method into RenderSystem.java
    private void drawHud(GameState state) {
        Player p1 = state.getPlayer1();
        Player p2 = state.getPlayer2();
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Scores
        gc.setFill(Color.WHITE);
        gc.fillText(p1.getName() + " Score: " + p1.getScore(), 20, 30);
        gc.fillText(p2.getName() + " Score: " + p2.getScore(), GameWindow.WIDTH - 200, 30);
        
        // Round Info
        gc.fillText("Round: " + state.getRound() + " / 10", GameWindow.WIDTH / 2 - 50, 30);

        // Game Status
        String statusText = "";
        Color statusColor = Color.WHITE;
        
        if (state.getBall().isMoving()) {
            statusText = "SHOT IN PROGRESS...";
            statusColor = Color.YELLOW;
        } else {
            Player striker = (state.getCurrentKickerId() == 1) ? p1 : p2;
            Player keeper = (state.getCurrentKickerId() == 1) ? p2 : p1;
            
            if (striker.getChosenDirection() == Direction.NONE) {
                statusText = "STRIKER (" + striker.getName() + "): Choose Direction!";
                statusColor = Color.LIGHTGREEN;
            } else if (keeper.getChosenDirection() == Direction.NONE) {
                statusText = "KEEPER (" + keeper.getName() + "): Choose Dive Direction!";
                statusColor = Color.CYAN;
            } else if (state.isAwaitingPowerConfirmation()) {
                statusText = "STRIKER (" + striker.getName() + "): Confirm Power!";
                statusColor = Color.ORANGE;
            } else {
                statusText = "Awaiting Shot Resolution...";
            }
        }
        
        gc.setFill(statusColor);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        gc.fillText(statusText, GameWindow.WIDTH / 2.0 - gc.getFont().getSize() * 4, 80);
    }

    public void renderBackground() {
        // Use the Field.png background if loaded
        if (fieldBackground != null) {
            // Scale and draw the background to fit the window
            gc.drawImage(fieldBackground, 0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
        } else {
            // Fallback: draw simple green field (your existing code)
            gc.setFill(Color.web("#2a7a2a"));
            gc.fillRect(0, 0, GameWindow.WIDTH, GameWindow.HEIGHT);
        }
        
        // Draw pitch lines (Goal area and posts)
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        // Goal area
        gc.strokeRect(GameWindow.WIDTH / 2 - 220, GameWindow.HEIGHT - 420, 440, 300);
        // Goal post outline (simplified)
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(GameWindow.WIDTH / 2 - 100, 100, 200, 10);
    }
    
    // ... (rest of the render method remains the same)
    
    public void render(GameState state) {
        renderBackground();

        // Draw players
        Player p1 = state.getPlayer1();
        Player p2 = state.getPlayer2();
        if (p1 != null) drawPlayer(p1, (p1.isStriker() ? strikerFrames : keeperFrames));
        if (p2 != null) drawPlayer(p2, (p2.isStriker() ? keeperFrames : strikerFrames)); // Roles are dynamic, so check which array to use
        
        // Draw input indicators (MUST be drawn over players)
        if (state.getBall().isMoving() == false) {
             drawInputIndicators(state);
        }

        // Draw ball (Pass the ball frames array)
        Ball ball = state.getBall();
        if (ball != null) {
            drawBall(ball, ballFrames);
        }
        
        // Draw scores / HUD (Last, so it's on top)
        drawHud(state);
    }
    
    // Updated drawPlayer signature to accept frames
    private void drawPlayer(Player p, Image[] frames) {
        // Get the current frame based on the player's animation index
        Image frame = (frames.length > 0) ? frames[p.getAnimationIndex() % frames.length] : null;
        
        double x = p.getX();
        double y = p.getY();
        
        // Draw player sprite/placeholder
        if (frame != null) {
            // Scale up the 48x48 sprite to 96x96 for better visibility
            gc.drawImage(frame, x - FRAME_W, y - FRAME_H, FRAME_W * 2, FRAME_H * 2); 
        } else {
            // placeholder rectangle
            gc.setFill(p.isStriker() ? Color.DARKBLUE : Color.RED);
            gc.fillRect(x - 24, y - 48, 48, 96);
        }
        
        // Draw selection highlight if committed (No change)
        if (p.getChosenDirection() != Direction.NONE) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeRect(x - 26, y - 50, 52, 100);
        }
    }
    
    // NEW drawBall method
    private void drawBall(Ball ball, Image[] frames) {
        Image frame = (frames.length > 0) ? frames[ball.getAnimIndex() % frames.length] : null;
        
        if (frame != null) {
             gc.drawImage(frame, 
                          ball.getX() - Ball.BALL_SIZE / 2, 
                          ball.getY() - Ball.BALL_SIZE / 2,
                          Ball.BALL_SIZE, Ball.BALL_SIZE);
        } else {
             // Placeholder circle for the ball
             gc.setFill(Color.RED);
             gc.fillOval(ball.getX() - Ball.BALL_SIZE / 2, ball.getY() - Ball.BALL_SIZE / 2, Ball.BALL_SIZE, Ball.BALL_SIZE);
        }
    }

    // ... (rest of the class remains the same)
}