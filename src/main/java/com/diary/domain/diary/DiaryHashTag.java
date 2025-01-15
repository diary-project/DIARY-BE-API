/* (C)2025 */
package com.diary.domain.diary;

import com.diary.domain.common.BaseEntity;
import com.diary.domain.hashtag.HashTag;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "diary_hashtags")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DiaryHashTag extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "diary_id")
  private Diary diary;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hashtag_id")
  private HashTag hashTag;

  @Builder
  public DiaryHashTag(Diary diary, HashTag hashTag) {
    this.diary = diary;
    this.hashTag = hashTag;
  }
}
