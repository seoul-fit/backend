package com.seoulfit.backend.publicdata.park.infrastructure.mapper;

import com.seoulfit.backend.publicdata.park.adapter.out.api.dto.SeoulParkApiResponse;
import com.seoulfit.backend.publicdata.park.domain.Park;

import java.util.List;

import static java.lang.Double.parseDouble;

public class ParkMapper {

    public static List<Park> mapToEntity(List<SeoulParkApiResponse.ParkInfo> parkInfoList) {
        return parkInfoList.stream()
                .map(parkInfo -> Park.builder()
                        .parkIdx(parkInfo.getPIdx())
                        .name(parkInfo.getPPark())
                        .content(parkInfo.getPListContent())
                        .area(parkInfo.getArea())
                        .openDate(parkInfo.getOpenDt())
                        .mainEquipment(parkInfo.getMainEquip())
                        .mainPlants(parkInfo.getMainPlants())
                        .guidance(parkInfo.getGuidance())
                        .visitRoad(parkInfo.getVisitRoad())
                        .useReference(parkInfo.getUseRefer())
                        .imageUrl(parkInfo.getPImg())
                        .zone(parkInfo.getPZone())
                        .address(parkInfo.getPAddr())
                        .managementDept(parkInfo.getPName())
                        .adminTel(parkInfo.getPAdmintel())
                        .grs80Longitude(
                                (parkInfo.getGLongitude() == null || parkInfo.getGLongitude().isBlank())  ? 0.0 : parseDouble(parkInfo.getGLongitude())
                        )
                        .grs80Latitude(
                                (parkInfo.getGLatitude() == null || parkInfo.getGLatitude().isBlank()) ? 0.0 : parseDouble(parkInfo.getGLatitude())
                        )
                        .longitude(
                                (parkInfo.getLongitude() == null || parkInfo.getLongitude().isBlank()) ? 0.0 : parseDouble(parkInfo.getLongitude())
                        )
                        .latitude(
                                (parkInfo.getLatitude() == null || parkInfo.getLatitude().isBlank()) ? 0.0 : parseDouble(parkInfo.getLatitude())
                        )
                        .templateUrl(parkInfo.getTemplateUrl())
                        .build()).toList();
    }

}
