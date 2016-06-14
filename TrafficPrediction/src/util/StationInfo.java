package util;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

public class StationInfo {
	@SuppressWarnings("unchecked")
	public static List<BasicDBObject> getNear(String stationId,int minDis,int maxDis){
		DB db = MongoDBAssis.getDb();
		ArrayList<Double> coordinate;
		DBCursor cursor =db.getCollection("StationInfo").find(new BasicDBObject("stationId",stationId));
		if(cursor.hasNext()){
			coordinate = (ArrayList<Double>) cursor.next().get("coordinate");
			
		}
		else return null;
		cursor=db.getCollection("StationInfo").find(new BasicDBObject("coordinate",
				new BasicDBObject("$near",new BasicDBObject("$geometry",new BasicDBObject("type","Point").append("coordinates",	 coordinate)).append("$minDistance", minDis).append("$maxDistance", maxDis))));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(cursor.hasNext()){
			BasicDBObject object = (BasicDBObject) cursor.next();
			//System.out.println(object);
			result.add(object);
			
		}
		return result;
		
	}
	
	
	@SuppressWarnings("unchecked")
	public static List<BasicDBObject> getNear_segsta(String stationId,int minDis,int maxDis){
		DB db = MongoDBAssis.getDb();
		ArrayList<Double> coordinate;
		DBCursor cursor =db.getCollection("StationInfo").find(new BasicDBObject("stationId",stationId));
		if(cursor.hasNext()){
			coordinate = (ArrayList<Double>) cursor.next().get("coordinate");
			
		}
		else return null;
		cursor=db.getCollection("StationInfo").find(new BasicDBObject("coordinate",
				new BasicDBObject("$near",new BasicDBObject("$geometry",new BasicDBObject("type","Point").append("coordinates",	 coordinate)).append("$minDistance", minDis).append("$maxDistance", maxDis))));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(cursor.hasNext()){
			BasicDBObject object = (BasicDBObject) cursor.next();
			//System.out.println(object);
			String station = object.getString("stationId");
			List<Integer> segmentList = QueryBls.getSegmentIdByStation(station);
			if(segmentList.size() ==0) return null;
			for(int i=0; i< segmentList.size();i++){
				BasicDBObject obj = new BasicDBObject("stationId",station);
				obj.append("segmentId", segmentList.get(i));
				result.add(obj);
			}
			
			
		}
		return result;
		
	}

	public static void main(String [] args){
		getNear( "12111300000000045252", 1,200);
	}

}
