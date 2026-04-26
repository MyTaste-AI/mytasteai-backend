package myaimentor_api.chat.external;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties({
		ExternalServiceProperties.Ai.class,
		ExternalServiceProperties.Mentor.class
})
public class WebClientConfig {

	@Bean
	public WebClient aiWebClient(ExternalServiceProperties.Ai ai) {
		return WebClient.builder().baseUrl(ai.baseUrl()).build();
	}

	@Bean
	public WebClient mentorWebClient(ExternalServiceProperties.Mentor mentor) {
		return WebClient.builder().baseUrl(mentor.baseUrl()).build();
	}
}
