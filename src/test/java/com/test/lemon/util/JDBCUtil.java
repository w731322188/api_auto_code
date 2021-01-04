package com.test.lemon.util;

import com.test.lemon.data.Constants;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author Mr吴
 * @date 2020/12/23  21:27
 */
public class JDBCUtil {


    /**
     * 获取到数据库连接对象
     *
     * @return
     */
    public static Connection getConnection() {
        String url = Constants.MYSQL_CLIENT_URL;
        String user = Constants.MYSQL_USER_NAME;
        String pwd = Constants.MYSQL_USER_PWD;
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, pwd);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void update(String sql) {
        //获取数据库连接
        Connection conn = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        try {
            queryRunner.update(conn, sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //关闭数据库连接
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 批量查询
     *
     * @param sql
     * @return
     */
    public static List<Map<String, Object>> queryList(String sql) {
        Connection conn = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        try {
            List<Map<String, Object>> result = queryRunner.query(conn, sql, new MapListHandler());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //关闭数据库连接
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询第一条数据
     *
     * @param sql
     * @return
     */
    public static Map<String, Object> queryOne(String sql) {
        Connection conn = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        try {
            Map<String, Object> result = queryRunner.query(conn, sql, new MapHandler());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //关闭数据库连接
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询结果集中的单个数据
     *
     * @param sql
     * @return
     */
    public static Object querySingle(String sql) {
        Connection conn = getConnection();
        QueryRunner queryRunner = new QueryRunner();
        try {
            Object result = queryRunner.query(conn, sql, new ScalarHandler<>());
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //关闭数据库连接
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
//        String sql  = "select count(*) from member where mobile_phone = '18515669606'";
//        System.out.println(querySingle(sql));
//        String sql  = "select * from member limit 100";
//        System.out.println(queryList(sql));

        String sql = "select * from member limit 100";
        System.out.println(queryOne(sql));
    }

}
