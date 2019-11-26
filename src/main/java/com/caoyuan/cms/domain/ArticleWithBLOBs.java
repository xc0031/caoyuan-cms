package com.caoyuan.cms.domain;

import java.io.Serializable;
/**
 * @ClassName:   ArticleWithBLOBs
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:00:39
 */
public class ArticleWithBLOBs extends Article implements Serializable {
    /**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 */
	private static final long serialVersionUID = 1L;

	private String content;

    private String summary;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }
}