#mybatis-pager
本插件实现方案是拦截Executor的query方法，还有一种方案是拦截StatementHandler的prepare方法。
拦截Executor主要是为了实现'查询总数的SQL'能够在mapper启用缓存时也缓存起来，StatementHandler无法实现。

参考了两个实现：
####[http://git.oschina.net/free/Mybatis_PageHelper](http://git.oschina.net/free/Mybatis_PageHelper)
####[https://github.com/miemiedev/mybatis-paginator](https://github.com/miemiedev/mybatis-paginator)

这两个实现在处理分页参数时都比较复杂（都采用把传入的当前parameterObject拷贝一个，然后添加分页所需的参数--ParameterMapping 和 ParameterMapping对应的值），本插件采用拷贝一个BoundSql，在其上添加ParameterMapping，而ParameterMapping对应的值放在additionalParameters。

插件参考了Hibernate Dialect的处理，具体请自行查看Hibernate源码。

#使用方法
mybatis.cfg.xml配置文件中添加
```xml
<plugins>
	<plugin interceptor="org.mybatis.plugin.pager.PageQueryInterceptor">
		<property name="offsetAsPageNum" value="true" />
	</plugin>
</plugins>
```
调用方法
```xml
sqlsession.selectList(String statement, Object parameter, RowBounds rowBounds) 或
sqlsession.selectMap(String statement, Object parameter, String mapKey, RowBounds rowBounds)
```
`selectList`调用返回值为[`PageList`](https://github.com/GitHanter/mybatis-pager/blob/master/src/main/java/org/mybatis/plugin/pager/model/PageList.java)

参数`offsetAsPageNum` 表示是否把传入的`RowBounds`的`offset`属性当做`pageNumber`处理，默认为`false`。(`RowBounds`的`limit`属性是pageSize)
