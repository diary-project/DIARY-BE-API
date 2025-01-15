/* (C)2025 */
package com.diary.domain.diary.dto;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.weather.Weather;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DiaryResponse {
  private Long id;
  private LocalDate date;
  private List<Weather> weathers;
  private String content;

  public static DiaryResponse from(Diary diary) {
    return DiaryResponse.builder()
        .id(diary.getId())
        .date(diary.getDate())
        .weathers(diary.getWeathers())
        .content(diary.getContent())
        .build();
  }
}
