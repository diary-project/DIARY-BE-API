/* (C)2025 */
package com.diary.domain.common;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalDate createDate;

  @CreatedDate
  @Column(nullable = false, updatable = false)
  private LocalTime createTime;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalDate updateDate;

  @LastModifiedDate
  @Column(nullable = false)
  private LocalTime updateTime;
}
