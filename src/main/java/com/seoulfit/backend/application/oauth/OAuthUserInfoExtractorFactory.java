package com.seoulfit.backend.application.oauth;

import com.seoulfit.backend.domain.OAuthProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class OAuthUserInfoExtractorFactory {

    private final Map<OAuthProvider, OAuthUserInfoExtractor> extractors;

    public OAuthUserInfoExtractorFactory(List<OAuthUserInfoExtractor> extractorList) {
        this.extractors = extractorList.stream()
                .collect(Collectors.toMap(
                        OAuthUserInfoExtractor::getProvider,
                        Function.identity()
                ));
    }

    public OAuthUserInfoExtractor getExtractor(OAuthProvider provider) {
        OAuthUserInfoExtractor extractor = extractors.get(provider);
        if (extractor == null) {
            throw new IllegalArgumentException("Unsupported OAuth provider: " + provider);
        }
        return extractor;
    }
}
