/* (C)2025 */
package com.diary.domain.diary.dto;

import com.diary.domain.weather.Weather;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DiaryUpdateRequest {
  private LocalDate date;
  private List<Weather> weathers;
  private String content;
}
