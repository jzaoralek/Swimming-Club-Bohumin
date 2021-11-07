package com.jzaoralek.scb.dataservice.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

@Configuration
public class RoutingTestConfiguration {

	@Bean
    public DataSource dataSource() {
        Map<Object, Object> targetDataSources = new HashMap<>();
        DataSource clientADatasource = clientADatasource();
        targetDataSources.put("scb", clientADatasource);
        DataSource clientBDatasource = clientBDatasource();
        targetDataSources.put("kosatky", clientBDatasource);

        ClientDataSourceRouter clientRoutingDatasource = new ClientDataSourceRouter();
        clientRoutingDatasource.setTargetDataSources(targetDataSources);
        clientRoutingDatasource.setDefaultTargetDataSource(clientADatasource);
        
        return clientRoutingDatasource;
    }
	
	private DataSource clientADatasource() {
		MysqlDataSource oracleRootSource = new MysqlDataSource();
		oracleRootSource.setUrl("jdbc:mysql://localhost:3306/scb");
	    oracleRootSource.setUser("scb");
	    oracleRootSource.setPassword("scb");
	    return oracleRootSource;
	}
	
	private DataSource clientBDatasource() {
		MysqlDataSource oracleRootSource = new MysqlDataSource();
		oracleRootSource.setUrl("jdbc:mysql://localhost:3306/kosatky");
	    oracleRootSource.setUser("kosatky");
	    oracleRootSource.setPassword("kosatky");
	    return oracleRootSource;
	}
}