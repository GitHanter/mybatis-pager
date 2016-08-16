/*
 * Copyright 2016-2019 All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.model;

import java.io.Serializable;
import java.util.List;

/**
 * @Description:
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:53:48
 * @version 1.0
 */
public class Page<T> implements Serializable {
    /**
	 * 
	 */
    private static final long serialVersionUID = 1L;

    /** 默认页码 */
    public static final int DEFAULT_PAGE_NUMBER = 1;

    /** 默认每页记录数 */
    public static final int DEFAULT_PAGE_SIZE = 15;

    /** 默认每页最多显示条数 */
    public static final int DEFAULT_MAX_RECORD_PER_PAGE = 200;

    private static final int DEFAULT_PAGING_SIZE = 9;// 滑动窗口大小

    private int recordPerPage = DEFAULT_MAX_RECORD_PER_PAGE;

    /** 页码 */
    private int pageNumber = DEFAULT_PAGE_NUMBER;

    /** 每页记录数 */
    private int pageSize = DEFAULT_PAGE_SIZE;
    
    private int pagingSize = DEFAULT_PAGING_SIZE;

    /** 总页数 */
    private int totalPages = 0;

    /** 总记录数 */
    private int totalRows = 0;

    private int startPage = 0;// 滑动窗口起始页码

    private int endPage = 0;// 滑动窗口终止页码

    private boolean hasNextPage = false; // 是否有下一页

    private boolean hasPreviousPage = false; // 是否有前一页

    private boolean pageManually = false;// 是否手工分页[即是否不使用插件，直接手动在Mybatis-Mapper里拼装分页SQL。默认为false，使用插件分页]

    private List<T> content;

    public Page() {

    }

    public Page(int pageNum, int pageSize) {
        this(pageNum, pageSize, false);
    }

    public Page(int pageNum, int pageSize, boolean manually) {
        setPageNumber(pageNum);
        setPageSize(pageSize);
        this.pageManually = manually;
    }

    public void afterPropertiesSet() {
        if ((totalRows % pageSize) == 0) {
            totalPages = totalRows / pageSize;
        } else {
            totalPages = totalRows / pageSize + 1;
        }

        if ((pageNumber - 1) > 0) {
            hasPreviousPage = true;
        } else {
            hasPreviousPage = false;
        }

        if (pageNumber >= totalPages) {
            hasNextPage = false;
        } else {
            hasNextPage = true;
        }

        // 滑动窗口计算
        if (totalPages <= 10 || pageNumber <= 3) {
            startPage = 1;
            endPage = 10 > totalPages ? totalPages : 10;
        } else {
            if (totalPages - pageNumber <= 7) {
                startPage = totalPages - 9;
                endPage = totalPages;
            } else {
                startPage = pageNumber - 2;
                endPage = pageNumber + 7;
            }
        }

    }

    public int getPagingSize() {
        return pagingSize;
    }

    public void setPagingSize(int pagingSize) {
        this.pagingSize = pagingSize;
    }

    public int getStartPage() {
        return startPage;
    }

    public void setStartPage(int startPage) {
        this.startPage = startPage;
    }

    public int getEndPage() {
        return endPage;
    }

    public void setEndPage(int endPage) {
        this.endPage = endPage;
    }

    public int getRecordPerPage() {
        return recordPerPage;
    }

    public void setRecordPerPage(int recordPerPage) {
        this.recordPerPage = recordPerPage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        if (pageNumber <= 0) {
            throw new IllegalArgumentException("'pageNumber' must be positive integer!");
        }
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        if (pageSize <= 0) {
            throw new IllegalArgumentException("'pageSize' must be positive integer!");
        }
        this.pageSize = pageSize;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        this.totalRows = totalRows;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isPageManually() {
        return pageManually;
    }

    public void setPageManually(boolean pageManually) {
        this.pageManually = pageManually;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }
}
