package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;

import com.jzaoralek.scb.dataservice.dao.AdmCustConfigDao;
import com.jzaoralek.scb.dataservice.datasource.ClientDataSourceRouter;
import com.jzaoralek.scb.dataservice.datasource.RoutingCustomerDSConfiguration;
import com.jzaoralek.scb.dataservice.service.AdmCustConfigService;
import com.sportologic.common.model.domain.CustomerConfig;

@Service("admCustConfigService")
public class AdmCustConfigServiceImpl implements AdmCustConfigService, BeanFactoryAware {

	@Autowired
	private AdmCustConfigDao admCustConfigDao;
	
	private BeanFactory beanFactory;
	
	@Override
	public Set<String> getCustCongigIds() {
		List<CustomerConfig> custConfigList = admCustConfigDao.getAll();
		return custConfigList.stream()
							.map(CustomerConfig::getCustId)
							.collect(Collectors.toSet());
	}
	
	@Override
	public List<CustomerConfig> getCustomerAll() {
		return admCustConfigDao.getAll();
	}

	@Override
	public void updateCustomerDSConfiguration() {
		RoutingCustomerDSConfiguration config = beanFactory.getBean(RoutingCustomerDSConfiguration.class);
		config.updateTargetDataSources();
		
		
		ClientDataSourceRouter ds = beanFactory.getBean(ClientDataSourceRouter.class);
		ds.setTargetDataSources(config.getTargetDataSources());
		ds.afterPropertiesSet();
		
		DefaultListableBeanFactory defListBeanFactory = (DefaultListableBeanFactory)beanFactory;
		defListBeanFactory.destroySingleton("dataSource");
		defListBeanFactory.registerSingleton("dataSource", ds);
		
		ds = beanFactory.getBean(ClientDataSourceRouter.class);
		System.out.println(ds);
		
		/*
		String updateUrl = "https://download.acme.com/ip-database-latest.mdb";

	    AbstractBeanDefinition definition = BeanDefinitionBuilder
	        .genericBeanDefinition(RoutingCustomerDSConfiguration.class)
	        .addPropertyValue("targetDataSources", updateUrl)
	        .getBeanDefinition();

	    listableBeanFactory
	        .registerBeanDefinition("ipDatabaseRepository", definition);

	    ipDatabaseRepository = listableBeanFactory
	        .getBean(IpDatabaseRepository.class);
	    */
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;		
	}
}
