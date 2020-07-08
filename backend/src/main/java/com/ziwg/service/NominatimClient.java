package com.ziwg.service;

import com.ziwg.model.nominatim.NominatimLocationPojo;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class NominatimClient {

    private static final String EMAIL = "235051@student.pwr.edu.pl";
    private static final String NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search?format=geocodejson&namedetails=1&limit=1";
    private static final String NOMINATIM_EMAIL_PARAM = "&email=";
    private static final String NOMINATIM_LOCATION_PARAM = "&q=";
    public static final int NOMINATIM_REQUEST_INTERVAL = 1200;

    private final RestTemplate restTemplate;

    @Autowired
    public NominatimClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public NominatimLocationPojo.Feature getLocation(String location) {
        NominatimLocationPojo results = executeLocationSearch(location).getBody();
        if (results != null && !results.getFeatures().isEmpty()) {
            return results.getFeatures().get(0);
        }
        return null;
    }

    private ResponseEntity<NominatimLocationPojo> executeLocationSearch(String location) {
        return restTemplate
                .exchange(getNominatimUrl(location, EMAIL),
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        NominatimLocationPojo.class);
    }

    private String getNominatimUrl(String location, String email) {
        return NOMINATIM_SEARCH_URL
                + getLocationQueryParam(location)
                + getEmailQueryParam(email);
    }

    private String getEmailQueryParam(String email) {
        return NOMINATIM_EMAIL_PARAM + email;
    }

    private String getLocationQueryParam(String location) {
        return NOMINATIM_LOCATION_PARAM + location;
    }
}