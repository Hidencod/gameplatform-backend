package com.gameplatform.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class PlayerProfile {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String displayName;
	private String profileUrl;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
	
	@OneToOne(mappedBy = "profile",cascade =  CascadeType.ALL)
	private PlayerStats stats;
}
