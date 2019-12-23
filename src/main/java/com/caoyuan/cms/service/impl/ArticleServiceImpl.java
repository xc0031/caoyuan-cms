package com.caoyuan.cms.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.ArticleMapper;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.service.ArticleService;
import com.caoyuan.cms.utils.AjaxException;
import com.caoyuan.cms.utils.ESUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @ClassName:   ArticleServiceImpl
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:01:41
 */
@Service
public class ArticleServiceImpl implements ArticleService {
	@Resource
	private ArticleMapper articleMapper;

	@Resource
	private RedisTemplate<String, Article> redisTemplate;

	@Resource
	private ElasticsearchTemplate elasticsearchTemplate;

	/**
	 * 	Es高亮搜索,显示在热门那里
	 */
	@Override
	public PageInfo<Article> selectEs(String key, Integer page, Integer pageSize) {
		Class[] classes = { User.class, Category.class, Channel.class };
		AggregatedPage<Article> selectObjects = ESUtils.selectObjects(
				elasticsearchTemplate, Article.class, Arrays.asList(classes), page,
				pageSize, "id", new String[] { "title" }, key);
		List<Article> content = selectObjects.getContent();
		long totalElements = selectObjects.getTotalElements();
		Page<Article> page2 = new Page<Article>(page, pageSize);
		page2.addAll(content);
		page2.setTotal(totalElements);
		return new PageInfo<Article>(page2, 5);
	}

	/**
	 * 	管理员与用户界面的查询的文章/图片集列表
	 * 	还有首页非热门文章也是不存redis中的(目前无法解决)
	 */
	@Override
	public PageInfo<Article> selects(Article article, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<Article> articles = articleMapper.selects(article);
		return new PageInfo<Article>(articles);
	}

	/**
	 * 	图片集,存5个
	 */
	@Override
	public PageInfo<Article> selectPic(Article article, Integer page, Integer pageSize) {
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		List<Article> articles = null;
		if (redisTemplate.hasKey("pic_article")) {
			// 如果有对应的键，则直接从redis中获取数据
			// 获取数据
			articles = opsForList.range("pic_article", 0, -1);
		} else {
			// 如果没有对应的键
			// 从mysql中获取数据
			PageHelper.startPage(page, pageSize);
			articles = articleMapper.selects(article);
			// 获取完数据以后，存入redis中
			opsForList.rightPushAll("pic_article", articles);
		}
		return new PageInfo<Article>(articles);
	}

	/**
	 * 最新文章
	 */
	@Override
	public PageInfo<Article> selectLast(Article article, Integer page, Integer pageSize) {
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		List<Article> articles = null;
		if (redisTemplate.hasKey("last_article")) {
			// 如果有对应的键，则直接从redis中获取数据
			// 获取数据
			articles = opsForList.range("last_article", 0, -1);
		} else {
			// 如果没有对应的键
			// 从mysql中获取数据
			PageHelper.startPage(page, pageSize);
			articles = articleMapper.selects(article);
			// 获取完数据以后，存入redis中
			opsForList.rightPushAll("last_article", articles);
		}

		return new PageInfo<Article>(articles);
	}

	/**
	 * 热门文章
	 * @param article
	 * @param page
	 * @param pageSize
	 * @return
	 */
	@Override
	public PageInfo<Article> selectHot(Article article, Integer page, Integer pageSize) {
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		// redis中没的话,先存redis
		if (!redisTemplate.hasKey("hot_article")) {
			// 如果没有对应的键
			// 从mysql中获取所有热门文章的数据
			List<Article> all_articles = articleMapper.selects(article);
			// 获取全部数据以后，存入redis中
			opsForList.rightPushAll("hot_article", all_articles);
		}
		// 如果有对应的键，则直接从redis中获取数据
		// 获取数据
		List<Article> articles = opsForList.range("hot_article", (page - 1) * pageSize,
				page * pageSize - 1);
		// 获取数据条数
		Long size = opsForList.size("hot_article");
		// 使用pagehelper插件提供的page分页类,传入pageNum和pageSize
		Page<Article> pages = new Page<Article>(page, pageSize);
		// page继承了ArrayList,传入数据
		pages.addAll(articles);
		// 传入总条数
		pages.setTotal(size);
		// 放入pageInfo设置数据,为了使用页码导航,第二个参数是页码个数
		PageInfo<Article> pageInfo = new PageInfo<Article>(pages, 5);

		return pageInfo;
	}

	/**
	 * 	管理员审核/删除/热门
	 */
	@Override
	public boolean update(ArticleWithBLOBs article) {
		try {
			int result = articleMapper.updateByPrimaryKeySelective(article);
			// 审核通过文章
			if (result > 0) {
				// 判断当前要审核/删除/是否热门文章
				// 审核文章通过以后，要清空redis中对应的数据
				// 清空redis
				if (article.getHot() != null) {
					// 热门操作
					redisTemplate.delete("hot_article");
				} else {
					// 非热门操作,只能全部清除
					redisTemplate.delete("last_article");
					redisTemplate.delete("pic_article");
					redisTemplate.delete("hot_article");
					// 删除和审核不通过的,都从es里删除
					if ((article.getDeleted() != null && article.getDeleted() == 1)
							|| (article.getStatus() != null
									&& article.getStatus() == -1)) {
						elasticsearchTemplate.delete(Article.class, article.getId() + "");
					} else {
						// 恢复正常的和审核通过的,都添加进es中
						IndexQuery query = new IndexQuery();
						// 根据子类中id的值，获取到Article对象的数据,存入完整数据
						Article article2 = articleMapper
								.selectByPrimaryKey(article.getId());
						query.setObject(article2);
						elasticsearchTemplate.index(query);
					}
				}
			}
			return result > 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AjaxException(1, "修改失败");
		}
	}

	@Override
	public ArticleWithBLOBs selectByPrimaryKey(Integer id) {
		return articleMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean insertSelective(ArticleWithBLOBs record) {
		try {
			return articleMapper.insertSelective(record) > 0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AjaxException(1, "发布失败");
		}
	}

	@Override
	public void updateHits(Integer id) {
		articleMapper.updateHits(id);
	}

}
