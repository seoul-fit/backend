package com.seoulfit.backend.search.application.port.in;

import com.seoulfit.backend.search.adapter.in.web.dto.SearchIndexResponse;
import com.seoulfit.backend.search.adapter.in.web.dto.SearchResultResponse;

public interface SearchUseCase {
    
    SearchResultResponse searchIndex(String keyword, int page, int size);
    
    Object getPublicDataByIndex(Long indexId);
}