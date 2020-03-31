package com.realmax.opentrafficversion.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBOpenHelper {

    private static String driver = "com.mysql.jdbc.Driver";//MySQL 驱动
    private static String user = "driving";//用户名
    private static String password = "PiXbcFDjRmSAYJ4E";//密码
    private static Connection conn = null;

    /**
     * 获取ioa_mes01数据库连接
     *
     * @return 返回一个数据库连接
     */
    public static Connection getDrivingConn() {
        if (conn == null) {
            try {
                Class.forName(driver);//获取MYSQL驱动
                //MYSQL数据库连接Url
                String IOA_MES01_URL = "jdbc:mysql://212.64.85.235:3306/driving?useSSL=false";
                conn = DriverManager.getConnection(IOA_MES01_URL, user, password);//获取连接

            } catch (ClassNotFoundException | SQLException e) {
                e.printStackTrace();
            }
        }
        return conn;
    }

    /**
     * 关闭数据库(Connection、PreparedStatement)
     */
    public static void closeAll(Connection conn, PreparedStatement ps) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭数据库(Connection)
     */
    public static void closeAll(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭数据库(PreparedStatement、ResultSet)
     */
    public static void closeAll(PreparedStatement ps, ResultSet rs) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭数据库(PreparedStatement)
     */
    public static void closeAll(PreparedStatement ps) {
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}