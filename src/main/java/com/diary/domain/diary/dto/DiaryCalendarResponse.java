/* (C)2025 */
package com.diary.domain.diary.dto;

import com.diary.domain.diary.entity.Diary;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryCalendarResponse {
  private List<LocalDate> dates;
  private boolean canNavigatePrevious;
  private boolean canNavigateNext;

  public static DiaryCalendarResponse of(
      List<Diary> diaries, YearMonth currentMonth, LocalDate oldestDiaryDate) {
    List<LocalDate> dates = diaries.stream().map(Diary::getDate).toList();

    // 다음 달로 이동 가능 여부: 현재 날짜보다 미래인 경우 불가능
    boolean canNavigateNext = !currentMonth.plusMonths(1).isAfter(YearMonth.from(LocalDate.now()));

    // 이전 달로 이동 가능 여부: 가장 오래된 일기보다 이전인 경우 불가능
    boolean canNavigatePrevious =
        oldestDiaryDate != null
            && !currentMonth.minusMonths(1).isBefore(YearMonth.from(oldestDiaryDate));

    return DiaryCalendarResponse.builder()
        .dates(dates)
        .canNavigatePrevious(canNavigatePrevious)
        .canNavigateNext(canNavigateNext)
        .build();
  }
}
