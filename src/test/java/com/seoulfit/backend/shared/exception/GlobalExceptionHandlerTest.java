package com.seoulfit.backend.shared.exception;

import com.seoulfit.backend.shared.dto.ErrorResponse;
import com.seoulfit.backend.user.domain.exception.OAuthUserAlreadyExistsException;
import com.seoulfit.backend.user.domain.exception.OAuthUserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * GlobalExceptionHandler 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("GlobalExceptionHandler 단위 테스트")
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;
    
    @Nested
    @DisplayName("유효성 검증 예외 처리")
    class ValidationExceptionTest {
        
        @Test
        @DisplayName("MethodArgumentNotValidException 처리 - 단일 필드 오류")
        void handleValidationExceptions_SingleFieldError() {
            // given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            HttpServletRequest request = mock(HttpServletRequest.class);
            BindingResult bindingResult = mock(BindingResult.class);
            FieldError fieldError = new FieldError("user", "email", "이메일 형식이 올바르지 않습니다");
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));
            when(request.getRequestURI()).thenReturn("/api/test");
            
            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, request);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getMessage()).isEqualTo("입력값 검증에 실패했습니다.");
            assertThat(response.getBody().getCode()).isEqualTo("VALIDATION_FAILED");
            assertThat(response.getBody().getErrors()).hasSize(1);
            assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("email");
            assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("이메일 형식이 올바르지 않습니다");
        }
        
        @Test
        @DisplayName("MethodArgumentNotValidException 처리 - 다중 필드 오류")
        void handleValidationExceptions_MultipleFieldErrors() {
            // given
            MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
            HttpServletRequest request = mock(HttpServletRequest.class);
            BindingResult bindingResult = mock(BindingResult.class);
            
            List<FieldError> fieldErrors = Arrays.asList(
                new FieldError("user", "email", "이메일은 필수입니다"),
                new FieldError("user", "password", "비밀번호는 8자 이상이어야 합니다"),
                new FieldError("user", "nickname", "닉네임은 필수입니다")
            );
            
            when(exception.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);
            when(request.getRequestURI()).thenReturn("/api/test");
            
            // when
            ResponseEntity<ErrorResponse> response = globalExceptionHandler.handleValidationExceptions(exception, request);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().getErrors()).hasSize(3);
            assertThat(response.getBody().getErrors().get(0).getField()).isEqualTo("email");
            assertThat(response.getBody().getErrors().get(0).getMessage()).isEqualTo("이메일은 필수입니다");
            assertThat(response.getBody().getErrors().get(1).getField()).isEqualTo("password");
            assertThat(response.getBody().getErrors().get(1).getMessage()).isEqualTo("비밀번호는 8자 이상이어야 합니다");
            assertThat(response.getBody().getErrors().get(2).getField()).isEqualTo("nickname");
            assertThat(response.getBody().getErrors().get(2).getMessage()).isEqualTo("닉네임은 필수입니다");
        }
    }
    
    @Nested
    @DisplayName("일반 예외 처리")
    class GeneralExceptionTest {
        
        @Test
        @DisplayName("IllegalArgumentException 처리")
        void handleIllegalArgumentException() {
            // given
            String errorMessage = "잘못된 파라미터입니다";
            IllegalArgumentException exception = new IllegalArgumentException(errorMessage);
            
            // when
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleIllegalArgumentException(exception);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
        }
        
        @Test
        @DisplayName("일반 Exception 처리")
        void handleGenericException() {
            // given
            Exception exception = new Exception("예상치 못한 오류");
            
            // when
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(exception);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo("서버 내부 오류가 발생했습니다.");
        }
    }
    
    @Nested
    @DisplayName("OAuth 관련 예외 처리")
    class OAuthExceptionTest {
        
        @Test
        @DisplayName("OAuthUserAlreadyExistsException 처리")
        void handleOAuthUserAlreadyExistsException() {
            // given
            String errorMessage = "이미 존재하는 OAuth 사용자입니다";
            OAuthUserAlreadyExistsException exception = new OAuthUserAlreadyExistsException(errorMessage);
            
            // when
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleOAuthUserAlreadyExistsException(exception);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
            assertThat(response.getBody().get("errorCode")).isEqualTo("OAUTH_USER_ALREADY_EXISTS");
        }
        
        @Test
        @DisplayName("OAuthUserNotFoundException 처리")
        void handleOAuthUserNotFoundException() {
            // given
            String errorMessage = "OAuth 사용자를 찾을 수 없습니다";
            OAuthUserNotFoundException exception = new OAuthUserNotFoundException(errorMessage);
            
            // when
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleOAuthUserNotFoundException(exception);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
            assertThat(response.getBody().get("errorCode")).isEqualTo("OAUTH_USER_NOT_FOUND");
        }
        
        @Test
        @DisplayName("OAuth2AuthenticationException 처리")
        void handleOAuth2AuthenticationException() {
            // given
            OAuth2Error oAuth2Error = new OAuth2Error("invalid_token", "토큰이 만료되었습니다", null);
            OAuth2AuthenticationException exception = new OAuth2AuthenticationException(oAuth2Error);
            
            // when
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleOAuth2AuthenticationException(exception);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            String message = (String) response.getBody().get("message");
            assertThat(message).contains("OAuth 인증에 실패했습니다");
        }
    }
    
    @Nested
    @DisplayName("인증 관련 예외 처리")
    class AuthenticationExceptionTest {
        
        @Test
        @DisplayName("BadCredentialsException 처리")
        void handleBadCredentialsException() {
            // given
            String errorMessage = "아이디 또는 비밀번호가 일치하지 않습니다";
            BadCredentialsException exception = new BadCredentialsException(errorMessage);
            
            // when
            ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleBadCredentialsException(exception);
            
            // then
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(response.getBody()).isNotNull();
            assertThat(response.getBody().get("message")).isEqualTo(errorMessage);
        }
    }
    
    @Nested
    @DisplayName("응답 형식 검증")
    class ResponseFormatTest {
        
        @Test
        @DisplayName("모든 예외 응답에 message 필드 포함")
        void allResponsesContainMessage() {
            // given
            IllegalArgumentException illegalArg = new IllegalArgumentException("테스트");
            BadCredentialsException badCred = new BadCredentialsException("테스트");
            Exception generic = new Exception("테스트");
            
            // when
            ResponseEntity<Map<String, Object>> response1 = globalExceptionHandler.handleIllegalArgumentException(illegalArg);
            ResponseEntity<Map<String, Object>> response2 = globalExceptionHandler.handleBadCredentialsException(badCred);
            ResponseEntity<Map<String, Object>> response3 = globalExceptionHandler.handleGenericException(generic);
            
            // then
            assertThat(response1.getBody()).containsKey("message");
            assertThat(response2.getBody()).containsKey("message");
            assertThat(response3.getBody()).containsKey("message");
        }
        
        @Test
        @DisplayName("OAuth 예외에는 errorCode 포함")
        void oauthExceptionsContainErrorCode() {
            // given
            OAuthUserAlreadyExistsException exists = new OAuthUserAlreadyExistsException("테스트");
            OAuthUserNotFoundException notFound = new OAuthUserNotFoundException("테스트");
            
            // when
            ResponseEntity<Map<String, Object>> response1 = globalExceptionHandler.handleOAuthUserAlreadyExistsException(exists);
            ResponseEntity<Map<String, Object>> response2 = globalExceptionHandler.handleOAuthUserNotFoundException(notFound);
            
            // then
            assertThat(response1.getBody()).containsKey("errorCode");
            assertThat(response2.getBody()).containsKey("errorCode");
        }
    }
    
    @Nested
    @DisplayName("HTTP 상태 코드 검증")
    class HttpStatusTest {
        
        @Test
        @DisplayName("각 예외에 대한 적절한 HTTP 상태 코드")
        void appropriateHttpStatusCodes() {
            // given
            MethodArgumentNotValidException validation = mock(MethodArgumentNotValidException.class);
            HttpServletRequest request = mock(HttpServletRequest.class);
            BindingResult bindingResult = mock(BindingResult.class);
            when(validation.getBindingResult()).thenReturn(bindingResult);
            when(bindingResult.getFieldErrors()).thenReturn(List.of());
            when(request.getRequestURI()).thenReturn("/api/test");
            
            IllegalArgumentException illegalArg = new IllegalArgumentException("테스트");
            OAuthUserAlreadyExistsException exists = new OAuthUserAlreadyExistsException("테스트");
            OAuthUserNotFoundException notFound = new OAuthUserNotFoundException("테스트");
            BadCredentialsException badCred = new BadCredentialsException("테스트");
            OAuth2AuthenticationException oauth2 = new OAuth2AuthenticationException("테스트");
            Exception generic = new Exception("테스트");
            
            // when & then
            assertThat(globalExceptionHandler.handleValidationExceptions(validation, request).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(globalExceptionHandler.handleIllegalArgumentException(illegalArg).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(globalExceptionHandler.handleOAuthUserAlreadyExistsException(exists).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
            assertThat(globalExceptionHandler.handleOAuthUserNotFoundException(notFound).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(globalExceptionHandler.handleBadCredentialsException(badCred).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(globalExceptionHandler.handleOAuth2AuthenticationException(oauth2).getStatusCode())
                .isEqualTo(HttpStatus.UNAUTHORIZED);
            assertThat(globalExceptionHandler.handleGenericException(generic).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}