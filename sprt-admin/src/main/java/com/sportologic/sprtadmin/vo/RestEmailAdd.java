package com.sportologic.sprtadmin.vo;

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

    public String getCommand() {
        return command;
    }

    public String getDomain() {
        return domain;
    }

    public String getUsername() {
        return username;
    }

    public String getPass() {
        return pass;
    }

    public String getName() {
        return name;
    }

    public String getQuotaMB() {
        return quotaMB;
    }

    public void setQuotaMB(String quotaMB) {
        this.quotaMB = quotaMB;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public void setName(String name) {
        this.name = name;
    }
}
