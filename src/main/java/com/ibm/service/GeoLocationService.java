package com.ibm.service;

import com.ibm.constants.GeoLocationFields;
import com.ibm.exception.GeoLocationClientException;
import com.ibm.exception.GeoLocationServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class GeoLocationService implements IGeoLocationService{

    private static Logger logger = LoggerFactory.getLogger(GeoLocationService.class);

    private final RestTemplate restTemplate;

    @Value("${geo.location.api.endpoint}")
    private String geoLocationEndpoint;

    public GeoLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Map<String, String> getGeoLocationForIp(String ip, List<String>... fields) throws GeoLocationClientException, GeoLocationServerException {

        ParameterizedTypeReference<Map<String, String>> responseType =
                new ParameterizedTypeReference<>() {};
        RequestEntity<Void> request = RequestEntity.get(geoLocationEndpoint + ip)
                .accept(MediaType.APPLICATION_JSON).build();
        Map<String, String> geoLocationJsonMap;

        try {
            geoLocationJsonMap = restTemplate.exchange(request, responseType).getBody();
        } catch (Exception ex) { // catch all exception for the service
            logger.error("Server error occurred while connecting!");
            throw new GeoLocationServerException("Geo location api server error");
        }

        if (GeoLocationFields.FAIL.equalsIgnoreCase(geoLocationJsonMap.get(GeoLocationFields.STATUS))) {
            logger.error("Invalid client input error.");
            throw new GeoLocationClientException(geoLocationJsonMap.get(GeoLocationFields.MESSAGE));
        }

        // Business Validation:
        // If the IP is not in Canada, return error message that user is not elligible to register
        if (!GeoLocationFields.CANADA.equalsIgnoreCase(geoLocationJsonMap.get(GeoLocationFields.COUNTRY))) {
            logger.error("User's IP address is not in Canada. Not allowed to register.");
            throw new GeoLocationClientException("User is not eligible to register!");
        }

        return geoLocationJsonMap;
    }

    @Override
    public String getCityForIp(String ip) throws GeoLocationClientException, GeoLocationServerException {
        Map<String, String> geoLocationMap = getGeoLocationForIp(ip, Arrays.asList(GeoLocationFields.CITY));
        return geoLocationMap.get(GeoLocationFields.CITY);
    }
}
