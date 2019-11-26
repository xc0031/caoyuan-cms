package com.caoyuan.cms.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.LinksMapper;
import com.caoyuan.cms.domain.Links;
import com.caoyuan.cms.service.LinksService;
import com.caoyuan.cms.utils.AjaxException;
import com.cy.util.StringUtil;
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

	@Override
	public boolean insert(Links links) {
		// 调用工具类判断是否是有效URL
		if (!StringUtil.isHttpUrl(links.getUrl()))
			throw new AjaxException(1, "不是有效的url");
		links.setCreated(new Date());
		linksMapper.insert(links);

		return true;

	}

	@Override
	public PageInfo<Links> selects(Integer page, Integer pageSize) {
		PageHelper.startPage(page, pageSize);
		List<Links> list = linksMapper.selects();
		return new PageInfo<Links>(list);
	}

}
