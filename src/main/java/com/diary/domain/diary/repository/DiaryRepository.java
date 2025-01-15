/* (C)2025 */
package com.diary.domain.diary.repository;

import com.diary.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiaryRepository extends JpaRepository<Diary, Long> {}
