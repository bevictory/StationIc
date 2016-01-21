/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:StationIc
 * File Name:CheckTime.java
 * Package Name:util
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月10日 下午1:27:51
 */
package util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import mongodb.MongoDBCoonnection;

/**
 * 类说明
 * @author dai.guohui
 * @version 1.0, 2015年12月10日 每次修改后更新版本号，日期和修改内容
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CheckTime {
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static MongoDatabase mongodb1 = MongoDBCoonnection.getInstance().getRemoteMongoDatabase();
	private static MongoDatabase mongodb2 = MongoDBCoonnection.getInstance().getMongoData();

	public static ArrayList<Document> getIcAsArray(String qcbh, String startTime, String endTime) {
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb.getCollection("icData")
			.find(new Document("$and",
				Arrays.asList(new Document("qcbh", qcbh), new Document("xfsj", new Document("$gt", startTime)),
					new Document("xfsj", new Document("$lt", endTime)))))
			.sort(new Document("xfsj", 1));
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		return array;
	}

	public static ArrayList<Document> getGpsAsArray(String productID, String startTime, String endTime) {
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb.getCollection("gpsAll2")
			.find(new Document("$and",
				Arrays.asList(new Document("busselfId", productID),
					new Document("arriveTime", new Document("$gt", startTime)),
					new Document("arriveTime", new Document("$lt", endTime)))))
			.sort(new Document("arriveTime", 1));
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		return array;
	}

	public static ArrayList<Integer> getIcInter(ArrayList<Document> array) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		if (array.size() < 30)
			return null;
		int inter = 0;
		int k=0;
		for (int i = 0; i < array.size(); i++) {
			int inter1 =Time.getInterDoc(array.get(i).getString("xfsj"), array.get(i + 1).getString("xfsj"));
			inter+=inter1;
			if (inter1 <= 40) {
				
				continue;
			} else {
				arr.add(inter);
				if(k++ >30) break;
				inter = 0;
			}
		}
		return arr;
	}

	public static ArrayList<Integer> getGpsInter(ArrayList<Document> array) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		if (array.size() < 30)
			return null;
		for (int i = 0; i < 40; i++) {
			arr.add(Time.getInterDoc(array.get(i).getString("arriveTime"), array.get(i + 1).getString("arriveTime")));
		}
		return arr;
	}

	public static int deal(ArrayList<Document> array_gps, int loca1, int loca2, ArrayList<Integer> ic, int loca3) {
		int inter = Time.getInterDoc(array_gps.get(loca1).getString("leaveTime"),
			array_gps.get(loca2).getString("arriveTime"));
		System.out.println("inter:"+inter);
		System.out.println("ic inter:"+ic.get(loca3));
		int stay1=Time.getInterDoc(array_gps.get(loca1).getString("arriveTime"),
			array_gps.get(loca1).getString("leaveTime")),stay2=Time.getInterDoc(array_gps.get(loca2).getString("arriveTime"),
				array_gps.get(loca2).getString("leaveTime"));
		int runTime1_mid = 30;
		if(loca1>=1)  runTime1_mid =Time.getInterDoc(array_gps.get(loca1).getString("leaveTime"),
			array_gps.get(loca1+1).getString("arriveTime"));
		
		int runTime2_mid = 30;
		if(loca1>=1) runTime2_mid =Time.getInterDoc(array_gps.get(loca2-1).getString("leaveTime"),
			array_gps.get(loca2).getString("arriveTime"));
		int runTime_pre =30;
		if(loca1>=1) runTime_pre=Time.getInterDoc(array_gps.get(loca1-1).getString("leaveTime"),
			array_gps.get(loca1).getString("arriveTime"));
		int runTime_next =30;
		if(loca2+1<=array_gps.size()) runTime_pre=Time.getInterDoc(array_gps.get(loca2).getString("leaveTime"),
			array_gps.get(loca2+1).getString("arriveTime"));
		int stay = stay1+stay2;
		int sub = ic.get(loca3) -inter;
		if(sub<0){
			if(sub+60>=0) {
				if(ic.get(loca3)+ic.get(loca3+1)<inter+stay+60)
					return 3;
				else
					return 1;
			}
			else{
				if(ic.get(loca3)+ic.get(loca3+1)<inter+stay+60) return 3;
				else return 2;
			}
		}else{
			if(sub<=stay+60) {
				if(ic.get(loca3)+ic.get(loca3+1)<inter+stay+60)
					return 3;
				else return 1;
			}
			
			else return 0;
		}
			 
		
			
	}

	public static void check(String startTime, String endTime) {
		final ArrayList<Document> array_bp = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb2.getCollection("busproduct").find();
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array_bp.add(arg0);
			}
		});
		for (int i = 0; i < 1; i++) {
//			ArrayList<Document> array_ic = getIcAsArray("0" + array_bp.get(i).getString("busselfId"), startTime,
//				endTime);
//			ArrayList<Document> array_gps = getGpsAsArray(array_bp.get(i).getString("busselfId"), startTime, endTime);
			ArrayList<Document> array_ic = getIcAsArray("0" + "38704", startTime,
				endTime);
			ArrayList<Document> array_gps = getGpsAsArray("38704", startTime, endTime);
			ArrayList<Integer> ic = getIcInter(array_ic);
			ArrayList<Integer> gps = getGpsInter(array_gps);
			if(array_ic.size()==0||array_gps.size()==0) continue;
			int inter = 1;
			int loca1 = 0, loca2 = 1;
			int find=0;
			for (int k = 0; k < 80; k++) {
				loca1=k;loca2=loca1+1;
				System.out.println("k:"+k);
				if(find ==1) break;
				for (int j = 0; j < 30; j++) {
					int flag =deal(array_gps, loca1, loca2, ic, j);System.out.println("flag:"+flag);
					if (flag==1) {
						
						if (j <= 10) {
							System.out.println("loca1 loca2 j:"+loca1+" "+loca2+" "+j);
							System.out.println("loca1 j:"+k+" "+array_gps.get(k).getString("arriveTime")+" "+0+" "+array_ic.get(0).getString("xfsj"));

							loca1 = loca2;
							loca2++;
						} else {
							inter = Time.getInterDoc(array_ic.get(0).getString("xfsj"),
								array_gps.get(k).getString("arriveTime"));
							System.out.println("loca1 loca2 j:"+k+" "+array_gps.get(k).getString("arriveTime")+" "+" "+loca2+" "+0+" "+array_ic.get(0).getString("xfsj"));							if(inter >2000||inter +2000<0) {
								find =0;
								break;
							}
							System.out.println("success");
							find=1;
							break;
						}
					} else if(flag==0){
						
							loca2++;
							j--;
						 
					}else if(flag==3){
						if (j <= 10) {
							System.out.println("loca1 loca2 j:"+loca1+" "+loca2+" "+j);
							System.out.println("loca1 j:"+k+" "+array_gps.get(k).getString("arriveTime")+" "+0+" "+array_ic.get(0).getString("xfsj"));

							loca1 = loca2;
							loca2++;
						} else {
							inter = Time.getInterDoc(array_ic.get(0).getString("xfsj"),
								array_gps.get(k).getString("arriveTime"));
							System.out.println("loca1 loca2 j:"+k+" "+array_gps.get(k).getString("arriveTime")+" "+" "+loca2+" "+0+" "+array_ic.get(0).getString("xfsj"));
							if(inter >2000||inter +2000<0) {
								find =0;
								break;
							}
							
							find=1;
							System.out.println("success");
							break;
						}
						j++;
					}
					else{
						loca1=k+1;
						loca2=loca1+1;
						break;
					}
				}
			}

			System.out.println(array_bp.get(i).getString("busselfId"));
			System.out.println(ic);
			System.out.println(gps);
			System.out.println(inter);
		}

	}

	public static void main(String[] args) {
		String startTime = "2015-11-11 00:00:00";
		String endTime = "2015-11-12 00:00:00";
		check(startTime, endTime);
	}

}
