/* (C)2025 */
package com.diary.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
@EnableJpaAuditing
@ConditionalOnProperty(
    prefix = "spring.jpa",
    name = "auditing.enabled",
    havingValue = "true",
    matchIfMissing = true)
public class JpaAuditingConfig {}
