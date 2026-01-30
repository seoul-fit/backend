package com.seoulfit.backend.shared.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
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
        // 연락처 정보
        Contact contact = new Contact()
                .name("Seoul Fit Development Team")
                .email("contact@seoulfit.com")
                .url("https://github.com/seoulfit/backend");
        
        // 라이선스 정보
        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");
        
        // API 정보
        io.swagger.v3.oas.models.info.Info info = new io.swagger.v3.oas.models.info.Info()
                .title("UrbanPing API")
                .version("1.0.0")
                .description("서울시 공공 데이터를 활용한 실시간 도시 정보 알림 시스템 API\n\n" +
                           "## 🏙️ 주요 기능\n" +
                           "- **사용자 관리**: OAuth 2.0 기반 인증 (카카오, 구글)\n" +
                           "- **맞춤 알림**: 관심사 기반 개인화된 알림 서비스\n" +
                           "- **실시간 데이터**: 대기질, 날씨, 교통 정보 실시간 조회\n" +
                           "- **위치 서비스**: GPS 기반 주변 시설 및 이벤트 검색\n" +
                           "- **트리거 시스템**: 조건 기반 자동 알림 발송\n" +
                           "- **공공시설 정보**: 도서관, 공원, 체육시설, 문화행사 조회\n\n" +
                           "## 🔑 인증 방식\n" +
                           "Bearer Token을 사용합니다. 헤더에 `Authorization: Bearer {your-token}`을 포함해주세요.\n\n" +
                           "## 📊 데이터 출처\n" +
                           "서울시 공공데이터포털, 환경부 대기환경정보, 한국관광공사 등")
                .contact(contact)
                .license(license)
                .termsOfService("https://seoulfit.com/terms");
        
        // 서버 정보
        Server devServer = new Server()
                .url("http://localhost:8080")
                .description("Development Server");
        
        Server prodServer = new Server()
                .url("https://api.seoulfit.com")
                .description("Production Server");
        
        // 보안 스키마
        SecurityScheme bearerAuth = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization")
                .description("JWT 토큰을 입력하세요 (Bearer prefix 제외)");
        
        // Components
        Components components = new Components()
                .addSecuritySchemes("bearerAuth", bearerAuth);
        
        // 외부 문서
        ExternalDocumentation externalDocs = new ExternalDocumentation()
                .description("Seoul Fit API 가이드 문서")
                .url("https://docs.seoulfit.com");

        return new OpenAPI()
                .info(info)
                .addServersItem(devServer)
                .addServersItem(prodServer)
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(components)
                .externalDocs(externalDocs);
    }
}
