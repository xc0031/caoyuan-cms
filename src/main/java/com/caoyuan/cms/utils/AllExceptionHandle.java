package com.caoyuan.cms.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
/**
 * @ClassName: AllExceptionHandle 
 * @Description: 全局异常处理器
 * @author: 曹原
 * @date: 2019年11月20日 下午3:15:13
 */
@ControllerAdvice
public class AllExceptionHandle {
	
	/**
	 * 
	 * @Title: handleJson 
	 * @Description: 处理ajax请求的异常
	 * @param cmsJsonException
	 * @return
	 * @return: Result
	 */
	@ResponseBody
	@ExceptionHandler(AjaxException.class)
	public Result handleJson(AjaxException cmsJsonException) {
		return ResultUtil.error(cmsJsonException.getCode(), cmsJsonException.getMessage());
		
	}
	/**
	 * 
	 * @Title: handle 
	 * @Description: 处理普通请求的异常
	 * @param FormException
	 * @param request
	 * @return
	 * @return: ModelAndView
	 */
	@ExceptionHandler(FormException.class)
	public ModelAndView handle(FormException exception,HttpServletRequest request) {
		ModelAndView mv  = new ModelAndView();
	  //则获取错误消息,进行封装
		mv.addObject("message",exception.getMessage());
		//获取当前请求的url
		 String url = request.getRequestURI();
		// System.out.println(url+"=================");
		mv.setViewName(url);//
		return mv;
		
	}
	/**
	 * 
	 * @Title: handle 
	 * @Description: 处理系统异常
	 * @param exception
	 * @return
	 * @return: ModelAndView
	 */
	@ExceptionHandler(Exception.class)
	public String handle(Exception exception) {
		
		return "common/error";
		
	}
	
	

}
