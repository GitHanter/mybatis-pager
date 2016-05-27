/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager;

import java.util.Properties;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>
 * 参考链接<br>
 * http://git.oschina.net/free/Mybatis_PageHelper <br>
 * https://github.com/miemiedev/mybatis-paginator <br>
 * http://www.mobile-open.com/2016/921972.html <br>
 * <p>
 * 参考了两个项目。这两个项目处理时都过于复杂。<br>
 * 拦截器的原理使用了JDK Proxy，和Spring AOP 一样的机制，都是基于接口的。 <br>
 * 分页插件处理两个问题：<br>
 * 1)从源SQL(BoundSql中取得)，依据数据库dialect得到 分页SQL 和总数SQL <br>
 * 2)执行总数SQL，得到总数 ；执行分页SQL(还是按Mybatis原来的逻辑执行，即代理的目标(Executor或者StatementHandler)方法执行查询分页数据)，得到List，然后返回<br>
 * (如果要返回总数，则必须要提供一个List实现类，里面放入总数信息和查询得到的List,这样才不会改变方法返回值 <br>
 * <p>
 * 有两种方案生产成分页SQL：
 * <p>
 * (1)直接生成分页SQL，即分页的offset和limit值直接拼接到分页SQL中。这种方案简单，但是查询分页总数生成缓存Key时需要传入RowBound(offset,limit)
 * <p>
 * (2)使用Mybatis原生的ParameterHandler 来设置offset 和 limit参数, 此方案需要添加ParameterMapping 和 对parameterObject做处理，添加offset和limit值。<br>
 * 参考的两个项目对parameterObject的处理思路都一样，就是把原来的拿出来，然后添加offset和limit值，这样处理太复杂。<br>
 * 查看DefaultParameterHandler后可以知道Mybatis设置参数值，还可以从BundSql.additionalParameters拿到,<br>
 * 所以 可以从additionalParameters入手设置offset和limit (生产缓存Key时也会把additionalParameters纳入，这样完美解决问题，也和Mybatis原生实现契合)
 * <p>
 * 参考的两个实现还提供了排序功能，本人认为需要分页的SQL前提就是需要排序的，而Mybatis分页插件对程序员并非是"透明"的，分页插件只是使得程序员不必再写查询总数的SQL，<br>
 * 同时使用缓存的时候使得查询总数的SQL和查询分页数据的SQL都启用(程序员自己写时可能某个SQL上没有配置缓存)<br>
 * 所以分页插件内提供排序功能毫无用处，甚至造成误导。
 * <p>
 * 本插件参考了Hibernate Dialect的处理，有源码引用。
 * <p>
 * 调用方法sqlsession.selectList(String statement, Object parameter, RowBounds rowBounds), 或者 sqlsession.selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds)
 * 
 * @author Hanyanjing
 * @date 2016年5月27日 下午2:18:14
 * @version 1.0
 */
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class }) })
public class PageQueryInterceptor implements Interceptor {

    private static Logger logger = LoggerFactory.getLogger(PageQueryInterceptor.class);

    private static int MAPPED_STATEMENT_INDEX = 0;

    private static int PARAMETER_INDEX = 1;

    private static int ROWBOUNDS_INDEX = 2;

    private static int RESULT_HANDLER_INDEX = 3;

    public Object intercept(Invocation invocation) throws Throwable {
        final Executor executor = (Executor) invocation.getTarget();
        final Object[] queryArgs = invocation.getArgs();
        final MappedStatement ms = (MappedStatement)queryArgs[MAPPED_STATEMENT_INDEX];
        final Object parameter = queryArgs[PARAMETER_INDEX];
        final RowBounds rowBounds = (RowBounds)queryArgs[ROWBOUNDS_INDEX];
        
        if(rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET
                        && rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT){
            return invocation.proceed();
        }
        
        return null;
    }

    public Object plugin(Object target) {
        // TODO Auto-generated method stub
        return null;
    }

    public void setProperties(Properties properties) {
        // TODO Auto-generated method stub

    }

}
