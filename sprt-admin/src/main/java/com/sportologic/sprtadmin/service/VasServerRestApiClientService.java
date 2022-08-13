package com.sportologic.sprtadmin.service;

/**
 * REST communication with Vas-Server hosting API.
 */
public interface VasServerRestApiClientService {

    /**
     * Create new email account on Vas-server.
     * @param username
     * @param password
     * @return
     */
    public String createEmail(String username, String password);
    public void deleteEmail();
}
