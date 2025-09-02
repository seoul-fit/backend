package com.seoulfit.backend.shared.security.encryption;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * 민감 데이터 암호화 서비스
 * 
 * AES-256-GCM을 사용하여 데이터베이스에 저장되는 민감 정보를
 * 안전하게 암호화/복호화합니다.
 * 
 * **암호화 대상:**
 * - 사용자 개인정보 (전화번호, 주소 등)
 * - OAuth 토큰
 * - API 키
 * - 위치 정보
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    
    @Value("${app.security.encryption.key:#{null}}")
    private String encryptionKeyBase64;
    
    private SecretKey secretKey;
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * 암호화 키 초기화
     */
    private SecretKey getOrCreateSecretKey() {
        if (secretKey == null) {
            if (encryptionKeyBase64 != null && !encryptionKeyBase64.isEmpty()) {
                // 설정된 키 사용
                byte[] decodedKey = Base64.getDecoder().decode(encryptionKeyBase64);
                secretKey = new SecretKeySpec(decodedKey, "AES");
            } else {
                // 새 키 생성 (개발 환경용)
                try {
                    KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
                    keyGenerator.init(256);
                    secretKey = keyGenerator.generateKey();
                    log.warn("Generated new encryption key. For production, set app.security.encryption.key");
                    log.debug("Generated key (Base64): {}", 
                            Base64.getEncoder().encodeToString(secretKey.getEncoded()));
                } catch (Exception e) {
                    throw new EncryptionException("Failed to generate encryption key", e);
                }
            }
        }
        return secretKey;
    }

    /**
     * 문자열 암호화
     * 
     * @param plaintext 평문
     * @return Base64 인코딩된 암호문 (IV 포함)
     */
    public String encrypt(String plaintext) {
        if (plaintext == null) {
            return null;
        }
        
        try {
            // IV 생성
            byte[] iv = new byte[GCM_IV_LENGTH];
            secureRandom.nextBytes(iv);
            
            // 암호화
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey(), parameterSpec);
            
            byte[] plaintextBytes = plaintext.getBytes(StandardCharsets.UTF_8);
            byte[] ciphertext = cipher.doFinal(plaintextBytes);
            
            // IV와 암호문 결합
            ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + ciphertext.length);
            byteBuffer.put(iv);
            byteBuffer.put(ciphertext);
            
            // Base64 인코딩
            return Base64.getEncoder().encodeToString(byteBuffer.array());
            
        } catch (Exception e) {
            log.error("Encryption failed", e);
            throw new EncryptionException("Failed to encrypt data", e);
        }
    }

    /**
     * 문자열 복호화
     * 
     * @param encryptedData Base64 인코딩된 암호문
     * @return 복호화된 평문
     */
    public String decrypt(String encryptedData) {
        if (encryptedData == null) {
            return null;
        }
        
        try {
            // Base64 디코딩
            byte[] cipherMessage = Base64.getDecoder().decode(encryptedData);
            
            // IV와 암호문 분리
            ByteBuffer byteBuffer = ByteBuffer.wrap(cipherMessage);
            byte[] iv = new byte[GCM_IV_LENGTH];
            byteBuffer.get(iv);
            byte[] ciphertext = new byte[byteBuffer.remaining()];
            byteBuffer.get(ciphertext);
            
            // 복호화
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), parameterSpec);
            
            byte[] plaintext = cipher.doFinal(ciphertext);
            
            return new String(plaintext, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            log.error("Decryption failed", e);
            throw new EncryptionException("Failed to decrypt data", e);
        }
    }

    /**
     * 개인정보 마스킹
     * 
     * 민감 정보를 부분적으로 마스킹하여 로그나 화면 표시용으로 사용
     */
    public String mask(String sensitiveData, MaskingType type) {
        if (sensitiveData == null || sensitiveData.isEmpty()) {
            return sensitiveData;
        }
        
        return switch (type) {
            case EMAIL -> maskEmail(sensitiveData);
            case PHONE -> maskPhone(sensitiveData);
            case NAME -> maskName(sensitiveData);
            case CARD -> maskCard(sensitiveData);
            case DEFAULT -> maskDefault(sensitiveData);
        };
    }
    
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return "***@***";
        }
        
        String localPart = email.substring(0, atIndex);
        String domain = email.substring(atIndex);
        
        if (localPart.length() <= 3) {
            return localPart.charAt(0) + "**" + domain;
        }
        
        return localPart.substring(0, 2) + 
               "*".repeat(Math.min(localPart.length() - 2, 5)) + 
               domain;
    }
    
    private String maskPhone(String phone) {
        String cleaned = phone.replaceAll("[^0-9]", "");
        if (cleaned.length() < 8) {
            return "***-****-****";
        }
        
        return cleaned.substring(0, 3) + "-****-" + 
               cleaned.substring(cleaned.length() - 4);
    }
    
    private String maskName(String name) {
        if (name.length() <= 1) {
            return "*";
        }
        
        if (name.length() == 2) {
            return name.charAt(0) + "*";
        }
        
        return name.charAt(0) + 
               "*".repeat(name.length() - 2) + 
               name.charAt(name.length() - 1);
    }
    
    private String maskCard(String card) {
        String cleaned = card.replaceAll("[^0-9]", "");
        if (cleaned.length() < 12) {
            return "****-****-****-****";
        }
        
        return cleaned.substring(0, 4) + "-****-****-" + 
               cleaned.substring(cleaned.length() - 4);
    }
    
    private String maskDefault(String data) {
        if (data.length() <= 4) {
            return "*".repeat(data.length());
        }
        
        int visibleChars = Math.min(2, data.length() / 4);
        return data.substring(0, visibleChars) + 
               "*".repeat(data.length() - visibleChars * 2) + 
               data.substring(data.length() - visibleChars);
    }

    /**
     * 마스킹 타입
     */
    public enum MaskingType {
        EMAIL,
        PHONE,
        NAME,
        CARD,
        DEFAULT
    }

    /**
     * 암호화 예외
     */
    public static class EncryptionException extends RuntimeException {
        public EncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}