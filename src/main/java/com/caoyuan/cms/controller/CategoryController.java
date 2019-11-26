package com.caoyuan.cms.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.caoyuan.cms.domain.Category;
import com.caoyuan.cms.service.CategoryService;
import com.caoyuan.cms.utils.Result;
import com.caoyuan.cms.utils.ResultUtil;
/**
 * @ClassName:   CategoryController
 * @Description: 
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午6:58:46
 */
@RequestMapping("category")
@Controller
public class CategoryController {
	@Resource
	private CategoryService categoryService;
	
	/**
	 * 
	 * @Title: selects 
	 * @Description: 根据栏目查询分类
	 * @param channelId
	 * @return
	 * @return: List<Category>
	 */
	@ResponseBody
	@RequestMapping("selects")
	private Result<Category> selects(Integer channelId){
		return ResultUtil.success(categoryService.selectsByChannelId(channelId));
	}

}
