package com.caoyuan.cms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.ArticleMapper;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.ArticleWithBLOBs;
import com.caoyuan.cms.service.ArticleService;
import com.caoyuan.cms.utils.AjaxException;
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

	@Override
	public PageInfo<Article> selects(Article article, Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<Article> articles = articleMapper.selects(article);
		
		return new PageInfo<Article>(articles);
	}

	@Override
	public boolean update(ArticleWithBLOBs article) {
		try {
			return articleMapper.updateByPrimaryKeySelective(article)>0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AjaxException(1,"修改失败");
		}
	}

	@Override
	public ArticleWithBLOBs selectByPrimaryKey(Integer id) {
		return articleMapper.selectByPrimaryKey(id);
	}

	@Override
	public boolean insertSelective(ArticleWithBLOBs record) {
		try {
			return articleMapper.insertSelective(record)>0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AjaxException(1,"发布失败");
		}
	}

}
