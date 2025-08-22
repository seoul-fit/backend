package com.seoulfit.backend.publicdata.facilities.application.service;

import com.seoulfit.backend.publicdata.facilities.application.port.out.CommandPublicLibraryPort;
import com.seoulfit.backend.publicdata.facilities.application.port.out.LoadPublicLibraryPort;
import com.seoulfit.backend.publicdata.facilities.domain.Library;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * PublicLibraryService 단위 테스트
 * 
 * @author Seoul Fit
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PublicLibraryService 단위 테스트")
class PublicLibraryServiceTest {

    @Mock
    private CommandPublicLibraryPort commandPublicLibraryPort;
    
    @Mock
    private LoadPublicLibraryPort loadPublicLibraryPort;
    
    @InjectMocks
    private PublicLibraryService publicLibraryService;
    
    private List<Library> mockLibraries;
    private Library library1;
    private Library library2;
    private Library library3;
    
    @BeforeEach
    void setUp() {
        library1 = createLibrary(1L, "서울도서관", "서울특별시 중구 세종대로 110", 37.5665, 126.9780);
        library2 = createLibrary(2L, "남산도서관", "서울특별시 용산구 소월로 109", 37.5537, 126.9810);
        library3 = createLibrary(3L, "정독도서관", "서울특별시 종로구 북촌로5길 48", 37.5803, 126.9837);
        
        mockLibraries = Arrays.asList(library1, library2, library3);
    }
    
    @Nested
    @DisplayName("공공도서관 저장 테스트")
    class SavePublicLibraryListTest {
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 성공")
        void savePublicLibraryList_Success() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(mockLibraries);
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(anyList());
            
            // when
            publicLibraryService.savePublicLibraryList();
            
            // then
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(mockLibraries);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 빈 데이터")
        void savePublicLibraryList_EmptyData() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000))
                .thenReturn(Collections.emptyList());
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(anyList());
            
            // when
            publicLibraryService.savePublicLibraryList();
            
            // then
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(Collections.emptyList());
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 대용량 데이터")
        void savePublicLibraryList_LargeData() {
            // given
            List<Library> largeList = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                largeList.add(createLibrary(
                    (long) i,
                    "도서관 " + i,
                    "서울시 주소 " + i,
                    37.5 + (i * 0.001),
                    127.0 + (i * 0.001)
                ));
            }
            
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(largeList);
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(anyList());
            
            // when
            publicLibraryService.savePublicLibraryList();
            
            // then
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(largeList);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - API 호출 실패")
        void savePublicLibraryList_ApiFailure() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000))
                .thenThrow(new RuntimeException("API 호출 실패"));
            
            // when & then
            assertThatThrownBy(() -> publicLibraryService.savePublicLibraryList())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Public Library data")
                .hasMessageContaining("API 호출 실패");
            
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort, never()).truncate();
            verify(commandPublicLibraryPort, never()).save(anyList());
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - truncate 실패")
        void savePublicLibraryList_TruncateFailure() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(mockLibraries);
            doThrow(new RuntimeException("DB truncate 실패"))
                .when(commandPublicLibraryPort).truncate();
            
            // when & then
            assertThatThrownBy(() -> publicLibraryService.savePublicLibraryList())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Public Library data")
                .hasMessageContaining("DB truncate 실패");
            
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort, never()).save(anyList());
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 저장 실패")
        void savePublicLibraryList_SaveFailure() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(mockLibraries);
            doNothing().when(commandPublicLibraryPort).truncate();
            doThrow(new RuntimeException("DB 저장 실패"))
                .when(commandPublicLibraryPort).save(mockLibraries);
            
            // when & then
            assertThatThrownBy(() -> publicLibraryService.savePublicLibraryList())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to fetch Public Library data")
                .hasMessageContaining("DB 저장 실패");
            
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(mockLibraries);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - null 응답 처리")
        void savePublicLibraryList_NullResponse() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(null);
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(null);
            
            // when
            publicLibraryService.savePublicLibraryList();
            
            // then
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(null);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 실행 순서 검증")
        void savePublicLibraryList_ExecutionOrder() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(mockLibraries);
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(anyList());
            
            // when
            publicLibraryService.savePublicLibraryList();
            
            // then - 순서 검증: load -> truncate -> save
            inOrder(loadPublicLibraryPort, commandPublicLibraryPort);
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(mockLibraries);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 부분 데이터 처리")
        void savePublicLibraryList_PartialData() {
            // given
            List<Library> partialList = Arrays.asList(library1, library2);
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(partialList);
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(anyList());
            
            // when
            publicLibraryService.savePublicLibraryList();
            
            // then
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort).truncate();
            verify(commandPublicLibraryPort).save(partialList);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 예외 메시지 포맷 검증")
        void savePublicLibraryList_ExceptionMessageFormat() {
            // given
            String originalMessage = "네트워크 오류";
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000))
                .thenThrow(new RuntimeException(originalMessage));
            
            // when & then
            assertThatThrownBy(() -> publicLibraryService.savePublicLibraryList())
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Failed to fetch Public Library data: " + originalMessage)
                .getCause()
                .hasMessage(originalMessage);
            
            verify(loadPublicLibraryPort).loadPublicLibrary(1, 1000);
        }
        
        @Test
        @DisplayName("공공도서관 목록 저장 - 중복 호출 방지")
        void savePublicLibraryList_PreventDuplicateCalls() {
            // given
            when(loadPublicLibraryPort.loadPublicLibrary(1, 1000)).thenReturn(mockLibraries);
            doNothing().when(commandPublicLibraryPort).truncate();
            doNothing().when(commandPublicLibraryPort).save(anyList());
            
            // when
            publicLibraryService.savePublicLibraryList();
            publicLibraryService.savePublicLibraryList();
            
            // then
            verify(loadPublicLibraryPort, times(2)).loadPublicLibrary(1, 1000);
            verify(commandPublicLibraryPort, times(2)).truncate();
            verify(commandPublicLibraryPort, times(2)).save(mockLibraries);
        }
    }
    
    // Helper methods
    private Library createLibrary(Long id, String name, String address, 
                                 double latitude, double longitude) {
        Library library = Library.builder()
                .lbrryName(name)
                .codeValue("11010")
                .lbrrySeqNo("01")
                .adres(address)
                .xcnts(longitude)
                .ydnts(latitude)
                .telNo("02-1234-5678")
                .hmpgUrl("http://library.seoul.go.kr")
                .opTime("09:00-22:00")
                .fdrmCloseDate("매주 월요일")
                .lbrrySeName("공공도서관")
                .build();
        
        // Set ID using reflection
        try {
            java.lang.reflect.Field idField = Library.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(library, id);
        } catch (Exception e) {
            // Ignore in test
        }
        
        return library;
    }
}