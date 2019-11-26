package com.caoyuan.cms.vo;

import com.caoyuan.cms.domain.User;
/**
 * @ClassName:   UserVO
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:03:48
 */
public class UserVO extends User {
	

	private String repassword;//确认密码

	public String getRepassword() {
		return repassword;
	}

	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}
	
}
