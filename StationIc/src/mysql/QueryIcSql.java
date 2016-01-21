/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:ConvertTime.java
 * Package Name:mongodb
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��22�� ����1:04:24
 */
package mysql;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import util.Time;

/**
  * ��˵��
  * @author dai.guohui
  * @version 1.0, 2015��11��22��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
  */
public class QueryIcSql {
	
	
	public static String getSql(String startTime,String endTime ,String tableName){
		ArrayList<String> startArray = Time.getDateTime(startTime);
		ArrayList<String> endArray = Time.getDateTime(endTime);
		String sql = "";
		if(startArray.get(0).equals(endArray.get(0))){
//			sql ="select count(*) from "+tableName+ " where xfrq >= '"+startArray.get(0)
//		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
//			"' and xfsj >= '" + startArray.get(1)+
//		    "'  and xfsj <= '"+ endArray.get(1)+"'";
			int flag=Time.isSameDay(startArray.get(1),endArray.get(1));
			if(flag==0){
				sql ="select count(*) from "+tableName+ " where xfrq >= '"+startArray.get(0)
		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
			" ' and xfsj >= '" + Time.reduceTime(startArray.get(1))+
		    "'  and xfsj <= '"+ Time.addTime(endArray.get(1))+"'";
			}
			else if(flag ==1 ){
				sql = "select count(*) from "+tableName+ " where ((xfrq >= '"+Time.reduceDay(startArray.get(0),1)
			    + "' and  xfrq < '"+ startArray.get(0)+
				"' and xfsj >= '" + Time.reduceTime(startArray.get(1))+
			    "'  and xfsj <= '"+ "23:59:59"+"') or "+"(xfrq >= '"+startArray.get(0)
			    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
				"' and xfsj >= '" + "00:00:00"+
			    "'  and xfsj <= '"+ Time.addTime(endArray.get(1))+"')) ";
			}else {
				sql = "select count(*) from "+tableName+ " where ((xfrq >= '"+startArray.get(0)
			    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
				"' and xfsj >= '" + Time.reduceTime(startArray.get(1))+
			    "'  and xfsj <= '"+ "23:59:59"+"') or "+"(xfrq >= '"+Time.addDay(startArray.get(0),1)
			    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),2)+			   		
				"' and xfsj >= '" + "00:00:00"+
			    "' and xfsj <= '"+ Time.addTime(endArray.get(1))+"')) ";
			}
			
		}
		
		else {
//			sql = "select count(*) from "+tableName+ " where ((xfrq >= '"+startArray.get(0)
//		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
//			"' and xfsj >= '" + startArray.get(1)+
//		    "'  and xfsj <= '"+ "23:59:59"+"') or "+"(xfrq >= '"+Time.addDay(startArray.get(0),1)
//		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),2)+
//			"' and xfsj >= '" + "00:00:00"+
//		    "'  and xfsj <= '"+ endArray.get(1)+"')) ";
			sql = "select count(*) from "+tableName+ " where ((xfrq >= '"+startArray.get(0)
		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
			"' and xfsj >= '" + Time.reduceTime(startArray.get(1))+
		    "'  and xfsj <= '"+ "23:59:59"+"') or "+"(xfrq >= '"+Time.addDay(startArray.get(0),1)
		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),2)+
			"' and xfsj >= '" + "00:00:00"+
		    "'  and xfsj <= '"+ Time.addTime(endArray.get(1))+"')) ";
		}
		return sql;
	}
	public static String getSql(String startTime, String endTime){
		ArrayList<String> startArray = Time.getDateTime(startTime);
		ArrayList<String> endArray = Time.getDateTime(endTime);
		String sql = "";
		if(startArray.get(0).equals(endArray.get(0))){
			sql = " where xfrq >= '"+startArray.get(0)
		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
			"' and xfsj >= '" + startArray.get(1)+
		    "'  and xfsj <= '"+ endArray.get(1)+"'";

			
		}
		
		else {
			sql = " where ((xfrq >= '"+startArray.get(0)
		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),1)+
			"' and xfsj >= '" + startArray.get(1)+
		    "'  and xfsj <= '"+ "23:59:59"+"') or "+"(xfrq >= '"+Time.addDay(startArray.get(0),1)
		    + "' and  xfrq < '"+ Time.addDay(startArray.get(0),2)+
			"' and xfsj >= '" + "00:00:00"+
		    "'  and xfsj <= '"+ endArray.get(1)+"')) ";

		}
		return sql;
	}
	public static String getSql_table(String sql,String tableName){
		String sql_table ="select count(*) from "+tableName+ sql;
		return sql_table;
	}
	public static void main(String[] args){
		String startTime = "2015-07-18 23:58:00";String endTime = "2015-07-18 23:59:22";
		System.out.println(getSql(startTime, endTime,"chargeicdetail"));
		
	}

}
