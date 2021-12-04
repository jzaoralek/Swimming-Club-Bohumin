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
public class RoutingTestConfiguration {

	@Autowired
	private AdmCustConfigDao admCustConfigDao;
	
	@Bean
	@Primary
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        
        // Load customer datasources from admin database.
        List<CustomerConfig> custConfigList = admCustConfigDao.getAll();
        // TODO: OneApp, configuration exception pokud prazdne
        for (CustomerConfig custConfig : custConfigList) {
        	targetDataSources.put(custConfig.getCustId(), 
        						buildClientDatasource(custConfig));
        }
        
//        DataSource clientADatasource = clientADatasource();
//        targetDataSources.put("scb", clientADatasource);
//        DataSource clientBDatasource = clientBDatasource();
//        targetDataSources.put("kosatky", clientBDatasource);

        ClientDataSourceRouter clientRoutingDatasource = new ClientDataSourceRouter();
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        clientRoutingDatasource.setDefaultTargetDataSource(targetDataSources.get(0));
        
        return clientRoutingDatasource;
    }
	
	/**
	 * Create client datasource from CustomerConfig.
	 * @param custConfig
	 * @return
	 */
	private DataSource buildClientDatasource(CustomerConfig custConfig) {
		MysqlDataSource oracleRootSource = new MysqlDataSource();
		oracleRootSource.setUrl(custConfig.getDbUrl());
	    oracleRootSource.setUser(custConfig.getDbUser());
	    oracleRootSource.setPassword(custConfig.getDbPassword());
	    return oracleRootSource;
	}
	
//	private DataSource clientADatasource() {
//		MysqlDataSource oracleRootSource = new MysqlDataSource();
//		oracleRootSource.setUrl("jdbc:mysql://localhost:3306/scb");
//	    oracleRootSource.setUser("scb");
//	    oracleRootSource.setPassword("scb");
//	    return oracleRootSource;
//	}
//	
//	private DataSource clientBDatasource() {
//		MysqlDataSource oracleRootSource = new MysqlDataSource();
//		oracleRootSource.setUrl("jdbc:mysql://localhost:3306/kosatky");
//	    oracleRootSource.setUser("kosatky");
//	    oracleRootSource.setPassword("kosatky");
//	    return oracleRootSource;
//	}
}