/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:StationIc
 * File Name:QueryGps.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月2日 下午4:48:55
 */
package mongodb;

import java.util.ArrayList;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import util.Time;

/**
 * 类说明
 * @author dai.guohui
 * @version 1.0, 2015年12月2日 每次修改后更新版本号，日期和修改内容
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class QueryGps {
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static DB db = MongoDBCoonnection.getInstance().getDB();

	/**
	 * 
	 * 方法说明 如果是1,则后一小时的数据,否则是-1查前一小时的数据
	 * @author dai.guohui
	 * @version 1.0, 2015年12月2日
	 * @param collectionName
	 * @param doc
	 * @param flag
	 * @return 参数说明
	 * @see [类,类#方法，类#成员]
	 * @since [产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static Document queryGps(String collectionName, Document doc, int flag) {

		MongoCollection<Document> collection = mongodb.getCollection(collectionName);
		ArrayList<Document> list = new ArrayList<Document>();

		if (flag == 1) {
			list.add(new Document("segmentId", doc.getInteger("segmentId")));
			list.add(new Document("busselfId", doc.getString("busselfId")));
			list.add(new Document("arriveTime", new Document("$gt", doc.getString("arriveTime"))));
			list.add(new Document("arriveTime", new Document("$lte", Time.addHours(doc.getString("arriveTime"), 1))));
			// list.add(new Document("sngSerialId",new Document("$gte",doc.getInteger("sngSerialId"))));
		} else {
			list.add(new Document("segmentId", doc.getInteger("segmentId")));
			list.add(new Document("busselfId", doc.getString("busselfId")));
			list.add(new Document("arriveTime", new Document("$gte", Time.addHours(doc.getString("arriveTime"), -1))));
			list.add(new Document("arriveTime", new Document("$lt", doc.getString("arriveTime"))));
			// list.add(new Document("sngSerialId",new Document("$lte",doc.getInteger("sngSerialId"))));
		}

		long st = System.currentTimeMillis();
		FindIterable<Document> iter = collection.find(new Document("$and", list))
			.sort(new Document("arriveTime", flag)).limit(1);

		final ArrayList<Document> arr = new ArrayList<Document>();
		iter.forEach(new Block<Document>() {
			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				 System.out.println("queryGps "+arg0);
				arr.add(arg0);
			}
		});
		System.out.println("query gps time:"+(System.currentTimeMillis()-st));
		if (arr.size() > 0)
			return arr.get(0);
		else
			return null;
	}
	public static void main(String []args){
		System.out.println(queryGps("gpsAll", new Document("segmentId", 1746635)
				.append("stationId", "12")
				.append("busselfId", "25062").append("sngSerialId", 2)
				.append("arriveTime", "2015-11-10 08:00:00").append("leaveTime", "2015-11-10 07:00:00"), -1));
	}

}
