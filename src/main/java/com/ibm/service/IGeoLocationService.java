package com.ibm.service;

import java.util.List;
import java.util.Map;

public interface IGeoLocationService {
    /**
     * Return a Map of fields and corresponding values from the geo location service for a specific IP.
     *
     * The fields are optional and is used to filter only specific fields in response.
     *
     * @param ip Can be a single IPv4/IPv6 address or a domain name. If you don't supply a query the
     *           current IP address will be used.
     * @param fields Optional set of fields to be returned to the response.
     * @return Map key - field name. value - value of the corresponding field. For example - city -> toronto.
     */
    Map<String, String> getGeoLocationForIp(String ip, List<String>... fields) throws Exception;

    String getCityForIp(String ip) throws Exception;

}
