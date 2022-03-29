package com.ibm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.ibm.config.AppConfig;
import com.ibm.constants.GeoLocationFields;
import com.ibm.exception.GeoLocationClientException;
import com.ibm.exception.GeoLocationServerException;
import com.ibm.service.GeoLocationService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig({GeoLocationService.class, AppConfig.class})
public class GeoLocationServiceITTest {
    public static WireMockServer wireMockRule = new WireMockServer(options().dynamicPort());

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void beforeAll() {
        wireMockRule.start();
    }

    @AfterAll
    public static void afterAll() {
        wireMockRule.stop();
    }

    @AfterEach
    public void afterEach() {
        wireMockRule.resetAll();
    }

    @Autowired
    private GeoLocationService geoLocationService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("geo.location.api.endpoint", wireMockRule::baseUrl);
    }

    @Test
    void givenValidIP_whenUseGeoLocationForIP_thenSuccess() throws IOException, GeoLocationClientException, GeoLocationServerException {

        Map<String, String> responseObject = new HashMap<String, String>();
        responseObject.put(GeoLocationFields.CITY, "Burnaby");
        responseObject.put(GeoLocationFields.COUNTRY, "Canada");

        // Given
        wireMockRule.stubFor(get(urlEqualTo( "/142.28.2.3"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                responseObject
                                )
                        ))
        );

        // When
        String city = geoLocationService.getCityForIp("/142.28.2.3");

        // Then
        wireMockRule.verify(
                getRequestedFor(urlEqualTo("/142.28.2.3"))
        );

        assertEquals( "Burnaby", city);
    }


    @Test
    void givenNonWiremockIP_whenUseGeoLocationForIP_thenServerException() {

        Exception exception = assertThrows(GeoLocationServerException.class, () -> geoLocationService.getCityForIp("/notMocked"));

        String expectedMessage = "Geo location api server error";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

        wireMockRule.verify(
                getRequestedFor(urlEqualTo("/notMocked"))
        );
    }

    @Test
    void givenInvalidIP_whenUseGeoLocationForIP_thenClientException() throws IOException, GeoLocationClientException, GeoLocationServerException {

        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("status", "fail");
        responseObject.put("message", "invalid query");
        responseObject.put("query", "invalidIp");

        // Given
        wireMockRule.stubFor(get(urlEqualTo( "/invalidIp"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                        responseObject
                                )
                        ))
        );



        // When
        Exception exception = assertThrows(GeoLocationClientException.class, () -> geoLocationService.getCityForIp("/invalidIp"));

        // Then
        wireMockRule.verify(
                getRequestedFor(urlEqualTo("/invalidIp"))
        );

        String expectedMessage = "invalid query";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    void givenNonCanadianIp_whenUseGeoLocationForIP_thenClientException() throws IOException, GeoLocationClientException, GeoLocationServerException {

        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("city", "Paris");
        responseObject.put("country", "France");


        // Given
        wireMockRule.stubFor(get(urlEqualTo( "/NonCanadianIp"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                        responseObject
                                )
                        ))
        );



        // When
        Exception exception = assertThrows(GeoLocationClientException.class, () -> geoLocationService.getCityForIp("/NonCanadianIp"));

        // Then
        wireMockRule.verify(
                getRequestedFor(urlEqualTo("/NonCanadianIp"))
        );

        String expectedMessage = "User is not eligible to register!";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }
}
