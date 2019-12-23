package com.caoyuan.cms.es;

import java.util.List;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.caoyuan.cms.dao.ArticleMapper;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.service.ArticleService;

/*********************************************************
@ClassName:   Es
@author:	     曹原
@date: 		  2019年12月23日 下午7:37:34 
*********************************************************/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring-beans.xml")
public class Es {

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	@Resource
	private ArticleMapper articleMapper;

	@Test
	public void testToEs() {
		Article article = new Article();
		article.setDeleted(0);
		article.setStatus(1);
		article.setContentType(0);
		List<Article> articles = articleMapper.selects(article);
		for (Article article2 : articles) {
			IndexQuery query = new IndexQuery();
			query.setObject(article2);
			// 使用ElasticSearch将文章表所有文章建立全文索引。
			elasticsearchTemplate.index(query);
		}
		System.out.println("es数据添加完毕");
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
