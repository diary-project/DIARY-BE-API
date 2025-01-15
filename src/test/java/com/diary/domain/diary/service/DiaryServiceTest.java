/* (C)2025 */
package com.diary.domain.diary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.common.security.SecurityUtil;
import com.diary.domain.diary.dto.DiaryCalendarResponse;
import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import com.diary.domain.weather.Weather;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import lombok.Setter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

  @InjectMocks private DiaryService diaryService;

  @Mock private DiaryRepository diaryRepository;

  @Nested
  @DisplayName("일기 생성")
  class CreateDiary {

    @Test
    @DisplayName("성공적으로 일기를 생성한다")
    void createDiarySuccess() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        @Setter
        class TestDiaryCreateRequest extends DiaryCreateRequest {
          private LocalDate date;
          private List<Weather> weathers;
          private String content;

          @Override
          public LocalDate getDate() {
            return date;
          }

          @Override
          public List<Weather> getWeathers() {
            return weathers;
          }

          @Override
          public String getContent() {
            return content;
          }
        }

        TestDiaryCreateRequest request = new TestDiaryCreateRequest();
        request.setDate(LocalDate.now());
        request.setWeathers(List.of(Weather.SUNNY));
        request.setContent("오늘은 날씨가 좋았다.");

        when(diaryRepository.existsByUserIdAndDateAndIsDeletedFalse(userId, request.getDate()))
            .thenReturn(false);

        // when
        Diary result = diaryService.createDiary(request);

        // then
        ArgumentCaptor<Diary> diaryCaptor = ArgumentCaptor.forClass(Diary.class);
        verify(diaryRepository).save(diaryCaptor.capture());

        Diary savedDiary = diaryCaptor.getValue();
        assertThat(savedDiary.getUserId()).isEqualTo(userId);
        assertThat(savedDiary.getDate()).isEqualTo(request.getDate());
        assertThat(savedDiary.getWeathers()).isEqualTo(request.getWeathers());
        assertThat(savedDiary.getContent()).isEqualTo(request.getContent());
      }
    }

    @Test
    @DisplayName("같은 날짜에 이미 일기가 존재하면 예외가 발생한다")
    void createDiaryFailWithDuplicateDate() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        @Setter
        class TestDiaryCreateRequest extends DiaryCreateRequest {
          private LocalDate date;
          private List<Weather> weathers;
          private String content;

          @Override
          public LocalDate getDate() {
            return date;
          }

          @Override
          public List<Weather> getWeathers() {
            return weathers;
          }

          @Override
          public String getContent() {
            return content;
          }
        }

        TestDiaryCreateRequest request = new TestDiaryCreateRequest();
        request.setDate(LocalDate.now());
        request.setWeathers(List.of(Weather.SUNNY));
        request.setContent("오늘은 날씨가 좋았다.");

        when(diaryRepository.existsByUserIdAndDateAndIsDeletedFalse(userId, request.getDate()))
            .thenReturn(true);

        // when & then
        assertThatThrownBy(() -> diaryService.createDiary(request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_DIARY);
      }
    }
  }

  @Nested
  @DisplayName("일기 조회")
  class GetDiary {

    @Test
    @DisplayName("성공적으로 일기를 조회한다")
    void getDiarySuccess() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        LocalDate date = LocalDate.now();
        Diary diary =
            Diary.builder()
                .userId(userId)
                .date(date)
                .weathers(List.of(Weather.SUNNY))
                .content("오늘은 날씨가 좋았다.")
                .build();

        when(diaryRepository.findByUserIdAndDateAndIsDeletedFalse(userId, date))
            .thenReturn(Optional.of(diary));

        // when
        Diary result = diaryService.getDiaryByDate(date);

        // then
        assertThat(result.getUserId()).isEqualTo(userId);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.getWeathers()).isEqualTo(diary.getWeathers());
        assertThat(result.getContent()).isEqualTo(diary.getContent());
      }
    }

    @Test
    @DisplayName("존재하지 않는 일기를 조회하면 예외가 발생한다")
    void getDiaryFailWithNotFound() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        LocalDate date = LocalDate.now();
        when(diaryRepository.findByUserIdAndDateAndIsDeletedFalse(userId, date))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> diaryService.getDiaryByDate(date))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DIARY_NOT_FOUND);
      }
    }
  }

  @Nested
  @DisplayName("일기 수정")
  class UpdateDiary {

    @Test
    @DisplayName("성공적으로 일기를 수정한다")
    void updateDiarySuccess() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        LocalDate date = LocalDate.now();
        Diary diary =
            Diary.builder()
                .userId(userId)
                .date(date)
                .weathers(List.of(Weather.SUNNY))
                .content("오늘은 날씨가 좋았다.")
                .build();

        @Setter
        class TestDiaryUpdateRequest extends DiaryUpdateRequest {
          private List<Weather> weathers;
          private String content;

          @Override
          public List<Weather> getWeathers() {
            return weathers;
          }

          @Override
          public String getContent() {
            return content;
          }
        }

        TestDiaryUpdateRequest request = new TestDiaryUpdateRequest();
        request.setWeathers(List.of(Weather.RAINY));
        request.setContent("비가 왔다.");

        when(diaryRepository.findByUserIdAndDateAndIsDeletedFalse(userId, date))
            .thenReturn(Optional.of(diary));

        // when
        Diary result = diaryService.updateDiary(date, request);

        // then
        assertThat(result.getWeathers()).isEqualTo(request.getWeathers());
        assertThat(result.getContent()).isEqualTo(request.getContent());
      }
    }

    @Test
    @DisplayName("존재하지 않는 일기를 수정하면 예외가 발생한다")
    void updateDiaryFailWithNotFound() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        LocalDate date = LocalDate.now();
        @Setter
        class TestDiaryUpdateRequest extends DiaryUpdateRequest {
          private List<Weather> weathers;
          private String content;

          @Override
          public List<Weather> getWeathers() {
            return weathers;
          }

          @Override
          public String getContent() {
            return content;
          }
        }

        TestDiaryUpdateRequest request = new TestDiaryUpdateRequest();
        request.setWeathers(List.of(Weather.RAINY));
        request.setContent("비가 왔다.");

        when(diaryRepository.findByUserIdAndDateAndIsDeletedFalse(userId, date))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> diaryService.updateDiary(date, request))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DIARY_NOT_FOUND);
      }
    }
  }

  @Nested
  @DisplayName("일기 삭제")
  class DeleteDiary {

    @Test
    @DisplayName("성공적으로 일기를 삭제한다")
    void deleteDiarySuccess() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        LocalDate date = LocalDate.now();
        Diary diary =
            Diary.builder()
                .userId(userId)
                .date(date)
                .weathers(List.of(Weather.SUNNY))
                .content("오늘은 날씨가 좋았다.")
                .build();

        when(diaryRepository.findByUserIdAndDateAndIsDeletedFalse(userId, date))
            .thenReturn(Optional.of(diary));

        // when
        diaryService.deleteDiary(date);

        // then
        assertThat(diary.isDeleted()).isTrue();
        verify(diaryRepository).save(diary);
      }
    }

    @Test
    @DisplayName("존재하지 않는 일기를 삭제하면 예외가 발생한다")
    void deleteDiaryFailWithNotFound() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        LocalDate date = LocalDate.now();
        when(diaryRepository.findByUserIdAndDateAndIsDeletedFalse(userId, date))
            .thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> diaryService.deleteDiary(date))
            .isInstanceOf(BusinessException.class)
            .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DIARY_NOT_FOUND);
      }
    }
  }

  @Nested
  @DisplayName("달력 조회")
  class GetCalendar {

    @Test
    @DisplayName("성공적으로 달력을 조회한다")
    void getCalendarSuccess() {
      // given
      String userId = "t_test123";
      try (MockedStatic<SecurityUtil> securityUtil = Mockito.mockStatic(SecurityUtil.class)) {
        securityUtil.when(SecurityUtil::getCurrentUserId).thenReturn(userId);

        YearMonth yearMonth = YearMonth.now();
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<LocalDate> dates =
            List.of(LocalDate.now().minusDays(1), LocalDate.now(), LocalDate.now().plusDays(1));

        List<Diary> diaries =
            dates.stream()
                .map(
                    date ->
                        Diary.builder()
                            .userId(userId)
                            .date(date)
                            .weathers(List.of(Weather.SUNNY))
                            .content("날씨가 좋았다.")
                            .build())
                .toList();

        when(diaryRepository.findByUserIdAndDateBetweenAndIsDeletedFalse(
                userId, startDate, endDate))
            .thenReturn(diaries);

        // when
        DiaryCalendarResponse response =
            diaryService.getMonthlyDiaryDates(yearMonth.getYear(), yearMonth.getMonthValue());

        // then
        assertThat(response).isNotNull();
      }
    }
  }
}
