package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;

public class SegmentStationSequence {
	private String startTime;
	private String endTime;
	private int model =0;//0 dayModel; 1 
	public SegmentStationSequence(){
		
	}
	public SegmentStationSequence(String startTime,String endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTimeString) {
		this.startTime = startTimeString;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTimeString) {
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
//			System.out.println(segmentId);
//			System.out.println(start);
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
		if(list==null) return null;
		List<Integer> icList = (List<Integer>) list.get(0).get("trafficList");
		List<Integer> timeList = (List<Integer>) list.get(0).get("timeList");
		String startTime = list.get(0).getString("startTime");
		String endTime = list.get(0).getString("endTime");
		int j=0,sum=0;
		if(icList==null) return null;
		for(int i=0;i<timeList.size();i++){
			if(timeList.get(i)/mod ==j){
				sum+=icList.get(i);
				
			}else{
				result.add(sum);//System.out.println(j);
				sum=0;
				
				while(++j<timeList.get(i)/mod){
					result.add(0);
					//System.out.println(j);
				}
				
				j=timeList.get(i)/mod;
				sum+=icList.get(i);
			}
		}
		if(timeList.get(timeList.size()-1)/mod ==j) result.add(sum);
		//System.out.println("final "+j);
		while(++j<=Time.getInterBtwTime(startTime, endTime)/mod){
			result.add(0);
		}
		//System.out.println("segmentIC size "+ j--);
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
	public Boolean hasData(int segmentId,String stationId,String startTime,String endTime,int mod){
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
			if(find(segmentId,stationId, start, end) ==null){
				return false;
			}
		}
		return true;
	}
	public boolean hasWorkdayData(int segmentId,String stationId,String s, String e,int mod){
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
		
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			if(find(segmentId,stationId, start, end) ==null){
				return false;
			}
			//segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
//			//System.out.println(segmentId);
//			System.out.println(start);
//			System.out.println(start);
//			System.out.println(end);
								
		}
		if(find(segmentId, stationId, "2015-11-16 "+s, "2015-11-16 "+e)==null)
			return false;
		
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
			if(find(segmentId,stationId, start, end) ==null){
				return false;
			}
								
		}
		
		return true;
		//saveToFile("icArray", segmentIc);
	}
	public List<Integer> findWorkDayProcess(int segmentId,String stationId,String s, String e,int mod){
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
		
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
//			//System.out.println(segmentId);
//			System.out.println(start);
			//System.out.println(start);
			//System.out.println(end);
								
		}
		segmentIc.addAll(segmentIc(find(segmentId, stationId, "2015-11-16 "+s, "2015-11-16 "+e),mod));
		
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
			segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
//			System.out.println(segmentId);
//			System.out.println(start);
			//System.out.println(start);
			//System.out.println(end);
								
		}
		
		//System.out.println(segmentId+" "+s+" "+ e);
		//System.out.println(segmentIc);
		return segmentIc;
		//saveToFile("icArray", segmentIc);
	}
	
	public List<Integer> findAllDayProcess(int segmentId,String stationId,String s, String e,int mod){
		String startTime = "2015-11-10 "+s;
		String endTime = "2015-11-16 "+e;
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
			segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
//			//System.out.println(segmentId);
//			System.out.println(start);
			//System.out.println(start);
			//System.out.println(end);
								
		}
		//segmentIc.addAll(segmentIc(find(segmentId, stationId, "2015-11-16 "+s, "2015-11-16 "+e),mod));
		
		startTime = "2015-12-07 "+s;
		 endTime = "2015-12-13 "+e;
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
			segmentIc.addAll(segmentIc(find(segmentId,stationId, start, end),mod));
