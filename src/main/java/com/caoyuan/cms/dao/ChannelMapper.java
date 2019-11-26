package com.caoyuan.cms.dao;

import java.util.List;

import com.caoyuan.cms.domain.Channel;
/**
 * @ClassName:   ChannelMapper
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午6:59:44
 */
public interface ChannelMapper {
	/**
	 * 所有栏目
	 * @Title: selects 
	 * @Description: TODO
	 * @return
	 * @return: List<Channel>
	 */
	List<Channel> selects();
	
    int deleteByPrimaryKey(Integer id);

    int insert(Channel record);

    int insertSelective(Channel record);

    Channel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Channel record);

    int updateByPrimaryKey(Channel record);
}