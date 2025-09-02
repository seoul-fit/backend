package com.seoulfit.backend.shared.exception;

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 * 
 * HTTP 404 Not Found 상태 코드를 반환합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public class ResourceNotFoundException extends BusinessException {

    /**
     * 리소스와 식별자를 포함한 예외를 생성합니다.
     * 
     * @param resource 리소스 이름 (예: "사용자", "문화행사")
     * @param identifier 식별자 (예: ID, 이메일)
     */
    public ResourceNotFoundException(String resource, String identifier) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s을(를) 찾을 수 없습니다. (ID: %s)", resource, identifier), 
              404);
    }

    /**
     * 리소스 이름만으로 예외를 생성합니다.
     * 
     * @param resource 리소스 이름 (예: "사용자", "문화행사")
     */
    public ResourceNotFoundException(String resource) {
        super("RESOURCE_NOT_FOUND", 
              String.format("%s을(를) 찾을 수 없습니다.", resource), 
              404);
    }

    /**
     * 커스텀 메시지로 예외를 생성합니다.
     * 
     * @param message 커스텀 메시지
     */
    public static ResourceNotFoundException withMessage(String message) {
        return new ResourceNotFoundException(message, null);
    }

    private ResourceNotFoundException(String message, Object unused) {
        super("RESOURCE_NOT_FOUND", message, 404);
    }
}