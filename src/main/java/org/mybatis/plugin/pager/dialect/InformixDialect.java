/*
 * Copyright 2016-2019  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * Informix dialect.
 * 
 * @Description:
 * @author Hanyanjing
 * @date 2016年5月27日 下午5:34:25
 * @version 1.0
 */
public class InformixDialect extends DialectAdapter {

    @Override
    protected String getLimitStringInternal(MappedStatement mappedStatement,BoundSql pageBoundSql,String querySelect, int offset, int limit) {
        if (offset > 0) {
            throw new UnsupportedOperationException("query result offset is not supported");
        }
        return new StringBuilder(querySelect.length() + 8).append(querySelect).insert(querySelect.toLowerCase().indexOf("select") + 6, " first " + limit)
                                                          .toString();
    }

}
