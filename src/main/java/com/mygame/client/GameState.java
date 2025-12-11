package com.mygame.client;

/**
 * Holds the state of a local match (players, ball, current shot, turn count)
 * IMPROVEMENT: Simplified input reading by separating direction selection and confirmation.
 * FIX: Added player animation triggers and corrected the ball movement call.
 */
public class GameState {

    private Player p1, p2;
    private Ball ball;
    private final RenderSystem render;
    private final UIController ui;
    private final RestClient restClient = new RestClient();

    private int currentKickerId = 1;
    private int round = 0;
    private boolean awaitingInput = true;
    private boolean awaitingPowerConfirmation = false; // New state for Striker power choice

    public GameState(RenderSystem render, UIController ui) {
        this.render = render;
        this.ui = ui;
    }

    public void initLocalGame(Player p1, Player p2, Ball ball) {
        this.p1 = p1;
        this.p2 = p2;
        this.ball = ball;
        // Ensure roles are set correctly for the first round
        p1.setStriker(true);
        p2.setStriker(false);
        this.currentKickerId = 1;
        this.round = 1;
        this.awaitingInput = true;
        this.awaitingPowerConfirmation = false;
        resetBallPosition();
    }
    
    // Helper to reset the ball for the next shot
    private void resetBallPosition() {
        // Ensure the ball resets to the striker's starting position (or slightly in front)
        Player striker = (currentKickerId == 1) ? p1 : p2;
        ball.setPosition(striker.getX(), striker.getY()); 
        ball.setMoving(false);
    }


    public void update(InputSystem input) {
        p1.tick();
        p2.tick();
        ball.tick();

        Player striker = (currentKickerId == 1) ? p1 : p2;
        Player keeper = (currentKickerId == 1) ? p2 : p1;

        if (awaitingInput) {
            handleStrikerInput(striker, input);
            handleKeeperInput(keeper, input);
            
            // Check for Shot Phase transition
            if (striker.getChosenDirection() != Direction.NONE && !awaitingPowerConfirmation) {
                // Striker has chosen direction, now selecting power
                awaitingPowerConfirmation = true;
            }
            
            // Check for Shot Confirmation
            if (awaitingPowerConfirmation) {
                // Determine who is confirming power (Striker's keys)
                boolean strikerConfirmed = (striker.getPlayerId() == 1 && input.p1Confirm()) ||
                                           (striker.getPlayerId() == 2 && input.p2Confirm());
                                           
                if (strikerConfirmed && keeper.getChosenDirection() != Direction.NONE) {
                    // Both players have committed, resolve the shot
                    resolveShot(striker, keeper);
                    awaitingInput = false;      // Game enters animation phase
                    awaitingPowerConfirmation = false;
                }
            }

        } else {
            // Animate ball
            if (!ball.isMoving()) {
                // After ball stops, reset for next turn and swap roles
                currentKickerId = (currentKickerId == 1) ? 2 : 1;
                p1.setStriker(!p1.isStriker()); // Swap roles
                p2.setStriker(!p2.isStriker());
                
                // Reset states and animations
                p1.resetTurn();
                p2.resetTurn();
                
                resetBallPosition();
                
                awaitingInput = true;
                round++;
                if (round > 10) {
                    // Game Over Logic
                    ui.showEnd(p1.getScore(), p2.getScore());
                    
                    // SOA INTEGRATION: Save scores to Database via REST
                    // Only save non-zero scores or save both
                    System.out.println("Uploading scores to REST API...");
                    restClient.submitScore(p1.getName(), p1.getScore());
                    restClient.submitScore(p2.getName(), p2.getScore());
                }
            }
        }
    }
    
    private void handleStrikerInput(Player striker, InputSystem input) {
        int id = striker.getPlayerId();
        
        // 1. Direction Selection Phase
        if (striker.getChosenDirection() == Direction.NONE) {
            Direction dir = Direction.NONE;
            boolean confirmed = false;

            if (id == 1) {
                if (input.p1Left()) dir = Direction.LEFT;
                else if (input.p1Right()) dir = Direction.RIGHT;
                else if (input.p1PowerUp() || input.p1PowerDown()) dir = Direction.CENTER;
                if (input.p1Confirm()) confirmed = true;
            } else { // Player 2
                if (input.p2Left()) dir = Direction.LEFT;
                else if (input.p2Right()) dir = Direction.RIGHT;
                else if (input.p2PowerUp() || input.p2PowerDown()) dir = Direction.CENTER;
                if (input.p2Confirm()) confirmed = true;
            }
            
            // Update temporary selection for visualization
            if (dir != Direction.NONE) {
                striker.setDirectionSelection(dir);
            }
            
            // Confirm direction
            if (confirmed && striker.getDirectionSelection() != Direction.NONE) {
                striker.setChosenDirection(striker.getDirectionSelection());
            }
        }
        
        // 2. Power Selection Phase (Implicitly handled by Player.tick() and confirmation check in update())
    }

    private void handleKeeperInput(Player keeper, InputSystem input) {
        // Keeper only chooses a direction, and confirms it.
        if (keeper.getChosenDirection() == Direction.NONE) {
            Direction dir = Direction.NONE;
            boolean confirmed = false;
            
            int id = keeper.getPlayerId();

            if (id == 1) {
                if (input.p1Left()) dir = Direction.LEFT;
                else if (input.p1Right()) dir = Direction.RIGHT;
                else if (input.p1PowerUp() || input.p1PowerDown()) dir = Direction.CENTER;
                if (input.p1Confirm()) confirmed = true;
            } else { // Player 2
                if (input.p2Left()) dir = Direction.LEFT;
                else if (input.p2Right()) dir = Direction.RIGHT;
                else if (input.p2PowerUp() || input.p2PowerDown()) dir = Direction.CENTER;
                if (input.p2Confirm()) confirmed = true;
            }
            
            // Update temporary selection for visualization
            if (dir != Direction.NONE) {
                keeper.setDirectionSelection(dir);
            }
            
            // Confirm direction
            if (confirmed && keeper.getDirectionSelection() != Direction.NONE) {
                keeper.setChosenDirection(keeper.getDirectionSelection());
            }
        }
    }

    private void resolveShot(Player striker, Player keeper) {
        Direction sDir = striker.getChosenDirection();
        Direction kDir = keeper.getChosenDirection();
        double power = striker.getCurrentPower();

        System.out.println(striker.getName() + " shot: " + sDir + " at " + String.format("%.2f", power) + " power | Keeper dived: " + kDir);

        // 1. Trigger Animations
        striker.startAnimation(Player.ANIM_KICK_START_INDEX, Player.ANIM_KICK_FRAMES);
        keeper.startAnimation(Player.ANIM_DIVE_START_INDEX, Player.ANIM_DIVE_FRAMES);

        if (sDir != kDir) {
            // Goal 
            striker.addScore();
            System.out.println("GOAL! Score P1: " + p1.getScore() + ", P2: " + p2.getScore());
        } else {
            // Saved
            System.out.println("Saved by keeper!");
        }

        // 2. Start Ball Movement
        // This uses the improved logic from Ball.java to shoot up the screen (negative Y velocity)
        ball.shoot(sDir, power);
    }

    // Getters for RenderSystem/UIController
    public Player getPlayer1() { return p1; }
    public Player getPlayer2() { return p2; }
    public Ball getBall() { return ball; }
    public boolean isAwaitingPowerConfirmation() { return awaitingPowerConfirmation; }
    public int getCurrentKickerId() { return currentKickerId; }
    public int getRound() { return round; }
}