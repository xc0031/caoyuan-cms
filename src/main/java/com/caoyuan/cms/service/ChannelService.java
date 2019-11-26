package com.caoyuan.cms.service;

import java.util.List;

import com.caoyuan.cms.domain.Channel;
/**
 * @ClassName:   ChannelService
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:34
 */
public interface ChannelService {
	/**
	 * 所有栏目
	 * @Title: selects 
	 * @return
	 * @return: List<Channel>
	 */
	List<Channel> selects();
	
}
