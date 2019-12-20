package com.caoyuan.cms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.ChannelMapper;
import com.caoyuan.cms.domain.Channel;
import com.caoyuan.cms.service.ChannelService;

/**
 * @ClassName:   ChannelServiceImpl
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:01:56
 */
@Service
public class ChannelServiceImpl implements ChannelService {
	@Resource
	private ChannelMapper channelMapper;
	@Resource
	private RedisTemplate<String, Channel> redisTemplate;

	@Override
	public List<Channel> selects() {
		ListOperations<String, Channel> opsForList = redisTemplate.opsForList();
		List<Channel> channels = null;
		if (redisTemplate.hasKey("cms_channels")) {
			// 如果有对应的键，则直接从redis中获取数据
			// 获取数据
			channels = opsForList.range("cms_channels", 0, -1);
		} else {
			channels = channelMapper.selects();
			opsForList.rightPushAll("cms_channels", channels);
		}
		return channels;
	}
}
