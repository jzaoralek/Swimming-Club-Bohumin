package com.sportologic.sprtadmin.service;

import org.json.JSONObject;

public interface ReCaptchaService {
    JSONObject verify(String recaptchaResponse);
}
