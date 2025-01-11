/* (C)2025 */
package com.diary.common.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class JwtConfig {
  @Value("${jwt.secret:dj89sFD2SDF89sf2SDF89dsf2SDF89dsf2SDF89dsf2SDF89dsf2SDF89dsf2SDF89dsf2}")
  private String secret;

  @Value("${jwt.expiration:86400000}") // 24시간
  private Long expiration;

  @Value("${jwt.refresh-token.expiration:604800000}") // 7일
  private Long refreshTokenExpiration;
}
