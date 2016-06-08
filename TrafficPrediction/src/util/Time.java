
package util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import org.bson.Document;

public class Time {
	/**
	 * 在当前时间增加num*30秒
	 * @param time yyyy-MM-dd HH:mm:ss
	 * @param num
	 * @return
	 */
	public static String addTime(String time, int num ){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=null;
		try {
			date = formater.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		time = formater.format(new Date(date.getTime()+num*30*1000));
		return time;
	}
	public static String reduceTime(String time, int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date=null;
		try {
			date = formater.parse(time);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		time = formater.format(new Date(date.getTime()-num*30*1000));
		return time;
	}
	/**
	 * 在当前时间增加num天
	 * @param time yyyy-MM-dd HH:mm:ss
	 * @param num
	 * @return
	 */
	public static String addDay(String time,int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()+num*24*60*60*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public static String reduceDay(String time,int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()-num*24*60*60*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	/**
	 * 将当前时间分解为日期和时间两个部分
	 * @param time
	 * @return
	 */
	public static ArrayList<String> getDateTime(String time){
		StringTokenizer str = new StringTokenizer(time, " ");
		String str1=str.nextToken();
		String str2=str.nextToken();
		SimpleDateFormat formater1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat formater2 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			str1=formater1.format(formater2.parse(str1));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<String> array = new ArrayList<String>();
		array.add(str1);
		array.add(str2);
		return array;
	}
	/**
	 * 将时间减少num*30秒
	 * @param time HH:mm:ss
	 * @return
	 */
	public static String reduceTime(String time){
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()-30*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
	}
	public static String addTime(String time){
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()+30*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
		
	}
	public static String addHours(String time,int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()+num*60*60*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public static String addHalfHours(String time,int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()+num*30*60*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public static String add(String time,int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()+num*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public static String reduce(String time,int num){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String str = "";
		try {
			str = formater.format(new Date(formater.parse(time).getTime()-num*1000));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return str;
	}
	public static int disHours(String startTime, String endTime){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int num =0;
		try {
			 num = (int)(formater.parse(endTime).getTime()-formater.parse(startTime ).getTime())/60/60/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(num >=0) return num;
		
		else return -1;
	}
	public static int disHalfHours(String startTime, String endTime){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int num =0;
		try {
			 num = (int)(formater.parse(endTime).getTime()-formater.parse(startTime ).getTime())/30/60/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(num >=0) return num;
		
		else return -1;
	}
	public static int disDays(String startTime, String endTime){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int num =0;
		try {
			 num = (int)(formater.parse(endTime).getTime()-formater.parse(startTime ).getTime())/24/60/60/1000;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(num >=0) return num;
		
		else return -1;
	}
	/**
	 * 
	 * 方法说明
	 * @author 	dai.guohui
	 * @version 1.0, 2015年11月23日
	 * @param startTime
	 * @param endTime
	 * @return	参数说明 0 :同一天，1：减时间非同一天，2：加时间非同一天
	 * @see	[类,类#方法，类#成员]
	 * @since	[产品/模块版本] 表示从那个版本开始就有这个方法
	 */
	public static int isSameDay(String startTime,String endTime){
		SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss");
		try {
			Date date1 = new Date(formater.parse(startTime).getTime());
			Date date2 = new Date(formater.parse(endTime).getTime());
			
			if(date1.getHours() == 0){
				if(date1.getMinutes()==0 && date1.getSeconds() <=29) return 1;
				else return 0;
			}
			else if(date1.getHours() ==23){
				if(date2.getMinutes()==59 &&date2.getSeconds() >=30) return 2;
				else return 0;
			}else return 0;
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	public static ArrayList<Integer> getInterBtwDoc(Document doc1,Document doc2){
		ArrayList<Integer> array = new ArrayList<Integer>();
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		int dis = doc2.getInteger("sngSerialId") - doc1.getInteger("sngSerialId");
		int inter =0;
		try {
			 inter = (int)(formater.parse(doc2.getString("arriveTime")).getTime()-formater.parse(doc1.getString("leaveTime") ).getTime())/1000;
			 int stay =((int)(formater.parse(doc2.getString("leaveTime")).getTime()-formater.parse(doc2.getString("arriveTime") ).getTime())/1000)+
				 (int)(formater.parse(doc1.getString("leaveTime")).getTime()-formater.parse(doc1.getString("arriveTime") ).getTime())/1000;
			 int m =0;
			 if(stay <= 10) stay =10; 
			 if(dis > 0) m= (inter - (dis-1)*stay/2)/(dis);
			 else m = 60;
			 if(m >= 20) inter = m ;
			 else inter = 60;
			 array.add(inter);
			 array.add(stay/2);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return array;
		
		
	}
	public static int getDisBtwDoc(Document doc1,Document doc2){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		int dis =0;
		try {
			 dis = (int)(formater.parse(doc2.getString("arriveTime")).getTime()-formater.parse(doc1.getString("leaveTime") ).getTime())/1000;			 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dis >=0) return dis;
		
		else return -1;
	}
	public static int getInterDoc(String doc1,String doc2){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		int dis =0;
		try {
			 dis = (int)(formater.parse(doc2).getTime()-formater.parse(doc1 ).getTime())/1000;			 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dis;
		
		
	}
	/**
	 * 求解两个时间的差值
	 * @param time1
	 * @param time2
	 * @return
	 */
	public static int getInterBtwTime(String time1,String time2){
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		int dis =0;
		try {
			 dis = (int)(formater.parse(time2).getTime()-formater.parse(time1 ).getTime())/1000;			 
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(dis >=0) return dis;
		
		else return -1;
	}
	public static void main(String []args){
		String startTime = "2015-11-09 00:00:00";String endTime = "2015-11-20 00:00:00";
		System.out.println(disDays(startTime, endTime));
	}
}
