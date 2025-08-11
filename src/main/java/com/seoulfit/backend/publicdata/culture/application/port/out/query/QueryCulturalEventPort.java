package com.seoulfit.backend.publicdata.culture.application.port.out.query;

import com.seoulfit.backend.publicdata.culture.domain.CulturalEvent;

import java.util.List;

public interface QueryCulturalEventPort {
    List<CulturalEvent> getAllCulturalEvent();
}
