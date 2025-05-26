package com.subash.product_jwt_copy.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

@Component
public class JwtTokenProvider {

	private final String jwtSecret;
	private final long jwtExpiration;
	private final SecretKey key;

	public JwtTokenProvider(@Value("${app.jwt.secret}") String jwtSecret,

			@Value("${app.jwt.expiration}") long jwtExpiration) {
		this.jwtSecret = jwtSecret;
		this.jwtExpiration = jwtExpiration;
		if (jwtSecret == null || jwtSecret.isEmpty()) {
			throw new IllegalArgumentException("JWT secret cannot be null or empty");
		}
		if (jwtSecret.length() < 32) {
			throw new IllegalArgumentException("JWT secret must be at least 32 characters for HS256");
		}
		if (jwtExpiration <= 0) {
			throw new IllegalArgumentException("JWT expiration must be positive");
		}
		this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(Authentication authentication) {
		if (authentication == null || authentication.getPrincipal() == null) {
			throw new IllegalArgumentException("Authentication or principal cannot be null");
		}

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		if (userDetails.getUsername() == null || userDetails.getUsername().isEmpty()) {
			throw new IllegalArgumentException("Username cannot be null or empty");
		}

		Map<String, Object> claims = new HashMap<>();
		claims.put("roles",
				userDetails.getAuthorities().stream().map(auth -> auth.getAuthority()).collect(Collectors.toList()));

		return Jwts.builder().claims(claims).subject(userDetails.getUsername())
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + jwtExpiration)).signWith(key).compact();
	}

	public String getUsernameFromJWT(String token) {
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("JWT token cannot be null or empty");
		}
		return extractClaim(token, Claims::getSubject);
	}

	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	private Claims extractAllClaims(String token) {
		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("JWT token cannot be null or empty");
		}
		try {
			return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
		} catch (JwtException e) {
			throw new IllegalArgumentException("Invalid JWT token: " + e.getMessage(), e);
		}
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		if (token == null || token.isEmpty() || userDetails == null) {
			return false;
		}
		try {
			final String userName = getUsernameFromJWT(token);
			return userName.equals(userDetails.getUsername()) && !isTokenExpired(token);
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}
