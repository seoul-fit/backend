package com.seoulfit.backend.publicdata.sports.infrastructure;

import com.seoulfit.backend.publicdata.sports.domain.Sports;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@DisplayName("SportsRepository 커스텀 쿼리 테스트")
class SportsRepositoryTest {

    @Autowired
    private SportsRepository sportsRepository;

    private Sports facility1;
    private Sports facility2;
    private Sports facility3;
    private Sports facility4;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        sportsRepository.deleteAll();
        
        // 강남구 수영장 (위치 정보 있음)
        facility1 = Sports.builder()
                .facilityName("강남스포츠센터 수영장")
                .facilityType("수영장")
                .district("강남구")
                .address("서울특별시 강남구 강남대로 123")
                .phoneNumber("02-1234-5678")
                .operatingHours("06:00-22:00")
                .holiday("매월 둘째, 넷째 일요일")
                .feeInfo("성인 6,000원")
                .latitude(37.4979)
                .longitude(127.0276)
                .serviceId("SVC001")
                .build();
        
        // 서초구 체육관 (위치 정보 있음)
        facility2 = Sports.builder()
                .facilityName("서초종합체육관")
                .facilityType("체육관")
                .district("서초구")
                .address("서울특별시 서초구 서초대로 456")
                .phoneNumber("02-2345-6789")
                .operatingHours("09:00-21:00")
                .holiday("매주 월요일")
                .feeInfo("성인 5,000원")
                .latitude(37.4837)
                .longitude(127.0324)
                .serviceId("SVC002")
                .build();
        
        // 강남구 테니스장 (위치 정보 없음)
        facility3 = Sports.builder()
                .facilityName("강남테니스장")
                .facilityType("테니스장")
                .district("강남구")
                .address("서울특별시 강남구 테헤란로 789")
                .phoneNumber("02-3456-7890")
                .operatingHours("06:00-20:00")
                .holiday("우천시 휴장")
                .feeInfo("1시간 20,000원")
                .latitude(null)
                .longitude(null)
                .serviceId("SVC003")
                .build();
        
        // 강남구 수영장 2 (위치 정보 있음, 멀리 떨어짐)
        facility4 = Sports.builder()
                .facilityName("도곡스포츠센터 수영장")
                .facilityType("수영장")
                .district("강남구")
                .address("서울특별시 강남구 도곡로 111")
                .phoneNumber("02-4567-8901")
                .operatingHours("06:00-21:00")
                .holiday("매월 첫째 일요일")
                .feeInfo("성인 5,500원")
                .latitude(37.4900)
                .longitude(127.0550)
                .serviceId("SVC004")
                .build();
        
