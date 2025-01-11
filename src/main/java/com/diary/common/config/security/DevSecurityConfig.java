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
        // Swagger UI v2
        .requestMatchers(
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**")
        .permitAll()
        // Swagger UI v3
        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**")
        .permitAll()
        // Actuator endpoints
        .requestMatchers("/actuator/**")
        .permitAll()
        // 나머지 요청은 인증 필요
        .anyRequest()
        .authenticated();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("Configuring security filter chain for DEV profile");
    return baseSecurityFilterChain(http);
  }
}
