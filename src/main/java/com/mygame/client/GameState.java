package com.mygame.client;

/**
 * Holds the state of a local match (players, ball, current shot, turn count)
 */
public class GameState {

    private Player p1, p2;
    private Ball ball;
    private final RenderSystem render;
    private final UIController ui;

    private int currentKickerId = 1;
    private int round = 0;
    private boolean awaitingInput = true;

    public GameState(RenderSystem render, UIController ui) {
        this.render = render;
        this.ui = ui;
    }

    public void initLocalGame(Player p1, Player p2, Ball ball) {
        this.p1 = p1;
        this.p2 = p2;
        this.ball = ball;
        // place keeper and striker appropriately
        p1.setStriker(true);
        p2.setStriker(false);
        this.currentKickerId = 1;
        this.round = 1;
    }


    public void update(InputSystem input) {
        p1.tick();
        p2.tick();
        ball.tick();

        Player striker = (currentKickerId == 1) ? p1 : p2;
        Player keeper = (currentKickerId == 1) ? p2 : p1;

        if (awaitingInput) {
            // === Handle striker input ===
            Direction dir = Direction.NONE;
            if (striker.getPlayerId() == 1) {
                if (input.p1Left()) dir = Direction.LEFT;
                if (input.p1Right()) dir = Direction.RIGHT;
                if (input.p1PowerUp() || input.p1PowerDown()) dir = Direction.CENTER;
                if (input.p1Confirm()) striker.setChosenDirection(dir);
            } else {
                if (input.p2Left()) dir = Direction.LEFT;
                if (input.p2Right()) dir = Direction.RIGHT;
                if (input.p2PowerUp() || input.p2PowerDown()) dir = Direction.CENTER;
                if (input.p2Confirm()) striker.setChosenDirection(dir);
            }

            // === Handle keeper input ===
            Direction keeperDir = Direction.NONE;
            if (keeper.getPlayerId() == 1) {
                if (input.p1Left()) keeperDir = Direction.LEFT;
                if (input.p1Right()) keeperDir = Direction.RIGHT;
                if (input.p1PowerUp() || input.p1PowerDown()) keeperDir = Direction.CENTER;
                if (input.p1Confirm()) keeper.setChosenDirection(keeperDir);
            } else {
                if (input.p2Left()) keeperDir = Direction.LEFT;
                if (input.p2Right()) keeperDir = Direction.RIGHT;
                if (input.p2PowerUp() || input.p2PowerDown()) keeperDir = Direction.CENTER;
                if (input.p2Confirm()) keeper.setChosenDirection(keeperDir);
            }

            // Once both players have chosen direction, resolve the shot
            if (striker.getChosenDirection() != Direction.NONE && keeper.getChosenDirection() != Direction.NONE) {
                resolveShot(striker, keeper);
                awaitingInput = false;  // ball will move
            }

        } else {
            // Animate ball toward direction (simplified arcade movement)
            if (!ball.isMoving()) {
                // After ball stops, reset for next turn
                currentKickerId = (currentKickerId == 1) ? 2 : 1;
                striker.setChosenDirection(Direction.NONE);
                keeper.setChosenDirection(Direction.NONE);
                awaitingInput = true;
                round++;
                if (round > 10) ui.showEnd();
            }
        }
    }

    private void resolveShot(Player striker, Player keeper) {
        Direction s = striker.getChosenDirection();
        Direction k = keeper.getChosenDirection();

        System.out.println(striker.getName() + " shot: " + s + " | Keeper dived: " + k);

        if (s != k) {
            // Goal
            striker.addScore();
            System.out.println("GOAL!");
        } else {
            // Saved
            System.out.println("Saved by keeper!");
        }

        // Move ball visually toward direction
        double targetX = GameWindow.WIDTH / 2.0;
        switch (s) {
            case LEFT -> targetX -= 200;
            case RIGHT -> targetX += 200;
            case CENTER -> targetX = GameWindow.WIDTH / 2.0;
        }
        ball.kick(1.0, mapDirectionToAngle(s));
    }

    private double mapDirectionToAngle(Direction dir) {
        return switch (dir) {
            case LEFT -> 60;
            case RIGHT -> 120;
            case CENTER -> 90;
            default -> 90;
        };
    }

    // getters
    public Player getPlayer1() { return p1; }
    public Player getPlayer2() { return p2; }
    public Ball getBall() { return ball; }
}
