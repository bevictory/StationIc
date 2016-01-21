/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:QueryBusno.java
 * Package Name:mongodb
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��20�� ����10:20:18
 */
package mongodb;

import java.io.IOException;

import org.bson.BasicBSONObject;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;

/**
  * ��˵��
  * @author dai.guohui
  * @version 1.0, 2015��11��20��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
  */
public class QueryBusno {
	public static String getBusNo(DB db,String collectionName,String product){
		BasicDBObject  doc = new BasicDBObject("productId",product);
		DBCursor cursor=null;
		cursor = db.getCollection(collectionName).find(doc);
		if(cursor.hasNext()) return (String) cursor.next().get("busselfId");
		else return "0";
	}
	public static String getProductId(DB db,String collectionName,String product){
		BasicDBObject  doc = new BasicDBObject("busselfId",product);
		DBCursor cursor=null;
		cursor = db.getCollection(collectionName).find(doc);
		if(cursor.hasNext()) return (String) cursor.next().get("productId");
		else return "0";
	}
	public static void main(String []args){
		System.out.println(getBusNo(MongoDBCoonnection.getInstance().getDB(),"busproduct","51918612"));
	}
}
