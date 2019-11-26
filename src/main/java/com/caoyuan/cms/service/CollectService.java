package com.caoyuan.cms.service;

import org.apache.ibatis.annotations.Param;

import com.caoyuan.cms.domain.Collect;
import com.caoyuan.cms.domain.User;
import com.github.pagehelper.PageInfo;
/**
 * @ClassName:   CollectService
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:40
 */
public interface CollectService {

	/**
	 * 
	 * @Title: insert 
	 * @Description: 增加
	 * @param links
	 * @return
	 * @return: int
	 */
	boolean insert(Collect Collect);
	/**
	 * 
	 * @Title: selects 
	 * @Description: 列表
	 * @return
	 * @return: List<Links>
	 */
   PageInfo<Collect> selects(Integer page,Integer pageSize,User user);
   
   
   /**
    * 
    * @Title: selectByText 
    * @Description: 根据登录人和文章标题查询是否收藏
    * @param text
    * @return
    * @return: int
    */
   int selectByText(@Param("text")String text ,@Param("user")User user);
    /**
     * 
     * @Title: deleteById 
     * @Description: 删除收藏
     * @param id
     * @return
     * @return: int
     */
    boolean deleteById(Integer id);
}
