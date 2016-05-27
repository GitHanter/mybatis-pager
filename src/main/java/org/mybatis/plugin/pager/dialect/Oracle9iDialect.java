/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import java.util.Locale;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:59:20   
 * @version 1.0
 */
public class Oracle9iDialect extends Oracle8iDialect {

    @Override
    protected String getLimitStringInternal(String sql, int offset, int limit) {
        boolean hasOffset = offset > 0;
        sql = sql.trim();
        String forUpdateClause = null;
        boolean isForUpdate = false;
        final int forUpdateIndex = sql.toLowerCase(Locale.ROOT).lastIndexOf("for update");
        if (forUpdateIndex > -1) {
            // save 'for update ...' and then remove it
            forUpdateClause = sql.substring(forUpdateIndex);
            sql = sql.substring(0, forUpdateIndex - 1);
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
            pagingSelect.append(" ) row_ where rownum <= ?) where rownum_ > ?");
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }

        if (isForUpdate) {
            pagingSelect.append(" ");
            pagingSelect.append(forUpdateClause);
        }

        return pagingSelect.toString();
    }
}
