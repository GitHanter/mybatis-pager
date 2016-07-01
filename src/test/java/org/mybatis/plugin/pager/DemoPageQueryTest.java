/*
 * Copyright 2014-2015  All rights reserved.
 * Email: han.yanjingyy@gmail.com
 */

package org.mybatis.plugin.pager;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mybatis.plugin.pager.model.PageList;

/**
 * @Description: 
 * @author Hanyanjing
 * @date 2016年7月1日 下午3:05:41   
 * @version 1.0
 */
public class DemoPageQueryTest {

    private static final String conf = "mybatis.cfg.xml";

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
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
        Reader reader = Resources.getResourceAsReader(conf);
        SqlSessionFactoryBuilder builder = new SqlSessionFactoryBuilder();
        SqlSessionFactory sessionFactory = builder.build(reader);
        SqlSession session = sessionFactory.openSession();
        try {
            DemoPageQueryMapper mapper = session.getMapper(DemoPageQueryMapper.class);
            System.out.println("##################### testRowBound ########################");
            testRowBound(session);
            System.out.println("##################### testMapWithPage ########################");
            testMapWithPage(mapper);
        } finally {
            session.close();
        }

    }

    List<User> testMapWithPage(DemoPageQueryMapper mapper) {
        List<Object> list = new ArrayList<Object>();
        list.addAll(mapper.selectUsersByMap(null));
        print(list);
        return null;
    }

    List<User> testPageAnnotation(DemoPageQueryMapper mapper) {
        return null;
    }

    void testPageDirect(DemoPageQueryMapper mapper) {
    }

    void testRowBound(SqlSession session) {
        RowBounds pageBounds = new RowBounds(1, 2);
        print(session.selectList("org.mybatis.plugin.pager.DemoPageQueryMapper.selectUsersByRowBound", null, pageBounds));
    }

    private void print(List<Object> users) {
        for (Object o : users) {
            User user = (User) o;
            System.out.println("User:{id:" + user.getId() + ",firstName:" + user.getFirstName() + ",lastName:" + user.getLastName() + "}");
        }
        if (users instanceof PageList<?>) {
            PageList<?> list = (PageList<?>) users;
            System.out.println("Total Count:" + list.getPage().getTotalRows());
        }
    }

}
