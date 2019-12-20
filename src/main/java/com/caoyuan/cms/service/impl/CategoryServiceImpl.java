package com.caoyuan.cms.service.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.CategoryMapper;
import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.domain.Channel;
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
	@Resource
	private RedisTemplate<String, ?> redisTemplate;

	@Override
	public List<Category> selectsByChannelId(Integer channelId) {
		HashOperations<String, String, List<Category>> opsForHash = redisTemplate
				.opsForHash();
		List<Category> categories = null;
		if (opsForHash.hasKey("cms_categories", channelId + "")) {
			// 如果有对应的键，则直接从redis中获取数据
			// 获取数据
			categories = opsForHash.get("cms_categories", channelId + "");
		} else {
			categories = categoryMapper.selectsByChannelId(channelId);
			opsForHash.put("cms_categories", channelId + "", categories);
		}
		return categories;
	}

}
