/* (C)2025 */
package com.diary.domain.auth.service;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.common.security.JwtTokenProvider;
import com.diary.domain.auth.dto.SignInRequest;
import com.diary.domain.auth.dto.SignUpRequest;
import com.diary.domain.auth.dto.TokenResponse;
import com.diary.domain.user.entity.Role;
import com.diary.domain.user.entity.User;
import com.diary.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider tokenProvider;
  private final AuthenticationManagerBuilder authenticationManagerBuilder;

  @Transactional
  public void signUp(SignUpRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }

    User user =
        User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .role(Role.USER)
            .build();

    userRepository.save(user);
  }

  @Transactional
  public TokenResponse signIn(SignInRequest request) {
    // 1. 인증 토큰 생성
    UsernamePasswordAuthenticationToken authenticationToken =
        new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword());

    // 2. 실제 검증 (사용자 비밀번호 체크)
    Authentication authentication =
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);

    // 3. 인증 정보를 기반으로 JWT 토큰 생성
    String accessToken = tokenProvider.createToken(authentication);
    String refreshToken = tokenProvider.createRefreshToken(authentication);

    return new TokenResponse(accessToken, refreshToken, "Bearer");
  }
}
