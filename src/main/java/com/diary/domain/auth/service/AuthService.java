/* (C)2025 */
package com.diary.domain.auth.service;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.common.security.JwtTokenProvider;
import com.diary.domain.auth.dto.SignInRequest;
import com.diary.domain.auth.dto.SignUpRequest;
import com.diary.domain.auth.dto.TokenRefreshRequest;
import com.diary.domain.auth.dto.TokenResponse;
import com.diary.domain.auth.entity.RefreshToken;
import com.diary.domain.auth.repository.RefreshTokenRepository;
import com.diary.domain.user.entity.AuthProvider;
import com.diary.domain.user.entity.Role;
import com.diary.domain.user.entity.User;
import com.diary.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
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
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public void signUp(SignUpRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
    }

    String testUserId = UUID.randomUUID().toString().substring(0, 8);
    String userId = User.generateId(AuthProvider.TEST, testUserId);

    User user =
        User.builder()
            .id(userId)
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .name(request.getName())
            .role(Role.USER)
            .authProvider(AuthProvider.TEST)
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

    // 4. Refresh Token 저장
    saveRefreshToken(authentication, refreshToken);

    return new TokenResponse(accessToken, refreshToken, "Bearer");
  }

  @Transactional
  public TokenResponse refreshToken(TokenRefreshRequest request) {
    // 1. Refresh Token 검증
    if (!tokenProvider.validateToken(request.getRefreshToken())) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    // 2. Access Token에서 User ID 가져오기
    Authentication authentication = tokenProvider.getAuthentication(request.getRefreshToken());
    User user = (User) authentication.getPrincipal();

    // 3. 저장소에서 User ID를 기반으로 Refresh Token 값 가져오기
    RefreshToken refreshToken =
        refreshTokenRepository
            .findByUserId(user.getId())
            .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN));

    // 4. Refresh Token 일치하는지 검사
    if (!refreshToken.getToken().equals(request.getRefreshToken())) {
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }

    // 5. Refresh Token 만료 여부 검사
    if (refreshToken.isExpired()) {
      refreshTokenRepository.delete(refreshToken);
      throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
    }

    // 6. 새로운 토큰 생성
    String newAccessToken = tokenProvider.createToken(authentication);
    String newRefreshToken = tokenProvider.createRefreshToken(authentication);

    // 7. Refresh Token 업데이트
    refreshToken.updateToken(newRefreshToken, LocalDateTime.now().plusDays(7));
    refreshTokenRepository.save(refreshToken);

    return new TokenResponse(newAccessToken, newRefreshToken, "Bearer");
  }

  @Transactional
  public void logout(String userId) {
    refreshTokenRepository.deleteByUserId(userId);
  }

  private void saveRefreshToken(Authentication authentication, String refreshToken) {
    User user = (User) authentication.getPrincipal();
    LocalDateTime expiryDate = LocalDateTime.now().plusDays(7); // 7일 후 만료

    RefreshToken token =
        RefreshToken.builder()
            .id(UUID.randomUUID().toString())
            .userId(user.getId())
            .token(refreshToken)
            .expiryDate(expiryDate)
            .build();

    refreshTokenRepository.save(token);
  }
}
