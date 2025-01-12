/* (C)2025 */
package com.diary.domain.diary;

import com.diary.domain.common.BaseEntity;
import com.diary.domain.hashtag.HashTag;
import com.diary.domain.weather.Weather;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "diaries")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Diary extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private LocalDate date;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @Column(length = 255)
  private String image;

  @ElementCollection
  @Enumerated(EnumType.STRING)
  @CollectionTable(name = "diary_weathers", joinColumns = @JoinColumn(name = "diary_id"))
  @Column(name = "weather")
  private List<Weather> weathers = new ArrayList<>();

  @ManyToMany
  @JoinTable(
      name = "diary_hashtags",
      joinColumns = @JoinColumn(name = "diary_id"),
      inverseJoinColumns = @JoinColumn(name = "hashtag_id"))
  private List<HashTag> hashtags = new ArrayList<>();

  @Builder
  public Diary(
      LocalDate date,
      String comment,
      String image,
      List<Weather> weathers,
      List<HashTag> hashtags) {
    this.date = date;
    this.comment = comment;
    this.image = image;
    if (weathers != null) {
      this.weathers = weathers;
    }
    if (hashtags != null) {
      this.hashtags = hashtags;
    }
  }
}
