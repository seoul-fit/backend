package com.seoulfit.backend.shared.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 모든 REST 컨트롤러의 기본 클래스
 * 
 * API 버전 관리, 페이지네이션, 공통 유틸리티 메서드를 제공합니다.
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1")
public abstract class BaseController {
    
    /**
     * 현재 API 버전
     */
    protected static final String CURRENT_API_VERSION = "v1";
    
    /**
     * 기본 페이지 크기
     */
    protected static final int DEFAULT_PAGE_SIZE = 20;
    
    /**
     * 최대 페이지 크기 (성능 보호)
     */
    protected static final int MAX_PAGE_SIZE = 100;
    
    /**
     * 기본 정렬 방향
     */
    protected static final String DEFAULT_SORT_DIRECTION = "desc";
    
    /**
     * 페이지네이션 객체를 생성합니다.
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (최대 100개로 제한)
     * @return Pageable 객체
     */
    protected Pageable createPageable(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(page, size);
    }
    
    /**
     * 정렬이 포함된 페이지네이션 객체를 생성합니다.
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (최대 100개로 제한)
     * @param sortBy 정렬할 필드명
     * @param sortDir 정렬 방향 (asc, desc)
     * @return Pageable 객체
     */
    protected Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        size = Math.min(size, MAX_PAGE_SIZE);
        Sort.Direction direction = Sort.Direction.fromString(sortDir);
        Sort sort = Sort.by(direction, sortBy);
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * 다중 필드 정렬이 포함된 페이지네이션 객체를 생성합니다.
     * 
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기 (최대 100개로 제한)
     * @param sort Sort 객체
     * @return Pageable 객체
     */
    protected Pageable createPageable(int page, int size, Sort sort) {
        size = Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(page, size, sort);
    }
    
    /**
     * 성공 응답 메시지를 생성합니다.
     * 
     * @param operation 수행된 작업
     * @return 표준화된 성공 메시지
     */
    protected String createSuccessMessage(String operation) {
        return operation + "이(가) 성공적으로 완료되었습니다.";
    }
    
    /**
     * 리소스별 성공 응답 메시지를 생성합니다.
     * 
     * @param resource 리소스 이름
     * @param operation 수행된 작업
     * @return 표준화된 성공 메시지
     */
    protected String createResourceSuccessMessage(String resource, String operation) {
        return String.format("%s %s이(가) 성공적으로 완료되었습니다.", resource, operation);
    }
}