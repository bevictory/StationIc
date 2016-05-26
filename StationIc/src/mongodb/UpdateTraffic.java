/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:UpdateTraffic.java
 * Package Name:mongodb
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��25�� ����10:57:14
 */
package mongodb;

import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.client.MongoDatabase;

/**
  * ��˵��  ������·վ���ˢ������
  * @author dai.guohui
  * @version 1.0, 2015��11��25��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
  */
public class UpdateTraffic {
	/**
	 * ����վ���ˢ������
	 * @param mongodb db
	 * @param collectionName ����
	 * @param id id��
	 * @param traffic ������
	 */
	public static void update(DB mongodb,String collectionName,ObjectId id, int traffic){
		mongodb.getCollection(collectionName).update(new BasicDBObject("_id",id), 
			new BasicDBObject("$set",new BasicDBObject("traffic",traffic)));
	}
	public static void main(String []args){
		update(MongoDBCoonnection.getInstance().getDB(),"",new ObjectId("56556a593483b05c98d1cf79"),2);
	}

}
