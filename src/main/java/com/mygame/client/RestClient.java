package com.mygame.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class RestClient {

    private static final String API_URL = "http://localhost:8080/api/scores";
    private final HttpClient client;

    public RestClient() {
        this.client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    // Fire and forget (Async)
    public void submitScore(String playerName, int goals) {
        // Construct JSON manually to avoid Jackson dependency in Client
        String json = String.format("{\"playerName\":\"%s\", \"goals\":%d}", playerName, goals);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(response -> System.out.println("Score Saved: " + response))
                .exceptionally(e -> {
                    System.err.println("Failed to save score: " + e.getMessage());
                    return null;
                });
    }
    
    // In a real full impl, you would also add methods to GET the leaderboard
}