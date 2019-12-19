package com.caoyuan.cms.service;

import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.github.pagehelper.PageInfo;
/**
 * @ClassName:   ArticleService
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:23
 */
public interface ArticleService {

	/**
	 * @Title: selects 
	 * @Description: 文章列表
	 * @param article
	 * @return
	 * @return: List<Article>
	 */
	PageInfo<Article> selects(Article article,Integer page,Integer pageSize);

	boolean  update(ArticleWithBLOBs article);
	
	ArticleWithBLOBs selectByPrimaryKey(Integer id);
	
	/**
	 * @Title: insertSelective 
	 * @Description: 发布文章
	 * @param record
	 * @return
	 * @return: boolean
	 */
	 boolean insertSelective(ArticleWithBLOBs record);

	/** 
	 * 	最新文章
	 * @Title: selectLast 
	 * @param article
	 * @param page
	 * @param pageSize
	 * @return
	 * @return: PageInfo<Article>
	 */
	PageInfo<Article> selectLast(Article article, Integer page, Integer pageSize);

	/** 
	 * 	热门文章,重点在替换redis后,设置页码
	 * @Title: selectHot 
	 * @param article
	 * @param page
	 * @param pageSize
	 * @return
	 * @return: PageInfo<Article>
	 */
	PageInfo<Article> selectHot(Article article, Integer page, Integer pageSize);
}
