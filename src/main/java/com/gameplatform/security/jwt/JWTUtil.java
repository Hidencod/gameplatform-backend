package com.gameplatform.security.jwt;

import java.security.Signature;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.gameplatform.exception.JwtValidationException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

@Component
public class JWTUtil {
	
	@Value("${jwt.secret}")
	private String secret;
	@Value("${jwt.expiration}")
	private long expiration;
	public String generateToken(String username)
	{
		return Jwts.builder()
				.setSubject(username)
				.setIssuedAt(new Date())
				.setExpiration(new Date(System.currentTimeMillis()+expiration))
				.signWith(Keys.hmacShaKeyFor(secret.getBytes()),SignatureAlgorithm.HS256)
				.compact();
		
	}
	public String extractUsername(String token)
	{
		return Jwts.parserBuilder()
				.setSigningKey(secret.getBytes())
				.build()
				.parseClaimsJws(token)
				.getBody()
				.getSubject();
	}
	public boolean validateToken(String token)
	{
		try {
		 Jwts.parserBuilder()
				.setSigningKey(secret.getBytes())
				.build()
				.parseClaimsJws(token);
		
			return true;
		}catch (ExpiredJwtException e) {
			throw new JwtValidationException("Token expired", e);
		}catch (SignatureException e)
		{
			throw new JwtValidationException("Invalid signature", e);
		}catch (UnsupportedJwtException e) {
			throw new JwtValidationException("Unsupported jwt",e);
		}
		catch(MalformedJwtException e)
		{
			throw new JwtValidationException(" Malformed token",e);
			
		}
		catch (IllegalArgumentException e)
		{
			throw new JwtValidationException("Token is null or empty",e);
		}
		
	}

}
