package com.caoyuan.cms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.CategoryMapper;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.service.CategoryService;
/**
 * @ClassName:   CategoryServiceImpl
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:01:50
 */
@Service
public class CategoryServiceImpl implements CategoryService {
	@Resource
	private CategoryMapper categoryMapper;

	@Override
	public List<Category> selectsByChannelId(Integer channelId) {
		return categoryMapper.selectsByChannelId(channelId);
	}

}
