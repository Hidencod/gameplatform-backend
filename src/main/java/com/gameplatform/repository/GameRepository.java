package com.gameplatform.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import com.gameplatform.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
	List<Game> findByCategory(String category);
	List<Game> findByTagsContaining(String tag);
	List<Game> findAllByOrderByPlayCountDesc();
	
}
