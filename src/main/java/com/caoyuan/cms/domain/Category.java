package com.caoyuan.cms.domain;

import java.io.Serializable;
/**
 * @ClassName:   Category
 * @author:	            曹原
 * @date: 		 2019年11月24日 下午7:00:48
 */
public class Category implements Serializable{
    /**
	 * @fieldName: serialVersionUID
	 * @fieldType: long
	 */
	private static final long serialVersionUID = 1L;

	private Integer id;

    private String name;

    private Integer channelId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
}