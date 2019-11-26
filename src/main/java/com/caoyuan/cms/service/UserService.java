package com.caoyuan.cms.service;

import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.vo.UserVO;
import com.github.pagehelper.PageInfo;
/**
 * @ClassName:   UserService
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:57
 */
public interface UserService {

	/**
	 * @Title: selects 
	 * @Description: 用户列表
	 * @param user
	 * @return
	 * @return: List<User>
	 */
	 PageInfo<User> selects(User user,Integer page,Integer pageSize);

	  boolean update(User user);
	  //注册
	  boolean  insertSelective(UserVO userVO);
    //登录
	  User login(User user);
	  
		
}
