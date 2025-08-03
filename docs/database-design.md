# Seoul-Fit 데이터베이스 설계

## ERD 개요

Seoul-Fit 서비스의 데이터베이스 설계 문서입니다.

## 테이블 설계

### 1. users (사용자)
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL,
    status ENUM('ACTIVE', 'INACTIVE', 'DELETED') DEFAULT 'ACTIVE'
);
```

### 2. user_interests (사용자 관심사)
```sql
CREATE TABLE user_interests (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    interest_category ENUM('SPORTS', 'CULTURE', 'RESTAURANT', 'LIBRARY', 'PARK') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY unique_user_interest (user_id, interest_category)
);
```

### 3. facilities (시설)
```sql
CREATE TABLE facilities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    category ENUM('SPORTS', 'CULTURE', 'RESTAURANT', 'LIBRARY', 'PARK') NOT NULL,
    description TEXT,
    address VARCHAR(500) NOT NULL,
    district VARCHAR(50) NOT NULL,
    latitude DECIMAL(10, 8) NOT NULL,
    longitude DECIMAL(11, 8) NOT NULL,
    phone VARCHAR(20),
    operating_hours VARCHAR(255),
    website_url VARCHAR(500),
    reservation_url VARCHAR(500),
    external_id VARCHAR(100),
    data_source VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_location (latitude, longitude),
    INDEX idx_category (category),
    INDEX idx_district (district)
);
```

### 4. facility_amenities (시설 편의시설)
```sql
CREATE TABLE facility_amenities (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    facility_id BIGINT NOT NULL,
    amenity_name VARCHAR(100) NOT NULL,
    FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE CASCADE
);
```

### 5. congestion_data (혼잡도 데이터)
```sql
CREATE TABLE congestion_data (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    facility_id BIGINT NOT NULL,
    congestion_level ENUM('LOW', 'MODERATE', 'HIGH', 'VERY_HIGH') NOT NULL,
    recorded_at TIMESTAMP NOT NULL,
    data_source ENUM('REAL_TIME', 'PREDICTED') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (facility_id) REFERENCES facilities(id) ON DELETE CASCADE,
    INDEX idx_facility_time (facility_id, recorded_at)
);
```

### 6. notifications (알림)
```sql
CREATE TABLE notifications (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    type ENUM('CONGESTION_ALERT', 'RESERVATION_REMINDER', 'NEW_FACILITY', 'SYSTEM') NOT NULL,
    is_read BOOLEAN DEFAULT FALSE,
    related_facility_id BIGINT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    read_at TIMESTAMP NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (related_facility_id) REFERENCES facilities(id) ON DELETE SET NULL,
    INDEX idx_user_created (user_id, created_at)
);
```

### 7. notification_templates (알림 템플릿)
```sql
CREATE TABLE notification_templates (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    type ENUM('CONGESTION_ALERT', 'RESERVATION_REMINDER', 'NEW_FACILITY', 'SYSTEM') NOT NULL,
    title_template VARCHAR(255) NOT NULL,
    message_template TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 8. search_keywords (검색 키워드)
```sql
CREATE TABLE search_keywords (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    keyword VARCHAR(100) NOT NULL,
    search_count INT DEFAULT 1,
    last_searched_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY unique_keyword (keyword),
    INDEX idx_search_count (search_count DESC)
);
```

### 9. districts (구 정보)
```sql
CREATE TABLE districts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL UNIQUE,
    code VARCHAR(10) NOT NULL UNIQUE,
    center_latitude DECIMAL(10, 8) NOT NULL,
    center_longitude DECIMAL(11, 8) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

## 인덱스 전략

### 1. 위치 기반 검색 최적화
- facilities 테이블의 (latitude, longitude) 복합 인덱스
- 공간 인덱스 고려 (MySQL 8.0+)

### 2. 카테고리별 검색 최적화
- facilities 테이블의 category 인덱스
- user_interests 테이블의 (user_id, interest_category) 복합 인덱스

### 3. 시계열 데이터 최적화
- congestion_data 테이블의 (facility_id, recorded_at) 복합 인덱스
- notifications 테이블의 (user_id, created_at) 복합 인덱스

## 데이터 관계

1. **사용자 ↔ 관심사**: 1:N 관계
2. **시설 ↔ 편의시설**: 1:N 관계
3. **시설 ↔ 혼잡도 데이터**: 1:N 관계
4. **사용자 ↔ 알림**: 1:N 관계
5. **시설 ↔ 알림**: 1:N 관계 (선택적)

## 확장 고려사항

1. **파티셔닝**: 혼잡도 데이터, 알림 테이블의 시간 기반 파티셔닝
2. **샤딩**: 사용자 기반 샤딩 고려
3. **캐싱**: Redis를 활용한 실시간 혼잡도 데이터 캐싱
4. **아카이빙**: 오래된 혼잡도 데이터의 별도 테이블 이관
