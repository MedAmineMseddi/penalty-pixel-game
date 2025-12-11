package com.mygame.soa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scores")
@CrossOrigin(origins = "*") // Allow Game Client to access from any port
public class ScoreController {

    @Autowired
    private ScoreRepository repository;

    // CREATE: POST /api/scores
    @PostMapping
    public Score createScore(@RequestBody Score score) {
        return repository.save(score);
    }

    // READ ALL: GET /api/scores
    @GetMapping
    public List<Score> getAllScores() {
        return repository.findAll();
    }
    
    // READ LEADERBOARD: GET /api/scores/top
    @GetMapping("/top")
    public List<Score> getTopScores() {
        return repository.findTop5ByOrderByGoalsDesc();
    }

    // READ ONE: GET /api/scores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Score> getScoreById(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // UPDATE: PUT /api/scores/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Score> updateScore(@PathVariable Long id, @RequestBody Score scoreDetails) {
        return repository.findById(id).map(score -> {
            score.setGoals(scoreDetails.getGoals());
            score.setPlayerName(scoreDetails.getPlayerName());
            return ResponseEntity.ok(repository.save(score));
        }).orElse(ResponseEntity.notFound().build());
    }

    // DELETE: DELETE /api/scores/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteScore(@PathVariable Long id) {
        return repository.findById(id).map(score -> {
            repository.delete(score);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}