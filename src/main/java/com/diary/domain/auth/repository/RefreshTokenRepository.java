/* (C)2025 */
package com.diary.domain.auth.repository;

import com.diary.domain.auth.entity.RefreshToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
  Optional<RefreshToken> findByUserId(String userId);

  Optional<RefreshToken> findByToken(String token);

  void deleteByUserId(String userId);
}
