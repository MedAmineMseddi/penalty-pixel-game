package com.mygame.client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Very small UI overlay controller for screens:
 * Landing -> Player select -> Game -> End
 * IMPROVEMENT: Updated End screen to show scores.
 */
public class UIController {

    private final StackPane root;
    private final VBox landingPane;
    private final VBox selectPane;
    private final VBox endPane;
    private final Label endScoreLabel; // New field to update score dynamically

    public UIController() {
        root = new StackPane();
        root.setPickOnBounds(false); // allow canvas events through

        landingPane = buildLanding();
        selectPane = buildSelect();
        endPane = buildEnd();
        endScoreLabel = (Label) endPane.getChildren().get(1); // Get the dynamically updated label

        root.getChildren().addAll(landingPane);
    }

    private VBox buildLanding() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);
        box.setTranslateY(80);
        Label title = new Label("Penalty Pixel Game");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        Button start = new Button("Start (Local)");
        start.setOnAction(e -> hideAll()); // Start game immediately for local test
        Button quit = new Button("Quit");
        quit.setOnAction(e -> System.exit(0));
        box.getChildren().addAll(title, start, quit);
        return box;
    }

    private VBox buildSelect() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);
        box.setTranslateY(80);
        Label l = new Label("Player Select: (P1 choose Striker -> Press SPACE)");
        Button p1Striker = new Button("P1 Striker (Press)");
        p1Striker.setOnAction(e -> hideAll());
        box.getChildren().addAll(l, p1Striker);
        box.setVisible(false);
        return box;
    }

    private VBox buildEnd() {
        VBox box = new VBox(8);
        box.setAlignment(Pos.CENTER);
        box.setTranslateY(0);
        Label l = new Label("Game Over");
        l.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        Label score = new Label("P1: 0 - P2: 0"); // Placeholder for dynamic score
        Button replay = new Button("Replay");
        replay.setOnAction(e -> System.exit(0)); // Simple exit for now
        box.getChildren().addAll(l, score, replay);
        box.setVisible(false);
        return box;
    }

    public StackPane getRoot() {
        return root;
    }

    public void showSelect() {
        root.getChildren().clear();
        root.getChildren().add(selectPane);
        selectPane.setVisible(true);
    }

    public void showLanding() {
        root.getChildren().clear();
        root.getChildren().add(landingPane);
        landingPane.setVisible(true);
    }

    /**
     * Shows the end screen with final scores.
     */
    public void showEnd(int p1Score, int p2Score) {
        endScoreLabel.setText(String.format("P1: %d - P2: %d", p1Score, p2Score));
        root.getChildren().clear();
        root.getChildren().add(endPane);
        endPane.setVisible(true);
    }

    public void hideAll() {
        root.getChildren().clear();
    }

    public void updateUI(GameState state) {
        // Since we now render status directly on the canvas (RenderSystem), this can remain empty.
    }
}