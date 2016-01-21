/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:DBConnection.java
 * Package Name:main
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��17�� ����10:47:21
 */
package mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
  * ��˵��   mysql���ӵĸ�����
  * @author dai.guohui
  * @version 1.0, 2015��11��17��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
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
	 * ����˵�� ��������
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return
	 * @throws IOException	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
	 * ����˵�� ���24������dev1���ݿ������
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return
	 * @throws IOException	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
	 * ����˵�� ��ȡ22��������copy���ݿ������
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
	 * ����˵�� �ر����ݿ�����
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
