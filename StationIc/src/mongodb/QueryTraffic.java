/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:StationIc
 * File Name:QueryTraffic.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月7日 下午1:22:56
 */
package mongodb;

import java.util.Arrays;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.client.MongoDatabase;

import util.Time;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年12月7日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class QueryTraffic {
	
	public static int getTraffic(DB db,Document doc){
		int traffic ;long start = System.currentTimeMillis();
		traffic=db.getCollection("icData").find(new BasicDBObject("$and",Arrays.asList(new BasicDBObject("qcbh","0"+doc.getString("busselfId")),
			new BasicDBObject("xfsj",new BasicDBObject("$gte",Time.reduceTime(doc.getString("arriveTime"),1))),
			new BasicDBObject("xfsj",new BasicDBObject("$lte",Time.addTime(doc.getString("leaveTime"),1)))))).count();
		System.out.println("mongodb query time : "+(System.currentTimeMillis()- start));return traffic;
	}
	public static int getTraffic(DB db,Document doc,int num){
		int traffic ;long start = System.currentTimeMillis();
		int pre_inter= 10;
		int next_inter= 20;
		traffic=db.getCollection("icData").find(new BasicDBObject("$and",Arrays.asList(new BasicDBObject("qcbh","0"+doc.getString("busselfId")),
			new BasicDBObject("xfsj",new BasicDBObject("$gte",Time.add(Time.reduce(doc.getString("arriveTime"),pre_inter),num) )),
			new BasicDBObject("xfsj",new BasicDBObject("$lte",Time.add(Time.add(doc.getString("leaveTime"),next_inter),num) ))))).count();
		//System.out.println("mongodb query time : "+(System.currentTimeMillis()- start));
		return traffic;
	}
	public static int getTraffic(DB db,Document doc,Document doc1,Document doc2,int num){
		int traffic ;long start = System.currentTimeMillis();
		int pre_inter= Time.getDisBtwDoc(doc1, doc)<120?Time.getDisBtwDoc(doc1, doc)/3:10;
		int next_inter= Time.getDisBtwDoc(doc, doc2)<180?Time.getDisBtwDoc(doc, doc2)/3:20;
		traffic=db.getCollection("icData").find(new BasicDBObject("$and",Arrays.asList(new BasicDBObject("qcbh","0"+doc.getString("busselfId")),
			new BasicDBObject("xfsj",new BasicDBObject("$gte",Time.add(Time.reduce(doc.getString("arriveTime"),pre_inter),num) )),
			new BasicDBObject("xfsj",new BasicDBObject("$lte",Time.add(Time.add(doc.getString("leaveTime"),next_inter),num) ))))).count();
		//System.out.println("mongodb query time : "+(System.currentTimeMillis()- start));
		return traffic;
	}
	public static int getTraffic_(DB db,Document doc,Document doc1,Document doc2,int num){
		int traffic=0 ;long start = System.currentTimeMillis();
		int pre_inter= Time.getDisBtwDoc(doc1, doc)<120?Time.getDisBtwDoc(doc1, doc)/3:10;
		int next_inter= Time.getDisBtwDoc(doc, doc2)<180?Time.getDisBtwDoc(doc, doc2)/3:20;
		DBCursor cursor = db.getCollection("icData").find(new BasicDBObject("$and",Arrays.asList(new BasicDBObject("qcbh","0"+doc.getString("busselfId")),
			new BasicDBObject("xfsj",new BasicDBObject("$gte",Time.add(Time.reduce(doc.getString("arriveTime"),pre_inter),num) )),
			new BasicDBObject("xfsj",new BasicDBObject("$lte",Time.add(Time.add(doc.getString("leaveTime"),next_inter),num) )))));
		
		while(cursor.hasNext())
		{
			System.out.println(cursor.next());
			traffic++;
		}
		//System.out.println("mongodb query time : "+(System.currentTimeMillis()- start));
		return traffic;
	}
	public static int getTrafficSum(DB db,String qcbh,String startTime,String endTime){
		int traffic ;long start = System.currentTimeMillis();
		traffic=db.getCollection("icData").find(new BasicDBObject("$and",Arrays.asList(new BasicDBObject("qcbh","0"+qcbh),
			new BasicDBObject("xfsj",new BasicDBObject("$gte",startTime )),
			new BasicDBObject("xfsj",new BasicDBObject("$lte",endTime))))).count();
		//System.out.println("mongodb query time : "+(System.currentTimeMillis()- start));
		return traffic;
	}
	public static void main(String []args){
		Document doc =new Document("segmentId", 1746635)
		.append("stationId", "12")
		.append("busselfId", "15084").append("sngSerialId", 2)
		.append("arriveTime", "2015-11-10 08:50:00").append("leaveTime", "2015-11-10 08:52:00");
		long start = System.currentTimeMillis();
		System.out.println(getTraffic(MongoDBCoonnection.getInstance().getRemoteDB2(), doc));
		System.out.println(getTraffic(MongoDBCoonnection.getInstance().getRemoteDB2(), doc));
		 doc =new Document("segmentId", 1746635)
				.append("stationId", "12")
				.append("busselfId", "15086").append("sngSerialId", 1)
				.append("arriveTime", "2015-11-10 08:49:30").append("leaveTime", "2015-11-10 08:52:30");
		System.out.println(getTrafficSum(MongoDBCoonnection.getInstance().getRemoteDB2(), "15086","2015-11-10 00:00:00","2015-11-11 00:00:00"));
		System.out.println("time : "+(System.currentTimeMillis()- start));
	}

}
