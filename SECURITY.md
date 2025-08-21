# Security Policy

## 🔐 보안 정책

Seoul Fit 프로젝트의 보안을 중요하게 생각합니다. 이 문서는 보안 취약점을 신고하는 방법과 우리의 보안 정책을 설명합니다.

## 📌 지원되는 버전

현재 보안 업데이트를 받는 버전:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## 🚨 보안 취약점 신고

### 신고 방법

보안 취약점을 발견하셨다면, **공개 이슈로 보고하지 마세요**. 대신 다음 방법 중 하나를 사용해 주세요:

1. **이메일**: gmavsks@gmail.com
2. **GitHub Security Advisories**: [Security Advisories 페이지](https://github.com/seoul-fit/backend/security/advisories/new)
3. **PGP 암호화 이메일** (선택사항):
   ```
   PGP Key Fingerprint: [추후 제공]
   ```

### 신고 시 포함해야 할 정보

효과적인 취약점 처리를 위해 다음 정보를 포함해 주세요:

- **취약점 유형** (예: SQL Injection, XSS, CSRF 등)
- **영향받는 컴포넌트** (파일 경로, 모듈명)
- **재현 단계** (상세한 단계별 설명)
- **영향 범위** (어떤 데이터나 기능이 영향받는지)
- **개념 증명(PoC)** 코드 (있는 경우)
- **제안하는 수정 방법** (있는 경우)

### 예시 템플릿

```markdown
## 취약점 요약
[취약점에 대한 간단한 설명]

## 영향받는 버전
- 버전: [예: 1.0.0 - 1.0.3]

## 취약점 상세
[상세한 설명]

## 재현 단계
1. [첫 번째 단계]
2. [두 번째 단계]
3. [결과]

## 영향
[이 취약점이 악용될 경우의 영향]

## 제안하는 해결책
[가능한 해결 방법]
```

## ⏱️ 대응 시간

- **최초 응답**: 48시간 이내
- **상태 업데이트**: 5일마다
- **패치 릴리스**: 심각도에 따라
  - 🔴 Critical: 24시간 이내
  - 🟠 High: 7일 이내
  - 🟡 Medium: 30일 이내
  - 🟢 Low: 다음 정기 릴리스

## 📊 심각도 분류

### 🔴 Critical
- 인증 우회
- 원격 코드 실행
- 데이터 유출 (대량)
- 서비스 전체 중단

### 🟠 High
- 권한 상승
- SQL Injection
- 민감 정보 노출
- XSS (저장형)

### 🟡 Medium
- CSRF
- XSS (반사형)
- 세션 고정
- 정보 노출 (제한적)

### 🟢 Low
- 디버그 정보 노출
- 보안 헤더 누락
- 버전 정보 노출

## 🛡️ 보안 모범 사례

### 개발자를 위한 가이드라인

#### 1. 입력 검증
```java
// ❌ 잘못된 예
public User getUser(String id) {
    return repository.findById(id);
}

// ✅ 올바른 예
public User getUser(String id) {
    if (!isValidUserId(id)) {
        throw new IllegalArgumentException("Invalid user ID");
    }
    return repository.findById(id);
}
```

#### 2. SQL Injection 방지
```java
// ❌ 잘못된 예
String query = "SELECT * FROM users WHERE id = " + userId;

// ✅ 올바른 예 (Prepared Statement 사용)
@Query("SELECT u FROM User u WHERE u.id = :userId")
User findByUserId(@Param("userId") Long userId);
```

#### 3. 민감 정보 보호
```java
// ❌ 잘못된 예
log.info("User login: " + username + ", password: " + password);

// ✅ 올바른 예
log.info("User login attempt: {}", username);
```

#### 4. 암호화
```java
// 비밀번호는 항상 해시화
@Service
public class PasswordService {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    
    public String hashPassword(String rawPassword) {
        return encoder.encode(rawPassword);
    }
}
```

## 🔍 보안 검사 체크리스트

배포 전 확인사항:

- [ ] 모든 사용자 입력이 검증되는가?
- [ ] SQL Injection 취약점이 없는가?
- [ ] XSS 취약점이 없는가?
- [ ] CSRF 보호가 활성화되어 있는가?
- [ ] 민감한 정보가 로그에 기록되지 않는가?
- [ ] API 키와 시크릿이 환경변수로 관리되는가?
- [ ] HTTPS가 강제되는가?
- [ ] 보안 헤더가 설정되어 있는가?
- [ ] 의존성이 최신 버전인가?
- [ ] 에러 메시지가 민감한 정보를 노출하지 않는가?

## 🏆 보안 연구자 인정

보안 취약점을 책임감 있게 신고해 주신 연구자들께 감사드립니다:

### Hall of Fame
- [연구자 이름] - [취약점 유형] (날짜)
- (목록은 기여가 있을 때 업데이트됩니다)

## 📚 보안 리소스

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Java Security Guidelines](https://www.oracle.com/java/technologies/javase/seccodeguide.html)

## 🤝 책임감 있는 공개

우리는 책임감 있는 공개 원칙을 따릅니다:

1. 취약점 신고 접수
2. 취약점 확인 및 재현
3. 패치 개발 및 테스트
4. 패치 릴리스
5. 공개 발표 (신고자와 협의 후)

## 📞 연락처

- **보안팀 이메일**: gmavsks@gmail.com
- **일반 문의**: gmavsks@gmail.com
- **긴급 연락처**: [제공 예정]

---

*이 보안 정책은 정기적으로 검토되고 업데이트됩니다.*

*마지막 업데이트: 2025-08-21*