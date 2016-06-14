package mongodb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import util.SegmentStationTuple;
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
	public static List<Integer> getSameStation(DB mongodb , int lineId, String stationId){
		List<Integer> array = new ArrayList<Integer>();
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
	
		DBCursor cursor ;
		BasicDBObject doc = null;
		
		
		list.clear();

		list.add(new BasicDBObject("station.stationId",stationId));
		cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		Iterator<DBObject> ite = cursor.iterator();
		
		while(ite.hasNext()){
			doc = (BasicDBObject) ite.next();
			@SuppressWarnings("unchecked")
			ArrayList<BasicDBObject> l = (ArrayList<BasicDBObject>)doc.get("station");
			if(doc.getInt("lineId") != lineId){
				
				array.add(doc.getInt("lineId"));
			}
			
		}
		return array;
	}
	
	public static int getSngrialId(DB mongodb,int segmentId, String stationId){
		ArrayList<tuple> array = new ArrayList<tuple>();
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("lineId",segmentId));
		list.add(new BasicDBObject("station.stationId",segmentId));
		DBCursor cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		BasicDBObject doc = null;
		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
		@SuppressWarnings("unchecked")
		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
		
		int sngSerialId=lis.get(0).getInt("sngSerialId");
		return sngSerialId;
			
	}
	
	
	public static List<Integer> getSegmentIdByStation(String stationId){
		List<Integer> result =new ArrayList<Integer>();
		DBCursor cursor= MongoDBAssis.getDb().getCollection(collectionName).find( new BasicDBObject("station.stationId",stationId));
		if(!cursor.hasNext()) return null;
		while(cursor.hasNext()){
			BasicDBObject object;
			object =(BasicDBObject) cursor.next() ;
			result.add(object.getInt("lineId"));
		}
		return result;
	}
	public static void main(String[] args){
		List<Integer> array= getSameStation(MongoDBAssis.getDb(),18,"12111300000000045252");
		System.out.println(array);
		
		//System.out.println(getStationNum(MongoDBCoonnection.getInstance().getMongoData(),35557702));
		//System.out.println(getStationId(MongoDBCoonnection.getInstance().getDB(),35557702,2));
	}

}
