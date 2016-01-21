/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:QueryGpsST.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月19日 下午8:29:13
 */
package mongodb;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import util.Time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.StringTokenizer;
/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月19日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class GpsIntegrate {
	private static String busProName ="busproduct";
	private static final DB db = MongoDBCoonnection.getInstance().getDB();
	/**
	 * 
	 * 方法说明  Gps_st数据融合
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月20日
	 * @param mongodb
	 * @param collectionName
	 * @param beginTime
	 * @param endTime
	 * @return	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static ArrayList<Document> getStation(MongoDatabase mongodb,String collectionName,String beginTime,String endTime){
		ArrayList<Document> array = new ArrayList<Document>();
		MongoCollection<Document> collection = mongodb.getCollection(collectionName);
		ArrayList<Document> list = new ArrayList<Document>();
		ArrayList<Document> orList = new ArrayList<Document>();
		list.add(new Document("arrivalTime",new Document("$gt",beginTime)));
		list.add(new Document("arrivalTime",new Document("$lte",endTime)));
		//查询三条路线
//		orList.add(new Document("subrouteNo","302"));
//		orList.add(new Document("subrouteNo","33"));
//		orList.add(new Document("subrouteNo","2"));orList.add(new Document("subrouteNo","19"));
//		list.add(new Document("$or",orList));
		
		
		
		long st = System.currentTimeMillis();
		 FindIterable<Document> iter= collection.find(new Document("$and",list)).sort(new Document("productID",1).append("arrivalTime", 1));
		
		final ArrayList<Document> arr = new ArrayList<Document>();
		iter.forEach(new Block<Document>(){
			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				//System.out.println(arg0);
				arr.add(arg0);
			}			
		});System.out.println("query time:"+(System.currentTimeMillis()-st));
		System.out.println("Array size"+arr.size());
		Document predoc=null,doc=null,nextdoc =null;
		if(arr.size() ==0) return arr;
		int isDeal= 0;  
		for(int i=0;i<arr.size()-1;i++){
			if(isSameStation(arr.get(i),arr.get(i+1))){
				
				System.out.println(true);
				if(arr.get(i).getString("leaveFlag").equals("1") && arr.get(i+1).getString("leaveFlag").equals("2")){
					documentAdd1(array, arr, i, i+1);
					//array.add(union(arr.get(i),arr.get(i+1)));
					isDeal = 2;
				}else {
					if(isDeal==0) isDeal = 1;
				}
					
			}else{
				//if(i+2 < arr.size()&&isSameStation(arr.get(i+1), arr.get(i+2))) continue;
				if(i>=1&&isSameStation(arr.get(i), arr.get(i-1))&&isDeal==2) continue;
				System.out.println("false"+i);
				if(arr.get(i).getString("leaveFlag").equals("1")) {
					documentAdd(array, arr, i, 1);
					//array.add(union(arr.get(i),1));
					isDeal = 0;
				}
				if(arr.get(i).getString("leaveFlag").equals("2")) {
					documentAdd(array, arr, i, 2);
					//array.add(union(arr.get(i),2));
					isDeal = 0;
				}
			}
		}
		
		if(arr.size()>2&&isSameStation(arr.get(arr.size()-2), arr.get(arr.size()-1))&&isDeal==2){}
		else{
				
			if(arr.get(arr.size()-1).getString("leaveFlag").equals("1")) 
				documentAdd(array, arr,arr.size()-1, 1);
				//array.add(union(arr.get(arr.size()-1),1));
			if(arr.get(arr.size()-1).getString("leaveFlag").equals("2")) 
				documentAdd(array, arr,arr.size()-1, 2);
				//array.add(union(arr.get(arr.size()-1),2));
		}
		
//		if(cursor.hasNext()) predoc = cursor.next();
//		if(predoc.getString("busType") == "2") 
//		while(cursor.hasNext()){
//			nextdoc = cursor.next();
//		     if(predoc.getString("busType") == "1" && nextdoc.getString("busType") == "2"){
//		    	 doc = convert(predoc,nextdoc);
//		    	 array.add(doc);
//		     }
//		     predoc = nextdoc;
//		}
		return array;
	}

	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @return	返回值说明
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static String getBusProName() {
		return busProName;
	}

	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @param	参数名	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void setBusProName(String busProName) {
		GpsIntegrate.busProName = busProName;
	}

	/**
	 * 
	 * 方法说明 1、2时添加文档
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月24日
	 * @param getArr
	 * @param arr
	 * @param loca
	 * @param loca1	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void documentAdd1(ArrayList<Document> getArr,ArrayList<Document> arr,int loca,int loca1){
		//long start =System.currentTimeMillis();
		ArrayList<String> segment =new ArrayList<String>(); 
		//segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		//segment.add("35673101");segment.add("35583601");segment.add("35650929");segment.add("35689895");
		Document docu;
		Document doc = arr.get(loca), nextdoc = arr.get(loca1);
	    String busselfId = QueryBusno.getBusNo(db,busProName,(String)doc.get("productID"));
	   // long middle = System.currentTimeMillis();
	    //System.out.println("queryBusno Time:"+(middle-start));
	    ArrayList<String> array = QueryBls.getSgSd(db, doc.getString("subrouteNo"), doc.getString("dualSeriId"));
	    //System.out.println("querySegmentId Time:"+(System.currentTimeMillis()-middle));
	   // middle = System.currentTimeMillis();
	   // if(segment.contains(array.get(0))){
	    	 docu = new Document("segmentId",Integer.valueOf(array.get(0))).append("stationId", array.get(1))
	 	    	.append("busselfId", busselfId).append("sngSerialId",Integer.valueOf(array.get(2)))
	 	    	.append("arriveTime", doc.getString("arrivalTime")).append("leaveTime", nextdoc.getString("arrivalTime"));
	    	 System.out.println(docu);
	    	 getArr.add(docu);
	    	 //System.out.println("createDocu Time:"+(System.currentTimeMillis()-middle));
	   // }
	    //System.out.println("gpsIntegrate time:"+(System.currentTimeMillis()-start));
	   

	}
	/**
	 * 
	 * 方法说明  1 or 2 时添加文档
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月24日
	 * @param getArr
	 * @param arr
	 * @param loca
	 * @param flag	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void documentAdd(ArrayList<Document> getArr,ArrayList<Document> arr,int loca, int flag){
		long start =System.currentTimeMillis();
		ArrayList<String> segment =new ArrayList<String>(); 
		//segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		//segment.add("35673101");segment.add("35583601");segment.add("35650929");segment.add("35689895");
		Document docu;
		Document doc = arr.get(loca);
	    String busselfId = QueryBusno.getBusNo(db,busProName,(String)doc.get("productID"));
	    long middle = System.currentTimeMillis();
	    System.out.println("queryBusno Time:"+(middle-start));
	    ArrayList<String> array = QueryBls.getSgSd(db, doc.getString("subrouteNo"), doc.getString("dualSeriId"));
	    
	    System.out.println("querySegmentId Time:"+(System.currentTimeMillis()-middle));
	    middle = System.currentTimeMillis();
	    //if(segment.contains(array.get(0))){
	    	
	    	if(flag ==1 ) docu = new Document("segmentId",Integer.valueOf(array.get(0))).append("stationId", array.get(1))
	    		.append("busselfId", busselfId).append("sngSerialId",Integer.valueOf(array.get(2)))
	    		.append("arriveTime", doc.getString("arrivalTime")).append("leaveTime", Time.addTime(doc.getString("arrivalTime"),1));
	    	else docu = new Document("segmentId",Integer.valueOf(array.get(0))).append("stationId", array.get(1))
	    		.append("busselfId", busselfId).append("sngSerialId",Integer.valueOf(array.get(2)))
	    		.append("arriveTime",Time.reduceTime(doc.getString("arrivalTime"),1)).append("leaveTime",  doc.getString("arrivalTime"));
	    	System.out.println(docu);
	    	getArr.add(docu);
	    	System.out.println("createDocu Time:"+(System.currentTimeMillis()-middle));
	    //}	
	    System.out.println("gpsIntegrate time:"+(System.currentTimeMillis()-start));
	}
	/**
	 * 
	 * 方法说明  将整合的Gps到离站数据存入mongodb
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @param mongodb
	 * @param collectionName
	 * @param array	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void insertStation(MongoDatabase mongodb,String collectionName,ArrayList<Document> array){
		MongoCollection<Document> collection = mongodb.getCollection(collectionName);
		if(array.size() ==0) return;
//		for(int i=0;i<array.size();i++){
//			collection.insertOne(array.get(i));
//		}
		collection.insertMany(array);
		
	}
	public static boolean isSameStation(Document predoc, Document doc){
		if(predoc.getString("dualSeriId").toString().equals(doc.getString("dualSeriId").toString()) && 
			predoc.getString("productID").toString().equals(doc.getString("productID").toString())){
			return true;
		}else return false;
	}
//	public static Document union(Document doc,Document nextdoc){
//	    Document docu;
//	    String busselfId = QueryBusno.getBusNo(busProName,(String)doc.get("productID"));
//	    ArrayList<Integer> array = QuerySgOrRd.getSgRd(MongoDBCoonnection.getInstance().getMongoData(), doc.getString("subrouteNo"), doc.getString("dualSeriId"));
//	    docu = new Document("segmentId",array.get(0)).append("runDirection", array.get(1))
//	    	.append("dualserialId", Integer.valueOf(doc.getString("dualSeriId"))).append("busselfId", busselfId)
//	    	.append("arriveTime", doc.getString("arrivalTime")).append("leaveTime", nextdoc.getString("arrivalTime"));
//	    System.out.println(docu);
//	    return docu;
//	}
//	public static Document union(Document doc,int flag){
//	    Document docu;
//	    String busselfId = QueryBusno.getBusNo(busProName,(String)doc.get("productID"));
//	    ArrayList<Integer> array = QuerySgOrRd.getSgRd(MongoDBCoonnection.getInstance().getMongoData(), doc.getString("subrouteNo"), doc.getString("dualSeriId"));
//	    if(flag ==1 ) docu = new Document("segmentId",array.get(0)).append("runDirection", array.get(1))
//	    	.append("dualserialId", Integer.valueOf(doc.getString("dualSeriId"))).append("busselfId", busselfId)
//	    	.append("arriveTime", doc.getString("arrivalTime")).append("leaveTime", addTime(doc.getString("arrivalTime")));
//	    else docu = new Document("segmentId",array.get(0)).append("runDirection", array.get(1))
//	    	.append("dualserialId", Integer.valueOf(doc.getString("dualSeriId"))).append("busselfId", busselfId)
//	    	.append("arriveTime",reduceTime(doc.getString("arrivalTime"))).append("leaveTime",  doc.getString("arrivalTime"));
//	    System.out.println(docu);
//	    return docu;
//	}
	public static Document union(Document doc,Document nextdoc){
	    Document docu;
	    String busselfId = QueryBusno.getBusNo(db,busProName,(String)doc.get("productID"));
	    ArrayList<String> array = QueryBls.getSgSd(db, doc.getString("subrouteNo"), doc.getString("dualSeriId"));
	    docu = new Document("segmentId",Integer.valueOf(array.get(0))).append("stationId", array.get(1))
	    	.append("busselfId", busselfId)
	    	.append("arriveTime", doc.getString("arrivalTime")).append("leaveTime", nextdoc.getString("arrivalTime"));
	    System.out.println(docu);
	    return docu;
	}
	public static Document union(Document doc,int flag){
	    Document docu;
	    String busselfId = QueryBusno.getBusNo(db,busProName,(String)doc.get("productID"));
	    ArrayList<String> array = QueryBls.getSgSd(db, doc.getString("subrouteNo"), doc.getString("dualSeriId"));
	    if(flag ==1 ) docu = new Document("segmentId",Integer.valueOf(array.get(0))).append("stationId", array.get(1))
	    	.append("busselfId", busselfId)
	    	.append("arriveTime", doc.getString("arrivalTime")).append("leaveTime", Time.addTime(doc.getString("arrivalTime"),2));
	    else docu = new Document("segmentId",Integer.valueOf(array.get(0))).append("stationId", array.get(1))
	    	.append("busselfId", busselfId)
	    	.append("arriveTime",Time.reduceTime(doc.getString("arrivalTime"),2)).append("leaveTime",  doc.getString("arrivalTime"));
	    System.out.println(docu);
	    return docu;
	}
	
	public static void main(String []args) throws ParseException{
//		String time ="2015-10-09 12:59:30";
//		StringTokenizer str = new StringTokenizer(time, " ");
//		String str1=str.nextToken();
//		String str2=str.nextToken();
//		SimpleDateFormat formater1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		SimpleDateFormat formater2 = new SimpleDateFormat("yyyy-MM-dd");
//		System.out.println(formater1.format(formater2.parse(str1)));
//		System.out.println(str2);
//		System.out.println(addTime(time)); 
		MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteClient().getDatabase("czits_gps");
		ArrayList<Document> array=getStation(mongodb,"gpsSt","2015-11-10 06:00:00","2015-11-10 07:00:00");
		for(int i=0;i< array.size();i++){
			System.out.println(array.get(i).getString("busselfId"));
		}
		//insertStation(MongoDBCoonnection.getInstance().getMongoData(), "gpsintegrate", array);
	}

}
