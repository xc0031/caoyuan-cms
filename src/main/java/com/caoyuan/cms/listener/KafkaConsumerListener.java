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
@date: 		  2019年12月19日 下午4:07:37 
*********************************************************/
@Component
public class KafkaConsumerListener implements MessageListener<String, String> {

	@Resource
	private ArticleService articleService;

	@Override
	public void onMessage(ConsumerRecord<String, String> data) {
		String key = data.key();
		//添加1000章文章
		if (key != null && key.equals("article_add")) {
			String value = data.value();
			// 转换成对象
			ArticleWithBLOBs article = JSON.parseObject(value, ArticleWithBLOBs.class);
			// 存入mysql数据库
			articleService.insertSelective(article);
		}
		//kafka执行文章点击量+1
		if (key != null && key.equals("article_updateHits")) {
			String value = data.value();
			// 转换成对象
			int id = Integer.parseInt(value);
			// 存入mysql数据库
			articleService.updateHits(id);
			System.out.println(id + "=====点击量修改成功");
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
