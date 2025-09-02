package com.seoulfit.backend.shared.documentation;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.media.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Postman Collection 자동 생성 서비스
 * 
 * OpenAPI 스펙을 기반으로 Postman Collection을 자동 생성합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PostmanCollectionGenerator {

    private final ObjectMapper objectMapper;
    private final OpenAPI openAPI;

    /**
     * Postman Collection 생성
     */
    public PostmanCollection generateCollection() {
        PostmanCollection collection = new PostmanCollection();
        collection.info = createInfo();
        collection.item = new ArrayList<>();
        collection.variable = createVariables();
        collection.auth = createAuth();

        // API 경로별로 아이템 생성
        openAPI.getPaths().forEach((path, pathItem) -> {
            PostmanFolder folder = createFolderForPath(path, pathItem);
            collection.item.add(folder);
        });

        // 이벤트 스크립트 추가
        collection.event = createCollectionEvents();

        return collection;
    }

    /**
     * Collection 정보 생성
     */
    private PostmanInfo createInfo() {
        PostmanInfo info = new PostmanInfo();
        info.name = "Seoul Fit API";
        info.description = openAPI.getInfo().getDescription();
        info.schema = "https://schema.getpostman.com/json/collection/v2.1.0/collection.json";
        info._postman_id = UUID.randomUUID().toString();
        return info;
    }

    /**
     * 환경 변수 생성
     */
    private List<PostmanVariable> createVariables() {
        List<PostmanVariable> variables = new ArrayList<>();
        
        variables.add(createVariable("baseUrl", "http://localhost:8080", "API 서버 URL"));
        variables.add(createVariable("apiVersion", "v1", "API 버전"));
        variables.add(createVariable("authToken", "", "JWT 인증 토큰"));
        variables.add(createVariable("adminToken", "", "관리자 JWT 토큰"));
        
        return variables;
    }

    /**
     * 인증 설정
     */
    private PostmanAuth createAuth() {
        PostmanAuth auth = new PostmanAuth();
        auth.type = "bearer";
        auth.bearer = List.of(
            Map.of("key", "token", "value", "{{authToken}}", "type", "string")
        );
        return auth;
    }

    /**
     * 경로별 폴더 생성
     */
    private PostmanFolder createFolderForPath(String path, PathItem pathItem) {
        PostmanFolder folder = new PostmanFolder();
        folder.name = extractResourceName(path);
        folder.item = new ArrayList<>();
        folder.description = "Endpoints for " + folder.name;

        // HTTP 메서드별 요청 생성
        if (pathItem.getGet() != null) {
            folder.item.add(createRequest("GET", path, pathItem.getGet()));
        }
        if (pathItem.getPost() != null) {
            folder.item.add(createRequest("POST", path, pathItem.getPost()));
        }
        if (pathItem.getPut() != null) {
            folder.item.add(createRequest("PUT", path, pathItem.getPut()));
        }
        if (pathItem.getDelete() != null) {
            folder.item.add(createRequest("DELETE", path, pathItem.getDelete()));
        }
        if (pathItem.getPatch() != null) {
            folder.item.add(createRequest("PATCH", path, pathItem.getPatch()));
        }

        return folder;
    }

    /**
     * Postman 요청 생성
     */
    private PostmanRequest createRequest(String method, String path, Operation operation) {
        PostmanRequest request = new PostmanRequest();
        request.name = operation.getSummary() != null ? operation.getSummary() : method + " " + path;
        request.description = operation.getDescription();
        
        PostmanRequestDetails details = new PostmanRequestDetails();
        details.method = method;
        details.url = createUrl(path);
        details.header = createHeaders(operation);
        details.body = createBody(method, operation);
        details.auth = createRequestAuth(operation);
        
        request.request = details;
        request.response = createExampleResponses(operation);
        request.event = createRequestEvents(operation);

        return request;
    }

    /**
     * URL 생성
     */
    private PostmanUrl createUrl(String path) {
        PostmanUrl url = new PostmanUrl();
        url.raw = "{{baseUrl}}/api/{{apiVersion}}" + path;
        url.host = List.of("{{baseUrl}}");
        url.path = new ArrayList<>();
        
        String[] pathSegments = path.split("/");
        for (String segment : pathSegments) {
            if (!segment.isEmpty()) {
                if (segment.startsWith("{") && segment.endsWith("}")) {
                    // Path parameter
                    url.path.add(":" + segment.substring(1, segment.length() - 1));
                } else {
                    url.path.add(segment);
                }
            }
        }
        
        url.variable = createPathVariables(path);
        url.query = createQueryParameters(path);
        
        return url;
    }

    /**
     * 헤더 생성
     */
    private List<PostmanHeader> createHeaders(Operation operation) {
        List<PostmanHeader> headers = new ArrayList<>();
        
        headers.add(new PostmanHeader("Content-Type", "application/json"));
        headers.add(new PostmanHeader("Accept", "application/json"));
        
        // 인증이 필요한 경우
        if (operation.getSecurity() != null && !operation.getSecurity().isEmpty()) {
            headers.add(new PostmanHeader("Authorization", "Bearer {{authToken}}"));
        }
        
        return headers;
    }

    /**
     * 요청 본문 생성
     */
    private PostmanBody createBody(String method, Operation operation) {
        if (!method.equals("POST") && !method.equals("PUT") && !method.equals("PATCH")) {
            return null;
        }
        
        PostmanBody body = new PostmanBody();
        body.mode = "raw";
        body.raw = generateExampleRequestBody(operation);
        body.options = Map.of("raw", Map.of("language", "json"));
        
        return body;
    }

    /**
     * 예제 응답 생성
     */
    private List<PostmanResponse> createExampleResponses(Operation operation) {
        List<PostmanResponse> responses = new ArrayList<>();
        
        if (operation.getResponses() != null) {
            operation.getResponses().forEach((statusCode, apiResponse) -> {
                PostmanResponse response = new PostmanResponse();
                response.name = statusCode + " - " + apiResponse.getDescription();
                response.status = statusCode;
                response.code = Integer.parseInt(statusCode);
                response.body = generateExampleResponseBody(apiResponse);
                responses.add(response);
            });
        }
        
        return responses;
    }

    /**
     * Collection 이벤트 스크립트
     */
    private List<PostmanEvent> createCollectionEvents() {
        List<PostmanEvent> events = new ArrayList<>();
        
        // Pre-request script
        PostmanEvent preRequest = new PostmanEvent();
        preRequest.listen = "prerequest";
        preRequest.script = new PostmanScript();
        preRequest.script.type = "text/javascript";
        preRequest.script.exec = List.of(
            "// 토큰 자동 갱신 체크",
            "const tokenExpiry = pm.globals.get('tokenExpiry');",
            "if (tokenExpiry && new Date(tokenExpiry) < new Date()) {",
            "    console.log('Token expired, refreshing...');",
            "    // 토큰 갱신 로직",
            "}"
        );
        events.add(preRequest);
        
        // Test script
        PostmanEvent test = new PostmanEvent();
        test.listen = "test";
        test.script = new PostmanScript();
        test.script.type = "text/javascript";
        test.script.exec = List.of(
            "// 응답 시간 체크",
            "pm.test('Response time is less than 1000ms', function () {",
            "    pm.expect(pm.response.responseTime).to.be.below(1000);",
            "});",
            "",
            "// 응답 형식 체크",
            "pm.test('Response has valid format', function () {",
            "    const jsonData = pm.response.json();",
            "    pm.expect(jsonData).to.have.property('success');",
            "    pm.expect(jsonData).to.have.property('data');",
            "});"
        );
        events.add(test);
        
        return events;
    }

    /**
     * 요청별 이벤트 스크립트
     */
    private List<PostmanEvent> createRequestEvents(Operation operation) {
        List<PostmanEvent> events = new ArrayList<>();
        
        // 로그인 엔드포인트의 경우 토큰 저장 스크립트 추가
        if (operation.getOperationId() != null && operation.getOperationId().contains("login")) {
            PostmanEvent test = new PostmanEvent();
            test.listen = "test";
            test.script = new PostmanScript();
            test.script.type = "text/javascript";
            test.script.exec = List.of(
                "// 로그인 성공 시 토큰 저장",
                "if (pm.response.code === 200) {",
                "    const jsonData = pm.response.json();",
                "    if (jsonData.data && jsonData.data.token) {",
                "        pm.environment.set('authToken', jsonData.data.token);",
                "        console.log('Token saved to environment');",
                "    }",
                "}"
            );
            events.add(test);
        }
        
        return events;
    }

    /**
     * Postman Collection 파일로 저장
     */
    public void saveToFile(PostmanCollection collection, String filePath) throws IOException {
        File file = new File(filePath);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, collection);
        log.info("Postman collection saved to: {}", filePath);
    }

    // Helper methods
    
    private String extractResourceName(String path) {
        String[] segments = path.split("/");
        for (String segment : segments) {
            if (!segment.isEmpty() && !segment.startsWith("{")) {
                return segment.substring(0, 1).toUpperCase() + segment.substring(1);
            }
        }
        return "Unknown";
    }

    private PostmanVariable createVariable(String key, String value, String description) {
        PostmanVariable var = new PostmanVariable();
        var.key = key;
        var.value = value;
        var.description = description;
        return var;
    }

    private List<PostmanPathVariable> createPathVariables(String path) {
        // Path variables 추출 및 생성
        return new ArrayList<>();
    }

    private List<PostmanQueryParam> createQueryParameters(String path) {
        // Query parameters 추출 및 생성
        return new ArrayList<>();
    }

    private PostmanAuth createRequestAuth(Operation operation) {
        // 요청별 인증 설정
        return null;
    }

    private String generateExampleRequestBody(Operation operation) {
        // OpenAPI 스펙에서 예제 요청 본문 생성
        return "{\n  \"example\": \"data\"\n}";
    }

    private String generateExampleResponseBody(io.swagger.v3.oas.models.responses.ApiResponse response) {
        // OpenAPI 스펙에서 예제 응답 본문 생성
        return "{\n  \"success\": true,\n  \"data\": {},\n  \"message\": \"Success\"\n}";
    }

    // Postman Collection 데이터 모델
    
    public static class PostmanCollection {
        public PostmanInfo info;
        public List<Object> item;
        public List<PostmanVariable> variable;
        public PostmanAuth auth;
        public List<PostmanEvent> event;
    }

    public static class PostmanInfo {
        public String _postman_id;
        public String name;
        public String description;
        public String schema;
    }

    public static class PostmanFolder {
        public String name;
        public String description;
        public List<PostmanRequest> item;
    }

    public static class PostmanRequest {
        public String name;
        public String description;
        public PostmanRequestDetails request;
        public List<PostmanResponse> response;
        public List<PostmanEvent> event;
    }

    public static class PostmanRequestDetails {
        public String method;
        public PostmanUrl url;
        public List<PostmanHeader> header;
        public PostmanBody body;
        public PostmanAuth auth;
    }

    public static class PostmanUrl {
        public String raw;
        public List<String> host;
        public List<String> path;
        public List<PostmanPathVariable> variable;
        public List<PostmanQueryParam> query;
    }

    public static class PostmanHeader {
        public String key;
        public String value;
        public String description;
        
        public PostmanHeader(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    public static class PostmanBody {
        public String mode;
        public String raw;
        public Map<String, Object> options;
    }

    public static class PostmanAuth {
        public String type;
        public List<Map<String, String>> bearer;
    }

    public static class PostmanVariable {
        public String key;
        public String value;
        public String description;
    }

    public static class PostmanPathVariable {
        public String key;
        public String value;
    }

    public static class PostmanQueryParam {
        public String key;
        public String value;
        public boolean disabled;
    }

    public static class PostmanResponse {
        public String name;
        public String status;
        public int code;
        public String body;
    }

    public static class PostmanEvent {
        public String listen;
        public PostmanScript script;
    }

    public static class PostmanScript {
        public String type;
        public List<String> exec;
    }
}