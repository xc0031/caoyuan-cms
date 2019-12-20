package com.caoyuan.cms.es;

import javax.annotation.Resource;

import org.junit.runner.RunWith;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/*********************************************************
@ClassName:   EsTest
@author:	     曹原
@date: 		  2019年12月20日 上午9:36:00 
*********************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-beans.xml")
public class EsTest {
	
	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;
	
	
}

/**
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 */