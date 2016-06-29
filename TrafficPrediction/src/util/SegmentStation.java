package util;

import java.util.ArrayList;
import java.util.List;

import mongodb.MongoDBAssis;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;

public class SegmentStation {
	private String collectionNameString ="gps_11_10_IC";
	/**
	 * ��gps_ic���л��segment station �������б�
	 * @return
	 */
	@Deprecated
	public BasicDBList getSegmentStation(){
		BasicDBList list = (BasicDBList) MongoDBAssis.getDb().getCollection(collectionNameString).group(new BasicDBObject("segmentId",true).append("busselfId", true)
		, new BasicDBObject(),new BasicDBObject("count",0), "function(cur,pre){count=pre.count+cur.traffic;}");
		System.out.println(list);		
		return list;
	}
	/**
	 * ��BusLineStation ���л��segment station�������б�
	 * @return
	 */
	public List<Document> getSegmentSta(){
		DBCursor cursor = MongoDBAssis.getDb().getCollection("BusLineStation").find(new BasicDBObject(),new BasicDBObject("lineId",1).append("station.stationId", 1));
		List<Document> result = new ArrayList<Document>();
		while(cursor.hasNext()){
			BasicDBObject object = new BasicDBObject();
			object =(BasicDBObject) cursor.next();
			List<BasicDBObject> stationArray = (List<BasicDBObject>) object.get("station");
			for(int i=0;i<stationArray.size();i++){
				Document object2 = new Document().append("segmentId", object.getInt("lineId")).append("stationId", stationArray.get(i).getString("stationId"))
						.append("sequenceTraffic", new ArrayList<Document>());
				result.add(object2);
			}
		}
		return result;
	}
	public List<Document> getSegmentStaInfo(){
		DBCursor cursor = MongoDBAssis.getDb().getCollection("BusLineStation").find(new BasicDBObject(),new BasicDBObject("lineId",1).append("station.stationId", 1).append("station.stationName", 1));
		List<Document> result = new ArrayList<Document>();
		while(cursor.hasNext()){
			BasicDBObject object = new BasicDBObject();
			object =(BasicDBObject) cursor.next();
			List<BasicDBObject> stationArray = (List<BasicDBObject>) object.get("station");
			for(int i=0;i<stationArray.size();i++){
				Document object2 = new Document().append("segmentId", object.getInt("lineId")).append("stationId", stationArray.get(i).getString("stationId"))
						.append("stationName", stationArray.get(i).getString("stationName"));
				result.add(object2);
			}
		}
		return result;
	}
	/**��segmentStation���в���segment station ��Ϣ
	 * ����getSegmentSta ����
	 *
	 */
	public void insert(){
		MongoDBAssis.getMongoDatabase().getCollection("segmentStation").insertMany(getSegmentSta());
	}
	/**
	 * ��segmentStation���в�ѯ���segment station����Ϣ
	 * @return
	 */
	public List<BasicDBObject> getSegmentStationList(){
		DBCursor iterable =MongoDBAssis.getDb().getCollection("segmentStation")
				.find(new BasicDBObject(),new BasicDBObject("stationId",1).append("segmentId", 1));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(iterable.hasNext()){
			result.add((BasicDBObject) iterable.next());
		}
		return result;
	}
	public List<BasicDBObject> getSegStaFromAnaly(){
		DBCursor iterable =MongoDBAssis.getDb().getCollection("segmentstationAnalysis")
				.find(new BasicDBObject(),new BasicDBObject("stationId",1).append("_id", 0).append("segmentId", 1).append("stationName", 1)).sort(new BasicDBObject("icSum",-1));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(iterable.hasNext()){
			result.add((BasicDBObject) iterable.next());
		}
		return result;
	}
	public static int getIcSum(int segmentId, String stationId){
		FindIterable<Document> iterable=MongoDBAssis.getMongoDatabase().getCollection("segmentstationAnalysis")
				.find(new BasicDBObject("segmentId",segmentId).append("stationId",stationId));
		  if(iterable.iterator().hasNext()){
			 return  iterable.iterator().next().getInteger("icSum");
		  }
		  else return 0;
	}
	public static void main(String[] args){
		SegmentStation s = new SegmentStation();
		s.getSegmentStation();
		
	}
}
