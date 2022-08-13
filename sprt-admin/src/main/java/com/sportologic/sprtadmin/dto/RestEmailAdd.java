package com.sportologic.sprtadmin.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Object for email-add REST action.
 *
 * 	[
 * 		"command": "email-add",
 * 	    "domain": "sportologic.cz",
 * 	    "username": "TestUser001",
 * 	    "pass": "TestUser1234*",
 * 	    "name": "TestUser",
 * 	    "quotaMB": "",
 * ]
 */
@Getter
@Setter
public class RestEmailAdd {

    private String command;
    private String domain;
    private String username;
    private String pass;
    private String name;
    private String quotaMB;

    public RestEmailAdd(String command, String domain, String username, String pass, String name, String quotaMB) {
        this.command = command;
        this.domain = domain;
        this.username = username;
        this.pass = pass;
        this.name = name;
        this.quotaMB = quotaMB;
    }
}