/* (C)2025 */
package com.diary.domain.diary.service;

import com.diary.common.exception.BusinessException;
import com.diary.common.exception.ErrorCode;
import com.diary.common.security.SecurityUtil;
import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.repository.DiaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DiaryService {

  private final DiaryRepository diaryRepository;

  @Transactional(readOnly = true)
  public Diary getDiaryById(Long id) {
    return diaryRepository
        .findByIdAndUserId(id, SecurityUtil.getCurrentUserId())
        .orElseThrow(() -> new BusinessException(ErrorCode.DIARY_NOT_FOUND));
  }
}
