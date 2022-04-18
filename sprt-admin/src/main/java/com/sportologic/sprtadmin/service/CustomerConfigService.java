package com.sportologic.sprtadmin.service;

import com.sportologic.common.model.domain.CustomerConfig;
import com.sportologic.sprtadmin.vo.DBInitData;

import java.util.List;

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
    public void createCustomerInstance(CustomerConfig custConfig, DBInitData dbInitData);

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
     * Get CustomerConfig by customer name.
     * @param name
     * @return
     */
    public CustomerConfig findCustConfigByName(String name);
}
