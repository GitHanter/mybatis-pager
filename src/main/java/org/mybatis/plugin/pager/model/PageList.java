/**
 * 
 */
package org.mybatis.plugin.pager.model;

import java.util.ArrayList;
import java.util.Collection;


/**
 * @author hanyj
 *
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
