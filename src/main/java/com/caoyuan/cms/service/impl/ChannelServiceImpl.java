package com.caoyuan.cms.service.impl;

import java.util.List;

import javax.annotation.Resource;

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

	@Override
	public List<Channel> selects() {
		return channelMapper.selects();
	}

}
