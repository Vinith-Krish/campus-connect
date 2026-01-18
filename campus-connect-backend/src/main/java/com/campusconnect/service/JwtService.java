package com.campusconnect.service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Service;

import com.campusconnect.model.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JwtService {
	@Value("${jwt.secret}")
	private String secretKey;

	@Value("${jwt.expiration}")
	private Long expirationTime;

	// generate token
	public String generateToken(String email, Long userId, Role role) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("userId", userId);
		claims.put("role", role.name());

		return Jwts.builder().setClaims(claims).setSubject(email).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + expirationTime))
				.signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
	}
	// extract username
	public String extractUsername(String token) {
	    
	    return extractClaim(token, Claims::getSubject);
	}

	// extract all claims
	public Long extractUserId(String token) {
		Claims claims = extractAllClaims(token);
		return claims.get("userId", Long.class);
	}

	// extract role
	public Role extractRole(String token) {

		Claims claims = extractAllClaims(token);
		String roleName = claims.get("role", String.class);
		return Role.valueOf(roleName);
	}

	// validate token
	public boolean validateToken(String token, String username) {

		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username) && !isTokenExpired(token));
	}
	// check for token expiration
	private boolean isTokenExpired(String token) {
	    return extractExpiration(token).before(new Date());
	}
	// extract expiration date
	private Date extractExpiration(String token) {
	    return extractClaim(token, Claims::getExpiration);
	}
	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
	    final Claims claims = extractAllClaims(token);
	    return claimsResolver.apply(claims);
	}
	// extract all claims
	private Claims extractAllClaims(String token) {
	    return Jwts.parserBuilder()
	        .setSigningKey(getSignInKey())
	        .build()
	        .parseClaimsJws(token)
	        .getBody();
	}
	private Key getSignInKey() {
	    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
	    return Keys.hmacShaKeyFor(keyBytes);
	}
}
