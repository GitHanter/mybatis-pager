/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:48:49   
 * @version 1.0
 */
public class MySQLDialect extends DialectAdapter {

    @Override
    protected String getLimitStringInternal(MappedStatement mappedStatement,BoundSql pageBoundSql,String sql, int offset, int limit) {
    	StringBuffer buffer = new StringBuffer( sql.length()+20 ).append(sql);
    	if (offset > 0) {
    		buffer.append(" limit ?, ?");
			setPageParameter(mappedStatement, pageBoundSql, OFFSET_PARAM_NAME, offset, Integer.class);
			setPageParameter(mappedStatement, pageBoundSql, LIMIT_PARAM_NAME, limit, Integer.class);
		}else{
			buffer.append(" limit ?");
			setPageParameter(mappedStatement, pageBoundSql, LIMIT_PARAM_NAME, limit, Integer.class);
		}
			
    	return buffer.toString();
    }

}
