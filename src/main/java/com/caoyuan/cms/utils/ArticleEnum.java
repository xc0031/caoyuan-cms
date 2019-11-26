package com.caoyuan.cms.utils;

/**
 * @ClassName: ArticleEnum 
 * @Description: 文章的枚举类
 * @author: 曹原
 * @date: 2019年10月24日 下午3:13:00
 */
public enum ArticleEnum  {
	
	
	HTML(0,"html"),IMAGE(1,"image");
	
	ArticleEnum(Integer code,String name) {
		this.code =code;
		this.name =name;
	}
	
	
	private Integer code;
	private String name;
	public Integer getCode() {
		return code;
	}
	public String getName() {
		return name;
	}
	

	public static void main(String[] args) {
		System.out.println(HTML.getCode());
		System.out.println(HTML.getName());
		//IMAGE.getCode();
	}
}
