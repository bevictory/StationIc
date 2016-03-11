package mongodb;

import java.io.IOException;
import java.util.Properties;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoDBConnection {
	private Properties prop;
	private static MongoDBConnection mongodbConn=null;
	private String configFileName = "mongodb.properties";
	private String url;
	private MongoClient mongoClient;
	/**
	 * build 
	 */
	private MongoDBConnection(){
		prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream(configFileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		url = prop.getProperty("mongodb.url").trim();
	}
	/**
	 * singleton 
	 * @return MongoDBConnection
	 */
	public static MongoDBConnection getInstance(){
		if(mongodbConn == null){
			mongodbConn= new  MongoDBConnection();	
		}
		return mongodbConn;
	}
	/**
	 * get mongodb client
	 * @return
	 */
	public MongoClient getMongoClient() {
		if(mongoClient == null ){
			MongoClientURI connectUrl= new MongoClientURI(
					"mongodb://cpss:123456@"+url+":27017/?authSource=gps_ic");
			mongoClient = new MongoClient(connectUrl);
		}
		return mongoClient;
	}
	
	public String getUrl() {
		return url;
	}
	public static void main(String []args){
		MongoDBConnection mongodbConn = MongoDBConnection.getInstance();
		System.out.println(mongodbConn.getUrl());
	}

}
