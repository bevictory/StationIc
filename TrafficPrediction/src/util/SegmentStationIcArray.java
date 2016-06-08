package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mongodb.MongoDBAssis;

import org.bson.Document;



import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class SegmentStationIcArray {
	private String startTime;
	private String endTime;
	private int segmentId;
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	private String stationId;
	private static String collectionName="gps_11_10_IC";
	public SegmentStationIcArray(int segmentId,String stationId, String startTime,String endTime){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public String getCollectionName() {
		return collectionName;
	}
	public static void setCollectionName(String collectionName1) {
		collectionName = collectionName1;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	/**
	 * 获得在指定时间段内，特定线路特定站点的数据
	 * @return
	 */
	public List<Document> getSegmentStationIcArray(){
		MongoDatabase db  = MongoDBAssis.getMongoDatabase();
		FindIterable<Document> iter = db.getCollection(collectionName).find(new Document("$and", Arrays.asList(new Document(
				"segmentId", segmentId),new Document(
				"stationId", stationId),  new Document("arriveTime", new Document(
				"$gte", startTime)), new Document("arriveTime",
				new Document("$lte", endTime))))).sort(new BasicDBObject("arriveTime",1));
		final List<Document> trafficList =new ArrayList<Document>();
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document t) {
				// TODO Auto-generated method stub
				trafficList.add(t);
			}
		});
		if(trafficList.size()==0) return null;
		return trafficList;
	}
	/**
	 * 序列化刷卡数据
	 * @param trafficList
	 * @return
	 */
	public List<List<Integer>> sequenceIc(List<Document> trafficList){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		List<Integer> timeList = new ArrayList<Integer>();
		List<Integer> icList = new ArrayList<Integer>();
		if(trafficList.size()!=0){
			timeList.add(0);
			icList.add(trafficList.get(0).getInteger("traffic"));
		}
		String time = trafficList.get(0).getString("arriveTime");
		for(int i =1; i< trafficList.size(); i++){
			int dis = Time.getInterBtwTime(time, trafficList.get(i).getString("arriveTime"));
			timeList.add(dis);
			icList.add(trafficList.get(i).getInteger("traffic"));
		}
		
		result.add(timeList);
		result.add(icList);
		return result;
	}
	
	public List<Integer> segmentIc(List<List<Integer>> sequenceIc,int mod){
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> timeList = sequenceIc.get(0);
		List<Integer> icList = sequenceIc.get(1);
		int j=0,sum=0;
		for(int i=0;i<timeList.size();i++){
			if(timeList.get(i)/mod ==j){
				sum+=icList.get(i);
				
			}else{
				result.add(sum);
				sum=0;
				while(++j<timeList.get(i)/mod){
					result.add(0);
					
				}
				
				j=timeList.get(i)/mod;
				sum+=icList.get(i);
			}
		}
		if(timeList.get(timeList.size()-1)/mod ==j) result.add(sum);
		while(++j<Time.getInterBtwTime(startTime, endTime)/mod){
			result.add(0);
		}
		
		return result;
	}
	public List<Integer> getSegmentIc(int mod){
		List<Integer> result=segmentIc(sequenceIc(getSegmentStationIcArray()), mod);
		return result;
	}
	public static void main(String[] args){
		String stationId="12111300000000036090";
		String startTime ="2015-11-10 06:30:00";
		String endTime ="2015-11-10 09:30:00";
		
//		SegmentStationIcArray s= new SegmentStationIcArray(stationId, startTime, endTime);
//		System.out.println(s.sequenceIc(s.getSegmentStationIcArray()).get(0));
//		System.out.println(s.sequenceIc(s.getSegmentStationIcArray()).get(1));
//		System.out.println(s.getSegmentIc(300));
		
	}
}
