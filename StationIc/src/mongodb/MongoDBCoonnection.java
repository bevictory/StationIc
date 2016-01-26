/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:MongoDBCoonnection.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月18日 上午11:27:24
 */
package mongodb;

import java.io.IOException;
import java.util.Properties;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import mysql.DBConnection;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月18日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class MongoDBCoonnection {
	private Properties prop;
	private String configName = "mongodb.properties";
	private static MongoDBCoonnection mongoDBConn;
	private MongoClient mongoClient;
	private MongoDatabase mongoData;
	private MongoClient remoteClient;
	private MongoClient remoteClient2;
	private MongoClient remoteClient3;
	private MongoDatabase remoteMongoData;
	private MongoDatabase remoteMongoData2;
	private MongoDatabase remoteMongoData3;
	String url;int port;String databaseName;
	private MongoDBCoonnection() {
//		prop = new Properties();
//		try {
//			prop.load(this.getClass().getClassLoader().getResourceAsStream(configName));
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		url = prop.getProperty("mongodb.url").trim();
//		port = Integer.valueOf(prop.getProperty("mongodb.port").trim());
//		databaseName = prop.getProperty("mongodb.databasename").trim();
//		mongoClient = new MongoClient(url,port);
//		mongoData = mongoClient.getDatabase(databaseName);
		 
	}
	public static MongoDBCoonnection getInstance() {
		if(mongoDBConn ==null) {
			mongoDBConn = new MongoDBCoonnection();
			return mongoDBConn;
		}
		else return mongoDBConn;
	}
	/**
	 * 
	 * 方法说明 获得本地mongodb中DB数据库
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public DB getDB(){
		DB db = mongoClient.getDB(databaseName);
		return db;
	}
	/**
	 * 
	 * 方法说明 获得29服务器mongodb中gps数据库客户端连接
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public MongoClient getRemoteClient(){
		if(remoteClient ==null){
			MongoClientURI connectionString = new MongoClientURI(
				"mongodb://cpss:123456@192.168.1.104:27017/?authSource=gps_ic");
			remoteClient = new MongoClient(connectionString);
		}
		
		return remoteClient;
	}
	/**
	 * 
	 * 方法说明 获得29服务器上MondoDatabase类型数据库
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public MongoDatabase getRemoteMongoDatabase(){
		return getRemoteClient().getDatabase("gps_ic");
	}
	public MongoClient getRemoteMongoClient2(){
		if(remoteClient2 ==null){
		MongoClientURI connectionString = new MongoClientURI(
				"mongodb://cpss:123456@192.168.1.104:27017/?authSource=gps_ic");
		remoteClient2 = new MongoClient(connectionString);
		}
		return remoteClient2;
	}
	public MongoClient getRemoteMongoClient3(){
		if(remoteClient3 ==null){
		MongoClientURI connectionString = new MongoClientURI(
				"mongodb://cpss:123456@192.168.1.104:27017/?authSource=gps_ic");
		remoteClient3 = new MongoClient(connectionString);
		}
		return remoteClient3;
	}
	public MongoDatabase getRemoteMongoDatabase2(){
		return getRemoteMongoClient2().getDatabase("gps_ic");
	}
	public MongoDatabase getRemoteMongoDatabase3(){
		return getRemoteMongoClient3().getDatabase("gps_ic");
	}
	/**
	 * 
	 * 方法说明 获得29服务器上DB类型的数据库
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @return	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	@SuppressWarnings("deprecation")
	public DB getRemoteDB(){
		return getRemoteClient().getDB("gps_ic");
	}
	@SuppressWarnings("deprecation")
	public DB getRemoteDB2(){
		return getRemoteMongoClient2().getDB("gps_ic");
	}
	@SuppressWarnings("deprecation")
	public DB getRemoteDB3(){
		return getRemoteMongoClient3().getDB("gps_ic");
	}
	/**
	 * 方法说明 获得本地mongodb中的客户端
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月18日
	 * @return	返回值说明
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月18日
	 * @param	参数名	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	/**
	 * 方法说明  获得本地mongodb的MongoDatabase类型数据库
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月18日
	 * @return	返回值说明
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public MongoDatabase getMongoData() {
		return mongoData;
	}
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月18日
	 * @param	参数名	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public void setMongoData(MongoDatabase mongoData) {
		this.mongoData = mongoData;
	}
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月18日
	 * @return	返回值说明
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static MongoDBCoonnection getMongoDBConn() {
		return mongoDBConn;
	}
	

}
