package com.caoyuan.cms.dao;

import com.caoyuan.cms.domain.Settings;
/**
 * @ClassName:   SettingsMapper
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:00:05
 */
public interface SettingsMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Settings record);

    int insertSelective(Settings record);

    Settings selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Settings record);

    int updateByPrimaryKey(Settings record);
}