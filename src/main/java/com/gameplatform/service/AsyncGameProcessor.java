package com.gameplatform.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.gameplatform.entity.Game;
import com.gameplatform.entity.GameStatus;
import com.gameplatform.repository.GameRepository;

import jakarta.transaction.Transactional;

@Service
public class AsyncGameProcessor {
	private static final Logger logger = LoggerFactory.getLogger(AsyncGameProcessor.class);
	private final GameRepository gameRepository;
	private final R2Service r2Service;
	public AsyncGameProcessor(GameRepository gameRepository, R2Service r2Service)
	{
		this.gameRepository = gameRepository;
		this.r2Service = r2Service;
		
	}
	@Async
	@Transactional
	public void processGameUpload(Long gameId)
	{
		logger.info("Starting async processing for game Id:{}", gameId);
		Game game  = gameRepository.findById(gameId)
						.orElseThrow(()-> new RuntimeException("Game not found"));
		try {
			if (game.getGameStatus() == GameStatus.PROCESSING
					 || game.getGameStatus() == GameStatus.PUBLISHED) {
					    return;
					}
			game.setGameStatus(GameStatus.PROCESSING);
			game.setErrorMessage(null);
			gameRepository.save(game);
			logger.info("Game {} status is set to PROCEESING", gameId);
			String zipKey = "uploads/"+gameId+"/game.zip";
			//wait a bit and check if game exists
			if (!r2Service.exists(zipKey)) {
			    throw new RuntimeException("Uploaded file not found");
			}
			
			logger.info("file found, starting unzip process for game {}", gameId);
			//unzip and upload all files
			String gameFolder= "games/"+gameId+"/";
			r2Service.unzipAndUpload(zipKey, gameFolder);
			logger.info("Files are extracted and uploaded for game {}", gameId);
			
			//set the game url
			String gameUrl = r2Service.getPublicUrl(gameFolder+"index.html");
			game.setGameUrl(gameUrl);
			game.setGameStatus(GameStatus.PUBLISHED);
			
			//cleanup zip file
			r2Service.delete(zipKey);
			logger.info("Cleanup completed for game {}", gameId);
			gameRepository.save(game);
			logger.info("Game {} successfully published at {}", gameId, gameUrl);
			
		}catch (Exception e) {
			logger.error("Failed to process game {}: {}",gameId, e.getMessage(), e);
			game.setGameStatus(GameStatus.FAILED);
			game.setErrorMessage( e.getMessage());
			gameRepository.save(game);
		}
	}
}
