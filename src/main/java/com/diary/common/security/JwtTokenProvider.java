/* (C)2025 */
package com.diary.common.security;

import com.diary.domain.user.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

/** JWT 토큰 생성 및 검증을 담당하는 컴포넌트 */
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

  private final JwtConfig jwtConfig;

  /** JWT 서명에 사용할 키를 생성합니다. */
  private Key getSigningKey() {
    byte[] keyBytes = jwtConfig.getSecret().getBytes();
    return Keys.hmacShaKeyFor(keyBytes);
  }

  /**
   * 사용자 인증 정보를 기반으로 액세스 토큰을 생성합니다.
   *
   * @param authentication 인증 정보
   * @return JWT 액세스 토큰
   */
  public String createToken(Authentication authentication) {
    String authorities =
        authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

    User user = (User) authentication.getPrincipal();
    Date now = new Date();
    Date validity = new Date(now.getTime() + jwtConfig.getExpiration());

    return Jwts.builder()
        .setSubject(user.getId())
        .claim("email", user.getEmail())
        .claim("auth", authorities)
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * 사용자 인증 정보를 기반으로 리프레시 토큰을 생성합니다.
   *
   * @param authentication 인증 정보
   * @return JWT 리프레시 토큰
   */
  public String createRefreshToken(Authentication authentication) {
    User user = (User) authentication.getPrincipal();
    Date now = new Date();
    Date validity = new Date(now.getTime() + jwtConfig.getRefreshTokenExpiration());

    return Jwts.builder()
        .setSubject(user.getId())
        .setIssuedAt(now)
        .setExpiration(validity)
        .signWith(getSigningKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * JWT 토큰에서 인증 정보를 추출합니다.
   *
   * @param token JWT 토큰
   * @return 인증 정보
   */
  public Authentication getAuthentication(String token) {
    Claims claims =
        Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();

    Collection<? extends GrantedAuthority> authorities =
        Arrays.stream(claims.get("auth").toString().split(","))
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());

    User principal =
        User.builder().id(claims.getSubject()).email(claims.get("email", String.class)).build();

    return new UsernamePasswordAuthenticationToken(principal, token, authorities);
  }

  /**
   * JWT 토큰의 유효성을 검증합니다.
   *
   * @param token JWT 토큰
   * @return 토큰 유효성 여부
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
