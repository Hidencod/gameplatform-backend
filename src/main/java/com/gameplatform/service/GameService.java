package com.gameplatform.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.gameplatform.dto.CreateGameRequest;
import com.gameplatform.dto.GameResponse;
import com.gameplatform.dto.GameStatusResponse;
import com.gameplatform.dto.UploadUrlResponse;
import com.gameplatform.entity.Game;
import com.gameplatform.entity.GameStatus;
import com.gameplatform.repository.GameRepository;

import jakarta.transaction.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class GameService {
	private final GameRepository gameRepository;
	private final R2Service r2Service;
	private final AsyncGameProcessor asyncProcessor;
	public GameService(GameRepository gameRepository, R2Service r2Service, AsyncGameProcessor asyncProcessor)
	{
		this.gameRepository = gameRepository;
		this.r2Service = r2Service;
		this.asyncProcessor = asyncProcessor;
	}
	public Page<GameResponse> getAllGames(int page, int size)
	{
		Pageable pageable = PageRequest.of(page, size,Sort.by("id").descending());
		return gameRepository.findAll(pageable).map(game->convertToResponse(game));
	}
	
	public GameResponse getGameById(Long id)
	{
		Game game = gameRepository.findById(id).orElseThrow(()->new RuntimeException("Game not found"));
		return convertToResponse(game);
	}
	public List<GameResponse> getGamesByCategory(String category)
	{
		return gameRepository.findByCategory(category).stream()
				.map(this::convertToResponse).collect(Collectors.toList());
	}
	public List<GameResponse> getPopularGames()
	{
		return gameRepository.findAllByOrderByPlayCountDesc().stream().limit(50).map(this::convertToResponse).collect(Collectors.toList());
	}
	@Transactional
	public GameResponse createGame(CreateGameRequest request)
	{
		Game game  = new Game();
		game.setName(request.getName());
		game.setDescription(request.getDescription());
		game.setCategory(request.getCategory());
		game.setTags(request.getTags());
		game.setGameStatus(GameStatus.DRAFT);
		Game saved = gameRepository.save(game);
		return convertToResponse(saved);
	}
	@Transactional
	public UploadUrlResponse initiateUpload(Long gameId)
	{
		Game game  = gameRepository.findById(gameId)
				.orElseThrow(()-> new RuntimeException("Game not found"));
		if(game.getGameStatus() == GameStatus.PUBLISHED)
		{
			throw new RuntimeException("Game already published. Create a new version instead");
		}
		//set game status to upload
		game.setGameStatus(GameStatus.UPLOADING);
		gameRepository.save(game);
		//Generate presigned url
		String key = "uploads/" + gameId + "/game.zip";
		String uploadUrl = r2Service.generatePresignedPutUrl(key, Duration.ofMinutes(10));
		return new UploadUrlResponse(uploadUrl);
	}
	
	public GameStatusResponse getGameStatus(Long gameId)
	{
		Game game = gameRepository.findById(gameId)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

		return new GameStatusResponse(
				game.getId(),
	            game.getGameStatus(),
	            game.getGameUrl(),
	            game.getErrorMessage()
				);
	}
	public void deleteGame(Long id) {
	    Game game = gameRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Game not found"));

	    if (game.getGameUrl() != null) {
	        try {
	            String gameFolder = "games/" + id + "/";
	            r2Service.deleteFolder(gameFolder);
	        } catch (Exception e) {
	            // log error but DO NOT block DB deletion
	            e.printStackTrace();
	        }
	    }

	    gameRepository.deleteById(id);
	}

	public UploadUrlResponse generateUploadUrl(Long gameId)
	{
		Game game = gameRepository.findById(gameId)
				.orElseThrow(()-> new RuntimeException("Game not found"));
		
		 String key = "uploads/" + gameId + "game.zip";
		 
		 String uploadUrl = r2Service.generatePresignedPutUrl(key, Duration.ofMinutes(10));
		 
		 return new UploadUrlResponse(uploadUrl);
	}
	private GameResponse convertToResponse(Game game)
	{
		return new GameResponse(
				game.getId(),
				game.getName(),
				game.getDescription(),
				game.getGameUrl(),
				game.getThumbnailUrl(),
				game.getCategory(),
				game.getTags(),
				game.getPlayCount(),
				game.getAverageRating(),
				game.getCreatedAt(),
				game.getGameStatus()
		);
	}
}
