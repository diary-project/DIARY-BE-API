/* (C)2025 */
package com.diary.domain.user.entity;

import com.diary.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements UserDetails {

  @Id
  @Column(length = 50)
  private String id;

  @Column(unique = true, nullable = false)
  private String email;

  private String password;

  @Column(nullable = false)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private AuthProvider authProvider;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status = UserStatus.ACTIVE;

  @Column(name = "withdrawal_scheduled_at")
  private LocalDateTime withdrawalScheduledAt;

  @Builder
  public User(
      String id, String email, String password, String name, Role role, AuthProvider authProvider) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.name = name;
    this.role = role;
    this.authProvider = authProvider;
  }

  public static String generateId(AuthProvider provider, String providerId) {
    return switch (provider) {
      case TEST -> "t_" + providerId;
      case KAKAO -> "k_" + providerId;
      case APPLE -> "a_" + providerId;
    };
  }

  public void initiateWithdrawal() {
    this.status = UserStatus.WITHDRAWAL_REQUESTED;
    this.withdrawalScheduledAt = LocalDateTime.now().plusDays(30);
  }

  public void cancelWithdrawal() {
    this.status = UserStatus.ACTIVE;
    this.withdrawalScheduledAt = null;
  }

  public void completeWithdrawal() {
    this.status = UserStatus.WITHDRAWN;
    this.softDelete();
  }

  public boolean canLogin() {
    return this.status != UserStatus.WITHDRAWN;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return !isDeleted() && this.status != UserStatus.WITHDRAWN;
  }
}
