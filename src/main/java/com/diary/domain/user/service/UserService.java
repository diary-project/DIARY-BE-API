/* (C)2025 */
package com.diary.domain.user.service;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.domain.auth.repository.RefreshTokenRepository;
import com.diary.domain.user.entity.User;
import com.diary.domain.user.entity.UserStatus;
import com.diary.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;

  @Transactional
  public void initiateWithdrawal(String userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    user.initiateWithdrawal();
    refreshTokenRepository.deleteByUserId(userId);

    log.info("User withdrawal initiated: {}", userId);
  }

  @Transactional
  public void cancelWithdrawal(String userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    if (user.getStatus() != UserStatus.WITHDRAWAL_REQUESTED) {
      throw new BusinessException(ErrorCode.INVALID_STATUS_FOR_CANCEL_WITHDRAWAL);
    }

    user.cancelWithdrawal();
    log.info("User withdrawal cancelled: {}", userId);
  }

  @Transactional
  @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
  public void processScheduledWithdrawals() {
    log.info("Starting scheduled withdrawal process");
    LocalDateTime now = LocalDateTime.now();

    try {
      List<User> withdrawalUsers =
          userRepository.findByStatusAndWithdrawalScheduledAtBefore(
              UserStatus.WITHDRAWAL_REQUESTED, now);

      log.info("Found {} users to process for withdrawal", withdrawalUsers.size());

      for (User user : withdrawalUsers) {
        try {
          user.completeWithdrawal();
          log.info("User withdrawal completed: {}", user.getId());
        } catch (Exception e) {
          log.error("Error processing withdrawal for user {}: {}", user.getId(), e.getMessage());
        }
      }

      log.info("Completed scheduled withdrawal process");
    } catch (Exception e) {
      log.error("Error during scheduled withdrawal process: {}", e.getMessage(), e);
    }
  }

  @Transactional(readOnly = true)
  public User getUser(String userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
  }
}
