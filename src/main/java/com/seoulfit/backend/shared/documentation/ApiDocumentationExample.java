package com.seoulfit.backend.shared.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * API 문서화 예제 컨트롤러
 * 
 * OpenAPI 3.0 어노테이션 사용 예제를 보여줍니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/example")
@Tag(name = "예제 API", description = "API 문서화 예제를 위한 컨트롤러")
public class ApiDocumentationExample {

    /**
     * 상세한 API 문서화 예제
     */
    @Operation(
        summary = "공원 정보 조회",
        description = """
            지정된 ID의 공원 정보를 조회합니다.
            
            ### 주요 기능
            - 공원 기본 정보 조회
            - 시설 정보 포함
            - 실시간 혼잡도 정보
            
            ### 비즈니스 규칙
            - 삭제된 공원은 조회되지 않음
            - 비공개 공원은 관리자만 조회 가능
            """,
        tags = {"공원"},
        security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ParkResponse.class),
                examples = {
                    @ExampleObject(
                        name = "성공 예제",
                        summary = "정상적인 공원 정보",
                        value = """
                            {
                              "success": true,
                              "data": {
                                "id": 1,
                                "name": "서울숲",
                                "address": "서울특별시 성동구 뚝섬로 273",
                                "area": 1156498,
                                "facilities": ["산책로", "자전거도로", "놀이터"],
                                "congestionLevel": "NORMAL",
                                "latitude": 37.5444,
                                "longitude": 127.0374
                              },
                              "message": "조회 성공"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "공원을 찾을 수 없음",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "success": false,
                          "message": "ID 999에 해당하는 공원을 찾을 수 없습니다",
                          "code": "PARK_NOT_FOUND"
                        }
                        """
                )
            )
        )
    })
    @GetMapping("/parks/{id}")
    public ResponseEntity<ParkResponse> getPark(
        @Parameter(
            description = "공원 ID",
            required = true,
            example = "1",
            schema = @Schema(type = "integer", minimum = "1")
        )
        @PathVariable Long id,
        
        @Parameter(
            description = "상세 정보 포함 여부",
            required = false,
            in = ParameterIn.QUERY,
            schema = @Schema(type = "boolean", defaultValue = "false")
        )
        @RequestParam(defaultValue = "false") boolean includeDetails
    ) {
        // 구현 코드
        return ResponseEntity.ok(new ParkResponse());
    }

    /**
     * 페이징 처리 API 예제
     */
    @Operation(
        summary = "공원 목록 조회",
        description = "페이징과 필터링을 지원하는 공원 목록 조회"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ParkResponse.class))
            )
        )
    })
    @GetMapping("/parks")
    public ResponseEntity<List<ParkResponse>> getParks(
        @Parameter(description = "구 이름", example = "강남구")
        @RequestParam(required = false) String district,
        
        @Parameter(description = "최소 면적 (제곱미터)", example = "10000")
        @RequestParam(required = false) Integer minArea,
        
        @Parameter(description = "페이지 번호", example = "0")
        @RequestParam(defaultValue = "0") int page,
        
        @Parameter(description = "페이지 크기", example = "20")
        @RequestParam(defaultValue = "20") int size,
        
        @Parameter(
            description = "정렬 기준",
            example = "name,asc",
            schema = @Schema(
                type = "string",
                allowableValues = {"name,asc", "name,desc", "area,asc", "area,desc"}
            )
        )
        @RequestParam(defaultValue = "name,asc") String sort
    ) {
        // 구현 코드
        return ResponseEntity.ok(List.of());
    }

    /**
     * 요청 본문이 있는 API 예제
     */
    @Operation(
        summary = "공원 생성",
        description = "새로운 공원 정보를 등록합니다 (관리자 전용)"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "생성 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ParkResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    value = """
                        {
                          "success": false,
                          "message": "유효성 검증 실패",
                          "errors": {
                            "name": "공원 이름은 필수입니다",
                            "area": "면적은 0보다 커야 합니다"
                          }
                        }
                        """
                )
            )
        )
    })
    @PostMapping("/parks")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ParkResponse> createPark(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "공원 생성 정보",
            required = true,
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CreateParkRequest.class),
                examples = {
                    @ExampleObject(
                        name = "기본 예제",
                        summary = "필수 필드만 포함",
                        value = """
                            {
                              "name": "새로운 공원",
                              "address": "서울특별시 강남구",
                              "area": 50000,
                              "latitude": 37.5172,
                              "longitude": 127.0473
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "전체 예제",
                        summary = "모든 필드 포함",
                        value = """
                            {
                              "name": "새로운 공원",
                              "address": "서울특별시 강남구",
                              "area": 50000,
                              "facilities": ["산책로", "운동장", "화장실"],
                              "description": "도심 속 휴식 공간",
                              "openTime": "06:00",
                              "closeTime": "22:00",
                              "latitude": 37.5172,
                              "longitude": 127.0473
                            }
                            """
                    )
                }
            )
        )
        @RequestBody CreateParkRequest request
    ) {
        // 구현 코드
        return ResponseEntity.status(201).body(new ParkResponse());
    }

    /**
     * 파일 업로드 API 예제
     */
    @Operation(
        summary = "공원 이미지 업로드",
        description = "공원 이미지를 업로드합니다"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "업로드 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ImageUploadResponse.class)
            )
        )
    })
    @PostMapping(value = "/parks/{id}/images", consumes = "multipart/form-data")
    public ResponseEntity<ImageUploadResponse> uploadImage(
        @PathVariable Long id,
        
        @Parameter(
            description = "이미지 파일",
            required = true,
            content = @Content(mediaType = "multipart/form-data")
        )
        @RequestParam("file") org.springframework.web.multipart.MultipartFile file,
        
        @Parameter(description = "이미지 설명")
        @RequestParam(required = false) String description
    ) {
        // 구현 코드
        return ResponseEntity.ok(new ImageUploadResponse());
    }
}

