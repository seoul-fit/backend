package com.seoulfit.backend.shared.exception;

/**
 * 비즈니스 로직 처리 중 발생하는 예외의 기본 클래스
 * 
 * 모든 비즈니스 예외는 이 클래스를 상속받아 구현합니다.
 * HTTP 상태 코드와 에러 코드를 포함하여 일관된 에러 응답을 제공합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public abstract class BusinessException extends RuntimeException {
    
    private final String errorCode;
    private final int httpStatus;

    /**
     * 비즈니스 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드 (예: USER_NOT_FOUND)
     * @param message 사용자에게 표시될 메시지
     * @param httpStatus HTTP 상태 코드
     */
    protected BusinessException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * 원인이 있는 비즈니스 예외를 생성합니다.
     * 
     * @param errorCode 에러 코드 (예: USER_NOT_FOUND)
     * @param message 사용자에게 표시될 메시지
     * @param httpStatus HTTP 상태 코드
     * @param cause 원인 예외
     */
    protected BusinessException(String errorCode, String message, int httpStatus, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    /**
     * 에러 코드를 반환합니다.
     * 
     * @return 에러 코드
     */
    public String getErrorCode() {
        return errorCode;
    }

    /**
     * HTTP 상태 코드를 반환합니다.
     * 
     * @return HTTP 상태 코드
     */
    public int getHttpStatus() {
        return httpStatus;
    }
}