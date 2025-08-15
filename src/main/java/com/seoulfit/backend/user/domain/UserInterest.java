package com.seoulfit.backend.user.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 관심사 도메인 모델
 * 
 * 사용자와 관심사 카테고리 간의 연관관계를 나타내는 엔티티
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Entity
@Table(name = "user_interests", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "interest_category"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class UserInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_category", nullable = false, length = 50)
    private InterestCategory interestCategory;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder
    public UserInterest(User user, InterestCategory interestCategory) {
        this.user = user;
        this.interestCategory = interestCategory;
    }

    /**
     * 사용자 관심사 생성
     * @param user 사용자
     * @param interestCategory 관심사 카테고리
     * @return 사용자 관심사
     */
    public static UserInterest of(User user, InterestCategory interestCategory) {
        return UserInterest.builder()
                .user(user)
                .interestCategory(interestCategory)
                .build();
    }

    /**
     * 관심사 카테고리 일치 여부 확인
     * @param category 비교할 카테고리
     * @return 일치 여부
     */
    public boolean isCategory(InterestCategory category) {
        return this.interestCategory == category;
    }

    /**
     * 사용자 일치 여부 확인
     * @param user 비교할 사용자
     * @return 일치 여부
     */
    public boolean belongsTo(User user) {
        return this.user.equals(user);
    }
}
