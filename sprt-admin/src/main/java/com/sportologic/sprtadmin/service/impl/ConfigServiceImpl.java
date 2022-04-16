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

    @Override
    public String getDbBaseUrl() {
        return databaseBaseUrl;
    }

    @Override
    public String getDbScriptSrcFolder() {
        return dbScriptSrcFolder;
    }
}
