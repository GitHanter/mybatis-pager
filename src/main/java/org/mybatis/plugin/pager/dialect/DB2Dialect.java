/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:45:44   
 * @version 1.0
 */
public class DB2Dialect extends DialectAdapter {

    @Override
    protected String getLimitStringInternal(String sql, int offset, int limit) {
        if (offset == 0) {
            return sql + " fetch first " + limit + " rows only";
        }
        // nest the main query in an outer select
        return "select * from ( select inner2_.*, rownumber() over(order by order of inner2_) as rownumber_ from ( " + sql + " fetch first " + limit
               + " rows only ) as inner2_ ) as inner1_ where rownumber_ > " + offset + " order by rownumber_";
    }

}
