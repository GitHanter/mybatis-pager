/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.MappedStatement.Builder;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.transaction.Transaction;
import org.mybatis.plugin.pager.dialect.Dialect;
import org.mybatis.plugin.pager.dialect.internal.StandardDialectResolver;
import org.mybatis.plugin.pager.dialect.spi.DatabaseMetaDataDialectResolutionInfoAdapter;
import org.mybatis.plugin.pager.dialect.spi.DialectResolver;
import org.mybatis.plugin.pager.model.Page;
import org.mybatis.plugin.pager.model.PageList;
import org.mybatis.plugin.pager.resolver.PageParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;

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
 * 分页插件有有一点一定要注意，那就是不能改变传入的参数值，只能使用深拷贝复制一个，然后把这个拷贝的对象传给代理的方法(invocation.getTarget()中的原对象) MappedStatement 对象是在启动时解析Mapper xml创建的，不能对其进行更改，因为分页插件只是完成一次分页操作，下次调用该Mapper时有可能不再需要分页功能。 所以要改变传入参数时必须使用深拷贝 查看{@code DefaultSqlSession} 中{@code MappedStatement ms = configuration.getMappedStatement(statement);}
 * <p>
 * 拦截StatementHandler直接把BoundSql.sql设置成分页sql即可，之所以可以这样设置，是因为BoundSql是从MappedStatement取得的，也即 从SqlSource 创建的，所以每次查询都是一个新的，所以某一次查询改了Boundsql不会影响之后的查询。 而拦截Executor的query方法时，要实现和StatementHandler一样的效果，则需要覆盖MappedStatement的SqlSource属性，所以需要创建一个新的MappedStatement，
 * 复制除SqlSource以外的所有属性。然后把拦截时获取的BoundSql，更改sql为分页sql既可。而分页所需参数(offset,limit)的ParemeterMapping也可以在该BoundSql中直接加，参数值放在 additionalParameters里
 * <p>
 * ParameterMapping的顺序一定要和它在分页Sql中的顺序一致，如"limit <offset>,<limit>",那么 <offset>的ParameterMapping要在<limit>的ParameterMapping前；且如果有ParameterMapping，那么 sql语句中一定要有占位符(即如果直接拼接在分页Sql中，那么就不要添加ParameterMapping)
 * <p>
 * dialect最好是单例，所以实现时需要考虑线程安全问题
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
	
	private final ReentrantLock lock = new ReentrantLock();
	
	private Dialect dialect=null;
	
	private boolean offsetAsPageNum=false;

    private static Logger logger = LoggerFactory.getLogger(PageQueryInterceptor.class);

    private static int MAPPED_STATEMENT_INDEX = 0;

    private static int PARAMETER_INDEX = 1;

    private static int ROWBOUNDS_INDEX = 2;

    @SuppressWarnings("unchecked")
    public Object intercept(Invocation invocation) throws Throwable {
        final Executor executor = (Executor) invocation.getTarget();
        final Object[] queryArgs = invocation.getArgs();
        final MappedStatement ms = (MappedStatement)queryArgs[MAPPED_STATEMENT_INDEX];
        final Object parameterObject = queryArgs[PARAMETER_INDEX];
        final RowBounds rowBounds = (RowBounds)queryArgs[ROWBOUNDS_INDEX];
        
        Page<?> resolvedPageParam = null;
        int offset = -1;
        int limit = -1;
        
        Page<Object> page = null;
        
        if((rowBounds.getOffset() == RowBounds.NO_ROW_OFFSET&& rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT)
        		||rowBounds.getLimit()<= 0 || rowBounds.getOffset()< 0){
            resolvedPageParam = PageParameterResolver.resolveParam(parameterObject, ms);
            if (resolvedPageParam == null || resolvedPageParam.isPageManually()) {
                // Both RowBounds and Page parameter resolve failed, or Page Manually, this statement will not be processed as PageQuery.
                return invocation.proceed();
            } else {
                offset = calculateOffset(resolvedPageParam.getPageNumber(), resolvedPageParam.getPageSize());
                limit = resolvedPageParam.getPageSize();

                page = (Page<Object>) resolvedPageParam;
            }
        } else {
            // If both RowBounds and Page parameter used in the query, the RowBounds will take precedence.
            // Means if RowBounds with invalid value, the Page parameter will take over.
            page = new Page<Object>();
            offset = rowBounds.getOffset();
            limit = rowBounds.getLimit();

            if (offsetAsPageNum) {
                offset = calculateOffset(offset, limit);// (offset -1)*limit;

                page.setPageNumber(rowBounds.getOffset());
                page.setPageSize(rowBounds.getLimit());
            }else{
                page.setPageSize(limit);
                if (rowBounds.getOffset()> 0) {
                    //(pageNum -1)*pageSize = offset
                    int pageNum = rowBounds.getOffset()%rowBounds.getLimit()==0? rowBounds.getOffset()/rowBounds.getLimit()+1:rowBounds.getOffset()/rowBounds.getLimit()+2;
                    page.setPageNumber(pageNum);
                }
            }
        }
        
        RowBounds pageBounds = new RowBounds(offset, limit);
        
        // Mybatis内部使用的逻辑分页，已经进行物理分页，则不再需要逻辑分页
        queryArgs[ROWBOUNDS_INDEX] = RowBounds.DEFAULT;
        
        final BoundSql originalBoundSql = ms.getBoundSql(parameterObject);
        Transaction transaction = executor.getTransaction();
        
        Dialect dialect = getDialect(transaction.getConnection());
        
        String countSql = dialect.getCountSql(originalBoundSql.getSql());
        BoundSql copyBoundSql = copyToPageBoundSql(ms, originalBoundSql, countSql);
        
        
        Cache cache = ms.getCache();
        Integer count = null;
        if(cache != null && ms.isUseCache() && ms.getConfiguration().isCacheEnabled()){        	
        	CacheKey countCacheKey = executor.createCacheKey(ms, parameterObject, pageBounds, copyBoundSql);
        	count = (Integer)cache.getObject(countCacheKey);
        	if(count == null){
        		count = getCount(ms, transaction, parameterObject, copyBoundSql);
        		cache.putObject(countCacheKey, count);
            }
        } else {
            count = getCount(ms, transaction, parameterObject, copyBoundSql);
        }
        if (count!=null&&count==0) {
			PageList<Object> empty= new PageList<Object>();
            if (page!=null){
                //If resolved page parameter, should use the page information (with pageNumber and pageSize)
                page.setTotalRows(0);
                page.afterPropertiesSet();
                empty.setPage(page);
            }
            return empty;
		}
        
        // The Query get Result, count>0
        page.setTotalRows(count);
        page.afterPropertiesSet();
        
        String pageSql = dialect.getLimitString(ms, copyBoundSql, originalBoundSql.getSql(), pageBounds.getOffset(), pageBounds.getLimit());
        MetaObject bounSql = ms.getConfiguration().newMetaObject(copyBoundSql);
        bounSql.setValue("sql", pageSql);//把sql改为分页语句
        
        MappedStatement pageMappedStatement= copy2PageMappedStatement(ms, new DirectSqlSource(copyBoundSql));
        queryArgs[MAPPED_STATEMENT_INDEX] = pageMappedStatement;
        
        List<Object> list = (List<Object>) invocation.proceed();
        PageList<Object> pageList = new PageList<Object>(list,page);
        
        return pageList;
    }
    
    private int getCount(MappedStatement mappedStatement, Transaction transaction,Object parameterObject, BoundSql countBoundSql) throws SQLException{
    	final String count_sql = countBoundSql.getSql();
        logger.debug("Total count SQL [{}] ", count_sql);
        logger.debug("Total count Parameters: {} ", parameterObject);
        PreparedStatement countStmt = null;
        ResultSet rs = null;
        try {
        	Connection connection = transaction.getConnection();
            countStmt = connection.prepareStatement(count_sql);
            
            DefaultParameterHandler handler = new DefaultParameterHandler(mappedStatement,parameterObject,countBoundSql);
            handler.setParameters(countStmt);

            rs = countStmt.executeQuery();
            int count = 0;
            if (rs.next()) {
                count = rs.getInt(1);
            }
            logger.debug("Total count: {}", count);
            return count;
		} finally {
			try {
				if (countStmt!=null) {
					countStmt.close();
				}
				if (rs!=null) {
					rs.close();
				}
			} catch (Exception e) {
				// Swallow Exception
			}
			
		}
        
    }
    
    /**
     * 拷贝BoundSql
     * 
     * @param ms
     *            未采用深拷贝。BoundSql 我们只需变更sql和hasAdditionalParameter，ParameterMappings需要添加分页的参数。
     * @param boundSql
     * @param sql
     * @return
     */
    private BoundSql copyToPageBoundSql(MappedStatement ms, BoundSql boundSql,String sql) {
    	List<ParameterMapping> parameterMappings = new ArrayList<ParameterMapping>(boundSql.getParameterMappings());
		BoundSql newBoundSql = new BoundSql(ms.getConfiguration(),sql, parameterMappings, boundSql.getParameterObject());
		for (ParameterMapping mapping : boundSql.getParameterMappings()) {
		    String prop = mapping.getProperty();
		    if (boundSql.hasAdditionalParameter(prop)) {
		        newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
		    }
		}
		return newBoundSql;
	}
    
    /**
     * 
     * @param ms 原MappedStatement
     * @param diectSqlSource 包装了分页sql的SqlSource
     * @return
     */
    private MappedStatement copy2PageMappedStatement(MappedStatement ms,SqlSource diectSqlSource) {
		Builder builder = new Builder(ms.getConfiguration(),ms.getId(),diectSqlSource,ms.getSqlCommandType());
		
		builder.databaseId(ms.getDatabaseId());
		builder.resource(ms.getResource());
		builder.fetchSize(ms.getFetchSize());
		builder.statementType(ms.getStatementType());
		builder.keyGenerator(ms.getKeyGenerator());
		if(ms.getKeyProperties() != null && ms.getKeyProperties().length !=0){
            StringBuffer keyProperties = new StringBuffer();
            for(String keyProperty : ms.getKeyProperties()){
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length()-1, keyProperties.length());
			builder.keyProperty(keyProperties.toString());
		}
		
		if(ms.getKeyColumns() != null && ms.getKeyColumns().length !=0){
            StringBuffer keyColumns = new StringBuffer();
            for(String keyProperty : ms.getKeyColumns()){
            	keyColumns.append(keyProperty).append(",");
            }
            keyColumns.delete(keyColumns.length()-1, keyColumns.length());
			builder.keyColumn(keyColumns.toString());
		}
		
		//setStatementTimeout()
		builder.timeout(ms.getTimeout());
		
		//setStatementResultMap()
		builder.parameterMap(ms.getParameterMap());
		
		//setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
		builder.resultSetType(ms.getResultSetType());
	    
		//setStatementCache()
		builder.cache(ms.getCache());
		builder.flushCacheRequired(ms.isFlushCacheRequired());
		builder.useCache(ms.isUseCache());
		
		builder.lang(ms.getLang());
		
		return builder.build();
	}
    
    public static int calculateOffset(int pageNum, int pageSize) {
        if (pageSize <= 0 || pageNum <= 0) {
            throw new IllegalArgumentException("Both 'pageSize' and 'pageNum' should be POSITIVE Integer!");
        }
        // (pageNum -1)*pageSize = offset
        return (pageNum - 1) * pageSize;
    }

    private Dialect getDialect(Connection connection) throws Throwable{
    	lock.lock();
    	try {
    		if (dialect!=null) {
				return dialect;
			}
    		DatabaseMetaData databaseMetaData = connection.getMetaData();
    		DatabaseMetaDataDialectResolutionInfoAdapter resolutionInfo = new DatabaseMetaDataDialectResolutionInfoAdapter(databaseMetaData);
            DialectResolver dialectResolver = StandardDialectResolver.INSTANCE;
    		dialect = dialectResolver.resolveDialect(resolutionInfo);
    		return dialect;
		} finally {
			lock.unlock();
		}
    }
    
    public Object plugin(Object target) {
    	return Plugin.wrap(target, this);
    }

    public void setProperties(Properties properties) {
        String offsetProperty = properties.getProperty(Constants.OFFSET_AS_PAGE_NUMBER);
    	if (offsetProperty!=null) {
    		offsetAsPageNum = Boolean.valueOf(offsetProperty);
		}
    }
    
    public boolean isOffsetAsPageNum() {
        return offsetAsPageNum;
    }

    public static class DirectSqlSource implements SqlSource {
		private BoundSql boundSql;
		
		public DirectSqlSource(BoundSql boundSql) {
			this.boundSql = boundSql;
		}
		
		@Override
		public BoundSql getBoundSql(Object parameterObject) {
			return boundSql;
		}
	}

}
