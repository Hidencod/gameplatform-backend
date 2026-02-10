package com.gameplatform.entity;

public enum GameStatus {
	DRAFT,		//Game created , no file uploaded
	UPLOADING,	//File upload in progress
	PROCESSING,	//Unzipping and deploying
	PUBLISHED,	//Live and playable
	FAILED		//Processing failed
}
