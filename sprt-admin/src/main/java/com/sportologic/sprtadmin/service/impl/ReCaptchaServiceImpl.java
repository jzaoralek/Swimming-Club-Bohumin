package com.sportologic.sprtadmin.service.impl;

import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.ReCaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.zkoss.json.JSONObject;

@Service("reCaptchaService")
public class ReCaptchaServiceImpl implements ReCaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(ReCaptchaServiceImpl.class);

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigService configService;

    @Override
    public JSONObject verifyResponse(String response) {
        logger.info("Verifying reCaptcha response: {}", response);
        String secretKey = configService.getRecaptchaSecredKey();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "response="+response+"&secret="+secretKey;

        JSONObject responseEntity = (JSONObject)restTemplate.getForObject (VERIFY_URL + "?" + urlParameters, JSONObject.class);
        logger.info("Verifying reCaptcha responseEntity: {}", responseEntity);

        return responseEntity;
    }
}
