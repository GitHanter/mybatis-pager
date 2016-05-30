/*
 * Copyright 2016-2019 All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

import java.util.Locale;

import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:53:48   
 * @version 1.0
 */
public class SQLServerDialect extends DialectAdapter {

    static int getAfterSelectInsertPoint(String sql) {
        final int selectIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select" );
        final int selectDistinctIndex = sql.toLowerCase(Locale.ROOT).indexOf( "select distinct" );
        return selectIndex + (selectDistinctIndex == selectIndex ? 15 : 6);
    }

    @Override
    protected String getLimitStringInternal(MappedStatement mappedStatement,BoundSql pageBoundSql,String querySelect, int offset, int limit) {
        if ( offset > 0 ) {
            throw new UnsupportedOperationException( "query result offset is not supported" );
        }
        return new StringBuilder( querySelect.length() + 8 )
                .append( querySelect )
                .insert( getAfterSelectInsertPoint( querySelect ), " top " + limit )
                .toString();
    }

}
