/* (C)2025 */
package com.diary.domain.diary.entity;

import com.diary.common.entity.BaseEntity;
import com.diary.domain.weather.Weather;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "diaries",
    uniqueConstraints = {
      @UniqueConstraint(
          name = "uk_diary_user_date",
          columnNames = {"userId", "date"})
    })
public class Diary extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String userId;

  @Column(nullable = false)
  private LocalDate date;

  @ElementCollection
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "diary_weathers", joinColumns = @JoinColumn(name = "diary_id"))
  @Column(name = "weather")
  private List<Weather> weathers = new ArrayList<>();

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Builder
  public Diary(String userId, LocalDate date, List<Weather> weathers, String content) {
    this.userId = userId;
    this.date = date;
    this.content = content;
    if (weathers != null) {
      this.weathers = weathers;
    }
  }

  public void updateWeatherAndContent(List<Weather> weathers, String content) {
    if (weathers != null) {
      this.weathers = weathers;
    }
    this.content = content;
  }
}
