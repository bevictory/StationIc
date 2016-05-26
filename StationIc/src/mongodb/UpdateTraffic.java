/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:UpdateTraffic.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月25日 上午10:57:14
 */
package mongodb;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;

/**
  * 类说明  更新线路站点的刷卡数据
  * @author dai.guohui
  * @version 1.0, 2015年11月25日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class UpdateTraffic {
	/**
	 * 更新站点的刷卡数据
	 * @param mongodb db
	 * @param collectionName 表名
	 * @param id id号
	 * @param traffic 客流量
	 */
	public static void update(DB mongodb,String collectionName,ObjectId id, int traffic){
		mongodb.getCollection(collectionName).update(new BasicDBObject("_id",id), 
			new BasicDBObject("$set",new BasicDBObject("traffic",traffic)));
	}
	public static void main(String []args){
		update(MongoDBCoonnection.getInstance().getDB(),"",new ObjectId("56556a593483b05c98d1cf79"),2);
	}

}
