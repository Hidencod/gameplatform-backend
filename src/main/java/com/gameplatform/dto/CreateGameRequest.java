package com.gameplatform.dto;

import java.util.ArrayList;
import java.util.*;

import jakarta.validation.constraints.NotBlank;

public class CreateGameRequest {
	@NotBlank(message = "Game name is required")
	private String name;
	private String description;
	private String category;
	private List<String> tags = new ArrayList<>();
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
}
