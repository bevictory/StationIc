package mongodb;

import java.util.ArrayList;
import java.util.Iterator;

import util.tuple;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * @author daiguohui
 *
 */
public class QueryBls {
	public static String collectionName ="BusLineStation";
	public static ArrayList<tuple> getSameStation(DB mongodb , int lineId, int sngSerialId){
		ArrayList<tuple> array = new ArrayList<tuple>();
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("lineId",lineId));
		list.add(new BasicDBObject("station.sngSerialId",sngSerialId));
		DBCursor cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		BasicDBObject doc = null;
		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
		@SuppressWarnings("unchecked")
		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
		
		String stationId=lis.get(0).getString("stationId");
		
		list.clear();

		list.add(new BasicDBObject("station.stationId",stationId));
		cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		Iterator<DBObject> ite = cursor.iterator();
		
		while(ite.hasNext()){
			doc = (BasicDBObject) ite.next();
			@SuppressWarnings("unchecked")
			ArrayList<BasicDBObject> l = (ArrayList<BasicDBObject>)doc.get("station");
			if(doc.getInt("lineId") != lineId){
				tuple t = new tuple(doc.getInt("lineId"),l.get(0).getInt("sngSerialId"));
				array.add(t);
			}
			
		}
		return array;
	}
	public static void main(String[] args){
		ArrayList<tuple> array= getSameStation(MongoDBAssis.getDb(),35610028,18);
		System.out.println(array);
		
		//System.out.println(getStationNum(MongoDBCoonnection.getInstance().getMongoData(),35557702));
		//System.out.println(getStationId(MongoDBCoonnection.getInstance().getDB(),35557702,2));
	}

}
