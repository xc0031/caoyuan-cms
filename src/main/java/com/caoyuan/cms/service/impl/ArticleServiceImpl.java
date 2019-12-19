package com.caoyuan.cms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.ArticleMapper;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.service.ArticleService;
import com.caoyuan.cms.utils.AjaxException;
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

	@Override
	public PageInfo<Article> selects(Article article, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<Article> articles = articleMapper.selects(article);
		return new PageInfo<Article>(articles);
	}

	/**
	 * 最新文章
	 */
	@Override
	public PageInfo<Article> selectLast(Article article, Integer page, Integer pageSize) {
		// 第一次访问的时候，redis中没有数据，从mysql数据库中获取数据
		// 怎么判断是第一次访问？
		// 直接查看redis中有没有对应的数据，如果没有，则为第一次访问
		// 之后再次访问时，直接从redis中获取数据

		// 获取List类型的操作对象
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
		// 第一次访问的时候，redis中没有数据，从mysql数据库中获取数据
		// 怎么判断是第一次访问？
		// 直接查看redis中有没有对应的数据，如果没有，则为第一次访问
		// 之后再次访问时，直接从redis中获取数据

		// 获取List类型的操作对象
		ListOperations<String, Article> opsForList = redisTemplate.opsForList();
		PageInfo<Article> pageInfo = null;
		if (redisTemplate.hasKey("hot_article")) {
			// 如果有对应的键，则直接从redis中获取数据
			// 获取数据
			List<Article> articles = opsForList.range("hot_article",
					(page - 1) * pageSize, page * pageSize - 1);
			// 获取数据条数
			Long size = opsForList.size("hot_article");
			// 使用pagehelper插件提供的page分页类,传入pageNum和pageSize
			Page<Article> pages = new Page<Article>(page, pageSize);
			// page继承了ArrayList,传入数据
			pages.addAll(articles);
			// 传入总条数
			pages.setTotal(size);
			// 放入pageInfo设置数据,为了使用页码导航,第二个参数是页码个数
			pageInfo = new PageInfo<Article>(pages, 5);
		} else {
			// 如果没有对应的键
			// 从mysql中获取所有热门文章的数据
			List<Article> all_articles = articleMapper.selects(article);
			// 获取全部数据以后，存入redis中
			opsForList.rightPushAll("hot_article", all_articles);
			// 获取分页数据，显示出来
			PageHelper.startPage(page, pageSize);
			// 设置数据
			pageInfo = new PageInfo<Article>(articleMapper.selects(article),5);
		}
		return pageInfo;
	}

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
					//只删除热门文章的redis数据
					redisTemplate.delete("hot_article");
				}else {
					//只删除最新文章的redis数据
					redisTemplate.delete("last_article");
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

}
