/*
 * Copyright 2016-2019  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import java.util.Locale;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:58:39   
 * @version 1.0
 */
public class Oracle8iDialect extends DialectAdapter {

    /**
     * Oracle 是从1开始的，这样的选择是为了和MySQL统一
     * 使用pageSize = 100 来测试
     * offset = (pageNum - 1) * pageSize
     * > offset
     * <= offset + pageSize
     */
    @Override
    protected String getLimitStringInternal(MappedStatement mappedStatement,BoundSql pageBoundSql,String sql, int offset, int limit) {
        boolean hasOffset = offset > 0;
        sql = sql.trim();
        boolean isForUpdate = false;
        if (sql.toLowerCase(Locale.ROOT).endsWith(" for update")) {
            sql = sql.substring(0, sql.length() - 11);
            isForUpdate = true;
        }

        final StringBuilder pagingSelect = new StringBuilder(sql.length() + 100);
        if (hasOffset) {
            pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
        } else {
            pagingSelect.append("select * from ( ");
        }
        pagingSelect.append(sql);
        if (hasOffset) {
            pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
            setPageParameter(mappedStatement, pageBoundSql, OFFSET_END_PARAM_NAME, getOffsetEnd(offset, limit), Integer.class);
            setPageParameter(mappedStatement, pageBoundSql, OFFSET_PARAM_NAME, offset, Integer.class);
           
        } else {
            pagingSelect.append(" ) where rownum <= ?");
            setPageParameter(mappedStatement, pageBoundSql, OFFSET_PARAM_NAME, limit, Integer.class);
        }

        if (isForUpdate) {
            pagingSelect.append(" for update");
        }

        return pagingSelect.toString();
    }

}
