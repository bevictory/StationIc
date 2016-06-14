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

public class StationIcArray {
	private String startTime;
	private String endTime;
	private String stationId;
	private static String collectionName="gps_11_10_IC";
	public StationIcArray(String stationId, String startTime,String endTime){
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
	 * 从gps_ic表中获得相应站点的以时间排序的数据列表
	 * @return List<Document> or Null
	 */
	public List<Document> getStationIcArray(){
		MongoDatabase db  = MongoDBAssis.getMongoDatabase();
		FindIterable<Document> iter = db.getCollection(collectionName).find(new Document("$and", Arrays.asList(new Document(
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
	 * 根据List<Document> 的信息，将数据进行序列化
	 * @param trafficList 站点的数据列表
	 * @return List<List<Integer>> 保存着序列化的客流量
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
	public void dealTimeList(List<Integer> timeList,int inter){
		for(int i=0;i<timeList.size();i++){
			int time=timeList.get(i)-inter;
			timeList.set(i, time);
		}
	}
	/**
	 * 将序列化的数据按一定时间间隔进行归类
	 * @param sequenceIc 序列化的时间序列
	 * @param mod 时间间隔
	 * @return List<Integer> 按一定时间间隔归类的数据
	 */
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
		List<Integer> result=segmentIc(sequenceIc(getStationIcArray()), mod);
		return result;
	}
	public static void main(String[] args){
		String stationId="12111300000000036090";
		String startTime ="2015-11-16 06:30:00";
		String endTime ="2015-11-16 09:30:00";
		
		StationIcArray s= new StationIcArray(stationId, startTime, endTime);
		System.out.println(s.sequenceIc(s.getStationIcArray()).get(0));
		System.out.println(s.sequenceIc(s.getStationIcArray()).get(1));
		System.out.println(s.getSegmentIc(1800));
		
	}
}
