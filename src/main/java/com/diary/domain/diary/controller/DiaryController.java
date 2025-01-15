/* (C)2025 */
package com.diary.domain.diary.controller;

import com.diary.domain.diary.dto.DiaryCalendarResponse;
import com.diary.domain.diary.dto.DiaryCreateRequest;
import com.diary.domain.diary.dto.DiaryResponse;
import com.diary.domain.diary.dto.DiaryUpdateRequest;
import com.diary.domain.diary.service.DiaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "일기", description = "일기 관련 API")
@RestController
@RequestMapping("/api/v1/diary")
@RequiredArgsConstructor
public class DiaryController {

  private final DiaryService diaryService;

  @Operation(summary = "일기 작성", description = "새로운 일기를 작성합니다.")
  @PostMapping
  public ResponseEntity<DiaryResponse> createDiary(@RequestBody DiaryCreateRequest request) {
    return ResponseEntity.ok(DiaryResponse.from(diaryService.createDiary(request)));
  }

  @Operation(summary = "일기 조회", description = "특정 날짜의 일기를 조회합니다.")
  @GetMapping("/{date}")
  public ResponseEntity<DiaryResponse> getDiary(
      @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    return ResponseEntity.ok(DiaryResponse.from(diaryService.getDiaryByDate(date)));
  }

  @Operation(summary = "일기 수정", description = "특정 날짜의 일기를 수정합니다.")
  @PutMapping("/{date}")
  public ResponseEntity<DiaryResponse> updateDiary(
      @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
      @RequestBody DiaryUpdateRequest request) {
    return ResponseEntity.ok(DiaryResponse.from(diaryService.updateDiary(date, request)));
  }

  @Operation(summary = "일기 삭제", description = "특정 날짜의 일기를 삭제합니다.")
  @DeleteMapping("/{date}")
  public ResponseEntity<Void> deleteDiary(
      @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date) {
    diaryService.deleteDiary(date);
    return ResponseEntity.ok().build();
  }

  @Operation(summary = "월별 일기 작성일 조회", description = "특정 월에 일기를 작성한 날짜 목록을 조회합니다.")
  @GetMapping("/calendar")
  public ResponseEntity<DiaryCalendarResponse> getMonthlyDiaryDates(
      @RequestParam int year, @RequestParam int month) {
    return ResponseEntity.ok(diaryService.getMonthlyDiaryDates(year, month));
  }
}
