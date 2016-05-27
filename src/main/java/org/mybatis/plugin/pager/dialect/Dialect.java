/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午3:49:10   
 * @version 1.0
 */
public interface Dialect {

    /**
     * Given a limit and an offset, apply the limit clause to the query.
     *
     * @param query The query to which to apply the limit.
     * @param offset The offset of the limit
     * @param limit The limit of the limit ;)
     * @return The modified query statement with the limit applied.
     */
    String getLimitString(String query, int offset, int limit);

    /**
     * Given a limit and an offset, apply the limit clause to the query.
     *
     * @param query The query to which to apply the limit.
     * @param pageSize The limit of the limit.
     * @param pageNum Current page number.
     * @return The modified query statement with the limit applied.
     */
    String limitStringByPage(String query, int pageSize, int pageNum);

    /**
     * Total count query.
     * 
     * @param query
     * @return
     */
    String getCountSql(String query);
}
