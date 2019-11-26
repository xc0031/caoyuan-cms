package com.caoyuan.cms.domain;

import java.io.Serializable;
import java.util.Date;

/** 
* @author 作者:majingji
* @version 创建时间：2019年11月24日 下午3:33:59 
* 类功能说明 
*/
public class Comment implements Serializable{
	private Integer id;
	private User user;
	private ArticleWithBLOBs article;
	private String content;
	private Date created;
	public Comment() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public ArticleWithBLOBs getArticle() {
		return article;
	}

	public void setArticle(ArticleWithBLOBs article) {
		this.article = article;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Comment other = (Comment) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	

}
