package myaimentor_api.chat.external;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 외부 서비스 base URL 묶음.
 * - ai.service.base-url   : FastAPI AI 서비스
 * - mentor.service.base-url : Spring mentor 서비스 (봇 정보 조회용)
 */
public class ExternalServiceProperties {

	@ConfigurationProperties(prefix = "ai.service")
	public record Ai(String baseUrl) {
	}

	@ConfigurationProperties(prefix = "mentor.service")
	public record Mentor(String baseUrl) {
	}
}
