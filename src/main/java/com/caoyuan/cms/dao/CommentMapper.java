package com.caoyuan.cms.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.caoyuan.cms.domain.Comment;
import com.caoyuan.cms.domain.User;


/** 
* @author 作者:majingji
* @version 创建时间：2019年11月24日 下午4:01:52 
* 类功能说明 
*/
public interface CommentMapper {

	void insert(Comment comment);

	List<Comment> selects(Integer id);

	List<Comment> selectbyId(User user);

	void deleteComment(@Param("id")Integer id);

}
