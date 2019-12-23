package com.caoyuan.cms.es;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.GetQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.caoyuan.cms.dao.ArticleMapper;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.utils.ArticleEnum;
import com.caoyuan.cms.utils.ESUtils;


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

	@Resource
	private ArticleMapper articleMapper;

	/**
	 * 从mysql中获取数据，存入es中
	 */
	@Test
	public void testToES() {
		// 查询所有article数据
		Article article = new Article();
		article.setStatus(1);// 审核过的
		article.setDeleted(0);// 未删除
		article.setContentType(ArticleEnum.HTML.getCode());
		List<Article> list = articleMapper.selects(article);

		// 将数据存入es中
		for (Article article2 : list) {

			IndexQuery query = new IndexQuery();

			query.setObject(article2);

			elasticsearchTemplate.index(query);
		}

		System.out.println("存储完毕");
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
