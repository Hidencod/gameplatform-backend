package com.gameplatform.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.gameplatform.service.AsyncGameProcessor;
import com.gameplatform.service.GameService;

import jakarta.validation.Valid;

import java.util.List;
import com.gameplatform.dto.*;
@RestController
@RequestMapping("/api/games")
public class GamesController {

	private final GameService gameService;
	private final AsyncGameProcessor asyncGameProcessor;
	public GamesController(GameService gameService, AsyncGameProcessor asyncGameProcessor)
	{
		this.gameService = gameService;
		this.asyncGameProcessor = asyncGameProcessor;
	}
	@GetMapping()
	public ResponseEntity<Page<GameResponse>> getAllGames(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size
			)
	{
		return ResponseEntity.ok(gameService.getAllGames(page,size));
	}
	@GetMapping("/{id}")
	public ResponseEntity<GameResponse> getGameById(@PathVariable Long id)
	{
		return ResponseEntity.ok(gameService.getGameById(id));
	}
	@GetMapping("/caetegory/{category}")
	public ResponseEntity<List<GameResponse>> getGamesByCategory(@PathVariable String category)
	{
		return ResponseEntity.ok(gameService.getGamesByCategory(category));
	}
	@GetMapping("/popular")
	public ResponseEntity<List<GameResponse>> getPopularGames()
	{
		return ResponseEntity.ok(gameService.getPopularGames());
	}
	//admin create game
	@PostMapping()
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<GameResponse> createGame(@Valid @RequestBody CreateGameRequest request)
	{
		return ResponseEntity.ok(gameService.createGame(request));
	}
	@PostMapping("/{id}/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UploadUrlResponse> initiateUpload(@PathVariable Long id) {
	    return ResponseEntity.ok(gameService.initiateUpload(id));
	}
	@PostMapping("/{id}/upload/complete")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Void> uploadComplete(@PathVariable Long id) {
		
		System.out.println("Hello");
		asyncGameProcessor.processGameUpload(id);
	    return ResponseEntity.accepted().build();
	}

	// check upload/processing status
	@GetMapping("/{id}/status")
	public ResponseEntity<GameStatusResponse> getGameStatus(@PathVariable Long id)
	{
		return ResponseEntity.ok(gameService.getGameStatus(id));
	}
	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteGame(@PathVariable Long id)
	{
		gameService.deleteGame(id);
		return ResponseEntity.ok("Game deleted Successfully!");
	}
}
