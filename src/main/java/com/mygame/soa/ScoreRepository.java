package com.mygame.soa;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<Score, Long> {
    // Custom query to get top 5 scores
    List<Score> findTop5ByOrderByGoalsDesc();
}