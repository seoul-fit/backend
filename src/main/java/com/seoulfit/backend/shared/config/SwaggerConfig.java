package com.seoulfit.backend.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger/OpenAPI 설정 클래스입니다.
 * 
 * <p>UrbanPing API의 문서화를 위한 Swagger UI 설정을 담당합니다.
 * API 문서는 개발자들이 API를 쉽게 이해하고 테스트할 수 있도록 도와줍니다.</p>
 * 
 * <p>주요 기능:</p>
 * <ul>
 *   <li>API 문서 메타데이터 설정</li>
 *   <li>Bearer 토큰 인증 설정</li>
 *   <li>Swagger UI 커스터마이징</li>
 * </ul>
 * 
 * <p>Swagger UI는 다음 URL에서 접근할 수 있습니다:</p>
 * <ul>
 *   <li>Swagger UI: http://localhost:8080/swagger-ui.html</li>
 *   <li>API Docs: http://localhost:8080/api-docs</li>
 * </ul>
 * 
 * @author Seoul Fit
 * @version 1.0.0
 * @since 2025-01-01
 * @see OpenAPI
 * @see OpenAPIDefinition
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(title = "UrbanPing API Documentation", version = "1.0")
)
public class SwaggerConfig {

    /**
     * 커스텀 OpenAPI 설정을 생성합니다.
     * 
     * <p>API 문서의 기본 정보와 보안 설정을 구성합니다.
     * Bearer 토큰 인증을 지원하여 JWT 토큰을 사용한 API 테스트가 가능합니다.</p>
     * 
     * @return 설정된 OpenAPI 객체
     */
    @Bean
    public OpenAPI customOpenAPI() {
        io.swagger.v3.oas.models.info.Info info = new io.swagger.v3.oas.models.info.Info()
                .title("UrbanPing API")
                .version("1.0.0")
                .description("서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템 API\n\n" +
                           "주요 기능:\n" +
                           "- 사용자 관리 및 OAuth 인증\n" +
                           "- 관심사 기반 맞춤 알림\n" +
                           "- 실시간 도시 데이터 조회\n" +
                           "- 위치 기반 서비스\n" +
                           "- 트리거 시스템 관리");

        return new OpenAPI()
                .info(info)
                .addSecurityItem(
                        new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components());
    }
}
