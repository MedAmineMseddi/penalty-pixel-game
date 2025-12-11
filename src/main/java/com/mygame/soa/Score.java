package com.mygame.soa;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String playerName;

    @Column(nullable = false)
    private int goals;

    private LocalDateTime playedAt;

    public Score() {
        this.playedAt = LocalDateTime.now();
    }

    public Score(String playerName, int goals) {
        this.playerName = playerName;
        this.goals = goals;
        this.playedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    public int getGoals() { return goals; }
    public void setGoals(int goals) { this.goals = goals; }
    public LocalDateTime getPlayedAt() { return playedAt; }
}