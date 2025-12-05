package com.mygame.client;

import com.mygame.jms.JmsMatchClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class GameWindow extends Application {

    public static final int WIDTH = 1280;
    public static final int HEIGHT = 720;

    private Canvas canvas;
    private GameLoop gameLoop;
    
    // Add reference to JMS Client
    private JmsMatchClient jmsClient;

    @Override
    public void start(Stage stage) {
        canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // --- JMS INITIALIZATION ---
        // Connects to ActiveMQ immediately on startup
        jmsClient = new JmsMatchClient();
        
        // Example usage: Send a request immediately (or hook this up to UI buttons)
        jmsClient.sendCreateMatchRequest("PlayerOne"); 
        // --------------------------

        RenderSystem render = new RenderSystem(gc);
        InputSystem input = new InputSystem();
        UIController ui = new UIController();
        GameState state = new GameState(render, ui);

        StackPane root = new StackPane(canvas, ui.getRoot());
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        input.attach(scene);

        Player p1 = new Player("Player 1", 1, true);
        Player p2 = new Player("Player 2", 2, false);
        Ball ball = new Ball(WIDTH / 2.0, HEIGHT - 140);

        state.initLocalGame(p1, p2, ball);

        gameLoop = new GameLoop(state, render, input, ui);
        gameLoop.start();

        stage.setTitle("Penalty Pixel Game (JMS Enabled)");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        stage.setOnCloseRequest(e -> {
            gameLoop.stop();
            // Close JMS connection cleanly
            if (jmsClient != null) jmsClient.close();
            System.exit(0);
        });
    }
}