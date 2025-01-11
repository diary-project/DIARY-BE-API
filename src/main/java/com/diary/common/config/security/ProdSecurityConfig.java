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
@Profile("prod")
public class ProdSecurityConfig extends BaseSecurityConfig {
  private static final Logger log = LoggerFactory.getLogger(ProdSecurityConfig.class);

  public ProdSecurityConfig(JwtTokenProvider tokenProvider) {
    super(tokenProvider);
    log.info("ProdSecurityConfig is initialized");
  }

  @Override
  protected void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorize) {
    log.info("Configuring authorization for PROD profile");
    authorize
        // Actuator endpoints (필요한 경우에만 허용)
        .requestMatchers("/actuator/health", "/actuator/info")
        .permitAll()
        // 나머지 요청은 인증 필요
        .anyRequest()
        .authenticated();
  }

  @Override
  protected void additionalHttpConfig(HttpSecurity http) throws Exception {
    // 보안 헤더 설정
    http.headers(
        headers ->
            headers
                .frameOptions(frameOptions -> frameOptions.deny()) // X-Frame-Options 설정
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'")) // CSP 설정
        );
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    log.info("Configuring security filter chain for PROD profile");
    return baseSecurityFilterChain(http);
  }
}
