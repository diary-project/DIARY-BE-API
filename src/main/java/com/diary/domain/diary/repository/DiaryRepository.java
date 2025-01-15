/* (C)2025 */
package com.diary.domain.diary.repository;

import com.diary.domain.diary.entity.Diary;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
  Optional<Diary> findByIdAndUserId(Long id, String userId);
}
