package com.gameplatform.dto;

import com.gameplatform.entity.GameStatus;

public class GameStatusResponse {
	private Long id;
	private GameStatus gameStatus;
	private String gameUrl;
	private String errorMessage;
	private Integer progressPercentage;
	public GameStatusResponse(Long id, GameStatus gameStatus, String gameUrl, String errorMessage)
	{
		this.id = id;
		this.gameStatus = gameStatus;
		this.gameUrl = gameUrl;
		this.errorMessage = errorMessage;
		
	}
	private Integer calculateProgress(GameStatus status)
	{
		return switch (status) {
		case DRAFT -> 0;
		case UPLOADING -> 25;
		case PROCESSING ->75;
		case PUBLISHED -> 100;
		case FAILED ->null;
		};
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public GameStatus getGameStatus() {
		return gameStatus;
	}
	public void setGameStatus(GameStatus gameStatus) {
		this.gameStatus = gameStatus;
	}
	public String getGameUrl() {
		return gameUrl;
	}
	public void setGameUrl(String gameUrl) {
		this.gameUrl = gameUrl;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public Integer getProgressPercentage() {
		return progressPercentage;
	}
	public void setProgressPercentage(Integer progressPercentage) {
		this.progressPercentage = progressPercentage;
	}
}
