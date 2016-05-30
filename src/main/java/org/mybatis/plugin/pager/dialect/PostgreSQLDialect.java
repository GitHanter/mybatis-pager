/*
 * Copyright 2016-2019 All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * An SQL dialect for Postgres
 * 
 * @Description:
 * @author Hanyanjing
 * @date 2016年5月27日 下午5:30:21
 * @version 1.0
 */
public class PostgreSQLDialect extends DialectAdapter {

    @Override
    protected String getLimitStringInternal(MappedStatement mappedStatement,BoundSql pageBoundSql,String sql, int offset, int limit) {
    	StringBuffer buffer = new StringBuffer( sql.length()+20 ).append(sql);
        if(offset > 0){
            buffer.append(" limit ? offset ?");
            setPageParameter(mappedStatement, pageBoundSql, LIMIT_PARAM_NAME, limit, Integer.class);
			setPageParameter(mappedStatement, pageBoundSql, OFFSET_PARAM_NAME, offset, Integer.class);
        }else{
            buffer.append(" limit ?");
            setPageParameter(mappedStatement, pageBoundSql, LIMIT_PARAM_NAME, limit, Integer.class);
        }
		return buffer.toString();
    }

}
