/*
 * Copyright 2016-2019  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.dialect.spi;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;

/**
 * An implementation of DialectResolutionInfo that delegates calls to a wrapped {@link DatabaseMetaData}.
 *
 * @Description:
 * @author Hanyanjing
 * @date 2016年5月27日 下午4:25:07
 * @version 1.0
 */
public class DatabaseMetaDataDialectResolutionInfoAdapter implements DialectResolutionInfo {
    private final DatabaseMetaData databaseMetaData;

    public DatabaseMetaDataDialectResolutionInfoAdapter(DatabaseMetaData databaseMetaData) {
        this.databaseMetaData = databaseMetaData;
    }

    @Override
    public String getDatabaseName() throws SQLException {
        return databaseMetaData.getDatabaseProductName();
    }

    @Override
    public int getDatabaseMajorVersion() throws SQLException {
        return interpretVersion(databaseMetaData.getDatabaseMajorVersion());
    }

    private static int interpretVersion(int result) {
        return result < 0 ? NO_VERSION : result;
    }

    @Override
    public int getDatabaseMinorVersion() throws SQLException {
        return interpretVersion(databaseMetaData.getDatabaseMinorVersion());
    }

    @Override
    public String getDriverName() throws SQLException {
        return databaseMetaData.getDriverName();
    }

    @Override
    public int getDriverMajorVersion() {
        return interpretVersion(databaseMetaData.getDriverMajorVersion());
    }

    @Override
    public int getDriverMinorVersion() {
        return interpretVersion(databaseMetaData.getDriverMinorVersion());
    }

}