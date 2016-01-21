/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:StationTraffic.java
 * Package Name:main
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月23日 下午4:24:23
 */
package main;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import mongodb.GpsComplete;
import mongodb.MongoDBCoonnection;
import mongodb.QueryTraffic;
import mongodb.GpsIntegrate;
import mysql.QueryIcSql;

import mongodb.UpdateTraffic;
import mysql.DBConnection;
import mysql.GetTraffic;
import util.Time;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月23日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class StationTraffic {
	private static MongoDatabase mongodb2 = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static final DB db2 = MongoDBCoonnection.getInstance().getRemoteDB2();
	private static String collectionName ="";
	/**
	 * 
	 * 方法说明 从Gps_st 表中获得整合的gps数据
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月29日
	 * @param startTime
	 * @param endTime	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void stationTraffic(String startTime,String endTime){
		String start= startTime;
		String end = Time.addHours(startTime, 1);
		int hoursNum = Time.disHours(startTime, endTime);
		MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteClient().getDatabase("czits_gps");
		MongoDatabase remoteMongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
		GpsIntegrate.insertStation(remoteMongodb, collectionName, 
			GpsIntegrate.getStation(mongodb, "gpsSt",start,end));
		
		for(int i=1;i < hoursNum;i++){
			start = Time.addHours(start, 1);
			end = Time.addHours(end, 1);
//			System.out.println(start);
//			System.out.println(end);
			GpsIntegrate.insertStation(remoteMongodb,collectionName, 
				GpsIntegrate.getStation(mongodb, "gpsSt",start,end));
		}			  
	}
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @return	返回值说明
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static String getCollectionName() {
		return collectionName;
	}
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @param	参数名	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void setCollectionName(String collectionName) {
		StationTraffic.collectionName = collectionName;
	}
	public static void updateTraffic(String startTime,String endTime){
		
		FindIterable<Document> iter = mongodb2.getCollection(collectionName).find(new Document("$and",
			Arrays.asList(new Document("arriveTime",new Document("$gt",startTime)),
				new Document("arriveTime",new Document("$lte",endTime)))));
		
		
		try {
				final Connection conn =DBConnection.getInstance().getConnection();
				iter.forEach(new Block<Document>(){
						
						@Override
					public void apply(Document arg0) {
						// TODO Auto-generated method stub
						//System.out.println(arg0);
							//long start =System.currentTimeMillis();
						String buselfId = arg0.getString("busselfId");
						int traffic=0;
						traffic = QueryTraffic.getTraffic(db2, arg0);
//						String sql1=QueryIcSql.getSql(arg0.getString("arriveTime"), arg0.getString("leaveTime"),"chargeicdetail");
						//String sql1 = QueryIcSql.getSql_table(sql_base, "chargeicdetail");
						//String sql2 = QueryIcSql.getSql_table(sql_base, "freeicdetail");
//						traffic+=GetTraffic.getTraffic(conn,sql1, buselfId);
						//traffic+=QueryIc.getTraffic(sql2, buselfId);
						//System.out.println("gpsIntegrate time:"+(System.currentTimeMillis()-start));
						System.out.println(traffic);
						ObjectId id = (ObjectId)arg0.get("_id");
						UpdateTraffic.update(db2,collectionName,id, traffic);
					}
						
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		
	}
	public static void update_process(String startTime,String endTime){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		end = Time.addDay(start, 1);
		for (int i=0;i<Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0){start = Time.addHours(start, 24);
			end = Time.addHours(end, 24);}
			System.out.println(start);
			System.out.println(end);
			updateTraffic(start, end);
			
		}
	}
	public static void main(String [] args){
		String startTime = "2015-11-10 00:00:00";String endTime = "2015-11-17 00:00:00";
//		updateTraffic(startTime, endTime);
//		update_process(startTime, endTime);
//		String startTime = "2015-11-10 00:00:00";String endTime = "2015-11-17 00:00:00";
		setCollectionName("gpsAll2");
		
		//stationTraffic(startTime, endTime);
		//GpsComplete.setCollectionName("gpsAll_12");
		//GpsComplete.complete(startTime, endTime);
		update_process(startTime, endTime);
		//		DealGps.deal_process(startTime, endTime);
//		SaveICArray.save("2015-11-10 06:30:00", "2015-11-16 09:00:00");
//		System.out.println();
	}

}
