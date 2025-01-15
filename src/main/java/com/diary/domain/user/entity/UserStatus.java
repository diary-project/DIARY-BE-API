/* (C)2025 */
package com.diary.domain.user.entity;

public enum UserStatus {
  ACTIVE, // 활성 상태
  WITHDRAWAL_REQUESTED, // 탈퇴 요청 상태 (30일 유예 기간)
  WITHDRAWN // 탈퇴 완료 상태
}
