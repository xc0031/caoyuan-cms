package com.caoyuan.cms.dao;

import java.util.List;

import com.caoyuan.cms.domain.Links;
/**
 * @ClassName:   LinksMapper
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午6:59:59
 */
public interface LinksMapper {
	/**
	 * @Title: insert 
	 * @Description: 增加
	 * @param links
	 * @return
	 * @return: int
	 */
	int insert(Links links);
	/**
	 * 
	 * @Title: selects 
	 * @Description: 列表
	 * @return
	 * @return: List<Links>
	 */
    List<Links> selects();
}
