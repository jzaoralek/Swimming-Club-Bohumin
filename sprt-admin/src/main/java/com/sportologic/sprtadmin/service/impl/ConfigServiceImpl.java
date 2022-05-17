package com.sportologic.sprtadmin.service.impl;

import com.sportologic.sprtadmin.service.ConfigService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("configService")
public class ConfigServiceImpl implements ConfigService {

    @Value("${database.base.url}")
    private String databaseBaseUrl;

    @Value("${database.script.src.folder}")
    private String dbScriptSrcFolder;

    @Value("${sprt.base.url}")
    private String sprtBaseUrl;

    @Value("${sprt.base.http.url}")
    private String sprtBaseHttpUrl;

    @Value("${vpsc.apikey}")
    private String vpscApiKey;

    @Value("${vpsc.admin}")
    private String vpscAdmin;

    @Value("${recaptcha.sitekey}")
    private String recaptchaSiteKey;

    @Value("${recaptcha.secretkey}")
    private String recaptchaSecretKey;

    @Override
    public String getDbBaseUrl() {
        return databaseBaseUrl;
    }

    @Override
    public String getDbScriptSrcFolder() {
        return dbScriptSrcFolder;
    }

    @Override
    public String getSprtBaseUrl() {
        return sprtBaseUrl;
    }

    @Override
    public String getSprtBaseHttpUrl() {
        return sprtBaseHttpUrl;
    }

    @Override
    public String getVpscApiKey() {
        return vpscApiKey;
    }

    @Override
    public String getVpscAdmin() {
        return vpscAdmin;
    }

    @Override
    public String getRecaptchaSiteKey() {
        return recaptchaSiteKey;
    }

    @Override
    public String getRecaptchaSecredKey() {
        return recaptchaSecretKey;
    }
}