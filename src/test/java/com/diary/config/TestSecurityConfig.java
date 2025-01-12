/* (C)2025 */
package com.diary.config;

import com.diary.common.config.security.BaseSecurityConfig;
import com.diary.common.security.JwtTokenProvider;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@Profile("test")
public class TestSecurityConfig extends BaseSecurityConfig {

  public TestSecurityConfig(JwtTokenProvider tokenProvider) {
    super(tokenProvider);
  }

  @Override
  protected void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorize) {
    // 테스트 환경에서는 모든 요청 허용
    authorize.anyRequest().permitAll();
  }

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    return baseSecurityFilterChain(http);
  }
}
