/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:41:30   
 * @version 1.0
 */
public abstract class DialectAdapter implements Dialect {
    public String getLimitString(String query, int offset, int limit) {
        return getLimitStringInternal(preProcessQuery(query), offset, limit);
    }

    public String limitStringByPage(String query, int pageSize, int pageNum) {
        throw new UnsupportedOperationException("not implement yet.");
    }

    @Override
    public String getCountSql(String query) {
        return " select count(1) from (" + query + ") derived_ ";
    }

    private String preProcessQuery(String query) {
        return query;
    }

    /**
     * pre-processing Mybatis query sql. e.g remove ';'
     * 
     * @param query
     * @param offset
     * @param limit
     * @return
     */
    protected abstract String getLimitStringInternal(String query, int offset, int limit);
}
