package com.seoulfit.backend.user.adapter.in.web;

import com.seoulfit.backend.user.adapter.in.web.dto.*;
import com.seoulfit.backend.user.adapter.out.persistence.UserInterestPort;
import com.seoulfit.backend.user.adapter.out.persistence.UserPort;
import com.seoulfit.backend.user.domain.InterestCategory;
import com.seoulfit.backend.user.domain.User;
import com.seoulfit.backend.user.domain.UserInterest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 사용자 관심사 관리 컨트롤러
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
            summary = "사용자 관심사 조회",
            description = "현재 로그인한 사용자의 관심사 설정을 조회합니다."
    )
    @PostMapping
    public ResponseEntity<UserInterestResponse> getUserInterests(@RequestBody Long userId) {
        User user = userPort.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<InterestCategory> interests = userInterestPort.findInterestsByUserId(user.getId());
        UserInterestResponse response = UserInterestResponse.from(user.getId(), interests);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "사용자 관심사 변경",
            description = "사용자가 체크박스로 선택한 관심사들을 저장합니다. 기존 관심사는 모두 삭제되고 새로 설정됩니다."
    )
    @PutMapping
    @Transactional
    public ResponseEntity<UserInterestResponse> updateUserInterests(
            @Valid @RequestBody UserInterestRequest request) {
        if (!request.isValid()) {
            throw new IllegalArgumentException("유효하지 않은 관심사입니다.");
        }

        User user = userPort.findById(request.getUserId())
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

        UserInterestResponse response = UserInterestResponse.from(user.getId(), request.getInterests());

        return ResponseEntity.ok(response);
    }
}