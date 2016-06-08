package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mongodb.MongoDBAssis;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

public class SegmentStationSequence {
	private String startTime;
	private String endTime;
	public SegmentStationSequence(){
		
	}
	public SegmentStationSequence(String startTime,String endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public String getStartTimeString() {
		return startTime;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTime = startTimeString;
	}

	public String getEndTimeString() {
		return endTime;
	}

	public void setEndTimeString(String endTimeString) {
		this.endTime = endTimeString;
	}
	/**
	 * 向sementStation表中填充数据的过程
	 */
	public void process(){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		SegmentStation	segmentStation  = new SegmentStation();
		
		List<BasicDBObject> segmentStationList = segmentStation.getSegmentStationList();
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			for(int j =0 ;j<segmentStationList.size();j++){
				insertSet(segmentStationList.get(j), start, end);
			}
			System.out.println(start);
			System.out.println(end);
								
		}
	}
	/**
	 * 向数组插入时间序列
	 * @param object	station
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 */
	public void insertSet(BasicDBObject object,String startTime,String endTime){
		BasicDBObject obj =new BasicDBObject();
		SegmentStationIcArray segmentStationIcArray= new SegmentStationIcArray(object.getInt("segmentId"),object.getString("stationId"), startTime, endTime);
		List<Document> icArray  = segmentStationIcArray.getSegmentStationIcArray();
		if(icArray==null) return;
		obj.append("startTime", icArray.get(0).getString("arriveTime"))
		.append("endTime", icArray.get(icArray.size()-1).getString("leaveTime"));
		//System.out.println("insert "+object.getString("stationId"));
	    List<List<Integer>> sequenceIcList=segmentStationIcArray.sequenceIc(icArray);
	    	obj.append("timeList", sequenceIcList.get(0)).append("trafficList",sequenceIcList. get(1));
		MongoDBAssis.getDb().getCollection("segmentStation").update(new BasicDBObject("_id",object.getObjectId("_id")),
				new BasicDBObject("$addToSet",new BasicDBObject("sequenceTraffic",obj)));
	}
	
	
	/**
	 * 按天的粒度对站点进行查询，归类处理
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 * @param mod 时间间隔
	 * @return List<Integer> 按时间间隔归类好的时间序列
	 */
	public List<Integer> findBydayProcess(int segmentId,String stationId,String startTime,String endTime,int mod){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		SegmentStation segmentStation = new SegmentStation();
		List<Integer> segmentIc = new ArrayList<Integer>();	
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			segmentIc.addAll(segmentIc(findByday(segmentId,stationId, start, end),mod));
			//System.out.println(start);
			//System.out.println(end);
								
		}
		//System.out.println(segmentIc);
		//System.out.println(segmentIc.size());
		//saveToFile("icArray", segmentIc);
		return segmentIc;
		
	}
	/**
	 * 对获得序列化数据进行归类处理
	 * @param list
	 * @param mod
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> segmentIc(List<BasicDBObject> list,int mod){
		List<Integer> result = new ArrayList<Integer>();
		List<Integer> icList = (List<Integer>) list.get(0).get("trafficList");
		List<Integer> timeList = (List<Integer>) list.get(0).get("timeList");
		String startTime = list.get(0).getString("startTime");
		String endTime = list.get(0).getString("endTime");
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
		//System.out.println(result.size());
		return result;
	}
	
	/**
	 * 按天查询，获得线路站点在某一天的数据
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BasicDBObject> findByday(int segmentId,String stationId,String startTime,String endTime){
		DBCursor iterable=MongoDBAssis.getDb().getCollection("segmentStation").find(
				new BasicDBObject("segmentId",segmentId).append("stationId",stationId).append("sequenceTraffic",new BasicDBObject("$elemMatch",
						new BasicDBObject("startTime",new BasicDBObject("$gte",startTime)).append("endTime", new BasicDBObject("$lte",endTime))))
				,new BasicDBObject("sequenceTraffic.$",1));
		BasicDBObject object = (BasicDBObject) iterable.next();
		List<BasicDBObject> list = (List<BasicDBObject>) object.get("sequenceTraffic");
		//System.out.println(object);
		return list;
		
	}
	/**
	 * 按时间段查询线路站点的数据
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 * @param mod
	 * @return
	 */
	public List<Integer> findProcess(int segmentId,String stationId,String startTime,String endTime,int mod){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		
		List<Integer> segmentIc = new ArrayList<Integer>();	
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
			System.out.println(start);
			System.out.println(end);
								
		}
		System.out.println(segmentIc);
		return segmentIc;
		//saveToFile("icArray", segmentIc);
	}
	/**
	 * 获得指定时间段的数据
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 */
	@SuppressWarnings("unchecked")
	public List<BasicDBObject> find(int segmentId,String stationId,String startTime,String endTime){
		DBCursor iterable=MongoDBAssis.getDb().getCollection("station").find(
				new BasicDBObject("segmentId",segmentId).append("stationId",stationId).append("sequenceTraffic",new BasicDBObject("$elemMatch",
						new BasicDBObject("startTime",new BasicDBObject("$lte",startTime)).append("endTime", new BasicDBObject("$gte",endTime))))
				,new BasicDBObject("sequenceTraffic.$",1));
		BasicDBObject object = (BasicDBObject) iterable.next();
		List<BasicDBObject> list = (List<BasicDBObject>) object.get("sequenceTraffic");
		//System.out.println(object);
		List<Integer> icList = (List<Integer>) list.get(0).get("trafficList");
		List<Integer> timeList = (List<Integer>) list.get(0).get("timeList");
		String start= list.get(0).getString("startTime");
		String end= list.get(0).getString("endTime");
		int beginInter = Time.getInterBtwTime(start, startTime);
		int endInter = Time.getInterBtwTime(start, endTime);
		int startLoc=0,endLoc=0;
		for(int i=0 ;i <timeList.size();i++){
			if(timeList.get(i)>=beginInter){
				startLoc=i;
				break;
			}
		}
		for(int i=timeList.size()-1 ;i >=0;i--){
			if(timeList.get(i)<=endInter){
				endLoc=i+1;break;
			}
		}
		icList = icList.subList(startLoc, endLoc);
		timeList= timeList.subList(startLoc, endLoc);
		System.out.println(timeList);
		System.out.println(icList);
		dealTimeList(timeList, beginInter);
		BasicDBObject resultBasicDBObject  = new BasicDBObject().append("startTime", startTime).append("endTime", endTime)
				.append("timeList", timeList).append("trafficList", icList);
		list.set(0, resultBasicDBObject);
		return list;
		//System.out.println(timeList);
	}
	
	public void dealTimeList(List<Integer> timeList,int inter){
		for(int i=0;i<timeList.size();i++){
			int time=timeList.get(i)-inter;
			timeList.set(i, time);
		}
	}
	/**
	 * 获得某个字段的总数
	 * @param collectionName 表名
	 * @param filedName 字段名
	 * @return int类型
	 */
	public int getSum(String collectionName,String filedName){
		FindIterable<Document> iterable=MongoDBAssis.getMongoDatabase().getCollection(collectionName).find();
		MongoCursor<Document> cursor=iterable.iterator();
		int sum=0;
		while(cursor.hasNext()){
			sum+=cursor.next().getInteger(filedName);
		}
		return sum;
	}
	
	public void saveToFile(String fileName,List<Integer> array) {
		File file = new File(fileName);
		FileOutputStream outputStream;
		try {
			outputStream = new FileOutputStream(file);
			String string=array.toString().substring(1, array.toString().length()-1);
			outputStream.write(string.getBytes());
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void main(String []args){
		String startTime = "2015-12-07 00:00:00";
		String endTime = "2015-12-13 23:59:59";
		SegmentStationIcArray.setCollectionName("gps_12_07_IC");
		SegmentStationSequence s = new SegmentStationSequence(startTime, endTime);
		s.process();
		
		startTime = "2015-11-10 00:00:00";
		 endTime = "2015-11-16 23:59:59";
		SegmentStationIcArray.setCollectionName("gps_11_10_IC");
		 s = new SegmentStationSequence(startTime, endTime);
		s.process();
	}
	
}
