/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager.test;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mybatis.plugin.pager.Constants;
import org.mybatis.plugin.pager.DemoPageQueryMapper;
import org.mybatis.plugin.pager.User;
import org.mybatis.plugin.pager.model.Page;
import org.mybatis.plugin.pager.model.PageList;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年7月1日 下午3:05:41   
 * @version 1.0
 */
public class DemoPageQueryTest {

    private static final String conf = "mybatis.cfg.xml";

    private static final int Query_Page_Size = 2;

    private static final int Query_Page_Number = 1;

    private static Page<User> page;

    private static SqlSessionFactory sessionFactory = null;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        page = new Page<User>(Query_Page_Number, Query_Page_Size);
        Reader reader = Resources.getResourceAsReader(conf);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        sessionFactory = builder.build(reader);
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test() throws IOException {
        SqlSession session = sessionFactory.openSession();
        try {
            DemoPageQueryMapper mapper = session.getMapper(DemoPageQueryMapper.class);
            System.out.println("##################### testRowBound ########################");
            testRowBound(session);
            System.out.println("##################### testMapWithPage ########################");
            testMapWithPage(mapper);
            System.out.println("##################### testDirectPage ########################");
            testPageDirect(mapper);
            System.out.println("##################### testAnnotationPage ########################");
            testPageAnnotation(mapper);
        } finally {
            session.close();
        }

    }

    void testMapWithPage(DemoPageQueryMapper mapper) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(Constants.PAGE_PARAMETER_NAME, page);

        print(mapper.selectUsersByMap(params));
    }

    void testPageAnnotation(DemoPageQueryMapper mapper) {
        Map<String, Object> queryParams = new HashMap<String, Object>();
        queryParams.put("firstName", "same");
        queryParams.put("lastName", "");

        print(mapper.selectUsersByAnnotations(page, queryParams));
    }

    void testPageDirect(DemoPageQueryMapper mapper) {
        print(mapper.selectUsersByPage(page));
    }

    void testRowBound(SqlSession session) {
        RowBounds pageBounds = new RowBounds(1, 2);
        List<Object> users = session.selectList("org.mybatis.plugin.pager.DemoPageQueryMapper.selectUsersByRowBound", null, pageBounds);
        for (Object o : users) {
            User user = (User) o;
            System.out.println("User:{id:" + user.getId() + ",firstName:" + user.getFirstName() + ",lastName:" + user.getLastName() + "}");
        }
        if (users instanceof PageList<?>) {
            PageList<?> list = (PageList<?>) users;
            System.out.println("Total Count:" + list.getPage().getTotalRows() + ",Total Page:" + list.getPage().getTotalPages() + "/"
                               + list.getPage().getPageSize() + " per page");
        }
    }

    @Test
    @Ignore
    public void testObjectWrapper() throws IOException {
        Map<String, Object> parameterObject = new HashMap<String, Object>();
        parameterObject.put(Constants.PAGE_PARAMETER_NAME, page);
        MetaObject metaObject = sessionFactory.getConfiguration().newMetaObject(parameterObject);
        Object resolved = metaObject.getValue(Constants.PAGE_PARAMETER_NAME);
        System.out.println(resolved instanceof Page<?>);
    }

    private void print(List<User> users) {
        for (User user : users) {
            System.out.println("User:{id:" + user.getId() + ",firstName:" + user.getFirstName() + ",lastName:" + user.getLastName() + "}");
        }
        if (users instanceof PageList<?>) {
            PageList<?> list = (PageList<?>) users;
            System.out.println("Total Count:" + list.getPage().getTotalRows() + ",Total Page:" + list.getPage().getTotalPages() + "/"
                               + list.getPage().getPageSize() + " per page");
        }
    }

}
