package com.sportologic.sprtadmin.service.impl;

import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.VasServerRestApiClientService;
import com.sportologic.sprtadmin.dto.RestEmailAdd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VasServerRestApiClientServiceImpl implements VasServerRestApiClientService {

    private static final Logger logger = LoggerFactory.getLogger(VasServerRestApiClientServiceImpl.class);

    private static final String SPRT_DOMAIN = "sportologic.cz";
    private static final String VAS_SERVER_REST_API_URL = "https://pio12.vas-server.cz/admin/api/v1/api.php";
    private static final String VAS_SERVER_EMAIl_ADD_CMD = "email-add";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigService configService;

    @Override
    public String createEmail(String username, String password) {
        String newEmail = username + "@" + SPRT_DOMAIN;
        try {
            logger.info("Creating new email: {}", newEmail);
            HttpHeaders headers = new HttpHeaders();
            headers.setCacheControl("no-cache");
            headers.set("x-vpsc-apikey", configService.getVpscApiKey());
            headers.set("x-vpsc-admin", configService.getVpscAdmin());

            RestEmailAdd emailAdd = new RestEmailAdd(VAS_SERVER_EMAIl_ADD_CMD, SPRT_DOMAIN, username, password, username, "");

            HttpEntity<RestEmailAdd> entity = new HttpEntity<RestEmailAdd>(emailAdd, headers);

            ResponseEntity responseEntity = restTemplate.exchange(VAS_SERVER_REST_API_URL + "?" + VAS_SERVER_EMAIl_ADD_CMD, HttpMethod.POST, entity, String.class);

            logger.info("Creating new email response status code: {}", responseEntity.getStatusCode());
            logger.info("New email {} successfully created.", responseEntity.getBody());

            return newEmail;
        } catch (Exception e) {
            logger.error("Exception caught during creating new email: {}", newEmail, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteEmail() {
        throw new RuntimeException("Not implemented yet");
    }
}
