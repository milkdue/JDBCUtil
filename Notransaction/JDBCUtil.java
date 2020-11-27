package org.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;


import com.alibaba.druid.pool.DruidDataSourceFactory;

public class JDBCUtil {
	// 不支持事务
	private static DataSource ds;
	
	static{
		Properties pro = new Properties();
		try {
			pro.load(JDBCUtil.class.getClassLoader().getResourceAsStream("druid.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			// 创建druid连接对象
			ds = DruidDataSourceFactory.createDataSource(pro);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取连接对象
	 * 同一个线程保证获取同一个连接对象
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException{
		Connection conn = ds.getConnection();
		
		return conn;
	}
	/**
	 * 释放连接
	 * @param conn
	 */
	public static void free(Connection conn){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
