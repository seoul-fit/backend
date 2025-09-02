package com.seoulfit.backend.search.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.seoulfit.backend.search.adapter.in.web.dto.SearchResultResponse;
import com.seoulfit.backend.search.adapter.in.web.dto.SearchIndexResponse;
import com.seoulfit.backend.search.application.port.in.SearchUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * SearchController 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@WebMvcTest(controllers = SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("SearchController 단위 테스트")
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private SearchUseCase searchUseCase;
    
    @Test
    @WithMockUser
    @DisplayName("키워드 검색 - 성공")
    void searchByKeyword_Success() throws Exception {
        // given
        String keyword = "도서관";
        SearchIndexResponse indexResponse = new SearchIndexResponse();
        indexResponse.setName("서울도서관");
        indexResponse.setAddress("서울시 중구");
        
        SearchResultResponse mockResponse = new SearchResultResponse();
        mockResponse.setContent(Collections.singletonList(indexResponse));
        mockResponse.setPage(0);
        mockResponse.setSize(10);
        mockResponse.setTotalElements(1L);
        mockResponse.setTotalPages(1);
        
        when(searchUseCase.searchIndex(keyword, 0, 10))
            .thenReturn(mockResponse);
        
        // when & then
        mockMvc.perform(get("/api/search")
                .param("keyword", keyword)
                .param("page", "0")
                .param("size", "10"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content[0].name").value("서울도서관"));
        
        verify(searchUseCase, times(1)).searchIndex(keyword, 0, 10);
    }
    
    @Test
    @WithMockUser
    @DisplayName("검색 결과 없음")
    void search_NoResults() throws Exception {
        // given
        String keyword = "존재하지않는키워드";
        SearchResultResponse mockResponse = new SearchResultResponse();
        mockResponse.setContent(Collections.emptyList());
        mockResponse.setPage(0);
        mockResponse.setSize(10);
        mockResponse.setTotalElements(0L);
        mockResponse.setTotalPages(0);
        
        when(searchUseCase.searchIndex(keyword, 0, 10))
            .thenReturn(mockResponse);
        
        // when & then
        mockMvc.perform(get("/api/search")
                .param("keyword", keyword))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.content").isArray())
            .andExpect(jsonPath("$.data.content").isEmpty());
    }
    
    @Test
    @WithMockUser
    @DisplayName("인덱스 상세 조회")
    void getPublicDataByIndex_Success() throws Exception {
        // given
        Long indexId = 1L;
        Object mockData = new Object();
        
        when(searchUseCase.getPublicDataByIndex(indexId))
            .thenReturn(mockData);
        
        // when & then
        mockMvc.perform(get("/api/search/index/{id}", indexId))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
        
        verify(searchUseCase, times(1)).getPublicDataByIndex(indexId);
    }
}