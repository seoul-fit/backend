# Backend backlog

현재 배포를 안정적으로 유지하면서 후속 단계에서 처리할 항목입니다. 완료되지 않은 기능을 운영 기능처럼 문서화하거나 활성화하지 않습니다.

## GitHub Actions CI 재개

- 범위: Gradle test/build, 컨테이너 build·scan·push, 배포 digest 갱신 검증을 Actions로 복원합니다.
- 지금 하지 않는 이유: 인프라 전역에서 Actions가 중지되어 있어 현재 승인된 배포 경로는 로컬 빌드와 수동 GitOps 승격입니다.
- 시작 조건: 조직/runner 정책이 다시 열리고 Harbor·Vault 자격증명을 OIDC 또는 최소 권한 secret으로 공급할 수 있을 때 진행합니다.

## 이메일·SMS 발송 스텁 정리

- 현재 상태: `NotificationSenderAdapter`의 email/SMS 구현은 기본적으로 비활성화되어 있지만, 플래그를 켜면 실제 전송 없이 성공으로 기록할 수 있는 스텁입니다.
- 처리 방침: 실제 provider adapter와 통합 테스트가 준비될 때까지 두 플래그를 활성화하지 않습니다. 구현 전에는 명시적인 `NOT_IMPLEMENTED`/실패 결과를 반환하도록 바꾸고, 수신자 정보와 메시지 본문을 로그에 남기지 않도록 마스킹합니다.
- 지금 하지 않는 이유: 메일/SMS 제공자, 발신자 신원, 비용·쿼터와 개인정보 보관 정책이 아직 결정되지 않았습니다.

## 체육시설 geocoding 스텁 정리

- 현재 상태: `/api/sports/geocoding`은 처리 건수 0을 성공으로 응답하는 미구현 경로입니다.
- 처리 방침: provider와 주소 정규화·재시도·쿼터 정책이 정해지기 전에는 `501 Not Implemented` 등 명시적인 비지원 응답으로 전환합니다. 구현 시 idempotent batch, 실패 재처리, 좌표 provenance를 함께 설계합니다.
- 지금 하지 않는 이유: geocoding 제공자와 호출 예산이 정해지지 않았고, 신규 기능 개발은 현재 품질 마감 범위 밖입니다.

## DB 신원 분리 옵션

- 현재 상태: Flyway migration과 애플리케이션 runtime이 환경별 단일 DB 신원 `seoulfit_app`을 공유합니다.
- 옵션: `seoulfit_migrator`에는 DDL 권한을, `seoulfit_runtime`에는 필요한 DML 권한만 부여하고 migration Job과 Deployment의 Secret을 분리합니다.
- 지금 하지 않는 이유: 단일 애플리케이션·단일 replica 홈랩 운영에서는 별도 migration lifecycle과 Vault/CNPG role 관리 비용이 이득보다 큽니다.
- 재검토 조건: 배포 무중단화, 다중 replica, 독립 migration Job, 더 엄격한 최소 권한 감사가 필요해질 때 적용합니다.
