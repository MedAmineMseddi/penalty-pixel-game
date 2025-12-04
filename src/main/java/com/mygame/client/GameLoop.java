package com.mygame.client;

import javafx.animation.AnimationTimer;

/**
 * Fixed-step game loop using AnimationTimer for render and tick accumulation.
 * Tick rate: 60 updates per second (60 UPS).
 */
public class GameLoop {

    private static final double TICKS_PER_SEC = 60.0;
    private static final double NANOS_PER_TICK = 1e9 / TICKS_PER_SEC;

    private final GameState state;
    private final RenderSystem render;
    private final InputSystem input;
    private final UIController ui;

    private AnimationTimer timer;
    private boolean running = false;

    public GameLoop(GameState state, RenderSystem render, InputSystem input, UIController ui) {
        this.state = state;
        this.render = render;
        this.input = input;
        this.ui = ui;
    }

    public void start() {
        if (running) return;
        running = true;
        timer = new AnimationTimer() {
            private long last = 0;
            private double accumulator = 0;

            @Override
            public void handle(long now) {
                if (last == 0) last = now;
                long elapsed = now - last;
                last = now;
                accumulator += elapsed;

                // Tick updates at fixed step
                while (accumulator >= NANOS_PER_TICK) {
                    tick();
                    accumulator -= NANOS_PER_TICK;
                }

                // Render with interpolation (not used heavily here)
                render.render(state);
                ui.updateUI(state);
            }
        };
        timer.start();
    }

    public void stop() {
        if (!running) return;
        running = false;
        if (timer != null) timer.stop();
    }

    private void tick() {
        // Handle input & update state
        state.update(input);
    }
}
