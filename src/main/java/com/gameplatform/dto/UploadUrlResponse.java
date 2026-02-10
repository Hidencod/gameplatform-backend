package com.gameplatform.dto;

public class UploadUrlResponse {
	private String uploadUrl;
	public UploadUrlResponse(String uploadUrl)
	{
		this.uploadUrl = uploadUrl;
	}
	public String getUploadUrl() {
		return uploadUrl;
	}
	public void setUploadUrl(String uploadUrl) {
		this.uploadUrl = uploadUrl;
	}
	
}
