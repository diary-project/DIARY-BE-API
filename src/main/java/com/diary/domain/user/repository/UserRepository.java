/* (C)2025 */
package com.diary.domain.user.repository;

import com.diary.domain.user.entity.User;
import com.diary.domain.user.entity.UserStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  List<User> findByStatusAndWithdrawalScheduledAtBefore(UserStatus status, LocalDateTime dateTime);
}
