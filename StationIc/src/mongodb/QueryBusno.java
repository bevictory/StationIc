/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:QueryBusno.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月20日 上午10:20:18
 */
package mongodb;

import java.io.IOException;

import org.bson.BasicBSONObject;
import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月20日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
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
