package com.chemspider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestConversions {
	private static final Log log = LogFactory.getLog(TestConversions.class);

	@Autowired
	@Qualifier("chemSpiderService")
	InChISoap chemSpiderService;

	private static final String SMILE = "CN1CCC(CC1)COc2cc3c(cc2OC)c(ncn3)Nc4ccc(cc4F)Br";

	@Before
	public void setUpBefore() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("META-INF/services-client-config.xml");
		ctx.getAutowireCapableBeanFactory().autowireBean(this);
	}

	// @Ignore
	@Test
	public void test1() {
		SMILESToInChI inParam = new SMILESToInChI();
		inParam.setSmiles(SMILE);
		String inChI = chemSpiderService.smilesToInChI(SMILE);
		log.info("InChI="+inChI);
	}

}
