package com.seoulfit.backend.publicdata.culture.application.service.query;

import com.seoulfit.backend.publicdata.culture.application.port.in.QueryCulturalEventsUseCase;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalEventPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class QueryCulturalEventService implements QueryCulturalEventsUseCase {
    private final QueryCulturalEventPort culturalEventPort;

    @Override
    public List<CulturalEvent> getAllCulturalEvents() {
        List<CulturalEvent> allCulturalEvent = culturalEventPort.getAllCulturalEvent();
        log.info("Count : {}", allCulturalEvent.size());
        return culturalEventPort.getAllCulturalEvent();
    }

    @Override
    public List<CulturalEvent> getCulturalEventByLatitudeAndLongitude(String latitude, String longitude) {
        List<CulturalEvent> culturalEventLocation = culturalEventPort.getCulturalEventLocation(
                Double.parseDouble(latitude),
                Double.parseDouble(longitude)
        );
        log.info("EventLocation Count: {}", culturalEventLocation.size());
        return culturalEventLocation;
    }

}