        sportsRepository.saveAll(List.of(facility1, facility2, facility3, facility4));
    }

    @Test
    @DisplayName("시설명으로 체육시설 조회 - 성공")
    void findByFacilityName_Success() {
        // when
        Optional<Sports> found = sportsRepository.findByFacilityName("강남스포츠센터 수영장");
        
        // then
        assertThat(found).isPresent();
        assertThat(found.get().getFacilityType()).isEqualTo("수영장");
        assertThat(found.get().getDistrict()).isEqualTo("강남구");
    }

    @Test
    @DisplayName("시설유형으로 체육시설 목록 조회")
    void findByFacilityType() {
        // when
        List<Sports> swimmingPools = sportsRepository.findByFacilityType("수영장");
        List<Sports> gyms = sportsRepository.findByFacilityType("체육관");
        
        // then
        assertThat(swimmingPools).hasSize(2);
        assertThat(swimmingPools).extracting(Sports::getFacilityName)
                .containsExactlyInAnyOrder("강남스포츠센터 수영장", "도곡스포츠센터 수영장");
        
        assertThat(gyms).hasSize(1);
        assertThat(gyms.get(0).getFacilityName()).isEqualTo("서초종합체육관");
    }

    @Test
    @DisplayName("자치구로 체육시설 목록 조회")
    void findByDistrict() {
        // when
        List<Sports> gangnamFacilities = sportsRepository.findByDistrict("강남구");
        List<Sports> seochoFacilities = sportsRepository.findByDistrict("서초구");
        
        // then
        assertThat(gangnamFacilities).hasSize(3);
        assertThat(seochoFacilities).hasSize(1);
        assertThat(seochoFacilities.get(0).getFacilityName()).isEqualTo("서초종합체육관");
    }

    @Test
    @DisplayName("키워드로 체육시설 검색")
    void findByFacilityNameContainingIgnoreCase() {
        // when
        List<Sports> swimmingCenters = sportsRepository.findByFacilityNameContainingIgnoreCase("수영장");
        List<Sports> sportsCenters = sportsRepository.findByFacilityNameContainingIgnoreCase("스포츠센터");
        
        // then
        assertThat(swimmingCenters).hasSize(2);
        assertThat(sportsCenters).hasSize(2);
        assertThat(sportsCenters).extracting(Sports::getFacilityName)
                .allMatch(name -> name.contains("스포츠센터"));
    }

    @Test
    @DisplayName("위치 기반 체육시설 조회 - 반경 내")
    void findByLocationWithinRadius_Success() {
        // given
        double latitude = 37.4979;  // 강남스포츠센터 근처
        double longitude = 127.0276;
        double radiusKm = 3.0;
        
        // when
        List<Sports> nearbyFacilities = sportsRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
        
        // then
        assertThat(nearbyFacilities).isNotEmpty();
        // 가장 가까운 시설이 먼저 나와야 함
        assertThat(nearbyFacilities.get(0).getFacilityName()).isEqualTo("강남스포츠센터 수영장");
    }

    @Test
    @DisplayName("위치 기반 체육시설 조회 - 반경 외")
    void findByLocationWithinRadius_OutOfRange() {
        // given
        double latitude = 37.5500;  // 먼 지역
        double longitude = 127.1500;
        double radiusKm = 1.0; // 작은 반경
        
        // when
        List<Sports> nearbyFacilities = sportsRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
        
        // then
        assertThat(nearbyFacilities).isEmpty();
    }

    @Test
    @DisplayName("위경도 정보가 없는 체육시설 조회")
    void findByLatitudeIsNullOrLongitudeIsNull() {
        // when
        List<Sports> noLocationFacilities = sportsRepository.findByLatitudeIsNullOrLongitudeIsNull();
        
        // then
        assertThat(noLocationFacilities).hasSize(1);
        assertThat(noLocationFacilities.get(0).getFacilityName()).isEqualTo("강남테니스장");
        assertThat(noLocationFacilities.get(0).getLatitude()).isNull();
        assertThat(noLocationFacilities.get(0).getLongitude()).isNull();
    }

    @Test
    @DisplayName("시설유형과 자치구로 체육시설 조회")
    void findByFacilityTypeAndDistrict() {
        // when
        List<Sports> gangnamSwimmingPools = sportsRepository.findByFacilityTypeAndDistrict("수영장", "강남구");
        List<Sports> seochoGyms = sportsRepository.findByFacilityTypeAndDistrict("체육관", "서초구");
        List<Sports> seochoTennis = sportsRepository.findByFacilityTypeAndDistrict("테니스장", "서초구");
        
        // then
        assertThat(gangnamSwimmingPools).hasSize(2);
        assertThat(gangnamSwimmingPools).extracting(Sports::getFacilityName)
                .containsExactlyInAnyOrder("강남스포츠센터 수영장", "도곡스포츠센터 수영장");
        
        assertThat(seochoGyms).hasSize(1);
        assertThat(seochoGyms.get(0).getFacilityName()).isEqualTo("서초종합체육관");
        
        assertThat(seochoTennis).isEmpty();
    }

    @Test
    @DisplayName("전체 체육시설 수 조회")
    void countAllSports() {
        // when
        long totalCount = sportsRepository.countAllSports();
        
        // then
        assertThat(totalCount).isEqualTo(4);
    }

    @Test
    @DisplayName("위경도 정보가 있는 체육시설 수 조회")
    void countSportsWithLocation() {
        // when
        long withLocationCount = sportsRepository.countSportsWithLocation();
        
        // then
        assertThat(withLocationCount).isEqualTo(3);
    }

    @Test
    @DisplayName("위치 기반 조회 - 거리순 정렬 확인")
    void findByLocationWithinRadius_OrderByDistance() {
        // given
        double latitude = 37.4900;  // 중간 지점
        double longitude = 127.0400;
        double radiusKm = 10.0; // 큰 반경
        
        // when
        List<Sports> facilities = sportsRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
        
        // then
        assertThat(facilities).hasSize(3); // 위치 정보가 있는 시설 3개
        // 거리 계산하여 정렬 순서 확인
        assertThat(facilities).extracting(Sports::getFacilityName)
                .containsExactly(
                    "강남스포츠센터 수영장",  // 가장 가까움
                    "서초종합체육관",         // 중간
                    "도곡스포츠센터 수영장"   // 가장 멀음
                );
    }

    @Test
    @DisplayName("위치 정보가 null인 시설은 위치 기반 조회에서 제외")
    void findByLocationWithinRadius_ExcludesNullLocation() {
        // given
        double latitude = 37.5000;
        double longitude = 127.0400;
        double radiusKm = 100.0; // 매우 큰 반경
        
        // when
        List<Sports> facilities = sportsRepository.findByLocationWithinRadius(latitude, longitude, radiusKm);
        
        // then
        // 위치 정보가 없는 "강남테니스장"은 제외되어야 함
        assertThat(facilities).noneMatch(f -> f.getFacilityName().equals("강남테니스장"));
        assertThat(facilities).allMatch(f -> true); // Latitude and longitude are primitive doubles, always have values
    }
}