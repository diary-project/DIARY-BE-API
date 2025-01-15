/* (C)2025 */
package com.diary.common.config.security;

import com.diary.common.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("dev")
public class DevSecurityConfig extends BaseSecurityConfig {
  private static final Logger log = LoggerFactory.getLogger(DevSecurityConfig.class);

  public DevSecurityConfig(JwtTokenProvider tokenProvider) {
    super(tokenProvider);
    log.info("DevSecurityConfig is initialized");
  }

  @Override
  protected void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorize) {
    log.info("Configuring authorization for DEV profile");
    authorize
        // Swagger UI v3 (먼저 체크)
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html")
        .permitAll()
        // Swagger UI v2 (하위 호환성)
        .requestMatchers(
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/webjars/**")
        .permitAll()
        // Actuator endpoints
        .requestMatchers("/actuator/**")
        .permitAll()
        // API 요청에 대해서만 인증 필요
        .requestMatchers("/api/v1/**")
        .authenticated()
        // 나머지는 모두 허용
        .anyRequest()
        .permitAll();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("Configuring security filter chain for DEV profile");
    return baseSecurityFilterChain(http);
  }
}
