package com.jzaoralek.scb.dataservice.dao;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(locations={"/scb-data-service-test-context.xml"})
public abstract class BaseTestCase extends AbstractTransactionalJUnit4SpringContextTests {

}
