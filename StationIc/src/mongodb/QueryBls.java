/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:QuerySegmentId.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月20日 下午3:15:56
 */
package mongodb;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月20日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class QueryBls {
	public static int getSegmentId(MongoDatabase mongodb,String subRouteId,String runDirection){
		MongoCollection<Document> collection = mongodb.getCollection("buslinestation");
		ArrayList<Document> list = new ArrayList<Document>();
		list.add(new Document("subRouteInfoId",Integer.valueOf(subRouteId)));
		list.add(new Document("runDirection",Integer.valueOf(runDirection)));
		FindIterable<Document> iter =collection.find(new Document("$and",list));
		if(iter.iterator().hasNext()) return iter.iterator().next().getInteger("lineId");
		else return 0;
	}
	public static ArrayList<Integer> getSgRd(MongoDatabase mongodb,String subRouteId,String dualserialid){
		MongoCollection<Document> collection = mongodb.getCollection("buslinestation");
		ArrayList<Document> list = new ArrayList<Document>();ArrayList<Integer> array = new ArrayList<Integer>();
		list.add(new Document("subRouteInfoId",Integer.valueOf(subRouteId)));
		list.add(new Document("station.dualserialId",Integer.valueOf(dualserialid)));
		FindIterable<Document> iter =collection.find(new Document("$and",list));
		Document doc = null;
		if(iter.iterator().hasNext()) doc=iter.iterator().next();
		else {
			array.add(0);
			array.add(0);
			return array;
		}
		
		array.add(doc.getInteger("lineId"));
		array.add(doc.getInteger("runDirection"));
		return array;
	}
	public static ArrayList<String> getSgSd(DB mongodb,String subRouteId,String dualserialid){
		ArrayList<String> array = new ArrayList<String>();
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("subRouteInfoId",Integer.valueOf(subRouteId)));
		list.add(new BasicDBObject("station.dualserialId",Integer.valueOf(dualserialid)));
		DBCursor cursor =mongodb.getCollection("buslinestation").find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		BasicDBObject doc = null;
		
		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
		else {
			array.add("0");
			array.add("0");array.add("0");
			return array;
		}
		
		array.add(doc.get("lineId").toString());
		@SuppressWarnings("unchecked")
		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
		array.add(lis.get(0).getString("stationId"));array.add(lis.get(0).getString("sngSerialId"));
		return array;
//		ArrayList<String> array = new ArrayList<String>();
//		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
//		list.add(new BasicDBObject("subRouteInfoId",Integer.valueOf(subRouteId)));
//		list.add(new BasicDBObject("station.dualserialId",Integer.valueOf(dualserialid)));
//		DBCursor cursor =mongodb.getCollection("buslinestation").find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$.stationId",1));
//		BasicDBObject doc = null;
//		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
//		else {
//			array.add("0");
//			array.add("0");
//			return array;
//		}
//		
//		array.add(doc.get("lineId").toString());
//		@SuppressWarnings("unchecked")
//		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
//		array.add(lis.get(0).getString("stationId"));
//		return array;
	}
	public static int getSngSerialId(DB mongodb,int segmentId,String stationId){
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("lineId",segmentId));
		list.add(new BasicDBObject("station.stationId",stationId));
		DBCursor cursor =mongodb.getCollection("buslinestation").find(new BasicDBObject("$and",list),new BasicDBObject("station.$.sngSerialId",1));
		BasicDBObject doc = null;		
		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
		else {			
			return 0;
		}				
		@SuppressWarnings("unchecked")
		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
		return lis.get(0).getInt("sngSerialId");
		
	}
	public static int getStationNum(MongoDatabase mongodb,int segmentId){
		
		FindIterable<Document> iter =mongodb.getCollection("buslinestation").find(new Document("lineId",segmentId));
		Document doc = null;
		
		if(iter.iterator().hasNext()) doc=(Document) iter.iterator().next();
		else {
			
			return 0;
		}				
		@SuppressWarnings("unchecked")
		ArrayList<Document> lis = (ArrayList<Document>)doc.get("station");
		return lis.size();		
	}
	public static String getStationId(DB mongodb,int segmentId,int sngSerialId){
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("lineId",segmentId));
		list.add(new BasicDBObject("station.sngSerialId",sngSerialId));
		DBCursor cursor =mongodb.getCollection("buslinestation").find(new BasicDBObject("$and",list),new BasicDBObject("station.$",1));
		BasicDBObject doc = null;		
		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
		else {			
			return "0";
		}				
		@SuppressWarnings("unchecked")
		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
		return lis.get(0).getString("stationId");	
	}
	public static void main(String[] args){
		ArrayList<String> array= getSgSd(MongoDBCoonnection.getInstance().getDB(),"95","5");
		System.out.println(array.get(1));
		System.out.println(array.get(2));
		//System.out.println(getStationNum(MongoDBCoonnection.getInstance().getMongoData(),35557702));
		//System.out.println(getStationId(MongoDBCoonnection.getInstance().getDB(),35557702,2));
	}

}
