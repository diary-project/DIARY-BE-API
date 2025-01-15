/* (C)2025 */
package com.diary.domain.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.common.security.JwtTokenProvider;
import com.diary.domain.auth.dto.SignInRequest;
import com.diary.domain.auth.dto.SignUpRequest;
import com.diary.domain.auth.dto.TokenRefreshRequest;
import com.diary.domain.auth.dto.TokenResponse;
import com.diary.domain.auth.repository.RefreshTokenRepository;
import com.diary.domain.user.entity.AuthProvider;
import com.diary.domain.user.entity.Role;
import com.diary.domain.user.entity.User;
import com.diary.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @InjectMocks private AuthService authService;

  @Mock private UserRepository userRepository;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtTokenProvider tokenProvider;

  @Mock private AuthenticationManagerBuilder authenticationManagerBuilder;

  @Mock private RefreshTokenRepository refreshTokenRepository;

  @Nested
  @DisplayName("회원가입")
  class SignUp {

    @Test
    @DisplayName("성공적으로 회원가입을 완료한다")
    void signUpSuccess() {
      // given
      @Setter
      class TestSignUpRequest extends SignUpRequest {
        private String email;
        private String password;
        private String name;

        @Override
        public String getEmail() {
          return email;
        }

        @Override
        public String getPassword() {
          return password;
        }

        @Override
        public String getName() {
          return name;
        }
      }

      TestSignUpRequest request = new TestSignUpRequest();
      request.setEmail("test@example.com");
      request.setPassword("password123");
      request.setName("Test User");

      when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
      when(passwordEncoder.encode(any())).thenReturn("encodedPassword");

      // when
      authService.signUp(request);

      // then
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertThat(savedUser.getEmail()).isEqualTo(request.getEmail());
      assertThat(savedUser.getName()).isEqualTo(request.getName());
      assertThat(savedUser.getAuthProvider()).isEqualTo(AuthProvider.TEST);
      assertThat(savedUser.getRole()).isEqualTo(Role.USER);
      assertThat(savedUser.getId()).startsWith("t_");
    }

    @Test
    @DisplayName("이미 존재하는 이메일로 가입 시 예외가 발생한다")
    void signUpFailWithDuplicateEmail() {
      // given
      @Setter
      class TestSignUpRequest extends SignUpRequest {
        private String email;
        private String password;
        private String name;

        @Override
        public String getEmail() {
          return email;
        }

        @Override
        public String getPassword() {
          return password;
        }

        @Override
        public String getName() {
          return name;
        }
      }

      TestSignUpRequest request = new TestSignUpRequest();
      request.setEmail("test@example.com");
      request.setPassword("password123");
      request.setName("Test User");

      when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

      // when & then
      assertThatThrownBy(() -> authService.signUp(request))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_EMAIL);
    }
  }

  @Nested
  @DisplayName("로그인")
  class SignIn {

    @Test
    @DisplayName("성공적으로 로그인하고 토큰을 발급받는다")
    void signInSuccess() {
      // given
      @Setter
      class TestSignInRequest extends SignInRequest {
        private String email;
        private String password;

        @Override
        public String getEmail() {
          return email;
        }

        @Override
        public String getPassword() {
          return password;
        }
      }

      TestSignInRequest request = new TestSignInRequest();
      request.setEmail("test@example.com");
      request.setPassword("password123");

      Authentication authentication = mock(Authentication.class);
      User user = User.builder().id("t_test123").email("test@example.com").role(Role.USER).build();
      when(authentication.getPrincipal()).thenReturn(user);

      AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
      when(authenticationManagerBuilder.getObject()).thenReturn(authenticationManager);
      when(authenticationManager.authenticate(any())).thenReturn(authentication);

      String accessToken = "access.token.test";
      String refreshToken = "refresh.token.test";
      when(tokenProvider.createToken(authentication)).thenReturn(accessToken);
      when(tokenProvider.createRefreshToken(authentication)).thenReturn(refreshToken);

      // when
      TokenResponse response = authService.signIn(request);

      // then
      assertThat(response.getAccessToken()).isEqualTo(accessToken);
      assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
      assertThat(response.getTokenType()).isEqualTo("Bearer");

      ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor =
          ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
      verify(authenticationManager).authenticate(tokenCaptor.capture());

      UsernamePasswordAuthenticationToken capturedToken = tokenCaptor.getValue();
      assertThat(capturedToken.getPrincipal()).isEqualTo(request.getEmail());
      assertThat(capturedToken.getCredentials()).isEqualTo(request.getPassword());
    }
  }

  @Nested
  @DisplayName("토큰 갱신")
  class RefreshToken {

    @Test
    @DisplayName("성공적으로 토큰을 갱신한다")
    void refreshTokenSuccess() {
      // given
      String oldRefreshToken = "old.refresh.token";
      String newAccessToken = "new.access.token";
      String newRefreshToken = "new.refresh.token";
      String userId = "t_test123";

      @Setter
      class TestTokenRefreshRequest extends TokenRefreshRequest {
        private String refreshToken;

        @Override
        public String getRefreshToken() {
          return refreshToken;
        }
      }

      TestTokenRefreshRequest request = new TestTokenRefreshRequest();
      request.setRefreshToken(oldRefreshToken);

      Authentication authentication = mock(Authentication.class);
      User user = User.builder().id(userId).email("test@example.com").role(Role.USER).build();

      when(tokenProvider.validateToken(oldRefreshToken)).thenReturn(true);
      when(tokenProvider.getAuthentication(oldRefreshToken)).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(user);

      com.diary.domain.auth.entity.RefreshToken savedRefreshToken =
          com.diary.domain.auth.entity.RefreshToken.builder()
              .id("token_id")
              .userId(userId)
              .token(oldRefreshToken)
              .expiryDate(LocalDateTime.now().plusDays(7))
              .build();

      when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(savedRefreshToken));
      when(tokenProvider.createToken(authentication)).thenReturn(newAccessToken);
      when(tokenProvider.createRefreshToken(authentication)).thenReturn(newRefreshToken);

      // when
      TokenResponse response = authService.refreshToken(request);

      // then
      assertThat(response.getAccessToken()).isEqualTo(newAccessToken);
      assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
      assertThat(response.getTokenType()).isEqualTo("Bearer");

      verify(refreshTokenRepository).save(any(com.diary.domain.auth.entity.RefreshToken.class));
    }

    @Test
    @DisplayName("유효하지 않은 리프레시 토큰으로 갱신 시도시 예외가 발생한다")
    void refreshTokenFailWithInvalidToken() {
      // given
      String invalidRefreshToken = "invalid.refresh.token";

      @Setter
      class TestTokenRefreshRequest extends TokenRefreshRequest {
        private String refreshToken;

        @Override
        public String getRefreshToken() {
          return refreshToken;
        }
      }

      TestTokenRefreshRequest request = new TestTokenRefreshRequest();
      request.setRefreshToken(invalidRefreshToken);

      when(tokenProvider.validateToken(invalidRefreshToken)).thenReturn(false);

      // when & then
      assertThatThrownBy(() -> authService.refreshToken(request))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);
    }

    @Test
    @DisplayName("만료된 리프레시 토큰으로 갱신 시도시 예외가 발생한다")
    void refreshTokenFailWithExpiredToken() {
      // given
      String expiredRefreshToken = "expired.refresh.token";
      String userId = "t_test123";

      @Setter
      class TestTokenRefreshRequest extends TokenRefreshRequest {
        private String refreshToken;

        @Override
        public String getRefreshToken() {
          return refreshToken;
        }
      }

      TestTokenRefreshRequest request = new TestTokenRefreshRequest();
      request.setRefreshToken(expiredRefreshToken);

      Authentication authentication = mock(Authentication.class);
      User user = User.builder().id(userId).email("test@example.com").role(Role.USER).build();

      when(tokenProvider.validateToken(expiredRefreshToken)).thenReturn(true);
      when(tokenProvider.getAuthentication(expiredRefreshToken)).thenReturn(authentication);
      when(authentication.getPrincipal()).thenReturn(user);

      com.diary.domain.auth.entity.RefreshToken expiredToken =
          com.diary.domain.auth.entity.RefreshToken.builder()
              .id("token_id")
              .userId(userId)
              .token(expiredRefreshToken)
              .expiryDate(LocalDateTime.now().minusDays(1))
              .build();

      when(refreshTokenRepository.findByUserId(userId)).thenReturn(Optional.of(expiredToken));

      // when & then
      assertThatThrownBy(() -> authService.refreshToken(request))
          .isInstanceOf(BusinessException.class)
          .hasFieldOrPropertyWithValue("errorCode", ErrorCode.TOKEN_EXPIRED);

      verify(refreshTokenRepository).delete(expiredToken);
    }
  }

  @Nested
  @DisplayName("로그아웃")
  class Logout {

    @Test
    @DisplayName("성공적으로 로그아웃한다")
    void logoutSuccess() {
      // given
      String userId = "t_test123";

      // when
      authService.logout(userId);

      // then
      verify(refreshTokenRepository).deleteByUserId(userId);
    }
  }
}
