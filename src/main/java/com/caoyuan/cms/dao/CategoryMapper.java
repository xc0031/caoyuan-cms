package com.caoyuan.cms.dao;

import java.util.List;

import com.caoyuan.cms.domain.Category;
/**
 * @ClassName:   CategoryMapper
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午6:59:36
 */
public interface CategoryMapper {
	/**
	 * @Title: selectsByChannelId 
	 * @Description: 根据栏目查询分类
	 * @param channelId
	 * @return
	 * @return: List<Category>
	 */
	List<Category> selectsByChannelId(Integer channelId);
	
    int deleteByPrimaryKey(Integer id);

    int insert(Category record);

    int insertSelective(Category record);

    Category selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Category record);

    int updateByPrimaryKey(Category record);
}