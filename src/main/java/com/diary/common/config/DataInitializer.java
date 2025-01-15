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
      log.info("Initializing test data...");

      // 테스트 사용자 생성
      User testUser = createTestUser();
      log.info("Created test user: {}", testUser.getEmail());

      // 테스트 일기 생성
      createTestDiaries(testUser);
      log.info("Created test diaries for user: {}", testUser.getEmail());

      log.info("Test data initialization completed.");
    };
  }

  private User createTestUser() {
    String testUserId = "t_" + System.currentTimeMillis();
    User user =
        User.builder()
            .id(testUserId)
            .email("test@example.com")
            .password(passwordEncoder.encode("test1234"))
            .role(Role.USER)
            .authProvider(AuthProvider.TEST)
            .build();

    return userRepository.save(user);
  }

  private void createTestDiaries(User user) {
    // 오늘 일기
    Diary today =
        Diary.builder()
            .userId(user.getId())
            .date(LocalDate.now())
            .weathers(List.of(Weather.SUNNY))
            .content("오늘은 날씨가 좋아서 공원에 다녀왔습니다. 봄이 오는 것 같아 기분이 좋네요.")
            .build();

    // 어제 일기
    Diary yesterday =
        Diary.builder()
            .userId(user.getId())
            .date(LocalDate.now().minusDays(1))
            .weathers(List.of(Weather.CLOUDY))
            .content("어제는 흐리고 추웠지만, 집에서 책을 읽으며 보내서 나쁘지 않았습니다.")
            .build();

    // 지난주 일기
    Diary lastWeek =
        Diary.builder()
            .userId(user.getId())
            .date(LocalDate.now().minusWeeks(1))
            .weathers(List.of(Weather.RAINY))
            .content("비가 왔지만 우산을 들고 카페에 가서 커피를 마셨습니다. 빗소리를 들으며 커피를 마시니 운치가 있었네요.")
            .build();

    diaryRepository.saveAll(List.of(today, yesterday, lastWeek));
  }
}
