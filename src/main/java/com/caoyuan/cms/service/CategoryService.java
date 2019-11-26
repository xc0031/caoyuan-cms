package com.caoyuan.cms.service;

import java.util.List;

import com.caoyuan.cms.domain.Category;
/**
 * @ClassName:   CategoryService
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:29
 */
public interface CategoryService {

	/**
	 * 
	 * @Title: selectsByChannelId 
	 * @Description: 根据栏目查询分类
	 * @param channelId
	 * @return
	 * @return: List<Category>
	 */
	List<Category> selectsByChannelId(Integer channelId);
}
