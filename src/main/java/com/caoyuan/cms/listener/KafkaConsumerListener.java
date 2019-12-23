package com.caoyuan.cms.listener;

import javax.annotation.Resource;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.service.ArticleService;

/*********************************************************
@ClassName:   KafkaConsumerListener
@author:	     曹原
@date: 		  2019年12月23日 下午7:20:17 
*********************************************************/
@Component
public class KafkaConsumerListener implements MessageListener<String, String> {

	@Resource
	private ArticleService articleService;

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		String key = data.key();
		// (7)编写Kafka消费者，将接到的数据保存到CMS项目数据库。（6分）
		if (key != null && key.equals("article_add")) {
			String value = data.value();
			ArticleWithBLOBs article = JSON.parseObject(value, ArticleWithBLOBs.class);
			articleService.insertSelective(article);
		}
		if (key != null && key.equals("article_Hits")) {
			String value = data.value();
			articleService.updateHits(Integer.parseInt(value));
			System.out.println("kafka修改点击量成功");
		}

	}

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
