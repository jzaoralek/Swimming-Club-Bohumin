package com.jzaoralek.scb.dataservice.datasource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.CollectionUtils;

import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.sportologic.common.model.domain.CustomerConfig;

@Configuration
public class RoutingCustomerDSConfiguration {

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
	private AdmCustConfigDao admCustConfigDao;
	private Map<Object, Object> targetDataSources;

	@Bean
	@Primary
	@Autowired
    public DataSource dataSource(AdmCustConfigDao admCustConfigDao) {
        this.admCustConfigDao = admCustConfigDao;
        
        // init datasources
        targetDataSources = buildTargetDataSources();

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
	
	public Map<Object, Object> buildTargetDataSources() {
		Map<Object, Object> ret = new HashMap<>();
        // Load customer datasources from admin database.
        List<CustomerConfig> custConfigList = admCustConfigDao.getAll();
        if (CollectionUtils.isEmpty(custConfigList)) {
        	LOG.error("Empty customer config list.");
        	throw new IllegalStateException("Empty customer config list.");
        }
        for (CustomerConfig custConfig : custConfigList) {
        	ret.put(custConfig.getCustId(), 
        						buildClientDatasource(custConfig));
        }
        
        return ret;
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
	
	public void setTargetDataSources(Map<Object, Object> targetDataSources) {
		this.targetDataSources = targetDataSources;
	}
	
	public Map<Object, Object> getTargetDataSources() {
		return targetDataSources;
	}
}