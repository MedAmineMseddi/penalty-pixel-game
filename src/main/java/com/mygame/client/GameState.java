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
        // tick players and ball
        p1.tick();
        p2.tick();
        ball.tick();

        // Very simple local turn handling: when kicker confirms, resolve shot
        Player kicker = (currentKickerId == 1) ? p1 : p2;
        Player keeper = (currentKickerId == 1) ? p2 : p1;

        if (awaitingInput) {
            // choose direction with left/right and power with up/down
            boolean confirm = (kicker.getPlayerId() == 1) ? input.p1Confirm() : input.p2Confirm();
            boolean left = (kicker.getPlayerId() == 1) ? input.p1Left() : input.p2Left();
            boolean right = (kicker.getPlayerId() == 1) ? input.p1Right() : input.p2Right();
            double power = 0.5; // default power
            if (kicker.getPlayerId() == 1) {
                if (input.p1PowerUp()) power = 1.0;
                if (input.p1PowerDown()) power = 0.2;
            } else {
                if (input.p2PowerUp()) power = 1.0;
                if (input.p2PowerDown()) power = 0.2;
            }
            // simple direction selection
            double angle = 90; // center
            if (left) angle = 60;
            if (right) angle = 120;
            if (confirm) {
                // perform kick
                ball.setPosition(kicker.getX(), kicker.getY() - 20);
                ball.kick(power, angle);
                awaitingInput = false;
            }
        } else {
            // after ball moves, evaluate if it crossed keeper line (simple)
            if (!ball.isMoving()) {
                // decide goal or save by random approximating keeper reaction
                boolean goal = Math.random() < 0.6; // placeholder probability
                if (goal) {
                    kicker.addScore();
                } else {
                    keeper.addScore(); // not realistic but placeholder
                }
                // alternate kicker and reset
                currentKickerId = (currentKickerId == 1) ? 2 : 1;
                awaitingInput = true;
                round++;
                // end condition
                if (round>10) {
                    ui.showEnd();
                }
            }
        }
    }

    // getters
    public Player getPlayer1() { return p1; }
    public Player getPlayer2() { return p2; }
    public Ball getBall() { return ball; }
}
