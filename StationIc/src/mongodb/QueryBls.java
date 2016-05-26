/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:QuerySegmentId.java
 * Package Name:mongodb
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��20�� ����3:15:56
 */
package mongodb;

import java.util.ArrayList;
import java.util.Iterator;

import org.bson.Document;

import util.tuple;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

/**
  * ��˵��
  * @author dai.guohui
  * @version 1.0, 2015��11��20��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
  */

public class QueryBls {
	private static String collectionName ="BusLineStation";
	public static int getSegmentId(MongoDatabase mongodb,String subRouteId,String runDirection){
		MongoCollection<Document> collection = mongodb.getCollection(collectionName);
		ArrayList<Document> list = new ArrayList<Document>();
		list.add(new Document("subRouteInfoId",Integer.valueOf(subRouteId)));
		list.add(new Document("runDirection",Integer.valueOf(runDirection)));
		FindIterable<Document> iter =collection.find(new Document("$and",list));
		if(iter.iterator().hasNext()) return iter.iterator().next().getInteger("lineId");
		else return 0;
	}
	public static ArrayList<Integer> getSgRd(MongoDatabase mongodb,String subRouteId,String dualserialid){
		MongoCollection<Document> collection = mongodb.getCollection(collectionName);
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
	public static ArrayList<tuple> getSameStation(DB mongodb , int lineId, int sngSerialId){
		ArrayList<tuple> array = new ArrayList<tuple>();
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("lineId",lineId));
		list.add(new BasicDBObject("station.sngSerialId",sngSerialId));
		DBCursor cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		BasicDBObject doc = null;
		if(cursor.iterator().hasNext()) doc=(BasicDBObject) cursor.iterator().next();
		@SuppressWarnings("unchecked")
		ArrayList<BasicDBObject> lis = (ArrayList<BasicDBObject>)doc.get("station");
		
		String stationId=lis.get(0).getString("stationId");
		
		list.clear();

		list.add(new BasicDBObject("station.stationId",stationId));
		cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
		Iterator<DBObject> ite = cursor.iterator();
		
		while(ite.hasNext()){
			doc = (BasicDBObject) ite.next();
			@SuppressWarnings("unchecked")
			ArrayList<BasicDBObject> l = (ArrayList<BasicDBObject>)doc.get("station");
			tuple t = new tuple(doc.getInt("lineId"),l.get(0).getInt("sngSerialId"));
			array.add(t);
		}
		return array;
	}
	public static ArrayList<String> getSgSd(DB mongodb,String subRouteId,String dualserialid){
		ArrayList<String> array = new ArrayList<String>();
		ArrayList<BasicDBObject> list = new ArrayList<BasicDBObject>();
		list.add(new BasicDBObject("subRouteInfoId",Integer.valueOf(subRouteId)));
		list.add(new BasicDBObject("station.dualserialId",Integer.valueOf(dualserialid)));
		DBCursor cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("lineId",1).append("station.$",1));
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
		DBCursor cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("station.$.sngSerialId",1));
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
		
		FindIterable<Document> iter =mongodb.getCollection(collectionName).find(new Document("lineId",segmentId));
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
		DBCursor cursor =mongodb.getCollection(collectionName).find(new BasicDBObject("$and",list),new BasicDBObject("station.$",1));
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
		ArrayList<tuple> array= getSameStation(MongoDBCoonnection.getInstance().getDB(),1766503,1);
		System.out.println(array);
		
		//System.out.println(getStationNum(MongoDBCoonnection.getInstance().getMongoData(),35557702));
		//System.out.println(getStationId(MongoDBCoonnection.getInstance().getDB(),35557702,2));
	}

}
