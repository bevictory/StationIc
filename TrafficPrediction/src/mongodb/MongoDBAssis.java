package mongodb;

import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;

/**
 * get mongodb databse db
 * @author daiguohui
 *
 */
public class MongoDBAssis {
	private static MongoDatabase mongoDatabase= null;
	private static DB db = null;
	private static String databaseName  ="gps_ic";
	static{
		mongoDatabase=MongoDBConnection.getInstance().getMongoClient().getDatabase(databaseName);
		db=MongoDBConnection.getInstance().getMongoClient().getDB(databaseName);
	}
	public static void setMongoDatabase(MongoDatabase mongoDatabase) {
		MongoDBAssis.mongoDatabase = mongoDatabase;
	}
	public static void setDb(DB db) {
		MongoDBAssis.db = db;
	}
	public static MongoDatabase getMongoDatabase() {
		
		return mongoDatabase;
	}
	@SuppressWarnings("deprecation")
	public static DB getDb() {
		
		return db;
	}
	
}
