package org.shiwu;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;


import com.alibaba.druid.pool.DruidDataSourceFactory;

public class JDBCUtil {
	private static DataSource ds;
	// 事务保证在同一个线程中连接是同一个连接
	private static ThreadLocal<Connection> th;
	
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

		th = new ThreadLocal<Connection>();
	}
	
	/**
	 * 获取连接对象
	 * 同一个线程保证获取同一个连接对象
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection(){
		Connection conn = th.get();
		if(conn == null){
			try {
				conn = ds.getConnection();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			th.set(conn);
		}
		
		return conn;
	}
	/**
	 * 释放连接
	 * @param conn
	 */
	public static void free(){
		Connection conn = th.get();
		if(conn != null){
			try {
				conn.setAutoCommit(true);
				conn.close();
				th.remove();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
