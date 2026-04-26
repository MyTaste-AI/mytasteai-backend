package myaimentor_api.mentor.ai;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 마이크로서비스(FastAPI) 접속 설정 — application-*.yml 의 ai.service.* 와 매핑.
 */
@ConfigurationProperties(prefix = "ai.service")
public record AiServiceProperties(String baseUrl) {
}
