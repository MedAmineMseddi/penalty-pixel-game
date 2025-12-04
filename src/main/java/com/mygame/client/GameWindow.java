package com.mygame.client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Main JavaFX window. Creates canvas and boots GameLoop.
 */
public class GameWindow extends Application {

    public static final int WIDTH = 1280;  // you said 1680x1050, but use 1280x720 for development
    public static final int HEIGHT = 720;

    private Canvas canvas;
    private GameLoop gameLoop;

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Systems
        RenderSystem render = new RenderSystem(gc);
        InputSystem input = new InputSystem();
        UIController ui = new UIController();
        GameState state = new GameState(render, ui);

        // Attach input to scene
        StackPane root = new StackPane(canvas, ui.getRoot());
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        input.attach(scene);

        // Create two players and ball
        Player p1 = new Player("Player 1", 1, true);
        Player p2 = new Player("Player 2", 2, false);
        Ball ball = new Ball(WIDTH / 2.0, HEIGHT - 140);

        state.initLocalGame(p1, p2, ball);

        // Start loop
        gameLoop = new GameLoop(state, render, input, ui);
        gameLoop.start();

        stage.setTitle("Penalty Pixel Game (Local)");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Clean up on close
        stage.setOnCloseRequest(e -> {
            gameLoop.stop();
        });
    }
}
