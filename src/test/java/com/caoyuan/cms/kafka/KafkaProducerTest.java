package com.caoyuan.cms.kafka;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.alibaba.fastjson.JSON;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.service.CategoryService;
import com.caoyuan.cms.service.ChannelService;
import com.caoyuan.cms.utils.ArticleEnum;
import com.cy.util.DateUtil;
import com.cy.util.RandomUtil;
import com.cy.util.StreamUtil;

/*********************************************************
@ClassName:   KafkaProducerTest
@author:	     曹原
@date: 		  2019年12月23日 下午6:41:31 
*********************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-beans.xml")
public class KafkaProducerTest {

	@Resource
	private ChannelService channelService;

	@Resource
	private CategoryService categoryService;

	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;

	@Test
	public void testAdd() throws FileNotFoundException {
		// 查出来全部频道
		List<Channel> channels = channelService.selects();
		// 查出来全部频道下的种类
		Map<Integer, List<Category>> map = new HashMap<>();
		for (Channel channel : channels) {
			List<Category> categories = categoryService
					.selectsByChannelId(channel.getId());
			map.put(channel.getId(), categories);
		}

		// 读取小说目录
		File dir = new File("E:\\text");
		File[] listFiles = dir.listFiles();
		for (File file : listFiles) {
			String title = file.getName().replace(".txt", "");
			String content = StreamUtil.readTextFile(file);
			ArticleWithBLOBs article = new ArticleWithBLOBs();
			// (1)将文件名作为Article对象的title属性值。文本内容作为Article对象的content属性值。（2分）
			article.setTitle(title);
			article.setContent(content);
			// (2)在文本内容中截取前140个字作为摘要。（2分）
			if (content.length() > 140) {
				article.setSummary(content.substring(0, 140));
			} else {
				article.setSummary(content);
			}
			// (3)“点击量”和“是否热门”、“频道”字段要使用随机值。（2分）
			article.setHits(RandomUtil.random(0, 10000));
			article.setHot(RandomUtil.random(0, 1));
			Integer channelId = channels.get(RandomUtil.random(0, channels.size() - 1))
					.getId();
			article.setChannelId(channelId);
			List<Category> categories = map.get(channelId);
			Integer categoryId = categories
					.get(RandomUtil.random(0, categories.size() - 1)).getId();
			article.setCategoryId(categoryId);
			// (4)文章发布日期从2019年1月1日模拟到今天。（2分）
			article.setCreated(
					DateUtil.randomDate(DateUtil.parse("2019-01-01", 0), new Date()));
			// (5)其它的字段随便模拟。
			article.setContentType(ArticleEnum.HTML.getCode());
			article.setDeleted(0);
			article.setPicture("05cdf03e-bb71-453e-b5db-78bc6660ad5b.jpg");
			article.setStatus(1);
			article.setUpdated(new Date());
			// (6)编写Kafka生产者，然后将生成Article对象通过Kafka发送到消费端。（6分）
			String jsonString = JSON.toJSONString(article);
			kafkaTemplate.sendDefault("article_add", jsonString);
			// (7)编写Kafka消费者，将接到的数据保存到CMS项目数据库。（6分）

		}
		System.out.println("文章生成并已发送完毕");
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
