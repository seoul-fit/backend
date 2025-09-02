package com.seoulfit.backend.shared.versioning;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * API 버전 관리 어노테이션
 * 
 * 컨트롤러 클래스나 메서드에 적용하여 API 버전을 지정합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiVersion {
    
    /**
     * 지원하는 API 버전 목록
     */
    String[] value() default {"v1"};
    
    /**
     * deprecated 여부
     */
    boolean deprecated() default false;
    
    /**
     * deprecated 시 대체 버전
     */
    String alternativeVersion() default "";
    
    /**
     * deprecated 예정일
     */
    String deprecationDate() default "";
    
    /**
     * 버전별 변경 내용 설명
     */
    String changelog() default "";
}