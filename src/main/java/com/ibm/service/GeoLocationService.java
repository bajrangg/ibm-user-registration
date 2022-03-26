package com.ibm.service;

import com.ibm.constants.GeoLocationFields;
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

    @Value("${geo.location.api.endpoint}")
    private String geoLocationEndpoint;

    @Override
    public Map<String, String> getGeoLocationForIp(String ip, List<String>... fields) throws Exception {

        RestTemplate restTemplate = new RestTemplate();

        ParameterizedTypeReference<Map<String, String>> responseType =
                new ParameterizedTypeReference<>() {};
        RequestEntity<Void> request = RequestEntity.get(geoLocationEndpoint + ip)
                .accept(MediaType.APPLICATION_JSON).build();
        Map<String, String> geoLocationJsonMap = restTemplate.exchange(request, responseType).getBody();

        if (GeoLocationFields.FAIL.equalsIgnoreCase(geoLocationJsonMap.get(GeoLocationFields.STATUS))) {
            throw new Exception(geoLocationJsonMap.get(GeoLocationFields.MESSAGE));
        }
        return geoLocationJsonMap;
    }

    @Override
    public String getCityForIp(String ip) throws Exception {
        Map<String, String> geoLocationMap = getGeoLocationForIp(ip, Arrays.asList(GeoLocationFields.CITY));
        return geoLocationMap.get(GeoLocationFields.CITY);
    }
}
