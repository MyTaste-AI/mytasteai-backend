package myaimentor_api.mentor.ai;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * AI 서비스 호출용 WebClient 빈 등록.
 */
@Configuration
@EnableConfigurationProperties(AiServiceProperties.class)
public class AiWebClientConfig {

	@Bean
	public WebClient aiServiceWebClient(AiServiceProperties properties) {
		return WebClient.builder()
				.baseUrl(properties.baseUrl())
				.build();
	}
}
