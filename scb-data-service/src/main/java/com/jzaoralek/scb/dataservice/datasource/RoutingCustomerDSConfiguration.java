package com.jzaoralek.scb.dataservice.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.jzaoralek.scb.dataservice.domain.CustomerConfig;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Configuration
public class RoutingCustomerDSConfiguration {

	@Bean
	@Primary
	@Autowired
    public DataSource dataSource(AdmCustConfigDao admCustConfigDao) {
        Map<Object, Object> targetDataSources = new HashMap<>();
        
        // Load customer datasources from admin database.
        // TODO: OneApp, configuration exception pokud prazdne
        List<CustomerConfig> custConfigList = admCustConfigDao.getAll();
        for (CustomerConfig custConfig : custConfigList) {
        	targetDataSources.put(custConfig.getCustId(), 
        						buildClientDatasource(custConfig));
        }

        // Get default datasource.
        DataSource defaultDataSource = null;
        CustomerConfig custConfigDefault = admCustConfigDao.getDefault();
        if (custConfigDefault != null) {
        	defaultDataSource = buildClientDatasource(custConfigDefault);
        } else {
        	defaultDataSource = (DataSource)targetDataSources.get(0);
        }

        ClientDataSourceRouter clientRoutingDatasource = new ClientDataSourceRouter();
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        clientRoutingDatasource.setDefaultTargetDataSource(defaultDataSource);
        
        return clientRoutingDatasource;
    }
	
	/**
	 * Create client datasource from CustomerConfig.
	 * @param custConfig
	 * @return
	 */
	private DataSource buildClientDatasource(CustomerConfig custConfig) {
		MysqlDataSource mySqlRootSource = new MysqlDataSource();
		mySqlRootSource.setUrl(custConfig.getDbUrl());
	    mySqlRootSource.setUser(custConfig.getDbUser());
	    mySqlRootSource.setPassword(custConfig.getDbPassword());
	    mySqlRootSource.setCharacterEncoding("utf8");
	    mySqlRootSource.setUseUnicode(true);
	    return mySqlRootSource;
	}
}