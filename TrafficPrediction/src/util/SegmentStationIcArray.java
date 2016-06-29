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
	private static String collectionName="gps";
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
	
	
	
	public List<Document> getSegmentStationIcArray(String busselfId){
		MongoDatabase db  = MongoDBAssis.getMongoDatabase();
		FindIterable<Document> iter = db.getCollection(collectionName).find(new Document("$and", Arrays.asList(new Document(
				"segmentId", segmentId),new Document(
				"busselfId", busselfId),  new Document("arriveTime", new Document(
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
		if(trafficList == null )return null;
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
	public List<List<Integer>> sequenceArriveAndLeave(List<Document> trafficList){
		List<List<Integer>> result = new ArrayList<List<Integer>>();
		List<Integer> timeList = new ArrayList<Integer>();
		List<Integer> icList = new ArrayList<Integer>();
		if(trafficList == null )return null;
		if(trafficList.size()!=0){
			timeList.add(0);
			icList.add(trafficList.get(0).getInteger("traffic"));
		}
		String time = trafficList.get(0).getString("arriveTime");
		
		timeList.add( Time.getInterBtwTime(time, trafficList.get(0).getString("leaveTime")));
		for(int i =1; i< trafficList.size(); i++){
			int dis = Time.getInterBtwTime(time, trafficList.get(i).getString("arriveTime"));
			timeList.add(dis);
			dis = Time.getInterBtwTime(time, trafficList.get(i).getString("leaveTime"));
			timeList.add(dis);
			icList.add(trafficList.get(i).getInteger("traffic"));
		}
		
		result.add(timeList);
		result.add(icList);
		return result;
	}
	
	public List<Integer> icSequence(List<Document> icList){
	List<Integer> result = new ArrayList<Integer>();
		List<Integer> timeList = new ArrayList<Integer>();
		
		if(icList == null )return null;
		if(icList.size()!=0){
			//timeList.add(0);
			
		}
		String time = icList.get(0).getString("xfsj");
		
		timeList.add( Time.getInterBtwTime(time, icList.get(0).getString("xfsj")));
		for(int i =1; i< icList.size(); i++){
			int dis = Time.getInterBtwTime(time, icList.get(i).getString("xfsj"));
			timeList.add(dis);
			
		}
		
		
		
		return timeList;
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
	
	public static List<Document> getIc(String collection, String busselfId,
			String startTime, String endTime) {
		//如果时间已经校准，如若校准时间小于10分钟，则按校准时间查询IC卡数据
//		if(hasChecked(busselfId))
//		{
//			int inter =  -CheckTime2.get_inter(busselfId);
//			//String preGpsLeaveTime = getPreGps( busselfId, startTime);
//			if(Math.abs(inter) < 60*10)
//			 {
//				startTime  = Time.add(startTime,inter);
//				endTime  = Time.add(endTime, inter);
//			}
//		}
		String qcbh = "0"+busselfId;
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = MongoDBAssis.getMongoDatabase()
				.getCollection(collection)
				.find(new Document("$and", Arrays.asList(new Document("qcbh", qcbh), new Document("xfsj",
						new Document("$gte", startTime)), new Document("xfsj",
						new Document("$lt", endTime)))))
				.sort(new Document("xfsj", 1));
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		return array;
	}
	
	public static List<Document> clusterIc(List<Document> icList){
		int length = icList.size();List<Document> result = new ArrayList<Document>();
		if(length ==0) return result;
		int sum=0;
		int pre=1;
		
		//result.add(icList.get(0).append("num", pre));
		for(int i=1;i<length;i++){
			if(Time.getInterBtwTime(icList.get(i-1).getString("xfsj"), icList.get(i).getString("xfsj"))<30){
				pre++;
				continue;
			}else{
				
				result.add(icList.get(i-pre).append("num", pre));
				sum+=pre;
				pre=1;
				//System.out.println(icList.get(i));
			}
		}
		
			result.add(icList.get(length-pre).append("num", pre));
			sum+=pre;
		
		System.out.println("cluster sum "+(sum));
		System.out.println(result);
		return result;
	}
	public static void main(String[] args){
		String stationId="12111300000000036090";
		String startTime ="2015-11-10 07:52:00";
		String endTime ="2015-11-10 08:30:00";
		
//		SegmentStationIcArray s= new SegmentStationIcArray(stationId, startTime, endTime);
//		System.out.println(s.sequenceIc(s.getSegmentStationIcArray()).get(0));
//		System.out.println(s.sequenceIc(s.getSegmentStationIcArray()).get(1));
//		System.out.println(s.getSegmentIc(300));
		SegmentStationIcArray s = new SegmentStationIcArray(17372101, stationId, startTime, endTime);
		
		System.out.println(s.sequenceArriveAndLeave(s.getSegmentStationIcArray("38703")).get(0));
		System.out.println(s.icSequence(s.clusterIc(s.getIc("icData", "38703", startTime, endTime))));
		
		
	}
}
