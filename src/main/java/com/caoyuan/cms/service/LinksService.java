package com.caoyuan.cms.service;

import com.caoyuan.cms.domain.Links;
import com.github.pagehelper.PageInfo;
/**
 * @ClassName:   LinksService
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:48
 */
public interface LinksService {

	/**
	 * @Title: insert 
	 * @Description: 增加
	 * @param links
	 * @return
	 * @return: int
	 */
	boolean insert(Links links);
	/**
	 * 
	 * @Title: selects 
	 * @Description: 列表
	 * @return
	 * @return: List<Links>
	 */
   PageInfo<Links> selects(Integer page,Integer pageSize);
}
