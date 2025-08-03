package com.seoulfit.backend.tmp.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cultural_events",
        indexes = {
                @Index(name = "idx_event_location", columnList = "latitude, longitude"),
                @Index(name = "idx_event_date", columnList = "start_date, end_date"),
                @Index(name = "idx_event_district", columnList = "district"),
                @Index(name = "idx_event_category", columnList = "code_name")
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CulturalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code_name", nullable = false, length = 100)
    private String codeName; // 분류 (전시/미술, 콘서트, 클래식 등)

    @Column(name = "district", nullable = false, length = 50)
    private String district; // 자치구 (GUNAME)

    @Column(name = "title", nullable = false, length = 500)
    private String title; // 공연/행사명

    @Column(name = "event_date", length = 100)
    private String eventDate; // 날짜/시간 (원본 문자열)

    @Column(name = "start_date")
    private LocalDate startDate; // 시작일

    @Column(name = "end_date")
    private LocalDate endDate; // 종료일

    @Column(name = "place", length = 500)
    private String place; // 장소

    @Column(name = "org_name", length = 200)
    private String orgName; // 기관명

    @Column(name = "use_target", length = 200)
    private String useTarget; // 이용대상

    @Column(name = "use_fee", columnDefinition = "TEXT")
    private String useFee; // 이용요금

    @Column(name = "player", columnDefinition = "TEXT")
    private String player; // 출연자정보

    @Column(name = "program", columnDefinition = "TEXT")
    private String program; // 프로그램소개

    @Column(name = "etc_desc", columnDefinition = "TEXT")
    private String etcDesc; // 기타내용

    @Column(name = "org_link", length = 1000)
    private String orgLink; // 홈페이지 주소

    @Column(name = "main_img", length = 1000)
    private String mainImg; // 대표이미지

    @Column(name = "registration_date", length = 50)
    private String registrationDate; // 신청일

    @Column(name = "ticket", length = 50)
    private String ticket; // 시민/기관

    @Column(name = "theme_code", length = 100)
    private String themeCode; // 테마분류

    @Column(name = "latitude", precision = 10, scale = 8)
    private BigDecimal latitude; // 위도(Y좌표)

    @Column(name = "longitude", precision = 11, scale = 8)
    private BigDecimal longitude; // 경도(X좌표)

    @Column(name = "is_free", length = 10)
    private String isFree; // 유무료

    @Column(name = "homepage_addr", length = 1000)
    private String homepageAddr; // 문화포털상세URL

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId; // 외부 시스템 고유 ID (중복 방지용)

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public CulturalEvent(String codeName, String district, String title, String eventDate,
                        LocalDate startDate, LocalDate endDate, String place, String orgName,
                        String useTarget, String useFee, String player, String program,
                        String etcDesc, String orgLink, String mainImg, String registrationDate,
                        String ticket, String themeCode, BigDecimal latitude, BigDecimal longitude,
                        String isFree, String homepageAddr, String externalId) {
        this.codeName = codeName;
        this.district = district;
        this.title = title;
        this.eventDate = eventDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.place = place;
        this.orgName = orgName;
        this.useTarget = useTarget;
        this.useFee = useFee;
        this.player = player;
        this.program = program;
        this.etcDesc = etcDesc;
        this.orgLink = orgLink;
        this.mainImg = mainImg;
        this.registrationDate = registrationDate;
        this.ticket = ticket;
        this.themeCode = themeCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isFree = isFree;
        this.homepageAddr = homepageAddr;
        this.externalId = externalId;
    }

    public boolean isFreeEvent() {
        return "무료".equals(this.isFree);
    }

    public boolean isOngoing(LocalDate currentDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        return !currentDate.isBefore(startDate) && !currentDate.isAfter(endDate);
    }

    public boolean isUpcoming(LocalDate currentDate) {
        if (startDate == null) {
            return false;
        }
        return currentDate.isBefore(startDate);
    }
}
