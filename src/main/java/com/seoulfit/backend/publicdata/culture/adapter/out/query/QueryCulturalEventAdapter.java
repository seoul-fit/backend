package com.seoulfit.backend.publicdata.culture.adapter.out.query;

import com.seoulfit.backend.publicdata.culture.adapter.out.custom.CulturalEventRepository;
import com.seoulfit.backend.publicdata.culture.application.port.out.query.QueryCulturalEventPort;
import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class QueryCulturalEventAdapter implements QueryCulturalEventPort {
    private final CulturalEventRepository culturalEventRepository;

    @Override
    public List<CulturalEvent> getAllCulturalEvent() {
        return culturalEventRepository.findAll();
    }

}
