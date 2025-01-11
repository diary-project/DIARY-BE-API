/* (C)2025 */
package com.diary.common.config.security;

import com.diary.common.security.JwtAuthenticationFilter;
import com.diary.common.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
public abstract class BaseSecurityConfig {
  private final JwtTokenProvider tokenProvider;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  protected SecurityFilterChain baseSecurityFilterChain(HttpSecurity http) throws Exception {
    // JWT 필터 추가
    http.addFilterBefore(
        new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class);

    // 기본 설정
    http.csrf(AbstractHttpConfigurer::disable)
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .sessionManagement(
            sessionManagement ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    // 인증 설정
    http.authorizeHttpRequests(
        authorize -> {
          // 공통으로 허용할 경로 설정
          authorize
              .requestMatchers("/api/auth/**")
              .permitAll()
              .requestMatchers("/api/health")
              .permitAll();

          // 프로파일별 설정 추가
          configureAuthorization(authorize);
        });

    // 추가 설정을 위한 hook 메서드
    additionalHttpConfig(http);

    return http.build();
  }

  // 각 프로파일별로 구현할 메서드
  protected abstract void configureAuthorization(
      AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry
          authorize);

  // 추가 HTTP 설정을 위한 hook 메서드
  protected void additionalHttpConfig(HttpSecurity http) throws Exception {
    // 기본 구현은 비어있음
  }
}
