package myaimentor_api.chat.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot 의 mongo autoconfigure 가 자격 증명 처리에서 SCRAM-SHA-256
 * 인증을 일관되게 하지 못해 명시적으로 MongoClient 를 등록한다.
 * URI 한 줄로만 설정하면 driver 가 알아서 mechanism 을 negotiate.
 */
@Configuration
public class MongoConfig {

	@Bean
	public MongoClient mongoClient(@Value("${spring.data.mongodb.uri}") String uri) {
		MongoClientSettings settings = MongoClientSettings.builder()
				.applyConnectionString(new ConnectionString(uri))
				.build();
		return MongoClients.create(settings);
	}
}
