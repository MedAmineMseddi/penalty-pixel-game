package com.mygame.client;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

import java.util.HashSet;
import java.util.Set;

/**
 * Handles keyboard input for two local players.
 * Player 1 keys: A (left), D (right), W (power up), S (power down), SPACE (confirm)
 * Player 2 keys: LEFT, RIGHT, UP, DOWN, ENTER
 */
public class InputSystem {

    private final Set<KeyCode> pressed = new HashSet<>();

    public void attach(Scene scene) {
        scene.setOnKeyPressed(e -> pressed.add(e.getCode()));
        scene.setOnKeyReleased(e -> pressed.remove(e.getCode()));
    }

    public boolean isPressed(KeyCode k) {
        return pressed.contains(k);
    }

    // Convenience methods:
    public boolean p1Left() { return isPressed(KeyCode.A); }
    public boolean p1Right() { return isPressed(KeyCode.D); }
    public boolean p1PowerUp() { return isPressed(KeyCode.W); }
    public boolean p1PowerDown() { return isPressed(KeyCode.S); }
    public boolean p1Confirm() { return isPressed(KeyCode.SPACE); }

    public boolean p2Left() { return isPressed(KeyCode.LEFT); }
    public boolean p2Right() { return isPressed(KeyCode.RIGHT); }
    public boolean p2PowerUp() { return isPressed(KeyCode.UP); }
    public boolean p2PowerDown() { return isPressed(KeyCode.DOWN); }
    public boolean p2Confirm() { return isPressed(KeyCode.ENTER); }
}
