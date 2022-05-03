package com.sportologic.sprtadmin.service;

import org.zkoss.json.JSONObject;

public interface ReCaptchaService {

    JSONObject verifyResponse(String response);
}
