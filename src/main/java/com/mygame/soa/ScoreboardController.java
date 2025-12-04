package com.mygame.soa;

import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple Spring Boot REST controller for scoreboard persistence.
 *
 * To run: include Spring Boot dependencies and run a SpringApplication (not included here).
 * Endpoints:
 *  POST /matchResult  { matchId, players, winner, score }
 *  GET  /leaderboard
 */
@RestController
@RequestMapping("/api")
public class ScoreboardController {

    // In-memory simple store: player -> wins
    private final Map<String, Integer> wins = new ConcurrentHashMap<>();

    @PostMapping("/matchResult")
    public Map<String, Object> postResult(@RequestBody MatchResult result) {
        // increment winner count
        if (result.getWinner() != null) {
            wins.merge(result.getWinner(), 1, Integer::sum);
        }
        Map<String, Object> resp = new HashMap<>();
        resp.put("status", "ok");
        resp.put("winner", result.getWinner());
        return resp;
    }

    @GetMapping("/leaderboard")
    public List<Map.Entry<String,Integer>> leaderboard() {
        List<Map.Entry<String,Integer>> list = new ArrayList<>(wins.entrySet());
        list.sort((a,b) -> Integer.compare(b.getValue(), a.getValue()));
        return list;
    }

    public static class MatchResult {
        private String matchId;
        private List<String> players;
        private String winner;
        private String score;

        // getters/setters
        public String getMatchId() { return matchId; }
        public void setMatchId(String matchId) { this.matchId = matchId; }
        public List<String> getPlayers() { return players; }
        public void setPlayers(List<String> players) { this.players = players; }
        public String getWinner() { return winner; }
        public void setWinner(String winner) { this.winner = winner; }
        public String getScore() { return score; }
        public void setScore(String score) { this.score = score; }
    }
}
