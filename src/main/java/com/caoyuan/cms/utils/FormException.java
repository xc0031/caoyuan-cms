package com.caoyuan.cms.utils;
/**
 * @ClassName: FormException 
 * @Description: 自定义异常类
 * @author: 曹原
 * @date: 2019年11月20日 下午3:15:26
 */
public class FormException extends RuntimeException {

	/**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 * @Description: 
	 */
	private static final long serialVersionUID = 1L;
	private String message;
	
	public FormException() {
		
	}
	public FormException(String message) {
		super(message);
		this.message =message;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
