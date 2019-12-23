package com.caoyuan.cms.kafka;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.service.CategoryService;
import com.caoyuan.cms.service.ChannelService;
import com.cy.util.DateUtil;
import com.cy.util.RandomUtil;
import com.cy.util.StreamUtil;

/*********************************************************
@ClassName:   ProducerTest
@author:	     曹原
@date: 		  2019年12月19日 下午4:12:35 
*********************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-beans.xml")
public class ProducerTest {

	@Resource
	private KafkaTemplate<String, String> kafkaTemplate;

	@Resource
	private ChannelService channelService;

	@Resource
	private CategoryService categoryService;

	@Test
	public void testSendMsg() throws FileNotFoundException {
		// 栏目
		// 获取所有的栏目，存入list中
		List<Channel> channels = channelService.selects();
		// 获取栏目下的种类
		HashMap<Integer, List<Category>> map = new HashMap<>();
		for (Channel channel : channels) {
			List<Category> categories = categoryService
					.selectsByChannelId(channel.getId());
			map.put(channel.getId(), categories);
		}

		// 加载爬虫爬到的文件夹
		File dir = new File(
				"E:\\eclipse_Linux\\caoyuan-cms\\src\\main\\webapp\\resource\\xiaoshuo");
		// 获取文件的数组
		File[] listFiles = dir.listFiles();
		// 循环遍历
		for (File file : listFiles) {
			// 读取file数据，获取文章内容
			String content = StreamUtil.readTextFile(file);
			// 获取标题
			String name = file.getName().replace(".txt", "");
			// 封装数据
			ArticleWithBLOBs article = new ArticleWithBLOBs();

			article.setTitle(name);
			article.setContent(content);

			// (4)在文本内容中截取前140个字作为摘要。（2分）
			String summary = content;
			if (content.length() > 140) {
				summary = content.substring(0, 140);
			}
			article.setSummary(summary);

			// (5)“点击量”和“是否热门”、“频道”字段要使用随机值。（2分）
			// 点击量
			article.setHits(RandomUtil.random(0, 10000));
			// 是否热门
			article.setHot(RandomUtil.random(0, 1));
			// 栏目
			// 随机取出来一个
			Channel channel = channels.get(RandomUtil.random(0, channels.size() - 1));
			article.setChannelId(channel.getId());
			// 类别
			// 获取指定栏目下的类别
			List<Category> categories = map.get(channel.getId());
			// 判断集合是否null或者空元素
			if (!CollectionUtils.isEmpty(categories)) {
				Category category = categories
						.get(RandomUtil.random(0, categories.size() - 1));
				article.setCategoryId(category.getId());
			}
			// (6)文章发布日期从2019年1月1日模拟到今天。（2分）
			article.setCreated(
					DateUtil.randomDate(DateUtil.parse("2019-01-01", 0), new Date()));
			// 更新时间为本日
			article.setUpdated(new Date());
			// (7)其它的字段随便模拟。
			// 状态
			article.setStatus(1);
			// 删除
			article.setDeleted(0);
			// 类型
			article.setContentType(0);
			// 图片
			article.setPicture("c924227b-227e-4ca0-b4e0-518633e75706.jpg");
			System.out.println(article.getId() + "=======>" + article.getTitle());
			// 转换成json字符串
			String json = JSON.toJSONString(article);
			// 发送到kafka
			kafkaTemplate.sendDefault("article_add", json);
		}
		System.out.println("文章添加成功");
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
