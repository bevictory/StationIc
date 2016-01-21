/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:dealGps.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月26日 下午6:45:17
 */
package mongodb;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import mysql.DBConnection;
import mysql.GetTraffic;
import util.Time;

/**
 * 类说明
 * @author dai.guohui
 * @version 1.0, 2015年11月26日 每次修改后更新版本号，日期和修改内容
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class GpsComplete2 {
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getMongoData();
	private static MongoDatabase remoteMongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static DB db = MongoDBCoonnection.getInstance().getDB();
	private static DB remoteDb = MongoDBCoonnection.getInstance().getRemoteDB2();
	private static String collectionName = "gpsAll1";

	/**
	 * 方法说明
	 * @author dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @return 返回值说明
	 * @since [产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static String getCollectionName() {
		return collectionName;
	}

	/**
	 * 方法说明
	 * @author dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @param 参数名 参数说明
	 * @see [类,类#方法，类#成员]
	 * @since [产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static void setCollectionName(String collectionName) {
		GpsComplete2.collectionName = collectionName;
	}
	
	public static void complete(String startTime, String endTime) {
		FindIterable<Document> iter = remoteMongodb.getCollection(collectionName)
			.find(new Document("$and",
				Arrays.asList(new Document("arriveTime", new Document("$gt", startTime)),
					new Document("arriveTime", new Document("$lte", endTime)))))
			.sort(new Document("segmentId", 1).append("busselfId", 1).append("arriveTime", 1));
		final ArrayList<Document> array = new ArrayList<Document>();
		Connection conn = null;
		try {
			conn = DBConnection.getInstance().getConnection();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		int isLine = 0;
		int stationNum = 0;
		int id = 1;
		Document doc;
		ArrayList<Integer> inter = new ArrayList<Integer>();
		inter.add(60);
		inter.add(30);
		for (int i = 0; i < array.size(); i++) {
			doc = array.get(i);
			long start =System.currentTimeMillis();
			// isLine标志线路运行开始
			if (isLine == 0) {
				stationNum = QueryBls.getStationNum(mongodb, array.get(i).getInteger("segmentId"));
				isLine = 1;
				// 判断线路开始的gps数据是否为1，若不是则处理，若是则跳过继续
				if (id != doc.getInteger("sngSerialId")) {
					
					dealPre(i, id, conn, array);
					if(doc.getInteger("sngSerialId") ==stationNum){
						isLine =0;
						id=1;
					}else{
						id = doc.getInteger("sngSerialId") + 1;
					}
						

				} else {
					id++;
//					UpdateTraffic.update(remoteDb, collectionName, (ObjectId) doc.get("_id"),
//						GetTraffic.getTraffic(conn, doc));
				}

			} else {
				// 同一线路首先去重
				if (isSameBus(doc, array.get(i-1))&&doc.getInteger("sngSerialId") == array.get(i - 1).getInteger("sngSerialId")) {
					//remoteDb.getCollection(collectionName).remove(new BasicDBObject("_id", (ObjectId) doc.get("_id")));
					id = doc.getInteger("sngSerialId") + 1;
					System.out.println("delete ..."+doc);
					continue;
				}
				// 判断是否为线路最后的站点gps数据，比较id与站点标号大小
				if (id == stationNum) {
					System.out.println("id = stationNum "+id);
					if (id == doc.getInteger("sngSerialId")) {
						isLine = 0;
						id = 1;
						
						continue;
					} else {
						
						dealNext(i, id, stationNum, conn, array);
						
						isLine = 0;
						id = 1;
						i--;
						continue;
					}
				} else if (id > doc.getInteger("sngSerialId")) {
					System.out.println("id >doc "+ doc.getInteger("sngSerialId"));
					// 该线路的后续站点数据丢失
//					if (Time.getDisBtwDoc(array.get(i - 1), doc) <= 120
//						&& (array.get(i - 1).getInteger("sngSerialId") - doc.getInteger("sngSerialId")) < 0) {
//						// id= doc.getInteger("sngSerialId")+1;
//						continue;
//					}
					dealNext(i, id, stationNum, conn, array);

					i--;
					id = 1;
					isLine = 0;

				} else if (id < doc.getInteger("sngSerialId")) {
					System.out.println("id < doc "+ doc.getInteger("sngSerialId"));
					// 该线路的中间站点数据错位
					if (array.size() >= i + 2 && id == array.get(i + 1).getInteger("sngSerialId")) {
						id = doc.getInteger("sngSerialId") + 1;
						i++;
						continue;
					}
					
					if (i > 1 && isSameBus(doc, array.get(i-1))) {
						inter = Time.getInterBtwDoc(array.get(i - 1), doc);
						add(conn, doc, id, inter);
						id = doc.getInteger("sngSerialId") + 1;
					}else {
						dealNext(i, id, stationNum, conn, array);
						i--;
						id = 1;
						isLine = 0;
					}
					
//					inter = Time.getInterBtwDoc(array.get(i - 1), doc);
//					add(conn, doc, id, inter);
//					id = doc.getInteger("sngSerialId") + 1;
					
				} else {
					id++;
//					UpdateTraffic.update(remoteDb, collectionName, (ObjectId) doc.get("_id"),
//						GetTraffic.getTraffic(conn, doc));
				}
			}
			System.out.println("complete time : "+(System.currentTimeMillis()-start));
		}
	}
	public static void dealPre(int i,int id,Connection conn,ArrayList<Document> array){
		System.out.println("dealPre");
		Document doc = array.get(i);
		ArrayList<Integer> inter =new ArrayList<Integer>();
		inter.add(60);inter.add(30);
		//判断是否为同一车辆的数据，如果是则正常处理，如果不是则判断前面时间段是否存在同车辆数据
		if (i > 1 &&  isSameBus(doc, array.get(i-1))) {

			if (i + 1 < array.size()
				&& array.get(i + 1).getInteger("sngSerialId") >= doc.getInteger("sngSerialId"))
				inter = Time.getInterBtwDoc(doc, array.get(i + 1));
			else {
				inter.set(0, 60);
				inter.set(1, 30);
			}
			add(conn, doc, id, inter);
			
		} else {
			Document d = QueryGps.queryGps(collectionName, doc, -1);
			if (d == null) {
				if (i + 1 < array.size()&&isSameBus(doc, array.get(i+1))
					&& array.get(i + 1).getInteger("sngSerialId") >= doc.getInteger("sngSerialId"))
					inter = Time.getInterBtwDoc(doc, array.get(i + 1));
				else {
					inter.set(0, 60);
					inter.set(1, 30);
				}
				add(conn, doc, id, inter);
				
			}else{
				if(doc.getInteger("sngSerialId") > d.getInteger("sngSerialId")){
					if (i + 1 < array.size()&&isSameBus(doc, array.get(i+1))
						&& array.get(i + 1).getInteger("sngSerialId") >= doc.getInteger("sngSerialId"))
						inter = Time.getInterBtwDoc(doc, array.get(i + 1));
					else {
						inter.set(0, 60);
						inter.set(1, 30);
					}
					add(conn, doc, d.getInteger("sngSerialId")+1, inter);
				}else if(doc.getInteger("sngSerialId") == d.getInteger("sngSerialId")&&Time.getDisBtwDoc(d, doc) <= 120){
					//remoteDb.getCollection(collectionName).remove(new BasicDBObject("_id", (ObjectId) doc.get("_id")));
					System.out.println("delete..");
					id = doc.getInteger("sngSerialId") + 1;
				}else {
					if (i + 1 < array.size()&&isSameBus(doc, array.get(i+1))
						&& array.get(i + 1).getInteger("sngSerialId") >= doc.getInteger("sngSerialId"))
						inter = Time.getInterBtwDoc(doc, array.get(i + 1));
					else {
						inter.set(0, 60);
						inter.set(1, 30);
					}
					add(conn, doc, id, inter);
				}
			}
		}
	}
	public static boolean isSameBus(Document doc,Document doc2){
		if(doc.getInteger("segmentId").equals(doc2.getInteger("segmentId"))
			&& doc.getString("busselfId").equals(doc2.getString("busselfId"))){
			return true;
		}else return false;
	}
	public static void dealNext(int i,int id,int stationNum,Connection conn,ArrayList<Document> array){
		System.out.println("dealNext");
		Document doc = array.get(i);
		ArrayList<Integer> inter =new ArrayList<Integer>();
		inter.add(60);inter.add(30);
		if (id > 2 && i > 2 && isSameBus(array.get(i - 2), array.get(i - 1))) {
			inter = Time.getInterBtwDoc(array.get(i - 2), array.get(i - 1));
		} else {
			inter.set(0, 60);
			inter.set(1, 30);
		}
		if ( isSameBus(doc, array.get(i-1))) {			
			addFinal(conn, array.get(i - 1), stationNum, inter);			
		} else {
			Document d = QueryGps.queryGps(collectionName, array.get(i - 1), 1);
			if (d == null) {				
				addFinal(conn, array.get(i - 1), stationNum, inter);
				
			} else {
				if (id < d.getInteger("sngSerialId")) {
					inter = Time.getInterBtwDoc(array.get(i - 1), d);
					add(conn, d, id, inter);					
				} else if(id >d.getInteger("sngSerialId")){					
					addFinal(conn, array.get(i - 1), stationNum, inter);
					
				}
			}
		}
	}
	public static void add(Connection conn, Document doc, int loc, ArrayList<Integer> array) {
		String arriveTime = null, leaveTime = null;

		int traffic = 0;

		for (int i = doc.getInteger("sngSerialId") - 1; i >= loc; i--) {
			if (i == doc.getInteger("sngSerialId") - 1)
				leaveTime = Time.reduce(doc.getString("arriveTime"), array.get(0));
			else
				leaveTime = Time.reduce(arriveTime, array.get(0));
			arriveTime = Time.reduce(leaveTime, array.get(1));
			Document docu = new Document("segmentId", doc.getInteger("segmentId"))
				.append("stationId", QueryBls.getStationId(db, doc.getInteger("segmentId"), i))
				.append("busselfId", doc.getString("busselfId")).append("sngSerialId", i)
				.append("arriveTime", arriveTime).append("leaveTime", leaveTime);
			long start = System.currentTimeMillis();
			traffic = GetTraffic.getTraffic(conn, docu);
			docu = docu.append("traffic", traffic);
			System.out.println("query ic time: "+(System.currentTimeMillis()-start));
			System.out.println(docu);
			//remoteMongodb.getCollection(collectionName).insertOne(docu);

		}

	}

	public static void addFinal(Connection conn, Document doc, int loc, ArrayList<Integer> array) {
		String arriveTime = null, leaveTime = null;

		int traffic = 0;

		for (int i = doc.getInteger("sngSerialId") + 1; i <= loc; i++) {
			if (i == doc.getInteger("sngSerialId") + 1)
				arriveTime = Time.add(doc.getString("leaveTime"), array.get(0));
			else
				arriveTime = Time.add(leaveTime, array.get(0));
			leaveTime = Time.add(arriveTime, array.get(1));
			Document docu = new Document("segmentId", doc.getInteger("segmentId"))
				.append("stationId", QueryBls.getStationId(db, doc.getInteger("segmentId"), i))
				.append("busselfId", doc.getString("busselfId")).append("sngSerialId", i)
				.append("arriveTime", arriveTime).append("leaveTime", leaveTime);
			long start = System.currentTimeMillis();
			

			traffic = GetTraffic.getTraffic(conn, docu);
			docu = docu.append("traffic", traffic);
			System.out.println("query ic time: "+(System.currentTimeMillis()-start));			
			System.out.println(docu);
			//remoteMongodb.getCollection(collectionName).insertOne(docu);

		}
	}

	public static void deal_process(String startTime, String endTime) {
		int dis = Time.disHours(startTime, endTime)/4;
		for (int i = 0; i < dis; i++) {
			if (i == 0) {
				endTime = Time.addHours(startTime, 4);
				complete(startTime, endTime);
			} else {
				startTime = Time.addHours(startTime, 4);
				endTime = Time.addHours(startTime, 4);
				complete(startTime, endTime);
			}

			System.out.println(startTime);
			System.out.println(endTime);
		}
	}

	public static void main(String[] args) {
		String startTime = "2015-11-12 00:00:00";
		String endTime = "2015-11-17 00:00:00";
		deal_process(startTime, endTime);
	}
}
