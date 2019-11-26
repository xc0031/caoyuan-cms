package com.caoyuan.cms.dao;

import com.caoyuan.cms.domain.Slide;
/**
 * @ClassName:   SlideMapper
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:00:10
 */
public interface SlideMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Slide record);

    int insertSelective(Slide record);

    Slide selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Slide record);

    int updateByPrimaryKey(Slide record);
}