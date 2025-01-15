/* (C)2025 */
package com.diary.domain.auth.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "refresh_tokens")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefreshToken {

  @Id
  @Column(name = "token_id")
  private String id;

  @Column(name = "user_id", nullable = false)
  private String userId;

  @Column(name = "token", nullable = false)
  private String token;

  @Column(name = "expiry_date", nullable = false)
  private LocalDateTime expiryDate;

  @Builder
  public RefreshToken(String id, String userId, String token, LocalDateTime expiryDate) {
    this.id = id;
    this.userId = userId;
    this.token = token;
    this.expiryDate = expiryDate;
  }

  public boolean isExpired() {
    return LocalDateTime.now().isAfter(expiryDate);
  }

  public void updateToken(String token, LocalDateTime expiryDate) {
    this.token = token;
    this.expiryDate = expiryDate;
  }
}
