package com.seoulfit.backend.user.domain;

/**
 * 카카오 OAuth 스코프 정의
 * 카카오 공식 문서 기준 동의항목
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
public enum KakaoScope {
    
    // 기본 정보
    PROFILE_NICKNAME("profile_nickname", "닉네임"),
    PROFILE_IMAGE("profile_image", "프로필 사진"),
    ACCOUNT_EMAIL("account_email", "카카오계정(이메일)"),
    
    // 추가 정보
    GENDER("gender", "성별"),
    AGE_RANGE("age_range", "연령대"),
    BIRTHDAY("birthday", "생일"),
    BIRTHYEAR("birthyear", "출생연도"),
    
    // 친구 및 메시지
    FRIENDS("friends", "카카오톡 친구 목록"),
    TALK_MESSAGE("talk_message", "카카오톡 메시지 전송"),
    
    // 기타
    PHONE_NUMBER("phone_number", "전화번호"),
    LEGAL_NAME("legal_name", "실명"),
    LEGAL_BIRTH_DATE("legal_birth_date", "법정생년월일"),
    LEGAL_GENDER("legal_gender", "법정성별");

    private final String scope;
    private final String description;

    KakaoScope(String scope, String description) {
        this.scope = scope;
        this.description = description;
    }

    public String getScope() {
        return scope;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 여러 스코프를 콤마로 연결
     */
    public static String join(KakaoScope... scopes) {
        if (scopes == null || scopes.length == 0) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < scopes.length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(scopes[i].getScope());
        }
        return sb.toString();
    }

    /**
     * 기본 스코프 (닉네임, 프로필 이미지, 이메일)
     */
    public static String getBasicScopes() {
        return join(PROFILE_NICKNAME, PROFILE_IMAGE, ACCOUNT_EMAIL);
    }
}
