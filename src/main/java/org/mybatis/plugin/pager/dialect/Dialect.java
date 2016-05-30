/*
 * Copyright 2016-2019  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午3:49:10   
 * @version 1.0
 */
public interface Dialect {
	
	String OFFSET_PARAM_NAME="mybatis_offset_";
	
	String LIMIT_PARAM_NAME = "mybatis_limit_";
	
	String OFFSET_END_PARAM_NAME="mybatis_offsetEnd_";

    /**
     * Given a limit and an offset, apply the limit clause to the query.
     *
     * @param mappedStatement
     * @param pageBoundSql
     * @param query The query to which to apply the limit.
     * @param offset The offset of the limit
     * @param limit The limit of the limit ;)
     * @return The modified query statement with the limit applied.
     */
    String getLimitString(MappedStatement mappedStatement,BoundSql pageBoundSql,String query, int offset, int limit);

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
