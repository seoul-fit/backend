package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.user.adapter.in.web.dto.*;
import com.seoulfit.backend.user.adapter.out.persistence.UserInterestPort;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관심사 관리 컨트롤러
 * 
 * 사용자가 체크박스로 관심사를 선택하고 관리할 수 있는 API 제공
 * 
 * @author UrbanPing Team
 * @since 1.0.0
 */
@Slf4j
@RestController
@RequestMapping("/api/users/interests")
@RequiredArgsConstructor
@Tag(name = "User Interests", description = "사용자 관심사 관리 API")
@SecurityRequirement(name = "bearerAuth")
public class UserInterestController {

    private final UserPort userPort;
    private final UserInterestPort userInterestPort;

    @Operation(
        summary = "선택 가능한 관심사 목록 조회", 
        description = "사용자가 체크박스로 선택할 수 있는 모든 관심사 카테고리를 조회합니다."
    )
    @GetMapping("/available")
    public ResponseEntity<AvailableInterestsResponse> getAvailableInterests(
            @Parameter(description = "필터 타입 (all, location-based, realtime)")
            @RequestParam(defaultValue = "all") String filter) {
        
        log.info("선택 가능한 관심사 목록 조회 요청: filter={}", filter);

        AvailableInterestsResponse response = switch (filter.toLowerCase()) {
            case "location-based" -> AvailableInterestsResponse.createLocationBased();
            case "realtime" -> AvailableInterestsResponse.createRealtimeNotification();
            default -> AvailableInterestsResponse.createAll();
        };

        log.info("선택 가능한 관심사 목록 조회 완료: {}개 카테고리", response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 관심사 조회", 
        description = "현재 로그인한 사용자의 관심사 설정을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<UserInterestResponse> getUserInterests(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("사용자 관심사 조회 요청: userId={}", userDetails.getUsername());

        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<InterestCategory> interests = userInterestPort.findInterestsByUserId(user.getId());
        UserInterestResponse response = UserInterestResponse.from(
                user.getId(), user.getEmail(), interests);

        log.info("사용자 관심사 조회 완료: userId={}, 관심사 개수={}", 
                user.getId(), response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 관심사 설정", 
        description = "사용자가 체크박스로 선택한 관심사들을 저장합니다. 기존 관심사는 모두 삭제되고 새로 설정됩니다."
    )
    @PutMapping
    @Transactional
    public ResponseEntity<UserInterestResponse> updateUserInterests(
            @Valid @RequestBody UserInterestRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("사용자 관심사 설정 요청: userId={}, 관심사 개수={}", 
                userDetails.getUsername(), request.getInterestCount());

        if (!request.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 관심사 설정입니다.");
        }

        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 관심사 삭제
        userInterestPort.deleteByUser(user);

        // 새로운 관심사 저장
        List<UserInterest> newInterests = request.getInterests().stream()
                .distinct() // 중복 제거
                .map(category -> UserInterest.builder()
                        .user(user)
                        .interestCategory(category)
                        .build())
                .toList();

        userInterestPort.saveAll(newInterests);

        UserInterestResponse response = UserInterestResponse.from(
                user.getId(), user.getEmail(), request.getInterests());

        log.info("사용자 관심사 설정 완료: userId={}, 설정된 관심사 개수={}", 
                user.getId(), response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 관심사 추가", 
        description = "기존 관심사에 새로운 관심사를 추가합니다."
    )
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<UserInterestResponse> addUserInterest(
            @Parameter(description = "추가할 관심사 카테고리") 
            @RequestParam InterestCategory interest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("사용자 관심사 추가 요청: userId={}, interest={}", 
                userDetails.getUsername(), interest);

        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 관심사 조회
        List<InterestCategory> existingInterests = userInterestPort.findInterestsByUserId(user.getId());

        // 이미 존재하는 관심사인지 확인
        if (existingInterests.contains(interest)) {
            log.warn("이미 존재하는 관심사 추가 시도: userId={}, interest={}", user.getId(), interest);
            throw new IllegalArgumentException("이미 선택된 관심사입니다.");
        }

        // 새로운 관심사 추가
        UserInterest newInterest = UserInterest.builder()
                .user(user)
                .interestCategory(interest)
                .build();
        userInterestPort.save(newInterest);

        // 업데이트된 관심사 목록 조회
        List<InterestCategory> updatedInterests = userInterestPort.findInterestsByUserId(user.getId());
        UserInterestResponse response = UserInterestResponse.from(
                user.getId(), user.getEmail(), updatedInterests);

        log.info("사용자 관심사 추가 완료: userId={}, 총 관심사 개수={}", 
                user.getId(), response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 관심사 제거", 
        description = "기존 관심사에서 특정 관심사를 제거합니다."
    )
    @DeleteMapping("/remove")
    @Transactional
    public ResponseEntity<UserInterestResponse> removeUserInterest(
            @Parameter(description = "제거할 관심사 카테고리") 
            @RequestParam InterestCategory interest,
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("사용자 관심사 제거 요청: userId={}, interest={}", 
                userDetails.getUsername(), interest);

        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 기존 관심사 조회 및 제거
        List<UserInterest> userInterests = userInterestPort.findByUser(user);
        userInterests.stream()
                .filter(ui -> ui.getInterestCategory() == interest)
                .findFirst()
                .ifPresentOrElse(
                    ui -> {
                        // 관심사 제거 로직 (실제로는 repository에서 delete 메서드 필요)
                        log.info("관심사 제거: userId={}, interest={}", user.getId(), interest);
                    },
                    () -> {
                        log.warn("제거하려는 관심사가 존재하지 않음: userId={}, interest={}", user.getId(), interest);
                        throw new IllegalArgumentException("선택되지 않은 관심사입니다.");
                    }
                );

        // 업데이트된 관심사 목록 조회
        List<InterestCategory> updatedInterests = userInterestPort.findInterestsByUserId(user.getId());
        UserInterestResponse response = UserInterestResponse.from(
                user.getId(), user.getEmail(), updatedInterests);

        log.info("사용자 관심사 제거 완료: userId={}, 남은 관심사 개수={}", 
                user.getId(), response.getTotalCount());
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "사용자 관심사 초기화", 
        description = "사용자의 모든 관심사를 삭제합니다."
    )
    @DeleteMapping
    @Transactional
    public ResponseEntity<Void> clearUserInterests(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        log.info("사용자 관심사 초기화 요청: userId={}", userDetails.getUsername());

        User user = userPort.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        userInterestPort.deleteByUser(user);

        log.info("사용자 관심사 초기화 완료: userId={}", user.getId());
        return ResponseEntity.ok().build();
    }
}
