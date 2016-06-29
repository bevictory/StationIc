package util;

import java.awt.Insets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bson.Document;

import mongodb.MongoDBAssis;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;

public class Station {
	private String collectionNameString ="BusLineStation";
	
	/**
	 *从BusLineStation表中获得所有station的列表，并插入到station表中
	 */
	public void getStationId(){
		DBCursor iterable = MongoDBAssis.getDb().getCollection(collectionNameString).find(
				new BasicDBObject(),new BasicDBObject("station.stationId",1).append("_id", 0).append("station.stationName", 1));
		System.out.println(iterable.size());
		List<String> set = new ArrayList<String>();
		List<Document> nameList = new ArrayList<Document>();
		int num=0;
		while(iterable.hasNext()){
			ArrayList<BasicDBObject> arr=(ArrayList<BasicDBObject>) iterable.next().get("station");
			//System.out.println(arr.size());
			for(int i=0 ;i <arr.size();i++){
				String stationIdString=arr.get(i).getString("stationId");
				String stationName = arr.get(i).getString("stationName");
				
				//stationIdString.trim();
				///System.out.println(stationIdString);
				num++;
				if(!set.contains(stationIdString)){
					set.add(stationIdString);
					Document object = new Document("stationId",stationIdString).append("stationName", stationName);
					nameList.add(object);
				}else{
					//System.out.println(stationIdString);
					}
			}
		}
		System.out.println(num);
		System.out.println(set.size());
		System.out.println(nameList.size());
		insertStation(nameList, "station");
	}
	
	/**
	 * 向stationInfo
	 * 表中插入数据
	 */
	public void getStationInfo(){
		DBCursor iterable = MongoDBAssis.getDb().getCollection(collectionNameString).find(
				new BasicDBObject(),new BasicDBObject("station.stationId",1).append("_id", 0).append("station.stationName", 1).append("station.coordinate", 1));
		System.out.println(iterable.size());
		List<String> set = new ArrayList<String>();
		List<Document> nameList = new ArrayList<Document>();
		int num=0;
		while(iterable.hasNext()){
			ArrayList<BasicDBObject> arr=(ArrayList<BasicDBObject>) iterable.next().get("station");
			//System.out.println(arr.size());
			for(int i=0 ;i <arr.size();i++){
				String stationIdString=arr.get(i).getString("stationId");
				String stationName = arr.get(i).getString("stationName");
				ArrayList<Double> coordinate= (ArrayList<Double>) arr.get(i).get("coordinate");
				//stationIdString.trim();
				///System.out.println(stationIdString);
				num++;
				if(!set.contains(stationIdString)){
					set.add(stationIdString);
					Document object = new Document("stationId",stationIdString).append("stationName", stationName).append("coordinate", coordinate);
					nameList.add(object);
				}else{
					//System.out.println(stationIdString);
					}
			}
		}
		System.out.println(num);
		System.out.println(set.size());
		System.out.println(nameList.size());
		insertStation(nameList, "StationInfo");
	}
	public void insertStationInfo(List<Document> list,String name){
		MongoDBAssis.getMongoDatabase().getCollection(name).insertMany(list);
	}
	/**
	 * 插入station信息
	 * @param list
	 * @param name
	 */
	public void insertStation(List<Document> list,String name){
		MongoDBAssis.getMongoDatabase().getCollection(name).insertMany(list);
	}
	/**
	 * 向station表中插入sequenceTraffic字段
	 */
	public void insert(){
		ArrayList<Document> array = new ArrayList<Document>();
		MongoDBAssis.getMongoDatabase().getCollection("station").updateMany(new Document(), new Document("$set",new Document("sequenceTraffic",array)));
	}
	/**
	 * 从station表中获得stationId 的列表
	 * @return List<BasicDBObject>
	 */
	public List<BasicDBObject> getStationIdList(){
		DBCursor iterable =MongoDBAssis.getDb().getCollection("station").find(new BasicDBObject(),new BasicDBObject("stationId",1));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(iterable.hasNext()){
			result.add((BasicDBObject) iterable.next());
		}
		return result;
	}
	/**
	 * 获得station的信息，包括stationId stationName
	 * @return List<BasicDBObject>
	 */
	public List<BasicDBObject> getStationInfoList(){
		DBCursor iterable =MongoDBAssis.getDb().getCollection("station").find(new BasicDBObject(),new BasicDBObject("stationId",1).append("stationName", 1));
		List<BasicDBObject> result = new ArrayList<BasicDBObject>();
		while(iterable.hasNext()){
			result.add((BasicDBObject) iterable.next());
		}
		return result;
	}
	
	public static int getStationIcSum(String stationId){
		  FindIterable<Document> iterable=MongoDBAssis.getMongoDatabase().getCollection("stationAnalysis").find(new BasicDBObject("stationId",stationId));
		  if(iterable.iterator().hasNext()){
			 return  iterable.iterator().next().getInteger("icSum");
		  }
		  else return 0;
	}
	public static List<BasicDBObject> getStaFromAnaly(){

		  DBCursor iterable =MongoDBAssis.getDb().getCollection("stationAnalysis")
					.find(new BasicDBObject(),new BasicDBObject("stationId",1).append("_id", 0).append("stationName", 1)).sort(new BasicDBObject("icSum",-1));
			List<BasicDBObject> result = new ArrayList<BasicDBObject>();
			while(iterable.hasNext()){
				result.add((BasicDBObject) iterable.next());
			}
			return result;	
	}
	public int  getBusNum(){
		List<Document> list= MongoDBAssis.getDb().getCollection("gps_11_10_IC").distinct("segmentId");
		return list.size();
	}
	 public static void main(String[] args){
		Station s =new Station();
		s.insert();
	 }
}
