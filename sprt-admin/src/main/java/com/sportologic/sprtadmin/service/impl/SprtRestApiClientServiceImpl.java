package com.sportologic.sprtadmin.service.impl;

import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.SprtRestApiClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SprtRestApiClientServiceImpl implements SprtRestApiClientService {

    private static final Logger logger = LoggerFactory.getLogger(SprtRestApiClientServiceImpl.class);

    private static final String SPRT_CUST_DS_RELOAD_URI = "api/cust-ds-config-reload.zul";

    @Autowired
    private ConfigService configService;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void reloadCustDbConfig() {
        String sprtBaseHttpUrl = configService.getSprtBaseHttpUrl();
        logger.info("Reloading customer DS config via url: {}", sprtBaseHttpUrl + SPRT_CUST_DS_RELOAD_URI);
        restTemplate.exchange(sprtBaseHttpUrl + SPRT_CUST_DS_RELOAD_URI, HttpMethod.GET, null, String.class);
    }
}
