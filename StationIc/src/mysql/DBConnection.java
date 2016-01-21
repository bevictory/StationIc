/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:DBConnection.java
 * Package Name:main
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月17日 上午10:47:21
 */
package mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
  * 类说明   mysql连接的辅助类
  * @author dai.guohui
  * @version 1.0, 2015年11月17日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class DBConnection {
	private Properties prop ;
	private String resourceName ="jdbc.properties";
	private Connection conn ;
	public static DBConnection dbconn;
	private DBConnection() {
	}
	/**
	 * 
	 * 方法说明 单例方法
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return
	 * @throws IOException	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static DBConnection getInstance() {
		if(dbconn ==null) {
			dbconn = new DBConnection();
			return dbconn;
		}
		else return dbconn;
	}
	/**
	 * 
	 * 方法说明 获得24服务器dev1数据库的连接
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return
	 * @throws IOException	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public  Connection getConnection()throws IOException {
		prop = new Properties();
		prop.load(this.getClass().getClassLoader().getResourceAsStream(resourceName));
//		System.out.println(prop.getProperty("jdbc.driverClassName"));
//		System.out.println(prop.getProperty("jdbc.username"));
//		System.out.println(prop.getProperty("jdbc.password"));
		String classname =prop.getProperty("jdbc.driverClassName").trim() ;
		String url = prop.getProperty("jdbc.url").trim();
		String username = prop.getProperty("jdbc.username").trim();
		String password = prop.getProperty("jdbc.password").trim();
		try {
		
			
			Class.forName(classname);
			conn = DriverManager.getConnection(url,username,password);
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection conn1 = DriverManager.getConnection("jdbc:mysql://192.168.200.24:3306/czits_dev1?autoReconnect=true&amp;useUnicode=TRUE&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull",username,"czits123456");
//		    System.out.println("connect success");
			System.out.println("connect to mysql successful");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Not Find Driver");
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * 
	 * 方法说明 获取22服务器上copy数据库的连接
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public Connection getConnection_busno(){
		prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream(resourceName));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
//		System.out.println(prop.getProperty("jdbc.driverClassName"));
//		System.out.println(prop.getProperty("jdbc.username"));
//		System.out.println(prop.getProperty("jdbc.password"));
		String classname =prop.getProperty("jdbc.driverClassName").trim() ;
		String url = prop.getProperty("busno.url").trim();
		String username = prop.getProperty("busno.username").trim();
		String password = prop.getProperty("busno.password").trim();
		try {
		
			
			Class.forName(classname);
			conn = DriverManager.getConnection(url,username,password);
//			Class.forName("com.mysql.jdbc.Driver");
//			Connection conn1 = DriverManager.getConnection("jdbc:mysql://192.168.200.24:3306/czits_dev1?autoReconnect=true&amp;useUnicode=TRUE&amp;characterEncoding=UTF-8&amp;zeroDateTimeBehavior=convertToNull",username,"czits123456");
//		    System.out.println("connect success");
			System.out.println("connect to mysql successful");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Not Find Driver");
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;
	}
	/**
	 * 
	 * 方法说明 关闭数据库连接
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public void closeConnection(){
		try {
			
			if(conn != null ) conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
