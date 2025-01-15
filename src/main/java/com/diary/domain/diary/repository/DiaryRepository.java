/* (C)2025 */
package com.diary.domain.diary.repository;

import com.diary.domain.diary.entity.Diary;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
  Optional<Diary> findByIdAndUserIdAndIsDeletedFalse(Long id, String userId);

  Optional<Diary> findByUserIdAndDateAndIsDeletedFalse(String userId, LocalDate date);

  boolean existsByUserIdAndDateAndIsDeletedFalse(String userId, LocalDate date);

  List<Diary> findByUserIdAndDateBetweenAndIsDeletedFalse(
      String userId, LocalDate startDate, LocalDate endDate);

  @Query("SELECT MIN(d.date) FROM Diary d WHERE d.userId = :userId AND d.isDeleted = false")
  Optional<LocalDate> findOldestDiaryDate(@Param("userId") String userId);
}