//			System.out.println(segmentId);
//			System.out.println(start);
			//System.out.println(start);
			//System.out.println(end);
								
		}
		
		//System.out.println(segmentId+" "+s+" "+ e);
		//System.out.println(segmentIc);
		return segmentIc;
		//saveToFile("icArray", segmentIc);
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
		//System.out.println("find process ");
		List<Integer> segmentIc = new ArrayList<Integer>();	
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			
			List<Integer> segmentResult =segmentIc(find(segmentId,stationId, start, end),mod);
			if(segmentResult!=null) segmentIc.addAll(segmentResult);
//			System.out.println(segmentId);
//			System.out.println(start);
			//System.out.println(start);
			//System.out.println(end);
								
		}
		//System.out.println(segmentId+" "+startTime+" "+ endTime);
		//System.out.println(segmentIc);
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
		DBCursor iterable=MongoDBAssis.getDb().getCollection("segmentStation").find(
				new BasicDBObject("segmentId",segmentId).append("stationId",stationId).append("sequenceTraffic",new BasicDBObject("$elemMatch",
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
	
	
	public List<String> getStartEndTime(int segmentId, String stationId,int mod){
		FindIterable< Document>  iterable = MongoDBAssis.getMongoDatabase().getCollection("segmentStation").find(new Document("segmentId",segmentId).append("stationId", stationId));
		MongoCursor<Document> cursor=iterable.iterator();
		String start="",end="";
		while(cursor.hasNext()){
			Document doc = cursor.next();
			List<Document> list = (List<Document>) doc.get("sequenceTraffic");
			if(list.size() !=0){
				start = dealStartTime(list.get(0).getString("startTime"), mod);
				end = dealEndTime(list.get(0).getString("endTime"), mod);
			}
			for(int i=1;i<list.size();i++){
				String s = list.get(i).getString("startTime");
				String e = list.get(i).getString("endTime");
				s=dealStartTime(s, mod);
				e = dealEndTime(e, mod);
				if(start.compareTo(s) <0){
					start = s;
				}
				if(end.compareTo(e)>0){
					end = e;
				}
			}
		}
		List<String> list = new ArrayList<String>();
		list.add(start);
		list.add(end);
		return list;
	}
	public String dealStartTime(String startTime,int mod){
		startTime = Time.add(startTime, mod);
		return startTime.substring(11,15)+"0:00";
	}
	public String dealEndTime(String endTime,int mod){
		endTime = Time.add(endTime, -mod);
		return endTime.substring(11,15)+"0:00";
	}
	@SuppressWarnings("unchecked")
	public List<Integer> getStationIcNum(int segmentId,String stationId){
		FindIterable<Document> iterable = MongoDBAssis.getMongoDatabase().getCollection("segmentStation").find(
				new Document("segmentId",segmentId).append("stationId",stationId));
		MongoCursor<Document> cursor=iterable.iterator();
		int timeLength=0;
		int sum =0;
		List<Integer> result= new ArrayList<Integer>();
		while(cursor.hasNext()){
			List<Document> sequenceList = new ArrayList<Document>();
			sequenceList = (List<Document>) cursor.next().get("sequenceTraffic");
			if(sequenceList ==null) continue;
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
	 * 对segmentStation数据表进行统计分析，并保存到segmentStationAnalysis表中
	 */
	public void getStationIcProcess(){
		SegmentStation segsta = new SegmentStation();
		List<Document> segstaList = segsta.getSegmentStaInfo();
		List<Document> list = new ArrayList<Document>();
		for(int i=0 ;i<segstaList.size();i++){
			int segmentId = segstaList.get(i).getInteger("segmentId");
			String stationIdString =segstaList.get(i).getString("stationId");
			String stationName =segstaList.get(i).getString("stationName");
			List<Integer> ic_time=getStationIcNum(segmentId,stationIdString);
			Document document = new Document("segmentId",segmentId).append("stationId", stationIdString).append("stationName", stationName)
					.append("icSum", ic_time.get(0)).append("runTime", ic_time.get(1)).append("average", (double)ic_time.get(0)*60/ic_time.get(1));
			list.add(document);
		}
		MongoDBAssis.getMongoDatabase().getCollection("segmentstationAnalysis").insertMany(list);
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
	
	public static List<Double> divide(List<Integer> list,int mode){
		double []p =new double[ArrayHelper.getMax(list)/mode+1];
		for(int i=0;i<list.size();i++){
			p[list.get(i)/mode]+=1;
		}
		List<Double> result = new ArrayList<Double>();
		for(int i=0;i<p.length;i++){
			p[i]/=list.size();
			result.add(p[i]*100);
		}
		return result;
	}
	public static void main(String []args){
//		SegmentStation  segmentStation = new SegmentStation();
//		segmentStation.getSegmentSta();
//		segmentStation.insert();
//		String startTime = "2015-12-07 00:00:00";
//		String endTime = "2015-12-13 23:59:59";
//		SegmentStationIcArray.setCollectionName("gps_12_07_IC");
//		SegmentStationSequence s = new SegmentStationSequence(startTime, endTime);
//		s.process();
//		
//		startTime = "2015-11-10 00:00:00";
//		 endTime = "2015-11-16 23:59:59";
//		SegmentStationIcArray.setCollectionName("gps_11_10_IC");
//		 s = new SegmentStationSequence(startTime, endTime);
//		s.process();
//		s.getStationIcProcess();
		
//		String startTime = "2015-12-08 06:30:00", endTime = "2015-12-08 09:29:59";
////		SegmentStationSequence s = new SegmentStationSequence(startTime, endTime);
////		s.findProcess(35621447, "12111300000000045252", startTime, endTime, 30*60);
		int segmentId=35632502;
		String stationId="12111300000000045323";
		SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println(sequence.getStartEndTime(segmentId, stationId, 10*60));
		String start ="06:30:00", end ="09:59:59";
		String startTime = "2015-12-09 06:30:00", endTime = "2015-12-09 18:59:59";
//		SegmentStationSequence sequence = new SegmentStationSequence();
//		List<Integer> list=sequence.findProcess(segmentId, stationId,startTime , endTime,  30*60);
//		sequence.saveToFile("Array", list);
//		System.out.println(list);
//		list=sequence.findAllDayProcess(segmentId, stationId, start, end,  20*60);
//		sequence.saveToFile("icArray", list);
//		System.out.println(list);
//		list=sequence.findAllDayProcess(segmentId, stationId, start, end,  30*60);
//		sequence.saveToFile("icArray12", list);
//		System.out.println(list);
//		list=sequence.findAllDayProcess(segmentId, stationId, start, end,  60*60);
//		sequence.saveToFile("icArray12_60", list);
//		System.out.println(list);
		//		sequence.findProcess(35633102, "12111300000000045252", startTime, endTime,10*60);35616250, 35647249
//		List<Integer> list=sequence.findWorkDayProcess(35632502, "12111300000000045323", start, end, 30*60);
//		System.out.println(list);
//		System.out.println(divide(list, 5));
		
		Station st = new Station();
		//StationSequence sequence = new StationSequence();
		SegmentStation  segSta = new SegmentStation();

		List<BasicDBObject> segstalist=segSta.getSegStaFromAnaly();
		
	
		
		
		for(int i =0;i<100;i++){
			int seg =segstalist.get(i).getInt("segmentId") ;
			String sta =segstalist.get(i).getString("stationId");
			if(!sequence.hasWorkdayData(seg, sta, start, end, 15*60)) continue;
			if(QueryBls.getSameStation(MongoDBAssis.getDb(), seg, sta).size() ==0) continue;
			List<Integer> list=sequence.findWorkDayProcess(seg, sta, start, end, 15*60);
			//System.out.println(list);
			System.out.print(segstalist.get(i).getString("stationName")+" ");
			List<Double> list2=divide(list, 4);
			for(int j =0; j<list2.size();j++){
				System.out.printf("%.4f",list2.get(j));
				System.out.print(" ");
			}
			System.out.println();
		}
		
		
	}
	
}
