package com.sportologic.sprtadmin.vo;

import lombok.Getter;

@Getter
public class DBCredentials {

    private final String username;
    private final String password;
    private final String schema;

    public DBCredentials(String username, String password, String schema) {
        this.username = username;
        this.password = password;
        this.schema = schema;
    }
}
