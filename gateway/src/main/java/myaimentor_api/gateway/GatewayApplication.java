package myaimentor_api.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Gateway 는 reactive 모듈이라 common 의 servlet 기반 컴포넌트
 * (JwtAuthenticationFilter, GlobalExceptionHandler) 를 픽업하면 안 된다.
 * 따라서 scanBasePackages 는 gateway 자체와 common.auth 만 포함.
 */
@SpringBootApplication(scanBasePackages = {"myaimentor_api.gateway", "myaimentor_api.common.auth"})
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

}
