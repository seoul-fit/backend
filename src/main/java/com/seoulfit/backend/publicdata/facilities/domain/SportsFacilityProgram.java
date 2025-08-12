package com.seoulfit.backend.publicdata.facilities.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 서울시 공공체육시설별 운영프로그램 정보 도메인 엔티티
 * 서울시 공공 데이터 API에서 제공하는 체육시설 운영프로그램 정보를 저장
 */
@Entity
@Table(name = "sports_facility_programs", indexes = {
    @Index(name = "idx_sports_program_date", columnList = "dataDate"),
    @Index(name = "idx_sports_program_center", columnList = "centerName"),
    @Index(name = "idx_sports_program_subject", columnList = "subjectName"),
    @Index(name = "idx_sports_program_use_yn", columnList = "useYn")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SportsFacilityProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 시설명 (API의 CENTER_NAME)
     */
    @Column(name = "center_name", nullable = false, length = 200)
    private String centerName;

    /**
     * 종목시설명 (API의 GROUND_NAME)
     */
    @Column(name = "ground_name", length = 200)
    private String groundName;

    /**
     * 프로그램명 (API의 PROGRAM_NAME)
     */
    @Column(name = "program_name", length = 300)
    private String programName;

    /**
     * 종목명 (API의 SUBJECT_NAME)
     */
    @Column(name = "subject_name", length = 100)
    private String subjectName;

    /**
     * 장소 (API의 PLACE)
     */
    @Column(name = "place", length = 200)
    private String place;

    /**
     * 주소 (API의 ADDRESS)
     */
    @Column(name = "address", length = 300)
    private String address;

    /**
     * 홈페이지 (API의 HOMEPAGE)
     */
    @Column(name = "homepage", length = 500)
    private String homepage;

    /**
     * 주차면 (API의 PARKING_SIDE)
     */
    @Column(name = "parking_side", length = 50)
    private String parkingSide;

    /**
     * 문의전화 (API의 TEL)
     */
    @Column(name = "tel", length = 50)
    private String tel;

    /**
     * FAX (API의 FAX)
     */
    @Column(name = "fax", length = 50)
    private String fax;

    /**
     * 이메일 (API의 EMAIL)
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * 반명 (API의 CLASS_NAME)
     */
    @Column(name = "class_name", length = 200)
    private String className;

    /**
     * 레벨 (API의 P_LEVEL)
     */
    @Column(name = "p_level", length = 50)
    private String pLevel;

    /**
     * 대상 (API의 TARGET)
     */
    @Column(name = "target", length = 200)
    private String target;

    /**
     * 기간 (API의 TERM)
     */
    @Column(name = "term", length = 200)
    private String term;

    /**
     * 요일 (API의 WEEK)
     */
    @Column(name = "week", length = 100)
    private String week;

    /**
     * 진행시간(1회) (API의 CLASS_TIME)
     */
    @Column(name = "class_time", length = 100)
    private String classTime;

    /**
     * 수강료(원) (API의 FEE)
     */
    @Column(name = "fee", length = 100)
    private String fee;

    /**
     * 프로그램소개 (API의 INTRO)
     */
    @Column(name = "intro", columnDefinition = "TEXT")
    private String intro;

    /**
     * 전체정원수 (API의 CAPACITY)
     */
    @Column(name = "capacity", length = 50)
    private String capacity;

    /**
     * 접수방법 (API의 ENTER_WAY)
     */
    @Column(name = "enter_way", length = 200)
    private String enterWay;

    /**
     * 접수기간 (API의 ENTER_TERM)
     */
    @Column(name = "enter_term", length = 200)
    private String enterTerm;

    /**
     * 선별방법 (API의 SELECT_WAY)
     */
    @Column(name = "select_way", length = 200)
    private String selectWay;

    /**
     * 온라인예약링크 (API의 ONLINE_LINK)
     */
    @Column(name = "online_link", length = 500)
    private String onlineLink;

    /**
     * 사용여부(센터에서 강좌오픈여부) (API의 USE_YN)
     */
    @Column(name = "use_yn", length = 1)
    private String useYn;

    /**
     * 강좌시작일 (API의 CLASS_S)
     */
    @Column(name = "class_start_date", length = 20)
    private String classStartDate;

    /**
     * 강좌종료일 (API의 CLASS_E)
     */
    @Column(name = "class_end_date", length = 20)
    private String classEndDate;

    /**
     * 가격무료확인 (API의 FEE_FREE)
     */
    @Column(name = "fee_free", length = 1)
    private String feeFree;

    /**
     * 데이터 수집 날짜 (YYYYMMDD 형식)
     */
    @Column(name = "data_date", nullable = false, length = 8)
    private String dataDate;

    /**
     * 생성 일시
     */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    /**
     * 수정 일시
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder
    public SportsFacilityProgram(String centerName, String groundName, String programName,
                                String subjectName, String place, String address, String homepage,
                                String parkingSide, String tel, String fax, String email,
                                String className, String pLevel, String target, String term,
                                String week, String classTime, String fee, String intro,
                                String capacity, String enterWay, String enterTerm, String selectWay,
                                String onlineLink, String useYn, String classStartDate,
                                String classEndDate, String feeFree, String dataDate) {
        this.centerName = centerName;
        this.groundName = groundName;
        this.programName = programName;
        this.subjectName = subjectName;
        this.place = place;
        this.address = address;
        this.homepage = homepage;
        this.parkingSide = parkingSide;
        this.tel = tel;
        this.fax = fax;
        this.email = email;
        this.className = className;
        this.pLevel = pLevel;
        this.target = target;
        this.term = term;
        this.week = week;
        this.classTime = classTime;
        this.fee = fee;
        this.intro = intro;
        this.capacity = capacity;
        this.enterWay = enterWay;
        this.enterTerm = enterTerm;
        this.selectWay = selectWay;
        this.onlineLink = onlineLink;
        this.useYn = useYn;
        this.classStartDate = classStartDate;
        this.classEndDate = classEndDate;
        this.feeFree = feeFree;
        this.dataDate = dataDate;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 엔티티 업데이트
     */
    public void update(String centerName, String groundName, String programName,
                      String subjectName, String place, String address, String homepage,
                      String parkingSide, String tel, String fax, String email,
                      String className, String pLevel, String target, String term,
                      String week, String classTime, String fee, String intro,
                      String capacity, String enterWay, String enterTerm, String selectWay,
                      String onlineLink, String useYn, String classStartDate,
                      String classEndDate, String feeFree) {
        this.centerName = centerName;
        this.groundName = groundName;
        this.programName = programName;
        this.subjectName = subjectName;
        this.place = place;
        this.address = address;
        this.homepage = homepage;
        this.parkingSide = parkingSide;
        this.tel = tel;
        this.fax = fax;
        this.email = email;
        this.className = className;
        this.pLevel = pLevel;
        this.target = target;
        this.term = term;
        this.week = week;
        this.classTime = classTime;
        this.fee = fee;
        this.intro = intro;
        this.capacity = capacity;
        this.enterWay = enterWay;
        this.enterTerm = enterTerm;
        this.selectWay = selectWay;
        this.onlineLink = onlineLink;
        this.useYn = useYn;
        this.classStartDate = classStartDate;
        this.classEndDate = classEndDate;
        this.feeFree = feeFree;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 프로그램이 현재 사용 중인지 확인
     */
    public boolean isActive() {
        return "Y".equals(useYn);
    }

    /**
     * 무료 프로그램인지 확인
     */
    public boolean isFree() {
        return "Y".equals(feeFree);
    }

    /**
     * 특정 종목에 해당하는지 확인
     */
    public boolean isSubject(String targetSubject) {
        return subjectName != null && subjectName.contains(targetSubject);
    }

    /**
     * 특정 시설에 해당하는지 확인
     */
    public boolean isCenter(String targetCenter) {
        return centerName != null && centerName.contains(targetCenter);
    }
}
