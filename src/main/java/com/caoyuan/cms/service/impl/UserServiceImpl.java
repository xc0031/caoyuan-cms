package com.caoyuan.cms.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.UserMapper;
import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.service.UserService;
import com.caoyuan.cms.utils.FormException;
import com.caoyuan.cms.utils.AjaxException;
import com.caoyuan.cms.utils.Md5Util;
import com.caoyuan.cms.vo.UserVO;
import com.cy.util.StringUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
/**
 * @ClassName:   UserServiceImpl
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:02:16
 */
@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserMapper userMapper ;

	@Override
	public PageInfo<User> selects(User user,Integer page,Integer pageSize) {
		
		PageHelper.startPage(page, pageSize);
		List<User> users = userMapper.selects(user);
		return new PageInfo<User>(users);
	}

	@Override
	public boolean update(User user) {
		try {
			 return userMapper.updateByPrimaryKeySelective(user)>0;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AjaxException(1,"操作失败");
		}
	}

	@Override
	public boolean insertSelective(UserVO userVO) {
		
			//判断注册信息是否满足要求
			if(!StringUtil.hasText(userVO.getUsername()))
			 throw new FormException("用户名不能为空");	
			if(!StringUtil.hasText(userVO.getPassword()))
			 throw new FormException("密码不能为空");	
			if(!StringUtil.hasText(userVO.getRepassword()))
				 throw new FormException("确认密码不能为空");
			if(!userVO.getPassword().equals(userVO.getRepassword()))
				 throw new FormException("两次密码不一致");
			//用户名不能重复注册
			User u = userMapper.selectByName(userVO.getUsername());
			if(null!=u)
				 throw new FormException("用户名不能重复注册");
			
			//执行注册
			//对密码进行加密保存
			userVO.setPassword(Md5Util.md5Encoding(userVO.getPassword()));
			
			//用户注册的其他属性默认值
			userVO.setCreated(new Date());//注册时间
			userVO.setNickname(userVO.getUsername());//昵称
			userVO.setLocked(0);
			userVO.setRole("0");
			return userMapper.insertSelective(userVO)>0;
	}

	@Override
	public User login(User user) {
		
		//判断登录注册信息是否满足要求
		if(!StringUtil.hasText(user.getUsername()))
		 throw new FormException("用户名不能为空");	
		if(!StringUtil.hasText(user.getPassword()))
		 throw new FormException("密码不能为空");	
		//查询用户名是否存在
		User u = userMapper.selectByName(user.getUsername());
		if(null==u)
			 throw new FormException("用户名不存在");
		else if(u.getLocked()==1) {
			throw new FormException("账户被禁用!");
		}
		else if(!Md5Util.md5Encoding(user.getPassword()).equals(u.getPassword())) {
			throw new FormException("密码错误!");	
		}
		return u;
	}

}
