/*
 * Copyright 2016-2019 All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.model;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @Description:
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:54:43
 * @version 1.0
 */
public class PageList<E> extends ArrayList<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6405007212826272155L;
	
	private Page<E> page;
	
	public PageList() {
		page = new Page<E>();
		page.setContent(this);
	}
	
	public PageList(Collection<? extends E> c,Page<E> page) {
        super(c);
        this.page = page;
        page.setContent(this);
    }

	public Page<E> getPage() {
		return page;
	}

	public void setPage(Page<E> page) {
		this.page = page;
	}
	
}
