package com.seoulfit.backend.application.service.bases;

import com.seoulfit.backend.application.service.CulturalApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 서울시 공공데이터 API 추상 서비스
 */
@Slf4j
public abstract class CulturalBaseApiService<T> implements CulturalApiService<T> {

}
