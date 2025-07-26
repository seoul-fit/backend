package com.seoulfit.backend.presentation;

import com.seoulfit.backend.application.service.UserService;
import com.seoulfit.backend.domain.InterestCategory;
import com.seoulfit.backend.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@RequestBody SignupRequest request) {
        User user = userService.createUser(
                request.email(),
                request.password(),
                request.nickname(),
                request.interests()
        );

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "email", user.getEmail(),
                "nickname", user.getNickname(),
                "message", "회원가입이 완료되었습니다."
        ));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(@RequestParam Long userId) { // TODO: JWT에서 추출
        User user = userService.findByIdWithInterests(userId);
        
        UserResponse response = new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getNickname(),
                user.getInterests().stream()
                        .map(interest -> interest.getInterestCategory())
                        .toList()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/interests")
    public ResponseEntity<Map<String, String>> updateInterests(
            @RequestParam Long userId, // TODO: JWT에서 추출
            @RequestBody UpdateInterestsRequest request) {
        
        userService.updateInterests(userId, request.interests());
        
        return ResponseEntity.ok(Map.of("message", "관심사가 업데이트되었습니다."));
    }

    @PutMapping("/nickname")
    public ResponseEntity<Map<String, String>> updateNickname(
            @RequestParam Long userId, // TODO: JWT에서 추출
            @RequestBody UpdateNicknameRequest request) {
        
        userService.updateNickname(userId, request.nickname());
        
        return ResponseEntity.ok(Map.of("message", "닉네임이 업데이트되었습니다."));
    }

    @DeleteMapping("/me")
    public ResponseEntity<Map<String, String>> deleteUser(@RequestParam Long userId) { // TODO: JWT에서 추출
        userService.deleteUser(userId);
        
        return ResponseEntity.ok(Map.of("message", "회원탈퇴가 완료되었습니다."));
    }

    public record SignupRequest(
            String email,
            String password,
            String nickname,
            List<InterestCategory> interests
    ) {}

    public record UserResponse(
            Long id,
            String email,
            String nickname,
            List<InterestCategory> interests
    ) {}

    public record UpdateInterestsRequest(
            List<InterestCategory> interests
    ) {}

    public record UpdateNicknameRequest(
            String nickname
    ) {}
}