// DTO 클래스들

@Schema(description = "공원 응답 정보")
@lombok.Data
class ParkResponse {
    @Schema(description = "공원 ID", example = "1")
    private Long id;
    
    @Schema(description = "공원 이름", example = "서울숲", required = true)
    private String name;
    
    @Schema(description = "주소", example = "서울특별시 성동구 뚝섬로 273")
    private String address;
    
    @Schema(description = "면적 (제곱미터)", example = "1156498", minimum = "0")
    private Integer area;
    
    @Schema(description = "시설 목록", example = "[\"산책로\", \"자전거도로\", \"놀이터\"]")
    private List<String> facilities;
    
    @Schema(description = "혼잡도", allowableValues = {"LOW", "NORMAL", "HIGH", "VERY_HIGH"})
    private String congestionLevel;
    
    @Schema(description = "위도", example = "37.5444", minimum = "-90", maximum = "90")
    private Double latitude;
    
    @Schema(description = "경도", example = "127.0374", minimum = "-180", maximum = "180")
    private Double longitude;
}

@Schema(description = "공원 생성 요청")
@lombok.Data
class CreateParkRequest {
    @Schema(description = "공원 이름", example = "새로운 공원", required = true, minLength = 1, maxLength = 100)
    private String name;
    
    @Schema(description = "주소", example = "서울특별시 강남구", required = true)
    private String address;
    
    @Schema(description = "면적 (제곱미터)", example = "50000", required = true, minimum = "1")
    private Integer area;
    
    @Schema(description = "시설 목록", example = "[\"산책로\", \"운동장\"]")
    private List<String> facilities;
    
    @Schema(description = "설명", example = "도심 속 휴식 공간", maxLength = 500)
    private String description;
    
    @Schema(description = "개장 시간", example = "06:00", pattern = "^([01]\\d|2[0-3]):([0-5]\\d)$")
    private String openTime;
    
    @Schema(description = "폐장 시간", example = "22:00", pattern = "^([01]\\d|2[0-3]):([0-5]\\d)$")
    private String closeTime;
    
    @Schema(description = "위도", example = "37.5172", required = true)
    private Double latitude;
    
    @Schema(description = "경도", example = "127.0473", required = true)
    private Double longitude;
}

@Schema(description = "이미지 업로드 응답")
@lombok.Data
class ImageUploadResponse {
    @Schema(description = "이미지 URL", example = "https://cdn.seoulfit.com/parks/1/image.jpg")
    private String imageUrl;
    
    @Schema(description = "파일 크기 (bytes)", example = "2048576")
    private Long fileSize;
    
    @Schema(description = "업로드 시간", example = "2024-01-01T12:00:00")
    private String uploadedAt;
}