package org.jdbc;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

public abstract class BaseDao<T> {
	private Class<T> type;
	private QueryRunner qr = new QueryRunner();
	//开启下划线->驼峰转换所用
	private BeanProcessor bean = new GenerousBeanProcessor();
	private RowProcessor processor = new BasicRowProcessor(bean);
	
	@SuppressWarnings("unchecked")
	public BaseDao(){
		// 获取子类的运行时类型
		@SuppressWarnings("rawtypes")
		Class<? extends BaseDao> clazz = this.getClass();
		// 获取子类父类的类型
		Type genericSuperclass = clazz.getGenericSuperclass();
		// 获取父类泛型的类型，强转调用getActualTypeArguments
		ParameterizedType pt = (ParameterizedType)genericSuperclass;
		// 获取父类泛型类型的实参
		Type[] t = pt.getActualTypeArguments();
		// 实参赋值给type
		type = (Class<T>)t[0];
	}
	
	/**
	 * 更新数据通用方法
	 * @param sql
	 * @param param
	 * @return > 0 表示成功
	 * @throws SQLException
	 */
	public int update(String sql, Object... params) throws SQLException{
		// 获取连接
		Connection conn = JDBCUtil.getConnection();
		int len = 0;
		try {
			// 返回结果
			len = qr.update(conn, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			JDBCUtil.free(conn);
		}
		
		return len;
	}
	/**
	 * 获取第一个查询结果
	 * @param sql
	 * @param params
	 * @return 一个结果
	 * @throws SQLException
	 */
	public T getBean(String sql, Object... params) throws SQLException{
		// 获取连接
		Connection conn = JDBCUtil.getConnection();
		T t = null;
		try {
			t = qr.query(conn, sql, new BeanHandler<T>(type, processor), params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.free(conn);
		}
		
		return t;
	}
	/**
	 * 获取查询到的所有数据 封装为一个List集合
	 * @param sql
	 * @param params
	 * @return 一个集合
	 * @throws SQLException
	 */
	public List<T> getBeanList(String sql, Object... params) throws SQLException{
		// 获取连接
		Connection conn = JDBCUtil.getConnection();
		
		List<T> t = null;
		
		try {
			t = qr.query(conn, sql, new BeanListHandler<T>(type, processor), params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.free(conn);
		}
		
		return t;
	}
	/**
	 * 获取单个值 例如count(1)
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Object getSingleValue(String sql, Object... params) throws SQLException{
		Connection conn = JDBCUtil.getConnection();
		Object o = null;
		try {
			o = qr.query(conn, sql, new ScalarHandler<T>(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.free(conn);
		}
		
		return o;
	}
	/**
	 * 将结果集中的第一行数据封装到一个Map里，key是列名，value就是对应的值
	 * 例如分组查询平均值
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public Map<String,Object> getMap(String sql, Object... params) throws SQLException{
		Connection conn = JDBCUtil.getConnection();
		Map<String,Object> map = null;
		try {
			map = qr.query(conn, sql, new MapHandler(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.free(conn);
		}
		
		return map;
	}
	
	/**
	 * 将结果集中的每一行数据都封装到一个Map里，然后在存放到List中
	 * 例如分组查询平均值
	 * @param sql
	 * @param params
	 * @return
	 * @throws SQLException
	 */
	public List<Map<String,Object>> getMapList(String sql, Object... params) throws SQLException{
		Connection conn = JDBCUtil.getConnection();
		List<Map<String,Object>> list = null;
		try {
			list = qr.query(conn, sql, new MapListHandler(), params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.free(conn);
		}
		
		return list;
	}
	
	/**
	 * 通用的批处理方法
	 * @param sql
	 * @param params
	 * params:二维数组 -> 第一个维度：执行次数 二维：参数信息
	 * @throws SQLException 
	 */
	public void batchUpdate(String sql, Object[][] params) throws SQLException {
		// 获取连接
		Connection conn = JDBCUtil.getConnection();
		try {
			qr.batch(conn, sql, params);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			JDBCUtil.free(conn);
		}
	}
}
