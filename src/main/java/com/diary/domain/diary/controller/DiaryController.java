/* (C)2025 */
package com.diary.domain.diary.controller;

import com.diary.domain.diary.entity.Diary;
import com.diary.domain.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "일기", description = "일기 관련 API")
@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryController {

  private final DiaryService diaryService;

  @Operation(summary = "일기 조회", description = "특정 일기를 조회합니다.")
  @GetMapping("/{id}")
  public Diary getDiary(@PathVariable Long id) {
    return diaryService.getDiaryById(id);
  }
}
