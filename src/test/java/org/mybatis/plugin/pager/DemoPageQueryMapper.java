/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.plugin.pager.model.Page;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年7月1日 下午2:55:30   
 * @version 1.0
 */
public interface DemoPageQueryMapper {

    /*--------------------------------------------
    |      P A G E    P A R A M E T E R       |
    ============================================*/

    /**
     * 这种方式需要传入参数中有一个{@code org.mybatis.plugin.pager.Constants#PAGE_PARAMERTER_NAME page}为key，且类型为 {@code org.mybatis.plugin.pager.model.Page}的Entry
     * 
     * @param params
     * @return
     */
    List<User> selectUsersByMap(Map<String, Object> params);

    /**
     * 这种方式使用时Mybatis内部把参数转换为一个Map
     * 
     * @see org.apache.ibatis.binding.MapperMethod.ParamMap
     * @param page
     * @param param
     * @return
     */
    List<User> selectUsersByAnnotations(@Param(org.mybatis.plugin.pager.Constants.PAGE_PARAMERTER_NAME) Page<?> page, @Param("wtfParamYouWant") Object param);
    

    /**
     * 这种情况用的最少，因为查询基本都需要过滤条件
     * 
     * @param page
     * @return
     */
    List<User> selectUsersByPage(Page<?> page);

    /*--------------------------------------------
    |      R O W B O U N D S      |
    ============================================*/

    @Select("SELECT * FROM USER order by id")
    List<User> selectUsersByRowBound();
}
