package com.gameplatform.dto;
import java.time.LocalDate;
import java.util.*;

import com.gameplatform.entity.GameStatus;
public class GameResponse {
	private Long id;
	private String name;
	private String description;
	private String gameUrl;
	private String thumbnailUrl;
	private String category;
	private List<String> tags;
	private int playCount;
	private double averageRating;
	private LocalDate createdAt;
	private GameStatus status; 
	public GameResponse(Long id, String name,String description, String gameUrl, String thumbnailUrl, String category, List<String> tags,
			int playCount, double averageRating, LocalDate createdAt,GameStatus status)
	{
		this.id = id;
		this.name = name;
		this.setDescription(description);
		this.gameUrl = gameUrl;
		this.thumbnailUrl = thumbnailUrl;
		this.category = category;
		this.tags = tags;
		this.averageRating = averageRating;
		this.playCount = playCount;
		this.createdAt = createdAt;
		this.status = status;
			
	}
	public GameStatus getStatus() {
		return status;
	}
	public void setStatus(GameStatus status) {
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getGameUrl() {
		return gameUrl;
	}
	public void setGameUrl(String gameUrl) {
		this.gameUrl = gameUrl;
	}
	public String getThumbnailUrl() {
		return thumbnailUrl;
	}
	public void setThumbnailUrl(String thumbnailUrl) {
		this.thumbnailUrl = thumbnailUrl;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public List<String> getTags() {
		return tags;
	}
	public void setTags(List<String> tags) {
		this.tags = tags;
	}
	public int getPlayCount() {
		return playCount;
	}
	public void setPlayCount(int playCount) {
		this.playCount = playCount;
	}
	public double getAverageRating() {
		return averageRating;
	}
	public void setAverageRating(double averageRating) {
		this.averageRating = averageRating;
	}
	public LocalDate getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDate createdAt) {
		this.createdAt = createdAt;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
