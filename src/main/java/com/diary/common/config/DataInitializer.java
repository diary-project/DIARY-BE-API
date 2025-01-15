/* (C)2025 */
package com.diary.common.config;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.user.entity.AuthProvider;
import com.diary.domain.user.entity.Role;
import com.diary.domain.user.entity.User;
import com.diary.domain.user.repository.UserRepository;
import com.diary.domain.weather.Weather;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

  private final UserRepository userRepository;
  private final DiaryRepository diaryRepository;
  private final PasswordEncoder passwordEncoder;

  @Bean
  @Profile({"local", "dev"})
  public CommandLineRunner initializeData() {
    return args -> {
      try {
        // DB가 비어있는지 확인
        if (userRepository.count() > 0) {
          log.info("Database is not empty. Skipping data initialization.");
          return;
        }

        log.info("Initializing test data...");

        // 테스트 유저 생성
        User testUser =
            User.builder()
                .id("t_test123")
                .email("test@example.com")
                .password(passwordEncoder.encode("test1234"))
                .name("테스트 유저")
                .role(Role.USER)
                .authProvider(AuthProvider.TEST)
                .build();

        testUser = userRepository.save(testUser);
        log.info("Test user created: {}", testUser.getEmail());

        // 테스트 일기 생성
        createTestDiary(testUser.getId(), LocalDate.now(), List.of(Weather.SUNNY), "오늘은 날씨가 좋았다.");
        createTestDiary(
            testUser.getId(), LocalDate.now().minusDays(1), List.of(Weather.RAINY), "비가 왔다.");
        createTestDiary(
            testUser.getId(), LocalDate.now().minusDays(2), List.of(Weather.CLOUDY), "흐린 날씨였다.");

        log.info("Test data initialization completed successfully.");
      } catch (Exception e) {
        log.error("Failed to initialize test data", e);
        // 초기화 실패 시 로그만 남기고 애플리케이션은 계속 실행
      }
    };
  }

  private void createTestDiary(
      String userId, LocalDate date, List<Weather> weathers, String content) {
    try {
      Diary diary =
          Diary.builder().userId(userId).date(date).weathers(weathers).content(content).build();

      diaryRepository.save(diary);
      log.info("Test diary created for date: {}", date);
    } catch (Exception e) {
      log.error("Failed to create test diary for date: {}", date, e);
    }
  }
}
