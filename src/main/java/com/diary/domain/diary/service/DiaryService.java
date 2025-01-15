/* (C)2025 */
package com.diary.domain.diary.service;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.common.security.SecurityUtil;
import com.diary.domain.diary.dto.DiaryCalendarResponse;
import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiaryService {

  private final DiaryRepository diaryRepository;

  @Transactional(readOnly = true)
  public Diary getDiaryByDate(LocalDate date) {
    return diaryRepository
        .findByUserIdAndDateAndIsDeletedFalse(SecurityUtil.getCurrentUserId(), date)
        .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));
  }

  @Transactional
  @CacheEvict(
      value = "diaryCalendar",
      key = "#request.date.year + '-' + #request.date.monthValue + '-' + #userId")
  public Diary createDiary(DiaryCreateRequest request) {
    String userId = SecurityUtil.getCurrentUserId();

    // 해당 날짜에 이미 작성된 일기가 있는지 확인
    if (diaryRepository.existsByUserIdAndDateAndIsDeletedFalse(userId, request.getDate())) {
      throw new BusinessException(ErrorCode.DUPLICATE_DIARY);
    }

    Diary diary =
        Diary.builder()
            .userId(userId)
            .date(request.getDate())
            .weathers(request.getWeathers())
            .content(request.getContent())
            .build();

    return diaryRepository.save(diary);
  }

  @Transactional
  @CacheEvict(value = "diaryCalendar", key = "#date.year + '-' + #date.monthValue + '-' + #userId")
  public Diary updateDiary(LocalDate date, DiaryUpdateRequest request) {
    String userId = SecurityUtil.getCurrentUserId();
    Diary diary = getDiaryByDate(date);

    // 날씨와 내용만 수정 가능
    diary.updateWeatherAndContent(request.getWeathers(), request.getContent());

    return diary;
  }

  @Transactional
  @CacheEvict(value = "diaryCalendar", key = "#date.year + '-' + #date.monthValue + '-' + #userId")
  public void deleteDiary(LocalDate date) {
    String userId = SecurityUtil.getCurrentUserId();
    Diary diary = getDiaryByDate(date);
    diary.softDelete();
  }

  @Transactional(readOnly = true)
  @Cacheable(value = "diaryCalendar", key = "#year + '-' + #month + '-' + #userId")
  public DiaryCalendarResponse getMonthlyDiaryDates(int year, int month) {
    String userId = SecurityUtil.getCurrentUserId();
    YearMonth yearMonth = YearMonth.of(year, month);
    LocalDate startDate = yearMonth.atDay(1);
    LocalDate endDate = yearMonth.atEndOfMonth();

    List<Diary> diaries =
        diaryRepository.findByUserIdAndDateBetweenAndIsDeletedFalse(userId, startDate, endDate);

    LocalDate oldestDiaryDate = diaryRepository.findOldestDiaryDate(userId).orElse(null);

    return DiaryCalendarResponse.of(diaries, yearMonth, oldestDiaryDate);
  }
}
