/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.resolver;

import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.reflection.MetaObject;
import org.mybatis.plugin.pager.Constants;
import org.mybatis.plugin.pager.model.Page;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年7月1日 上午11:43:52   
 * @version 1.0
 */
public class PageParameterResolver {

    public static Page<?> resolveParam(Object parameterObject, MappedStatement ms) {
        if (parameterObject instanceof Page<?>) {
            /**
             * Mapper形如：List<User> selectUsers(Page<User> page);
             */
            return (Page<?>) parameterObject;
        } else if (parameterObject != null) {
            /**
             * Mapper形如：
             * (1) List<User> selectUsers(@Param(Constants.PAGE_PARAMERTER_NAME)Page<User> page,Object... arguments);
             * (2) 或者：List<User> selectUsers(Map<String,Object> params);
             * 且传入参数形如 Map<String,Object> params = new HashMap<String,Object>();
             * params.put(Constants.PAGE_PARAMERTER_NAME,new Page<Object>());
             */
            MetaObject metaObject = ms.getConfiguration().newMetaObject(parameterObject);
            Object resolved = metaObject.getValue(Constants.PAGE_PARAMERTER_NAME);
            if (resolved instanceof Page<?>) {
                return (Page<?>) resolved;
            }
        }
        return null;
    }

}
