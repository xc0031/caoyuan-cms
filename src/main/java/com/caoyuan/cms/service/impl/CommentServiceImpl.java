package com.caoyuan.cms.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.caoyuan.cms.dao.CommentMapper;
import com.caoyuan.cms.domain.Comment;
import com.caoyuan.cms.domain.User;
import com.caoyuan.cms.service.CommentService;
import com.caoyuan.cms.utils.AjaxException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

/** 
* @author 作者:majingji
* @version 创建时间：2019年11月24日 下午4:01:14 
* 类功能说明 
*/
@Service
public class CommentServiceImpl implements CommentService {
	@Resource
	private CommentMapper commentMapper;

	@Override
	public void insert(Comment comment) {
		try {
			commentMapper.insert(comment);
		} catch (Exception e) {
			throw new AjaxException(1, "评论失败");
		}

	}

	@Override
	public PageInfo<Comment> selects(Integer id, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Comment> list = commentMapper.selects(id);
		PageInfo<Comment> page = new PageInfo<Comment>(list);
		return page;
	}

	@Override
	public PageInfo<Comment> selectbyId(User user, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Comment> list = commentMapper.selectbyId(user);
		PageInfo<Comment> page = new PageInfo<Comment>(list);
		return page;
	}

	@Override
	public void deleteComment(Integer id) {
		try {
			commentMapper.deleteComment(id);
		} catch (Exception e) {
			throw new AjaxException(1, "删除失败");
		}

	}
}
