package com.sportologic.sprtadmin.service;

import org.json.JSONObject;

import java.util.LinkedHashMap;

public interface ReCaptchaService {
    LinkedHashMap verify(String recaptchaResponse);
}
