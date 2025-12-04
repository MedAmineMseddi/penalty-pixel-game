package com.mygame.client;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Very small UI overlay controller for screens:
 * Landing -> Player select -> Game -> End
 */
public class UIController {

    private final StackPane root;
    private final VBox landingPane;
    private final VBox selectPane;
    private final VBox endPane;

    public UIController() {
        root = new StackPane();
        root.setPickOnBounds(false); // allow canvas events through

        landingPane = buildLanding();
        selectPane = buildSelect();
        endPane = buildEnd();

        root.getChildren().addAll(landingPane);
    }

    private VBox buildLanding() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.TOP_CENTER);
        box.setTranslateY(80);
        Label title = new Label("Penalty Pixel Game");
        Button start = new Button("Start (Local)");
        start.setOnAction(e -> showSelect());
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
        Button replay = new Button("Replay");
        replay.setOnAction(e -> hideAll());
        box.getChildren().addAll(l, replay);
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

    public void showEnd() {
        root.getChildren().clear();
        root.getChildren().add(endPane);
        endPane.setVisible(true);
    }

    public void hideAll() {
        root.getChildren().clear();
    }

    public void updateUI(GameState state) {
        // Could display timers, instructions or highlight whose turn it is
    }
}
