package com.seoulfit.backend.publicdata.facilities.infrastructure.mapper;

import com.seoulfit.backend.publicdata.facilities.adapter.out.persistence.api.dto.SeoulLibraryInfoDto;
import com.seoulfit.backend.publicdata.facilities.domain.PublicLibrary;

import java.util.List;

public class PublicLibraryMapper {

    public static List<PublicLibrary> convertToEntity(SeoulLibraryInfoDto.SeoulPublicLibraryInfo seoulPublicLibraryInfo) {
        List<SeoulLibraryInfoDto.LibraryRowDTO> rows = seoulPublicLibraryInfo.getRows();

        if(rows==null || rows.isEmpty())
            return List.of();

        return rows.stream()
                .map(row -> PublicLibrary.builder()
                        .lbrryName(row.getLbrryName())
                        .guCode(row.getGuCode())
                        .codeValue(row.getCodeValue())
                        .adres(row.getAdres())
                        .telNo(row.getTelNo())
                        .hmpgUrl(row.getHmpgUrl())
                        .opTime(row.getOpTime())
                        .fdrmCloseDate(row.getFdrmCloseDate())
                        .lbrrySeName(row.getLbrrySeName())
                        .xcnts(row.getXcnts())
                        .ydnts(row.getYdnts())
                        .build())
                .toList();
    }
}
