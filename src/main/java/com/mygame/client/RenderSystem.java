package com.mygame.client;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

public class RenderSystem {

    private final GraphicsContext gc;

    private Image fieldBackground;
    private Image ballSheet;

    private Image strikerSheet;
    private Image keeperSheet;
    
    // Arrays of sliced frames
    private Image[] strikerFrames;
    private Image[] keeperFrames;
    private Image[] ballFrames;

    // Assumed frame dimensions 
    public static final int FRAME_W = 32; 
    public static final int FRAME_H = 48;
    
    public static final int BALL_FRAME_W = 32;
    public static final int BALL_FRAME_H = 32;


    public RenderSystem(GraphicsContext gc) {
        this.gc = gc;
        loadSprites();
    }

    // --- 1. SPRITE LOADING AND SLICING ---

    private void loadSprites() {
        try {
            // Load all sprite sheets
            InputStream isField = getClass().getResourceAsStream("/Field.png");
            InputStream isBall = getClass().getResourceAsStream("/Ball.png");
            InputStream isStriker = getClass().getResourceAsStream("/striker_sheet.png"); 
            InputStream isKeeper = getClass().getResourceAsStream("/keeper_sheet.png"); 

            fieldBackground = (isField != null) ? new Image(isField) : null;
            ballSheet = (isBall != null) ? new Image(isBall) : null;
            strikerSheet = (isStriker != null) ? new Image(isStriker) : null;
            keeperSheet = (isKeeper != null) ? new Image(isKeeper) : null;

            // Slice the Player sheets (32x48)
            strikerFrames = slice(strikerSheet, FRAME_W, FRAME_H);
            keeperFrames = slice(keeperSheet, FRAME_W, FRAME_H);
            
            // Slice the Ball sheet (32x32)
            ballFrames = slice(ballSheet, BALL_FRAME_W, BALL_FRAME_H);

        } catch (Exception e) {
            System.err.println("Error loading or slicing sprites: " + e.getMessage());
            e.printStackTrace();
            // Ensure arrays are initialized to empty arrays on failure, not null
            strikerFrames = new Image[0]; 
            keeperFrames = new Image[0];
            ballFrames = new Image[0];
        }
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

    // --- 2. MAIN RENDERING LOOP ---
    
    public void render(GameState state) {
        renderBackground();

        // Determine players and their roles
        Player p1 = state.getPlayer1();
        Player p2 = state.getPlayer2();
        
        // Use the correct frame array based on the player's role (striker or keeper)
        if (p1 != null) {
            Image[] frames = p1.isStriker() ? strikerFrames : keeperFrames;
            drawPlayer(p1, frames);
        }
        
        if (p2 != null) {
            Image[] frames = p2.isStriker() ? strikerFrames : keeperFrames;
            drawPlayer(p2, frames);
        }
        
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


    // --- 3. DRAWING HELPER METHODS ---

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
    
    private void drawPlayer(Player p, Image[] frames) {
        
        // 1. Declare the frame variable first, initializing it to null
        Image frame = null; 

        // 2. Safely calculate the index
        int index = p.getAnimationIndex();
        
        // 3. Assign the frame using an if statement (Handles frames == null or empty array)
        if (frames != null && frames.length > 0 && index >= 0 && index < frames.length) {
            frame = frames[index];
        }
        
        double x = p.getX();
        double y = p.getY();
        
        // Calculate scaled dimensions (2x size)
        final double DRAW_W = FRAME_W * 2; // 64
        final double DRAW_H = FRAME_H * 2; // 96
        
        // Calculate centered draw position
        final double DRAW_X = x - (DRAW_W / 2); 
        final double DRAW_Y = y - DRAW_H;
        
        // Draw player sprite/placeholder
        if (frame != null) {
            // Draw the frame using the calculated centered coordinates and scaled size
            gc.drawImage(frame, DRAW_X, DRAW_Y, DRAW_W, DRAW_H); 
        } else {
            // Fallback placeholder rectangle (32x96 centered)
            gc.setFill(p.isStriker() ? Color.DARKBLUE : Color.RED);
            gc.fillRect(x - 16, y - 96, 32, 96); 
        }
        
        // Draw selection highlight (adjusted for 36x100 box around the 64x96 sprite)
        if (p.getChosenDirection() != Direction.NONE) {
            gc.setStroke(Color.YELLOW);
            gc.setLineWidth(3);
            gc.strokeRect(DRAW_X - 2, DRAW_Y - 2, DRAW_W + 4, DRAW_H + 4); 
        }
    }
    
    private void drawBall(Ball ball, Image[] frames) {
        
        // 1. Declare the frame variable first
        Image frame = null;

        // 2. Safely calculate the index and assign the frame
        int index = ball.getAnimIndex();
        
        if (frames.length > 0 && index >= 0 && index < frames.length) {
            frame = frames[index];
        }
        
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
    
    /**
     * Draws a specific frame from the keeper sheet based on (i, j) indices.
     * NOTE: This assumes the keeper is Player 2 in this simplified scenario, 
     * but the logic should be used on the player designated as the keeper.
     */
    public void drawKeeperFrame(int i, int j, Player keeper) {
        // 1. Calculate Source Coordinates (Top-left corner of the desired frame on the sheet)
        double sourceX = j * FRAME_W; 
        double sourceY = i * FRAME_H;

        // 2. Define Drawn Dimensions (These should match the scaling used in drawPlayer)
        final double DRAW_W = FRAME_W * 2; // 64
        final double DRAW_H = FRAME_H * 2; // 96
        
        // 3. Define Destination Coordinates (Centered on keeper's position)
        double x = keeper.getX();
        double y = keeper.getY();
        final double DRAW_X = x - (DRAW_W / 2); // Center X
        final double DRAW_Y = y - DRAW_H;      // Place feet at Y

        // 4. Draw the specific section of the sheet
        if (keeperSheet != null) {
            gc.drawImage(keeperSheet, 
                         sourceX, sourceY, FRAME_W, FRAME_H, // Source rectangle (unscaled)
                         DRAW_X, DRAW_Y, DRAW_W, DRAW_H);    // Destination rectangle (2x scaled)
        } else {
            // Fallback placeholder rectangle 
            gc.setFill(Color.RED);
            gc.fillRect(x - 16, y - 96, 32, 96);
        }
    }
    
    // --- 4. HUD AND INDICATOR METHODS ---
    
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
        gc.fillText(text, x - text.length() * 4, y - 100);
    }

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
}