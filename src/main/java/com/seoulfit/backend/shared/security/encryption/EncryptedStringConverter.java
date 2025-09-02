package com.seoulfit.backend.shared.security.encryption;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * JPA 엔티티 필드 암호화 컨버터
 * 
 * 데이터베이스 저장 시 자동으로 암호화하고
 * 조회 시 자동으로 복호화합니다.
 * 
 * 사용 예:
 * ```java
 * @Entity
 * public class User {
 *     @Convert(converter = EncryptedStringConverter.class)
 *     private String phoneNumber;
 * }
 * ```
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Component
@Converter
@RequiredArgsConstructor
public class EncryptedStringConverter implements AttributeConverter<String, String> {

    private final DataEncryptionService encryptionService;

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }
        return encryptionService.encrypt(attribute);
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return encryptionService.decrypt(dbData);
    }
}