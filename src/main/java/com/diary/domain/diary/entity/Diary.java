/* (C)2025 */
package com.diary.domain.diary.entity;

import com.diary.domain.common.BaseEntity;
import com.diary.domain.hashtag.HashTag;
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

  @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<DiaryHashTag> diaryHashTags = new ArrayList<>();

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
      hashtags.forEach(this::addHashTag);
    }
  }

  public void addHashTag(HashTag hashTag) {
    DiaryHashTag diaryHashTag = DiaryHashTag.builder().diary(this).hashTag(hashTag).build();
    this.diaryHashTags.add(diaryHashTag);
  }

  public void removeHashTag(HashTag hashTag) {
    this.diaryHashTags.removeIf(diaryHashTag -> diaryHashTag.getHashTag().equals(hashTag));
  }
}
