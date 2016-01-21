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
import java.util.StringTokenizer;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import mongodb.MongoDBCoonnection;
import mongodb.QueryTraffic;

/**
 * 类说明
 * @author dai.guohui
 * @version 1.0, 2015年12月10日 每次修改后更新版本号，日期和修改内容
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class CheckTime2 {
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	private static MongoDatabase mongodb1 = MongoDBCoonnection.getInstance().getRemoteMongoDatabase();
	private static MongoDatabase mongodb2 = MongoDBCoonnection.getInstance().getMongoData();
	private static DB db = MongoDBCoonnection.getInstance().getRemoteDB2();
	private static MongoDatabase mongodb3 = MongoDBCoonnection.getInstance().getRemoteMongoDatabase3();
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
		FindIterable<Document> iter = mongodb3.getCollection("gpsAll_noComplete")
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
		int k = 0;
		for (int i = 0; i < array.size(); i++) {
			int inter1 = Time.getInterDoc(array.get(i).getString("xfsj"), array.get(i + 1).getString("xfsj"));
			arr.add(inter1);
			// inter+=inter1;
			// if (inter1 <= 40) {
			//
			// continue;
			// } else {
			// arr.add(inter);
			// if(k++ >30) break;
			// inter = 0;
			// }
		}
		return arr;
	}

	public static int getSum(ArrayList<Integer> array, int loca1, int loca2) {
		int sum = 0;
		for (int i = loca1; i <= loca2; i++) {
			sum += array.get(i);
		}
		return sum;
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

	public static boolean isIc(ArrayList<Document> array_gps, int gpsLoca, ArrayList<Document> ic, int icLoca,
		int inter) {
		String time = Time.add(ic.get(icLoca).getString("xfsj"), inter);
		int runTime = Time.getInterDoc(array_gps.get(gpsLoca).getString("leaveTime"),
			array_gps.get(gpsLoca + 1).getString("arriveTime"));

		if (Time.getInterDoc(array_gps.get(gpsLoca).getString("arriveTime"), time) >= 0
			&& Time.getInterDoc(time, array_gps.get(gpsLoca + 1).getString("arriveTime")) > runTime / 2) {
			return true;
		} else
			return false;
	}

	public static int deal(ArrayList<Document> array_gps, int loca1, int loca2, ArrayList<Integer> ic, int loca3) {
		int inter = Time.getInterDoc(array_gps.get(loca1).getString("leaveTime"),
			array_gps.get(loca2).getString("arriveTime"));
		System.out.println("inter:" + inter);
		System.out.println("ic inter:" + ic.get(loca3));
		int stay1 = Time.getInterDoc(array_gps.get(loca1).getString("arriveTime"),
			array_gps.get(loca1).getString("leaveTime")),
			stay2 = Time.getInterDoc(array_gps.get(loca2).getString("arriveTime"),
				array_gps.get(loca2).getString("leaveTime"));
		int stay = stay1 + stay2;
		int sub = ic.get(loca3) - inter;
		if (sub < 0) {
			if (sub + 60 >= 0) {
				if (ic.get(loca3) + ic.get(loca3 + 1) < inter + stay + 60)
					return 3;
				else
					return 1;
			} else {
				if (ic.get(loca3) + ic.get(loca3 + 1) < inter + stay + 60)
					return 3;
				else
					return 2;
			}
		} else {
			if (sub <= stay + 70) {
				if (ic.get(loca3) + ic.get(loca3 + 1) < inter + stay + 60)
					return 3;
				else
					return 1;
			}

			else
				return 0;
		}

	}

	public static boolean query_check(String qcbh) {
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb.getCollection("checkTime3").find(new Document("busselfId", qcbh));
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		return array.size() > 0 ? true : false;
	}
    public static void check_time_process(String qcbh,String startTime, String endTime){
    	ArrayList<Document> array_gps = getGpsAsArray(qcbh, startTime, endTime);
    	if(array_gps.size()<3||QueryTraffic.getTrafficSum(db, qcbh, startTime, endTime)==0){
			ArrayList<String> arrStart = Time.getDateTime(startTime), arrEnd = Time.getDateTime(endTime);
			String start = startTime, end;
			StringTokenizer str_start=new StringTokenizer(startTime, " ");
			str_start.nextToken();
			StringTokenizer str = new StringTokenizer(endTime, " ");
			str.nextToken();
			
			start = "2015-11-10"+ " " + str_start.nextToken();
			end = "2015-11-10" + " " + str.nextToken();
			int flag=0;
			array_gps = getGpsAsArray(qcbh, start, end);
			for (int i = 0; i <= 6; i++) {
				if (i > 0) {
					start = Time.addHours(start, 24);
					end = Time.addHours(end, 24);
					array_gps = getGpsAsArray(qcbh, start, end);
				}
				if(array_gps.size()<3||QueryTraffic.getTrafficSum(db, qcbh, start, end)==0)
				{
					flag++;//System.out.println("flag "+flag);
					continue;
				}
				//System.out.println(qcbh+QueryTraffic.getTrafficSum(db, qcbh, start, end));
				check_time(array_gps, qcbh, start, end);
				break;
			}
			if(flag==7){
				System.out.println("flag qcbh:"+flag+qcbh);
				start = "2015-11-10"+ " " + "00:00:00";
				end = "2015-11-10" + " " + "23:59:00";
				array_gps = getGpsAsArray(qcbh, start, end);
				for (int i = 0; i <= 6; i++) {
					if (i > 0) {
						start = Time.addHours(start, 24);
						end = Time.addHours(end, 24);
						array_gps = getGpsAsArray(qcbh, start, end);
					}
					System.out.println(start +" "+end);
					if(array_gps.size()<3||QueryTraffic.getTrafficSum(db, qcbh, start, end)==0)
					{
						
						continue;
					}
					check_time(array_gps, qcbh, start, end);
					break;
				}
			}
		} else {
			check_time(array_gps, qcbh, startTime, endTime);
		}
    }
	public static void check_time(ArrayList<Document> array_gps,String qcbh, String startTime, String endTime) {

	
		int traffic_sum = 0;
		int inter = 0;
		double p = 0;
		int bester_inter = 0;
		if (array_gps.size() < 3)
			return;
		
		for (int k = 0; k < 300; k++) {
            int sum =QueryTraffic.getTrafficSum(db, qcbh, Time.add(startTime,inter) ,Time.add(endTime,inter));
            if(sum ==0) break;
			for (int j = 0; j < array_gps.size() - 1; j++) {
				if (j > 0)
					traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j - 1),
						array_gps.get(j + 1), inter);
				else if (j == 0)
					traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j), array_gps.get(j + 1),
						inter);
				else if (j == array_gps.size() - 1)
					traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j - 1), array_gps.get(j),
						inter);
				//System.out.println(traffic_sum);
			}
			traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(array_gps.size()-1), array_gps.get(array_gps.size()-2), array_gps.get(array_gps.size()-1),
				inter);
			if (p > 0.95&&p<=1)
				break;
			System.out.println(
				"sum trafficsum " + sum+" "+traffic_sum);
			System.out.println("p inter:" + (double) traffic_sum
				/sum + " " + inter);
			if (p < (double) traffic_sum
				/sum && (double) traffic_sum
				/sum<=1) {
				p = (double) traffic_sum
					/sum;
				bester_inter = inter;
			}
			if (k == 150)
				inter = 0;
			if (k < 150)
				inter -= 2;
			else
				inter += 2;
			traffic_sum = 0;
		}
		System.out.println("p inter:" + p + " " + bester_inter);
		if(p<0.6) bester_inter=0;
		mongodb.getCollection("checkTime3").insertOne(new Document("busselfId", qcbh)
			.append("startTime", startTime).append("endTime", endTime).append("inter", bester_inter).append("prob", p));

	}
	public static void reCheck(String startTime, String endTime){
		final ArrayList<Document> array_bp = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb2.getCollection("busproduct").find();
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array_bp.add(arg0);
			}
		});
		for(int i=0;i<array_bp.size();i++){
			if(!query_check(array_bp.get(i).getString("busselfId"))){
				System.out.println(i);
				check_time_process(array_bp.get(i).getString("busselfId"), startTime, endTime);
			}else System.out.println("have checked "+i);
		}
	}
	public static int get_check_time(String qcbh, String startTime, String endTime) {

		ArrayList<Document> array_gps = getGpsAsArray(qcbh, startTime, endTime);
		int traffic_sum = 0;
		int inter = 0;
		double p = 0;
		int bester_inter = 0;
		if (array_gps.size() < 3)
			return 0;
		
		for (int k = 0; k < 300; k++) {
            int sum =QueryTraffic.getTrafficSum(db, qcbh, Time.add(startTime,inter) ,Time.add(endTime,inter));
            if(sum ==0) break;
			for (int j = 0; j < array_gps.size()-1 ; j++) {
				if (j > 0)
					traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j - 1),
						array_gps.get(j + 1), inter);
				else if (j == 0)
					traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j), array_gps.get(j + 1),
						inter);
				else if (j == array_gps.size() - 1)
					traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j - 1), array_gps.get(j),
						inter);
				//System.out.println(traffic_sum);
			}
			traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(array_gps.size()-1), array_gps.get(array_gps.size()-2), array_gps.get(array_gps.size()-1),
				inter);
			sum =QueryTraffic.getTrafficSum(db, qcbh, Time.add(startTime,inter) ,Time.add(array_gps.get(array_gps.size()-1).getString("leaveTime"),inter));
			if (p > 0.95&&p<=1)
				break;
			System.out.println(
				"sum trafficsum " + sum+" "+traffic_sum);
			System.out.println("p inter:" + (double) traffic_sum
				/sum + " " + inter);
			if (p < (double) traffic_sum
				/sum && (double) traffic_sum
				/sum<=1) {
				p = (double) traffic_sum
					/sum;
				bester_inter = inter;
			}
			if (k == 150)
				inter = 0;
			if (k < 150)
				inter -= 2;
			else
				inter += 2;
			traffic_sum = 0;
		}
			
		System.out.println("p inter:" + p + " " + bester_inter);
		return bester_inter;

	}
	public static void check_inter(String startTime, String endTime) {
		final ArrayList<Document> array_bp = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb2.getCollection("busproduct").find();
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array_bp.add(arg0);
			}
		});
		for (int i = 570; i < array_bp.size(); i++) {
			// ArrayList<Document> array_ic = getIcAsArray("0" + array_bp.get(i).getString("busselfId"),
			// startTime,endTime);
			ArrayList<Document> array_gps = getGpsAsArray(array_bp.get(i).getString("busselfId"), startTime, endTime);
			// ArrayList<Document> array_ic = getIcAsArray("0" + "38704", startTime,
			// endTime);
			// ArrayList<Document> array_gps = getGpsAsArray("38704", startTime, endTime);
			// ArrayList<Integer> ic = getIcInter(array_ic);
			// ArrayList<Integer> gps = getGpsInter(array_gps);
			int traffic_sum = 0;
			int inter = 0;
			double p = 0;
			int bester_inter = 0;
			if (array_gps.size() < 3)
				continue;
			for (int k = 0; k < 2000; k++) {

				for (int j = 0; j < array_gps.size() - 1; j++) {
					if (j > 0)
						traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j - 1),
							array_gps.get(j + 1), inter);
					else if (j == 0)
						traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j),
							array_gps.get(j + 1), inter);
					else if (j == array_gps.size() - 1)
						traffic_sum += QueryTraffic.getTraffic(db, array_gps.get(j), array_gps.get(j - 1),
							array_gps.get(j), inter);
					System.out.println(traffic_sum);
				}
				if (p > 0.95)
					break;
				System.out.println("sum "
					+ QueryTraffic.getTrafficSum(db, array_bp.get(i).getString("busselfId"), startTime, endTime));
				System.out.println("p inter:" + p + " " + inter);
				if (p < (double) traffic_sum
					/ QueryTraffic.getTrafficSum(db, array_bp.get(i).getString("busselfId"), startTime, endTime)) {
					p = (double) traffic_sum
						/ QueryTraffic.getTrafficSum(db, array_bp.get(i).getString("busselfId"), startTime, endTime);
					bester_inter = inter;
				}
				if (k == 1000)
					inter = 0;
				if (k < 1000)
					inter -= 2;
				else
					inter += 2;
				traffic_sum = 0;
			}
			System.out.println("p inter:" + p + " " + bester_inter);
			mongodb.getCollection("checkTime")
				.insertOne(new Document("busselfId", array_bp.get(i).getString("busselfId"))
					.append("startTime", startTime).append("endTime", endTime).append("inter", bester_inter));

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
			// ArrayList<Document> array_ic = getIcAsArray("0" + array_bp.get(i).getString("busselfId"), startTime,
			// endTime);
			// ArrayList<Document> array_gps = getGpsAsArray(array_bp.get(i).getString("busselfId"), startTime,
			// endTime);
			ArrayList<Document> array_ic = getIcAsArray("0" + "38704", startTime, endTime);
			ArrayList<Document> array_gps = getGpsAsArray("38704", startTime, endTime);
			ArrayList<Integer> ic = getIcInter(array_ic);
			ArrayList<Integer> gps = getGpsInter(array_gps);
			if (array_ic.size() == 0 || array_gps.size() == 0)
				continue;
			int inter = 1;
			int loca1 = 0, loca2 = 1;
			int find = 0;
			for (int k = 0; k < 80; k++) {
				loca1 = k;
				loca2 = loca1 + 1;
				System.out.println("k:" + k);
				if (find == 1)
					break;
				for (int j = 0; j < 30; j++) {
					int flag = deal(array_gps, loca1, loca2, ic, j);
					System.out.println("flag:" + flag);
					if (flag == 1) {

						if (j <= 10) {
							System.out.println("loca1 loca2 j:" + loca1 + " " + loca2 + " " + j);
							System.out.println("loca1 j:" + k + " " + array_gps.get(k).getString("arriveTime") + " " + 0
								+ " " + array_ic.get(0).getString("xfsj"));

							loca1 = loca2;
							loca2++;
						} else {
							inter = Time.getInterDoc(array_ic.get(0).getString("xfsj"),
								array_gps.get(k).getString("arriveTime"));
							System.out.println("loca1 loca2 j:" + k + " " + array_gps.get(k).getString("arriveTime")
								+ " " + " " + loca2 + " " + 0 + " " + array_ic.get(0).getString("xfsj"));
							if (inter > 2000 || inter + 2000 < 0) {
								find = 0;
								break;
							}
							System.out.println("success");
							find = 1;
							break;
						}
					} else if (flag == 0) {

						loca2++;
						j--;

					} else if (flag == 3) {
						if (j <= 10) {
							System.out.println("loca1 loca2 j:" + loca1 + " " + loca2 + " " + j);
							System.out.println("loca1 j:" + k + " " + array_gps.get(k).getString("arriveTime") + " " + 0
								+ " " + array_ic.get(0).getString("xfsj"));

							loca1 = loca2;
							loca2++;
						} else {
							inter = Time.getInterDoc(array_ic.get(0).getString("xfsj"),
								array_gps.get(k).getString("arriveTime"));
							System.out.println("loca1 loca2 j:" + k + " " + array_gps.get(k).getString("arriveTime")
								+ " " + " " + loca2 + " " + 0 + " " + array_ic.get(0).getString("xfsj"));
							if (inter > 2000 || inter + 2000 < 0) {
								find = 0;
								break;
							}

							find = 1;
							System.out.println("success");
							break;
						}
						j++;
					} else {
						loca1 = k + 1;
						loca2 = loca1 + 1;
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

	public static void check_process(String startTime, String endTime) {
		ArrayList<String> arrStart = Time.getDateTime(startTime), arrEnd = Time.getDateTime(endTime);
		String start = startTime, end;
		StringTokenizer str = new StringTokenizer(endTime, " ");
		str.nextToken();
		end = new StringTokenizer(startTime, " ").nextToken() + " " + str.nextToken();
		end = Time.addDay(start, 1);
		for (int i = 0; i < Time.disDays(arrStart.get(0), arrEnd.get(0)); i++) {
			if (i > 0) {
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			System.out.println(start);
			System.out.println(end);
			check_inter(start, end);

		}
	}

	public static int get_inter(String qcbh){
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb.getCollection("checkTime").find(new Document("busselfId", qcbh));
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		if(array.size() >0) return array.get(0).getInteger("inter");
		else return 0;
	}
	public static void main(String[] args) {
		String startTime = "2015-11-11 00:00:00";
		String endTime = "2015-11-11 23:59:59";
		// check_inter(startTime, endTime);
//		String qcbh = "15004";
//		System.out.println(query_check(qcbh));
		reCheck(startTime, endTime);
		//check_time_process("15004", startTime, endTime);
		//get_check_time("38703", startTime, endTime);
	}

}
