package com.sportologic.sprtadmin.service;

/**
 * REST communication with Sportologic hosting API.
 */
public interface SprtRestApiClientService {

    /**
     * Reload customer DS config to add new instance.
     */
    public void reloadCustDbConfig();
}
