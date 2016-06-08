package util;

import java.util.ArrayList;
import java.util.List;

import mongodb.MongoDBAssis;

import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;

public class SegmentStation {
	private String collectionNameString ="gps_11_10_IC";
	/**
	 * ��gps_ic���л��segment station �������б�
	 * @return
	 */
	public BasicDBList getSegmentStation(){
		BasicDBList list = (BasicDBList) MongoDBAssis.getDb().getCollection(collectionNameString).group(new BasicDBObject("segmentId",true).append("busselfId", true)
		, new BasicDBObject(),new BasicDBObject("count",0), "function(cur,pre){count=pre.count+1;}");
		System.out.println(list.size());		
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
	/*��segmentStation���в���segment station ��Ϣ
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
		DBCursor iterable =MongoDBAssis.getDb().getCollection("segmentStation").find(new BasicDBObject(),new BasicDBObject("stationId",1).append("segmentId", 1));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(iterable.hasNext()){
			result.add((BasicDBObject) iterable.next());
		}
		return result;
	}
	
	public static void main(String[] args){
		SegmentStation s = new SegmentStation();
		s.insert();
	}
}
