package org.openmrs.module.m2sysbiometrics.http;

import org.apache.commons.lang.BooleanUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.openmrs.module.m2sysbiometrics.exception.M2SysBiometricsException;
import org.openmrs.module.m2sysbiometrics.model.LoggingMixin;
import org.openmrs.module.m2sysbiometrics.model.M2SysRequest;
import org.openmrs.module.m2sysbiometrics.model.M2SysResponse;
import org.openmrs.module.m2sysbiometrics.model.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

/**
 * Serves as a base for all implementation of the resource interfaces. Provides method for basic
 * REST operations with the M2Sys servers.
 */
@Component
public class M2SysHttpClientImpl implements M2SysHttpClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(M2SysHttpClientImpl.class);

    private RestOperations restOperations = new RestTemplate();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        // don't log customer key
        objectMapper.getSerializationConfig().addMixInAnnotations(M2SysRequest.class, LoggingMixin.class);
        objectMapper.getSerializationConfig().addMixInAnnotations(M2SysResponse.class, LoggingMixin.class);

        MappingJacksonHttpMessageConverter messageConverter = new MappingJacksonHttpMessageConverter();
        messageConverter.setPrettyPrint(false);
        messageConverter.setObjectMapper(objectMapper);

        RestTemplate restTemplate = (RestTemplate) restOperations;
        restTemplate.getMessageConverters().add(messageConverter);
    }

    @Override
    public ResponseEntity<String> getServerStatus(String url, Token token) {
        try {
            return exchange(new URI(url), HttpMethod.GET, String.class, token);
        } catch (URISyntaxException e) {
            throw new M2SysBiometricsException(e);
        }
    }

    /**
     * Sends a post request to the M2Sys server.
     *
     * @param request the request to be sent
     * @return the response json
     */
    @Override
    public M2SysResponse postRequest(String url, M2SysRequest request, Token token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));

        debugRequest(url, request);

        try {
            ResponseEntity<M2SysResponse> responseEntity = exchange(new URI(url), HttpMethod.POST, request, headers,
                    M2SysResponse.class, token);
            M2SysResponse response = responseEntity.getBody();
            checkResponse(response);
            return response;
        } catch (Exception e) {
            throw new M2SysBiometricsException(e);
        }
    }

    private <T> ResponseEntity<T> exchange(URI url, HttpMethod method, Class<T> responseClass, Token token) {
        return exchange(url, method, null, new HttpHeaders(), responseClass, token);
    }

    private <T> ResponseEntity<T> exchange(URI url, HttpMethod method, Object body,
                                           HttpHeaders headers, Class<T> responseClass, Token token) {

        headers.add("Authorization", token.getTokenType() + " " + token.getAccessToken());

        return restOperations.exchange(url, method, new HttpEntity<>(body, headers), responseClass);
    }

    @Override
    public Token getToken(String host, String username, String password) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        String body = "grant_type=password&username=" + username + "&Password=" + password;

        return restOperations.exchange(host + "/cstoken", HttpMethod.POST,
                new HttpEntity<Object>(body, headers), Token.class).getBody();
    }

    private void checkResponse(M2SysResponse response) {
        if (BooleanUtils.isNotTrue(response.getSuccess())) {
            throw new M2SysBiometricsException("Failure response: " + response.getResponseCode());
        }
    }

    private void debugRequest(String url, Object request) {
        if (LOGGER.isDebugEnabled()) {
            if (request == null) {
                LOGGER.debug("{} called");
            } else {
                try {
                    String json = objectMapper.writerWithDefaultPrettyPrinter()
                            .writeValueAsString(request);
                    LOGGER.debug("{} called, request body:\n {}", url, json);
                } catch (IOException e) {
                    throw new M2SysBiometricsException(e);
                }
            }
        }
    }
}
