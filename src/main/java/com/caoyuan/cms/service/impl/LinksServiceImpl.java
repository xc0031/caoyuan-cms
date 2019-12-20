package com.caoyuan.cms.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.LinksMapper;
import com.caoyuan.cms.domain.Article;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.domain.Links;
import com.caoyuan.cms.service.LinksService;
import com.caoyuan.cms.utils.AjaxException;
import com.cy.util.StringUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/**
 * @ClassName:   LinksServiceImpl
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:09
 */
@Service
public class LinksServiceImpl implements LinksService {

	@Resource
	private LinksMapper linksMapper;
	@Resource
	private RedisTemplate<String, Links> redisTemplate;

	@Override
	public boolean insert(Links links) {
		// 调用工具类判断是否是有效URL
		if (!StringUtil.isHttpUrl(links.getUrl()))
			throw new AjaxException(1, "不是有效的url");
		links.setCreated(new Date());
		int insert = linksMapper.insert(links);
		if (insert>0) {
			redisTemplate.delete("cms_links");
		}
		return true;

	}

	@Override
	public PageInfo<Links> selects(Integer page, Integer pageSize) {
		ListOperations<String, Links> opsForList = redisTemplate.opsForList();
		// redis中没的话,先存redis
		if (!redisTemplate.hasKey("cms_links")) {
			List<Links> links = linksMapper.selects();
			opsForList.rightPushAll("cms_links", links);
		}
		// 如果有对应的键，则直接从redis中获取数据
		// 获取数据
		List<Links> links = opsForList.range("cms_links", (page - 1) * pageSize,
				page * pageSize - 1);
		// 获取数据条数
		Long size = opsForList.size("cms_links");
		// 使用pagehelper插件提供的page分页类,传入pageNum和pageSize
		Page<Links> pages = new Page<Links>(page, pageSize);
		// page继承了ArrayList,传入数据
		pages.addAll(links);
		// 传入总条数
		pages.setTotal(size);
		// 放入pageInfo设置数据,为了使用页码导航,第二个参数是页码个数
		return new PageInfo<Links>(pages, 5);
	}

}
