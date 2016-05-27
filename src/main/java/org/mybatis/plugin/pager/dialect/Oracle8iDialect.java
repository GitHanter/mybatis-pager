/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import java.util.Locale;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:58:39   
 * @version 1.0
 */
public class Oracle8iDialect extends DialectAdapter {

    @Override
    protected String getLimitStringInternal(String sql, int offset, int limit) {
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
        } else {
            pagingSelect.append(" ) where rownum <= ?");
        }

        if (isForUpdate) {
            pagingSelect.append(" for update");
        }

        return pagingSelect.toString();
    }

}
