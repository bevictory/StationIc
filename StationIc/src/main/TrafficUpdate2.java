/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:StationIc
 * File Name:TrafficUpdate.java
 * Package Name:main
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月14日 下午2:51:03
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

import mongodb.MongoDBCoonnection;
import mongodb.QueryTraffic;
import mongodb.UpdateTraffic;
import mysql.DBConnection;
import util.CheckTime2;
import util.Time;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年12月14日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class TrafficUpdate2 {
	private static MongoDatabase mongodb2 = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static final DB db2 = MongoDBCoonnection.getInstance().getRemoteDB2();
	private static String collectionName ="";
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年12月14日
	 * @return	返回值说明
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static String getCollectionName() {
		return collectionName;
	}
	/**
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年12月14日
	 * @param	参数名	参数说明
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void setCollectionName(String collectionName) {
		TrafficUpdate2.collectionName = collectionName;
	}
    public static void updateTraffic(String startTime,String endTime){
		
		FindIterable<Document> iter = mongodb2.getCollection(collectionName).find(new Document("$and",
			Arrays.asList(new Document("busselfId","38701"),new Document("arriveTime",new Document("$gt",startTime)),
				new Document("arriveTime",new Document("$lte",endTime))))).sort(new Document("busselfId",1).append("arriveTime", 1));
		final ArrayList<Document> array = new ArrayList<Document>();
		
		try {
				final Connection conn =DBConnection.getInstance().getConnection();
				iter.forEach(new Block<Document>(){
						
						@Override
					public void apply(Document arg0) {
						// TODO Auto-generated method stub
						//System.out.println(arg0);
							//long start =System.currentTimeMillis();
							array.add(arg0);
//						String buselfId = arg0.getString("busselfId");
//						int traffic=0;
//						traffic = QueryTraffic.getTraffic(db2, arg0);
////						String sql1=QueryIcSql.getSql(arg0.getString("arriveTime"), arg0.getString("leaveTime"),"chargeicdetail");
//						//String sql1 = QueryIcSql.getSql_table(sql_base, "chargeicdetail");
//						//String sql2 = QueryIcSql.getSql_table(sql_base, "freeicdetail");
////						traffic+=GetTraffic.getTraffic(conn,sql1, buselfId);
//						//traffic+=QueryIc.getTraffic(sql2, buselfId);
//						//System.out.println("gpsIntegrate time:"+(System.currentTimeMillis()-start));
//						System.out.println(traffic);
//						ObjectId id = (ObjectId)arg0.get("_id");
//						UpdateTraffic.update(db2,collectionName,id, traffic);
					}
						
				});
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		int isSameBus =0;
		int ord=0;
		String bus = "";
		for(int i =0;i<array.size();i++){
			
//			String busselfId = array.get(i).getString("busselfId");
//			int inter=CheckTime2.get_inter(busselfId);
//			
//			int traffic=0;ObjectId id = (ObjectId)array.get(i).get("_id");
//			traffic = QueryTraffic.getTraffic(db2, array.get(i),inter);
//			UpdateTraffic.update(db2, collectionName, id, traffic);
			String busselfId = array.get(i).getString("busselfId");
			if(isSameBus==1||bus.equals(busselfId)){
				ord++;
			}else{
				bus =busselfId;
				isSameBus =0;
				ord=0;
			}
			int inter=CheckTime2.get_inter(busselfId);
			int traffic=0;ObjectId id = (ObjectId)array.get(i).get("_id");
			System.out.println(array.get(i));
			if (i < array.size() - 1) {
				if (bus.equals(array.get(i + 1).getString("busselfId"))) {
					
					isSameBus =1;
					if (ord == 0) {
						
						traffic = QueryTraffic.getTraffic_(db2, array.get(i), array.get(i), array.get(i + 1), inter);
						UpdateTraffic.update(db2, collectionName, id, traffic);
					} else{
						traffic = QueryTraffic.getTraffic_(db2, array.get(i), array.get(i - 1), array.get(i + 1), inter);
						UpdateTraffic.update(db2, collectionName, id, traffic);
					}
					System.out.println("the same bus "+traffic);
				}else{
					isSameBus =0;
					if (ord == 0) {
						traffic = QueryTraffic.getTraffic_(db2, array.get(i), array.get(i), array.get(i), inter);
						UpdateTraffic.update(db2, collectionName, id, traffic);
					}else{
						traffic = QueryTraffic.getTraffic_(db2, array.get(i), array.get(i - 1), array.get(i ), inter);
						UpdateTraffic.update(db2, collectionName, id, traffic);
					}
					System.out.println(" "+traffic);
				}
			} else {
				 if(ord==0){
						traffic = QueryTraffic.getTraffic_(db2, array.get(i), array.get(i), array.get(i), inter);
						UpdateTraffic.update(db2, collectionName, id, traffic);
					}else{
						traffic = QueryTraffic.getTraffic_(db2, array.get(i), array.get(i - 1), array.get(i ), inter);
						UpdateTraffic.update(db2, collectionName, id, traffic);
					}
			}
		}
		
		
	}
   
	public static void update_process(String startTime,String endTime){
		int hoursNum = Time.disHours(startTime, endTime);
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		end = Time.addHours(start, 1);
		updateTraffic(start, end);
		for(int i=1;i < hoursNum;i++){
			start = Time.addHours(start, 1);
			end = Time.addHours(end, 1);
			System.out.println(start);
			System.out.println(end);
			updateTraffic(start, end);
		}
		
//		for (int i=0;i<Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
//			if(i>0){start = Time.addHours(start, 24);
//			end = Time.addHours(end, 24);}
//			System.out.println(start);
//			System.out.println(end);
//			updateTraffic(start, end);
//			
//		}
	}
	public static void main(String []args){
		String startTime = "2015-11-10 05:00:00";String endTime = "2015-11-11 10:00:00";
		setCollectionName("gpsAll2");
		//db2.getCollection(collectionName).rename("gps_fourBusLine_nocheck");
		update_process(startTime, endTime);
	}
	
}
