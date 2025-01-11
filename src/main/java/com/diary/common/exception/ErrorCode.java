/* (C)2025 */
package com.diary.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
  // Common
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST.value(), "잘못된 입력값입니다"),
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버 내부 오류가 발생했습니다"),

  // Auth
  INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED.value(), "아이디 또는 비밀번호가 올바르지 않습니다"),
  UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED.value(), "인증이 필요합니다"),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 토큰입니다"),
  TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다"),

  // User
  DUPLICATE_EMAIL(HttpStatus.CONFLICT.value(), "이미 사용 중인 이메일입니다"),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다");

  private final int status;
  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
