/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午5:09:30   
 * @version 1.0
 */
public class H2Dialect extends DialectAdapter {

    @Override
    protected String getLimitStringInternal(String sql, int offset, int limit) {
        return sql + (offset > 0 ? " limit ? offset ?" : " limit ?");
    }

}
