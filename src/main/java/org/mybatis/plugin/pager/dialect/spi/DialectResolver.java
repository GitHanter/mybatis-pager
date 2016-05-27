/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect.spi;

import org.mybatis.plugin.pager.dialect.Dialect;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午3:46:16   
 * @version 1.0
 */
public interface DialectResolver {
    /**
     * Determine the {@link Dialect} to use based on the given information. Implementations are expected to return
     * the {@link Dialect} instance to use, or {@code null} if the they did not locate a match.
     *
     * @param info
     *            Access to the information about the database/driver needed to perform the resolution
     * @return The dialect to use, or null.
     */
    public Dialect resolveDialect(DialectResolutionInfo info) throws Throwable;
}
