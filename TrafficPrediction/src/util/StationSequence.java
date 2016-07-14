package util;

import java.awt.peer.LightweightPeer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.SequenceInputStream;
import java.io.ObjectInputStream.GetField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.bson.Document;

import mongodb.MongoDBAssis;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class StationSequence {
	private String startTime;
	private String endTime;
	public StationSequence(){
		
	}
	public StationSequence(String startTime,String endTime){
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
	 * 根据一定时间段，按天对站点进行序列化处理
	 */
	public void process(){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		Station station = new Station();
		
		List<BasicDBObject> stationList = station.getStationIdList();
		
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			for(int j =0 ;j<stationList.size();j++){
				insertSet(stationList.get(j), start, end);
			}
			System.out.println(start);
			System.out.println(end);
								
		}
	}
	/**
	 * 向station表中的sequenceTraffic数组插入时间序列
	 * @param object	station
	 * @param startTime	开始时间
	 * @param endTime	结束时间
	 */
	public void insertSet(BasicDBObject object,String startTime,String endTime){
		BasicDBObject obj =new BasicDBObject();
		StationIcArray stationIcArray = new StationIcArray(object.getString("stationId"), startTime, endTime);
		List<Document> icArray  = stationIcArray.getStationIcArray();
		if(icArray==null) return;
		obj.append("startTime", icArray.get(0).getString("arriveTime"))
		.append("endTime", icArray.get(icArray.size()-1).getString("leaveTime"));
		//System.out.println("insert "+object.getString("stationId"));
	    List<List<Integer>> sequenceIcList=stationIcArray.sequenceIc(icArray);
	    	obj.append("timeList", sequenceIcList.get(0)).append("trafficList",sequenceIcList. get(1));
		MongoDBAssis.getDb().getCollection("station").update(new BasicDBObject("_id",object.getObjectId("_id")),
				new BasicDBObject("$addToSet",new BasicDBObject("sequenceTraffic",obj)));
	}
	/**
	 * 对不同站点进行统计分析的过程，统计站点的刷卡总量、时间长度以及平均，保存在stationAnalysis表中
	 */
	public void getStationIcProcess(){
		Station station = new Station();
		List<BasicDBObject> stationList = station.getStationInfoList();
		List<Document> list = new ArrayList<Document>();
		for(int i=0 ;i<stationList.size();i++){
			String stationIdString =stationList.get(i).getString("stationId");
			String stationName =stationList.get(i).getString("stationName");
			List<Integer> ic_time=getStationIcNum(stationIdString);
			Document document = new Document().append("stationId", stationIdString).append("stationName", stationName)
					.append("icSum", ic_time.get(0)).append("runTime", ic_time.get(1)).append("average", (double)ic_time.get(0)*60/ic_time.get(1));
			list.add(document);
		}
		MongoDBAssis.getMongoDatabase().getCollection("stationAnalysis").insertMany(list);
	}
	
	/**
	 * 从station表中获得站点的刷卡总数
	 * @param stationId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> getStationIcNum(String stationId){
		FindIterable<Document> iterable = MongoDBAssis.getMongoDatabase().getCollection("station").find(
				new Document("stationId",stationId));
		MongoCursor<Document> cursor=iterable.iterator();
		int timeLength=0;
		int sum =0;
		List<Integer> result= new ArrayList<Integer>();
		while(cursor.hasNext()){
			List<Document> sequenceList = new ArrayList<Document>();
			sequenceList = (List<Document>) cursor.next().get("sequenceTraffic");
			
			for(int i=0 ;i<sequenceList.size();i++){
				timeLength+=Time.getInterBtwTime(sequenceList.get(i).getString("startTime"), 
						sequenceList.get(i).getString("endTime"));
				List<Integer> icList = (List<Integer>) sequenceList.get(i).get("trafficList");
				for(int j=0;j<icList.size();j++){
					sum+=icList.get(j);
				}
			}
		}
		result.add(sum);
		result.add(timeLength);
		return result;
		
		
	}
	
	/**
	 * 按天的粒度对站点进行查询，归类处理
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 * @param mod 时间间隔
	 * @return List<Integer> 按时间间隔归类好的时间序列
	 */
	public List<Integer> findBydayProcess(String stationId,String startTime,String endTime,int mod){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		Station station = new Station();
		List<Integer> segmentIc = new ArrayList<Integer>();	
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			segmentIc.addAll(segmentIc(findByday(stationId, start, end),mod));
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
		while(++j<=Time.getInterBtwTime(startTime, endTime)/mod){
			result.add(0);
		}
		//System.out.println(result.size());
		return result;
	}
	
	/**
	 * 按天查询，获得站点在某一天的数据
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<BasicDBObject> findByday(String stationId,String startTime,String endTime){
		DBCursor iterable=MongoDBAssis.getDb().getCollection("station").find(
				new BasicDBObject("stationId",stationId).append("sequenceTraffic",new BasicDBObject("$elemMatch",
						new BasicDBObject("startTime",new BasicDBObject("$gte",startTime)).append("endTime", new BasicDBObject("$lte",endTime))))
				,new BasicDBObject("sequenceTraffic.$",1));
		BasicDBObject object = (BasicDBObject) iterable.next();
		List<BasicDBObject> list = (List<BasicDBObject>) object.get("sequenceTraffic");
		//System.out.println(object);
		return list;
		
	}
	/**
	 * 按时间段查询站点的数据
	 * @param stationId
	 * @param startTime
	 * @param endTime
	 * @param mod
	 * @return
	 */
	public List<Integer> findProcess(String stationId,String startTime,String endTime,int mod){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		Station station = new Station();
		List<Integer> segmentIc = new ArrayList<Integer>();	
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			segmentIc.addAll(segmentIc(find(stationId, start, end),mod));
//			System.out.println(start);
//			System.out.println(end);
								
		}
		//System.out.println(segmentIc);
		if(PredictDis.isPreDis) segmentIc=dis(segmentIc);
		return segmentIc;
		//saveToFile("icArray", segmentIc);
	}
	public Boolean hasData(String stationId,String startTime,String endTime,int mod){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		//System.out.println("find process ");
		List<Integer> segmentIc = new ArrayList<Integer>();	
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			if(find(stationId, start, end) ==null){
				return false;
			}
		}
		return true;
	}
	public boolean hasWorkdayData(String stationId,String s, String e,int mod){
		String startTime = "2015-11-10 "+s;
		String endTime = "2015-11-13 "+e;
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		//System.out.println("find process ");
		List<Integer> segmentIc = new ArrayList<Integer>();
		
//		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
//			if(i>0)
//			{
//				start = Time.addHours(start, 24);
//				end = Time.addHours(end, 24);
//			}
//			if(find(stationId, start, end) ==null){
//				return false;
//			}
//			//segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
////			//System.out.println(segmentId);
////			System.out.println(start);
////			System.out.println(start);
////			System.out.println(end);
//								
//		}
//		if(find( stationId, "2015-11-16 "+s, "2015-11-16 "+e)==null)
//			return false;;
		
		startTime = "2015-12-07 "+s;
		 endTime = "2015-12-10 "+e;
		 arrStart = Time.getDateTime(startTime);arrEnd = Time.getDateTime(endTime);
		 start=startTime;
		str =new StringTokenizer(endTime, " ");
		str.nextToken();
		
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		//System.out.println("find process ");
		
		
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			//segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
//			System.out.println(segmentId);
//			System.out.println(start);
			if(find(stationId, start, end) ==null){
				return false;
			}
								
		}
		
		return true;
		//saveToFile("icArray", segmentIc);
	}
	public List<Integer> findWorkDayProcess(String stationId,String s, String e,int mod){
		String startTime = "2015-11-10 "+s;
		String endTime = "2015-11-13 "+e;
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		//System.out.println("find process ");
		List<Integer> segmentIc = new ArrayList<Integer>();
		
//		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
//			if(i>0)
//			{
//				start = Time.addHours(start, 24);
//				end = Time.addHours(end, 24);
//			}
//			segmentIc.addAll(segmentIc(find(stationId, start, end),mod));
////			//System.out.println(segmentId);
////			System.out.println(start);
//			//System.out.println(start);
//			//System.out.println(end);
//								
//		}
//		segmentIc.addAll(segmentIc(find( stationId, "2015-11-16 "+s, "2015-11-16 "+e),mod));
		
		startTime = "2015-12-07 "+s;
		 endTime = "2015-12-10 "+e;
		 arrStart = Time.getDateTime(startTime);arrEnd = Time.getDateTime(endTime);
		 start=startTime;
		str =new StringTokenizer(endTime, " ");
		str.nextToken();
		
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		//System.out.println("find process ");
		
		
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			segmentIc.addAll(segmentIc(find(stationId, start, end),mod));
//			System.out.println(segmentId);
//			System.out.println(start);
			//System.out.println(start);
			//System.out.println(end);
								
		}
		
		//System.out.println(segmentId+" "+s+" "+ e);
		//System.out.println(segmentIc);
		if(PredictDis.isPreDis)segmentIc = dis(segmentIc);
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
	public List<BasicDBObject> find(String stationId,String startTime,String endTime){
		DBCursor iterable=MongoDBAssis.getDb().getCollection("station").find(
				new BasicDBObject("stationId",stationId).append("sequenceTraffic",new BasicDBObject("$elemMatch",
						new BasicDBObject("startTime",new BasicDBObject("$lte",startTime)).append("endTime", new BasicDBObject("$gte",endTime))))
				,new BasicDBObject("sequenceTraffic.$",1));
		if(!iterable.hasNext()) return null;
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
		//System.out.println(timeList);
		//System.out.println(icList);
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
	
	public static void getIcSumByHour(){
		String startTime = "2015-11-10 00:00:00";
		String endTime = "2015-11-16 23:59:59";
		Station station = new Station();
		
		List<BasicDBObject> stationList = station.getStationInfoList();
		
		
		StationSequence sequence = new StationSequence(startTime,endTime);
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			
			int disHours = Time.disHours(start, end);
			String start1= start,end1=end;
			for(int j=0;j<=disHours;j++){
				if(j>0){
					start1=Time.addHours(start1, 1);
					end1 =Time.addHours(start1, 1);
				}else end1 =Time.addHours(start1, 1);
				for(int k=0 ;k<stationList.size();k++){
					getStationByHour("gps_11_10_IC", stationList.get(k).getString("stationId"),stationList.get(k).getString("stationName"), start1, end1);
				}
				System.out.println(start1);
				System.out.println(end1);
			}
		}
		startTime = "2015-12-07 00:00:00";
		endTime = "2015-12-13 23:59:59";
		arrStart = Time.getDateTime(startTime);arrEnd = Time.getDateTime(endTime);
		start=startTime;
		str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			int disHours = Time.disHours(start, end);
			String start1= start,end1=end;
			for(int j=0;j<disHours;j++){
				if(j>0){
					start1=Time.addHours(start1, 1);
					end1 =Time.addHours(start1, 1);
				}else end1 =Time.addHours(start1, 1);
				for(int k=0 ;k<stationList.size();k++){
					getStationByHour("gps_12_07_IC", stationList.get(k).getString("stationId"),stationList.get(k).getString("stationName"), start1, end1);
				}
				System.out.println(start1);
				System.out.println(end1);
			}
		}
	}
	public static void getStationByHour(String collection, String stationId,String stationName,String startTime,String endTime){
		MongoDatabase db  = MongoDBAssis.getMongoDatabase();
		FindIterable<Document> iter = db.getCollection(collection).find(new Document("$and", Arrays.asList(new Document(
				"stationId", stationId),  new Document("arriveTime", new Document(
				"$gte", startTime)), new Document("arriveTime",
				new Document("$lte", endTime))))).sort(new BasicDBObject("arriveTime",1));
		int sum =0;
		MongoCursor<Document> cursor =iter.iterator();
		List<Document> list = new ArrayList<Document>();
		Document doc=null;
		while(cursor.hasNext()){
			
			doc=cursor.next();
			sum+=doc.getInteger("traffic");
			
		}
		System.out.println(sum);
		
		if(doc !=null) db.getCollection("stationIcByHour").insertOne(new Document("stationId",doc.getString("stationId"))
		.append("stationName", stationName).append("sum", sum).append("startTime", startTime).append("endTime", endTime));
		
	}
	@SuppressWarnings("unchecked")
	public static void getStationByHour(String collection, String startTime,String endTime){
		List<BasicDBObject> result=(List<BasicDBObject>) MongoDBAssis.getDb().getCollection(collection).group(new BasicDBObject("stationId",true),new BasicDBObject("arriveTime",
				new BasicDBObject("$gte",startTime)).append("arriveTime",new BasicDBObject("$lte",endTime) ), new BasicDBObject("count",0), 
				"function(cur,pre){count=pre.count+cur.traffic;}");
		System.out.println(result);
	}
	
	public static List<Double> divide(List<Integer> list,int mode){
		double []p =new double[ArrayHelper.getMax(list)/mode+1];
		//System.out.println(p.length);
		for(int i=0;i<list.size();i++){
			p[list.get(i)/mode]+=1;
		}
		List<Double> result = new ArrayList<Double>();
		for(int i=0;i<p.length;i++){
			p[i]/=list.size();
			result.add(p[i]*100);
		}
		//System.out.println(result.size());
		return result;
	}
	
	public static List<Integer> dis(List<Integer> list){
		double []p =new double[ArrayHelper.getMax(list)+1];
		//System.out.println(p.length);
		List<Integer> result = new ArrayList<Integer>();
		for(int i=1;i<list.size();i++){
			result.add(list.get(i)-list.get(i-1));
		}
		
		
		//System.out.println(result.size());
		return result;
	}
	
	public static void main(String []args){
//		Station station=new Station();
//		station.getStationId();
//		station.insert();
//		String startTime = "2015-11-10 00:00:00";
//		String endTime = "2015-11-16 23:59:59";
//		StationIcArray.setCollectionName("gps_11_10_IC");
//		StationSequence sequence = new StationSequence(startTime,endTime);
//		sequence.process();
//		 startTime = "2015-12-07 00:00:00";
//		 endTime = "2015-12-13 23:59:59";
//		StationIcArray.setCollectionName("gps_12_07_IC");
//		 sequence = new StationSequence(startTime,endTime);
//		sequence.process();
//		sequence.getStationIcProcess();
		
		
		
		StationSequence s = new StationSequence();
		//s.find("12111300000000045252",	 startTime, endTime);
		//List<Integer> list = s.findWorkDayProcess( "12111300000000045323", "06:30:00", "18:59:59",  10*60);
		//System.out.println(divide(list, 1));
//		s.saveToFile("icArray12_60",list);
		//s.findProcess("12111300000000045252", startTime, endTime, 5*60);
		//s.getStationIcProcess();
		//System.out.println(s.getSum());
		
		String startTime = "06:00:00", endTime = "09:59:59";
		String time1 =  "2015-12-13 06:30:00" ,time2 =  "2015-12-13 18:59:59";
//		MultiPreSS multiPreSS  = 
//				new MultiPreSS(35632502, "12111300000000045323", startTime, endTime,2,5, 30*60);
		
		List<Integer> list=s.findWorkDayProcess("12111300000000045323", startTime, endTime, 60*60);
		System.out.println(list);
		System.out.println(dis(list));
//		Station station = new Station();
//		StationSequence sequence = new StationSequence();
//		List<BasicDBObject> stalist=Station.getStaFromAnaly();
//		
//	
//		
//		
//		for(int i =0;i<100;i++){
//			//System.out.println("i "+i);
//			String sta =stalist.get(i).getString("stationId");
//			if(!sequence.hasWorkdayData( sta, startTime, endTime, 10*60)||!sequence.hasData(sta, time1, time2, 10*60)) continue;
//			if(StationInfo.getNear(sta, 1, 200).size() ==0) continue;
//			System.out.print(stalist.get(i).getString("stationName")+" ");
//			List<Integer> list=sequence.findWorkDayProcess(sta, startTime, endTime, 10*60);
//			List<Double> list2=divide(list, 6);
//			for(int j =0; j<list2.size();j++){
//				System.out.printf("%.4f",list2.get(j));
//				System.out.print(" ");
//			}
//			System.out.println();
//		}
		
	}

}
