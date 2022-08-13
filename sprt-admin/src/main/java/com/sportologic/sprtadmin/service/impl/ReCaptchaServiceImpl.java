package com.sportologic.sprtadmin.service.impl;

import org.json.JSONException;
import org.json.JSONObject;
import com.sportologic.sprtadmin.service.ConfigService;
import com.sportologic.sprtadmin.service.ReCaptchaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

@Service
public class ReCaptchaServiceImpl implements ReCaptchaService {

    private static final Logger logger = LoggerFactory.getLogger(ReCaptchaServiceImpl.class);

    private static final String VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ConfigService configService;

    @Override
    public LinkedHashMap verify(String recaptchaResponse) {
        logger.info("Verifying reCaptcha response: {}", recaptchaResponse);
        String secretKey = configService.getRecaptchaSecredKey();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept-Language", "en-US,en;q=0.5");

        String urlParameters = "response="+recaptchaResponse+"&secret="+secretKey;

        logger.info("Verifying reCaptcha response secretKey: {}", secretKey);

        LinkedHashMap responseMap = restTemplate.getForObject(VERIFY_URL + "?" + urlParameters, LinkedHashMap.class);
        logger.info("Verifying reCaptcha responseMap: {}", responseMap);

        return responseMap;
    }
}
