package com.ibm;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.ibm.config.AppConfig;
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

@SpringJUnitConfig({GeoLocationService.class, AppConfig.class})
public class GeoLocationServiceIT {
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

    @Test
    void givenCustomerExists_whenRetrieveCustomer_thenSuccess() throws IOException, GeoLocationClientException, GeoLocationServerException {

        Map<String, String> responseObject = new HashMap<>();
        responseObject.put("city", "Burnaby");
        responseObject.put("country", "Canada");

        // Given
        wireMockRule.stubFor(get(urlEqualTo( "/192.168.1.1"))
                .willReturn(aResponse()
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(objectMapper.writeValueAsString(
                                responseObject
                                )
                        ))
        );

        // When
        String city = geoLocationService.getCityForIp("/192.168.1.1");
        System.out.println(city);
        System.out.println(geoLocationService.getGeoLocationForIp("/192.168.1.1"));

        ///List<Customer> customers = customerSrvClient.getCustomers(DEFAULT_CUSTOMER_ID);

        // Then
        wireMockRule.verify(
                getRequestedFor(urlEqualTo("/192.168.1.1"))
        );


//        assertThat(city, is(notNullValue()));
//        assertThat(customers.size(), is(1));
//        assertThat(customers.get(0).getId(), is(DEFAULT_CUSTOMER_ID));
    }

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("geo.location.api.endpoint", wireMockRule::baseUrl);
    }

}
