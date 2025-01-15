/* (C)2025 */
package com.diary.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@EnableScheduling
public class SchedulingConfig {

  @Bean
  public ThreadPoolTaskScheduler taskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(5); // 동시에 실행될 수 있는 최대 스레드 수
    scheduler.setThreadNamePrefix("st-"); // 스레드 이름 접두사
    scheduler.setAwaitTerminationSeconds(60); // 종료 대기 시간
    scheduler.setWaitForTasksToCompleteOnShutdown(true); // 실행 중인 작업 완료 대기
    scheduler.setErrorHandler(
        throwable ->
            System.err.println("Scheduled task error: " + throwable.getMessage())); // 에러 처리
    return scheduler;
  }
}
