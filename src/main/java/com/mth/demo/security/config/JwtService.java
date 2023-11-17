package com.mth.demo.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

  // todo: set up environment variables for the secret key
  private static final String SECRET_KEY = System.getenv("SEC_KEY");
  private final List<String> blacklist = new ArrayList<>();

  public String extractUsername(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public String generateToken(UserDetails userDetails) {
    return generateToken(new HashMap<>(), userDetails);
  }

  public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
    return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
        .signWith(getSignInKey(), SignatureAlgorithm.HS256).compact();
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) &&
        !isTokenOnBlacklist(token);
  }

  private boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  private Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  private Claims extractAllClaims(String token) {
    return Jwts.parserBuilder().setSigningKey(getSignInKey()).build().parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
    return Keys.hmacShaKeyFor(keyBytes);
  }

  public String extractUserNameFromHeader(String request) {
    String jwt = splitToken(request);
    return extractUsername(jwt);
  }

  public boolean isTokenOnBlacklist(String token) {
    return blacklist.contains(token);
  }

  public void blacklist(String token) {
    String cleanToken = splitToken(token);
    Date expirationDate = extractExpiration(cleanToken);
    if (!blacklist.contains(cleanToken)) {
      blacklist.add(cleanToken);
    } else if (blacklist.contains(cleanToken) && expirationDate.before(new Date())) {
      cleanBlacklist(expirationDate);
    }
  }

  public void cleanBlacklist(Date expirationDateOfLastToken) {
    if (expirationDateOfLastToken.before(new Date())) {
      blacklist.clear();
    }
  }

  public String splitToken(String request) {
    return request.split(" ")[1].trim();
  }
}