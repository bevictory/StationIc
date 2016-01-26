/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:MongoDBCoonnection.java
 * Package Name:mongodb
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��18�� ����11:27:24
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
  * ��˵��
  * @author dai.guohui
  * @version 1.0, 2015��11��18��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
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
	 * ����˵�� ��ñ���mongodb��DB���ݿ�
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
	 */
	public DB getDB(){
		DB db = mongoClient.getDB(databaseName);
		return db;
	}
	/**
	 * 
	 * ����˵�� ���29������mongodb��gps���ݿ�ͻ�������
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
	 * ����˵�� ���29��������MondoDatabase�������ݿ�
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
	 * ����˵�� ���29��������DB���͵����ݿ�
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��23��
	 * @return	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
	 * ����˵�� ��ñ���mongodb�еĿͻ���
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��18��
	 * @return	����ֵ˵��
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
	 */
	public MongoClient getMongoClient() {
		return mongoClient;
	}
	/**
	 * ����˵��
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��18��
	 * @param	������	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
	 */
	public void setMongoClient(MongoClient mongoClient) {
		this.mongoClient = mongoClient;
	}
	/**
	 * ����˵��  ��ñ���mongodb��MongoDatabase�������ݿ�
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��18��
	 * @return	����ֵ˵��
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
	 */
	public MongoDatabase getMongoData() {
		return mongoData;
	}
	/**
	 * ����˵��
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��18��
	 * @param	������	����˵��
	 * @see	[��,��#��������#��Ա]
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
	 */
	public void setMongoData(MongoDatabase mongoData) {
		this.mongoData = mongoData;
	}
	/**
	 * ����˵��
	 * @author 	dai.guohui
	 * @version 1.0, 2015��11��18��
	 * @return	����ֵ˵��
	 * @since	[��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
	 */
	public static MongoDBCoonnection getMongoDBConn() {
		return mongoDBConn;
	}
	

}
