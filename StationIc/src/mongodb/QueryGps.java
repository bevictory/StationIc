/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:StationIc
 * File Name:QueryGps.java
 * Package Name:mongodb
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��12��2�� ����4:48:55
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
 * ��˵��
 * @author dai.guohui
 * @version 1.0, 2015��12��2�� ÿ���޸ĺ���°汾�ţ����ں��޸�����
 * @see [�����/����]
 * @since [��Ʒ/ģ��汾]
 */
public class QueryGps {
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static DB db = MongoDBCoonnection.getInstance().getDB();

	/**
	 * 
	 * ����˵�� �����1,���һСʱ������,������-1��ǰһСʱ������
	 * @author dai.guohui
	 * @version 1.0, 2015��12��2��
	 * @param collectionName
	 * @param doc
	 * @param flag
	 * @return ����˵��
	 * @see [��,��#��������#��Ա]
	 * @since [��Ʒ/ģ��汾] ��ʾ���Ǹ��汾��ʼ�����������
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
