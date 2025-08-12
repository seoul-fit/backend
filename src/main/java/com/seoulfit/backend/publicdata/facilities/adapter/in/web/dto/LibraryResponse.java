package com.seoulfit.backend.publicdata.facilities.adapter.in.web.dto;

import com.seoulfit.backend.publicdata.facilities.domain.Library;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 공공도서관 정보 API 응답 DTO
 */
@Getter
@Builder
@Schema(description = "공공도서관 정보 응답")
public class LibraryResponse {

    @Schema(description = "도서관 ID", example = "1")
    private Long id;

    @Schema(description = "도서관 일련번호", example = "LIB001")
    private String lbrrySeqNo;

    @Schema(description = "도서관명", example = "서울도서관")
    private String lbrryName;

    @Schema(description = "구 코드", example = "11010")
    private String guCode;

    @Schema(description = "구명", example = "종로구")
    private String codeValue;

    @Schema(description = "주소", example = "서울특별시 중구 세종대로 110")
    private String adres;

    @Schema(description = "전화번호", example = "02-2133-0300")
    private String telNo;

    @Schema(description = "홈페이지 URL", example = "https://lib.seoul.go.kr")
    private String hmpgUrl;

    @Schema(description = "운영시간", example = "평일 09:00~22:00, 주말 09:00~17:00")
    private String opTime;

    @Schema(description = "정기 휴관일", example = "매월 둘째, 넷째 월요일")
    private String fdrmCloseDate;

    @Schema(description = "도서관 구분명", example = "공공도서관")
    private String lbrrySeName;

    @Schema(description = "위도", example = "37.5665")
    private Double xcnts;

    @Schema(description = "경도", example = "126.9780")
    private Double ydnts;

    @Schema(description = "생성일시")
    private LocalDateTime createdAt;

    @Schema(description = "수정일시")
    private LocalDateTime updatedAt;

    /**
     * Library 도메인 객체를 LibraryResponse DTO로 변환
     */
    public static LibraryResponse from(Library library) {
        return LibraryResponse.builder()
                .id(library.getId())
                .lbrrySeqNo(library.getLbrrySeqNo())
                .lbrryName(library.getLbrryName())
                .guCode(library.getGuCode())
                .codeValue(library.getCodeValue())
                .adres(library.getAdres())
                .telNo(library.getTelNo())
                .hmpgUrl(library.getHmpgUrl())
                .opTime(library.getOpTime())
                .fdrmCloseDate(library.getFdrmCloseDate())
                .lbrrySeName(library.getLbrrySeName())
                .xcnts(library.getXcnts())
                .ydnts(library.getYdnts())
                .createdAt(library.getCreatedAt())
                .updatedAt(library.getUpdatedAt())
                .build();
    }

    /**
     * Library 리스트를 LibraryResponse 리스트로 변환
     */
    public static List<LibraryResponse> from(List<Library> libraries) {
        return libraries.stream()
                .map(LibraryResponse::from)
                .collect(Collectors.toList());
    }
}
