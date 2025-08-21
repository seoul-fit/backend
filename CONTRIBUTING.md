# Contributing to Seoul Fit

먼저 Seoul Fit 프로젝트에 기여하는 것을 고려해 주셔서 감사합니다! 🎉

Seoul Fit은 서울시민들에게 실시간 도시 정보를 제공하는 오픈소스 프로젝트입니다. 여러분의 기여는 프로젝트를 더욱 발전시키고 더 많은 사람들에게 도움을 줄 수 있습니다.

## 📋 목차

- [행동 강령](#행동-강령)
- [기여 방법](#기여-방법)
- [개발 환경 설정](#개발-환경-설정)
- [코딩 규칙](#코딩-규칙)
- [커밋 메시지 가이드](#커밋-메시지-가이드)
- [Pull Request 프로세스](#pull-request-프로세스)
- [이슈 보고](#이슈-보고)
- [코드 리뷰](#코드-리뷰)
- [라이선스](#라이선스)

## 행동 강령

이 프로젝트는 [Code of Conduct](CODE_OF_CONDUCT.md)를 따릅니다. 프로젝트에 참여함으로써 이 규칙을 준수하는 것에 동의하는 것으로 간주합니다.

## 기여 방법

### 🐛 버그 리포트

버그를 발견하셨나요? 다음 단계를 따라주세요:

1. **이슈 검색**: 먼저 [기존 이슈](https://github.com/seoul-fit/backend/issues)에서 동일한 버그가 보고되었는지 확인하세요.
2. **새 이슈 생성**: 동일한 이슈가 없다면 [새 이슈를 생성](https://github.com/seoul-fit/backend/issues/new?template=bug_report.md)하세요.
3. **상세한 정보 제공**:
   - 버그 재현 단계
   - 예상 동작과 실제 동작
   - 환경 정보 (OS, Java 버전 등)
   - 가능하다면 스크린샷이나 로그

### ✨ 새로운 기능 제안

새로운 기능을 제안하고 싶으신가요?

1. **이슈 생성**: [기능 요청 이슈](https://github.com/seoul-fit/backend/issues/new?template=feature_request.md)를 생성하세요.
2. **상세한 설명**: 
   - 기능의 목적과 필요성
   - 예상 사용 사례
   - 가능한 구현 방법

### 📝 문서 개선

문서 개선도 중요한 기여입니다:

- API 문서 업데이트
- README 개선
- 예제 코드 추가
- 번역

## 개발 환경 설정

### 필수 요구사항

- Java 21 이상
- Gradle 8.5 이상
- Git
- IDE (IntelliJ IDEA 권장)

### 프로젝트 설정

```bash
# 1. 저장소 Fork
# GitHub에서 Fork 버튼 클릭

# 2. Fork한 저장소 클론
git clone https://github.com/your-username/seoul-fit.git
cd seoul-fit

# 3. Upstream 저장소 추가
git remote add upstream https://github.com/seoul-fit/backend.git

# 4. 의존성 설치
./gradlew build

# 5. 환경변수 설정
cp .env.example .env.local
# .env.local 파일 편집하여 필요한 값 설정

# 6. 애플리케이션 실행
./gradlew bootRun

# 7. 테스트 실행
./gradlew test
```

## 코딩 규칙

### Java 코드 스타일

우리는 [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html)를 기반으로 합니다:

- **들여쓰기**: 4 spaces (탭 사용 금지)
- **최대 줄 길이**: 120자
- **중괄호**: K&R 스타일
- **명명 규칙**:
  - 클래스: `PascalCase`
  - 메서드/변수: `camelCase`
  - 상수: `UPPER_SNAKE_CASE`
  - 패키지: `lowercase`

### JavaDoc

모든 public 클래스와 메서드에는 JavaDoc이 필요합니다:

```java
/**
 * 사용자 정보를 관리하는 서비스
 * 
 * @author Your Name
 * @since 1.0.0
 */
public class UserService {
    
    /**
     * 사용자 ID로 사용자 정보를 조회합니다.
     * 
     * @param userId 조회할 사용자 ID
     * @return 사용자 정보
     * @throws UserNotFoundException 사용자를 찾을 수 없는 경우
     */
    public User findById(Long userId) {
        // ...
    }
}
```

### 테스트

- 모든 새로운 기능에는 테스트가 필요합니다
- 테스트 커버리지 60% 이상 유지
- 단위 테스트와 통합 테스트 구분
- 테스트 명명: `메서드명_시나리오_예상결과`

```java
@Test
void findById_existingUser_returnsUser() {
    // given
    Long userId = 1L;
    
    // when
    User result = userService.findById(userId);
    
    // then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
}
```

## 커밋 메시지 가이드

### 형식

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type

- `feat`: 새로운 기능
- `fix`: 버그 수정
- `docs`: 문서 수정
- `style`: 코드 포맷팅, 세미콜론 누락 등
- `refactor`: 코드 리팩토링
- `test`: 테스트 추가/수정
- `chore`: 빌드 프로세스, 도구 설정 등

### 예시

```
feat(notification): 실시간 알림 기능 추가

- WebSocket을 이용한 실시간 알림 구현
- 알림 우선순위 기능 추가
- 사용자별 알림 설정 관리

Closes #123
```

## Pull Request 프로세스

### 1. 브랜치 생성

```bash
# 최신 main 브랜치에서 시작
git checkout main
git pull upstream main

# 기능 브랜치 생성
git checkout -b feature/your-feature-name
```

### 2. 변경사항 커밋

```bash
git add .
git commit -m "feat: 새로운 기능 추가"
```

### 3. 테스트 실행

```bash
./gradlew test
./gradlew spotlessCheck  # 코드 스타일 검사
```

### 4. Push 및 PR 생성

```bash
git push origin feature/your-feature-name
```

GitHub에서 Pull Request 생성:

- **제목**: 명확하고 간결하게
- **설명**: 
  - 변경 사항 요약
  - 관련 이슈 번호
  - 테스트 방법
  - 스크린샷 (UI 변경인 경우)

### 5. PR 체크리스트

- [ ] 코드가 프로젝트 스타일 가이드를 따름
- [ ] 모든 테스트가 통과함
- [ ] 새로운 기능에 대한 테스트 추가
- [ ] JavaDoc 주석 추가/업데이트
- [ ] 문서 업데이트 (필요한 경우)
- [ ] 관련 이슈 링크

## 코드 리뷰

### 리뷰어를 위한 가이드

- **건설적인 피드백**: 개선 방법을 제안
- **명확한 설명**: 변경이 필요한 이유 설명
- **긍정적인 부분 언급**: 잘된 부분도 칭찬
- **질문하기**: 이해가 안 되는 부분은 질문

### 작성자를 위한 가이드

- **피드백에 감사**: 모든 리뷰에 감사 표현
- **설명 제공**: 구현 결정에 대한 이유 설명
- **빠른 대응**: 리뷰 코멘트에 신속히 응답

## 릴리스 프로세스

1. **버전 관리**: Semantic Versioning (MAJOR.MINOR.PATCH)
2. **변경 로그**: CHANGELOG.md 업데이트
3. **태그 생성**: `v1.0.0` 형식
4. **릴리스 노트**: 주요 변경사항 정리

## 도움 요청

도움이 필요하신가요?

- **Discord**: [Seoul Fit Discord Server](https://discord.gg/seoul-fit)
- **이메일**: gmavsks@gmail.com
- **이슈**: [GitHub Issues](https://github.com/seoul-fit/backend/issues)

## 기여자 인정

모든 기여자는 [AUTHORS.md](AUTHORS.md) 파일에 기록됩니다.

## 라이선스

이 프로젝트에 기여함으로써, 여러분의 기여가 [MIT 라이선스](LICENSE)에 따라 라이선스된다는 것에 동의합니다.

---

**감사합니다!** 여러분의 기여가 Seoul Fit을 더 나은 프로젝트로 만듭니다. 🙏