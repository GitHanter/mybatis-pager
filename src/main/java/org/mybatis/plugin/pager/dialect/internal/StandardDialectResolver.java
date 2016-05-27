/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect.internal;

import java.sql.SQLException;

import org.mybatis.plugin.pager.dialect.DB2Dialect;
import org.mybatis.plugin.pager.dialect.Dialect;
import org.mybatis.plugin.pager.dialect.H2Dialect;
import org.mybatis.plugin.pager.dialect.InformixDialect;
import org.mybatis.plugin.pager.dialect.MySQLDialect;
import org.mybatis.plugin.pager.dialect.Oracle8iDialect;
import org.mybatis.plugin.pager.dialect.Oracle9iDialect;
import org.mybatis.plugin.pager.dialect.PostgreSQLDialect;
import org.mybatis.plugin.pager.dialect.SQLServerDialect;
import org.mybatis.plugin.pager.dialect.spi.DialectResolutionInfo;
import org.mybatis.plugin.pager.dialect.spi.DialectResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:32:48   
 * @version 1.0
 */
/**
 * The standard DialectResolver implementation
 *
 * @author Steve Ebersole
 */
public class StandardDialectResolver implements DialectResolver {
    private static final Logger LOG = LoggerFactory.getLogger(StandardDialectResolver.class);

    /**
     * Singleton access
     */
    public static final StandardDialectResolver INSTANCE = new StandardDialectResolver();

    @Override
    public Dialect resolveDialect(DialectResolutionInfo info) throws SQLException {
        final String databaseName = info.getDatabaseName();

        if ("H2".equals(databaseName)) {
            return new H2Dialect();
        }

        if ("MySQL".equals(databaseName)) {
            return new MySQLDialect();
        }

        if ("PostgreSQL".equals(databaseName)) {
            return new PostgreSQLDialect();
        }

        if (databaseName.startsWith("Microsoft SQL Server")) {
            return new SQLServerDialect();
        }

        if ("Informix Dynamic Server".equals(databaseName)) {
            return new InformixDialect();
        }

        if (databaseName.startsWith("DB2/")) {
            return new DB2Dialect();
        }

        if ("Oracle".equals(databaseName)) {
            final int majorVersion = info.getDatabaseMajorVersion();

            switch (majorVersion) {
            case 12:
                // fall through
            case 11:
                // fall through
            case 10:
                // fall through
            case 9:
                return new Oracle9iDialect();
            case 8:
                return new Oracle8iDialect();
            default:
                LOG.error("Unknown Oracle major version {}", majorVersion);
            }
            return new Oracle8iDialect();
        }

        return null;
    }
}
