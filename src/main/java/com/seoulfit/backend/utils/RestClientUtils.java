package com.seoulfit.backend.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
public class RestClientUtils<T> {
    private final RestClient restClient;

    public RestClientUtils(@Qualifier("seoulApiRestClient") RestClient restClient) {
        this.restClient = restClient;
    }

    public T callGetApi(String url, Class<T> responseClass) {
        try {
            T response = restClient.get()
                    .uri(url)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, clientResponse) -> {
                        log.error("Client error calling Seoul API: {} - {} for URL: {}", 
                                clientResponse.getStatusCode(), clientResponse.getStatusText(), url);
                        throw new RestClientException("Seoul API client error: " + clientResponse.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, serverResponse) -> {
                        log.error("Server error calling Seoul API: {} - {} for URL: {}", 
                                serverResponse.getStatusCode(), serverResponse.getStatusText(), url);
                        throw new RestClientException("Seoul API server error: " + serverResponse.getStatusCode());
                    })
                    .body(responseClass);

            log.debug("Response: {}", response);
            
            return response;

        } catch (RestClientException e) {
            log.error("RestClient error fetching data from Seoul API for URL: {}", url, e);
            throw new RuntimeException("Failed to fetch data from Seoul API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error fetching data from Seoul API for URL: {}", url, e);
            throw new RuntimeException("Failed to fetch data from Seoul API", e);
        }
    }

    public T callPostApi(String url, Object request, Class<T> responseClass) {
        try {
            log.info("Calling POST API: {}", url);
            
            T response = restClient.post()
                    .uri(url)
                    .body(request)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (req, clientResponse) -> {
                        log.error("Client error calling Seoul POST API: {} - {} for URL: {}", 
                                clientResponse.getStatusCode(), clientResponse.getStatusText(), url);
                        throw new RestClientException("Seoul API client error: " + clientResponse.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (req, serverResponse) -> {
                        log.error("Server error calling Seoul POST API: {} - {} for URL: {}", 
                                serverResponse.getStatusCode(), serverResponse.getStatusText(), url);
                        throw new RestClientException("Seoul API server error: " + serverResponse.getStatusCode());
                    })
                    .body(responseClass);

            log.info("POST API call successful for URL: {}", url);
            return response;

        } catch (RestClientException e) {
            log.error("RestClient error in POST request to Seoul API for URL: {}", url, e);
            throw new RuntimeException("Failed to post data to Seoul API: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error in POST request to Seoul API for URL: {}", url, e);
            throw new RuntimeException("Failed to post data to Seoul API", e);
        }
    }
}
