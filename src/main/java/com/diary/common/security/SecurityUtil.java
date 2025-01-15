/* (C)2025 */
package com.diary.common.security;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.domain.user.entity.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {
  private SecurityUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static User getCurrentUser() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || authentication.getPrincipal() == null) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    if (!(authentication.getPrincipal() instanceof User)) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
    }

    return (User) authentication.getPrincipal();
  }

  public static String getCurrentUserId() {
    return getCurrentUser().getId();
  }

  public static String getCurrentUserEmail() {
    return getCurrentUser().getEmail();
  }
}
