/*
 * Copyright 2016-2019  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:41:30   
 * @version 1.0
 */
public abstract class DialectAdapter implements Dialect {
	
	@Override
    public String getLimitString(MappedStatement mappedStatement,BoundSql pageBoundSql,String query, int offset, int limit) {
        return getLimitStringInternal(mappedStatement,pageBoundSql,preProcessQuery(query), offset, limit);
    }
    
    @Override
    public String limitStringByPage(String query, int pageSize, int pageNum) {
        throw new UnsupportedOperationException("not implement yet.");
    }

    @Override
    public String getCountSql(String query) {
        return " select count(1) from (" + query + ") derived_ ";
    }

    /**
     * pre-processing Mybatis query sql. e.g remove ';'
     * @param query
     * @return
     */
    private String preProcessQuery(String query) {
    	if ( query.charAt( query.length() - 1) == ';' ) {
    		return query.substring(0, query.length() - 1);
		}
        return query;
    }
    
    /**
     * 添加参数
     * @param copyMappedStatement
     * @param copyBoundSql
     * @param name
     * @param value
     * @param type
     */
    protected void setPageParameter(MappedStatement copyMappedStatement,BoundSql copyBoundSql,String name, Object value, Class<?> type){
        ParameterMapping parameterMapping = new ParameterMapping.Builder(copyMappedStatement.getConfiguration(), name, type).build();
        copyBoundSql.getParameterMappings().add(parameterMapping);
        copyBoundSql.setAdditionalParameter(name, value);
    }
    
    protected int getOffsetEnd(int offset,int limit){
    	return offset+limit;
    }

    protected abstract String getLimitStringInternal(MappedStatement mappedStatement,BoundSql pageBoundSql,String query, int offset, int limit);
}
