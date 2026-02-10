package com.gameplatform.security.jwt;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.gameplatform.service.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter{

	private final JWTUtil jwtutil;
	private final CustomUserDetailsService customUserDetailsService;
	public JWTAuthenticationFilter(JWTUtil jwtUtil, CustomUserDetailsService userService)
	{
		this.jwtutil = jwtUtil;
		this.customUserDetailsService = userService;
	}
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException
	{
		String path  = request.getServletPath();
		return path.startsWith("/auth");
	}
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String header = request.getHeader("Authorization");
		try {
		if(header != null && header.contains("Bearer "))
		{
			
			String token = header.substring(7);
			String username = jwtutil.extractUsername(token);
			if(username != null&& jwtutil.validateToken(token) && SecurityContextHolder.getContext().getAuthentication() == null)
			{
				UserDetails details = customUserDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authToken = 
						new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities());
				SecurityContextHolder.getContext().setAuthentication(authToken);
			}
		}
		filterChain.doFilter(request, response);
		}catch(Exception e)
		{
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
	        response.getWriter().flush();
		}
	}

}
