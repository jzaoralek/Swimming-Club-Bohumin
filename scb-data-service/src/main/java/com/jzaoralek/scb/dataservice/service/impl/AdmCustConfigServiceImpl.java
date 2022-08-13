package com.jzaoralek.scb.dataservice.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private final Logger LOG = LoggerFactory.getLogger(getClass());
	
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
		LOG.info("Updating customer data source configuration.");
		RoutingCustomerDSConfiguration config = beanFactory.getBean(RoutingCustomerDSConfiguration.class);
		
		ClientDataSourceRouter ds = beanFactory.getBean(ClientDataSourceRouter.class);
		// update target datasources
		ds.setTargetDataSources(config.buildTargetDataSources());
		ds.afterPropertiesSet();
		
		// remove and register DataSourec bean with updated datasources
		DefaultListableBeanFactory defListBeanFactory = (DefaultListableBeanFactory)beanFactory;
		defListBeanFactory.destroySingleton("dataSource");
		defListBeanFactory.registerSingleton("dataSource", ds);
		
		LOG.info("Customer data source configuration successfully updated.");
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;		
	}
}
