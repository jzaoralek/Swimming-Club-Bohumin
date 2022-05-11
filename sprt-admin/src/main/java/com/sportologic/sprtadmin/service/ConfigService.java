package com.sportologic.sprtadmin.service;

public interface ConfigService {

    String getDbBaseUrl();
    String getDbScriptSrcFolder();
    String getSprtBaseUrl();
    String getSprtBaseHttpUrl();
    String getVpscApiKey();
    String getVpscAdmin();
    String getRecaptchaSiteKey();
    String getRecaptchaSecredKey();
}