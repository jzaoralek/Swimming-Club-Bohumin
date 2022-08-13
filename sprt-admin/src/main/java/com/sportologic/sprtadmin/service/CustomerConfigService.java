package com.sportologic.sprtadmin.service;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.exception.SprtValidException;
import com.sportologic.sprtadmin.vo.DBInitData;

import java.util.List;
import java.util.Locale;

/**
 * Services regarding creating, deleting and updating customer instances.
 */
public interface CustomerConfigService {

    /**
     * Create new customer instance
     * - record in CUSTOMER_CONFIGURATION
     * - email account
     * - DB schema, user, objects and init data
     * - REST call to reload sportologic customer DS
     */
    public DBInitData createCustomerInstance(DBInitData dbInitData, Locale locale) throws SprtValidException;

    /**
     * Sending conformation email to new customer.
     */
    public void sendConfEmailToCust(DBInitData dbInitData);

    /**
     * Sending notification email to Sprt management.
     * @param dbInitData
     */
    public void sendNotifEmailToSprt(DBInitData dbInitData);

    /**
     * Delete all objects regarding customer schema
     * - record in CUSTOMER_CONFIGURATION
     * - email account
     * - DB schema and user
     * - REST call to reload sportologic customer DS
     */
    public void deleteCustomerInstance();

    /**
     * Activate/deactivate customer.
     * @param activate
     */
    public void activateCustomer(boolean activate);

    /**
     * Return all customer config instances.
     * @return
     */
    public List<CustomerConfig> getCustConfigAll();

    /**
     * Checking unique customer name and customer id.
     * @param custName
     * @param locale
     * @throws SprtValidException
     */
    public void validateUniqueCustName(String custName, Locale locale) throws SprtValidException;
}
