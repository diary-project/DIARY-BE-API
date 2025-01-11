/* (C)2025 */
package com.diary.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Health Check", description = "Health Check API")
@RestController
@RequestMapping("/api/health")
public class HealthCheckController {

  @Operation(summary = "Health Check", description = "서버 상태를 확인합니다.")
  @GetMapping
  public ResponseEntity<Map<String, Object>> healthCheck() {
    Map<String, Object> response = new HashMap<>();
    response.put("status", "UP");
    response.put("timestamp", System.currentTimeMillis());

    return ResponseEntity.ok(response);
  }
}
