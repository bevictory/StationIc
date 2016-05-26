package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.Query;

import mongodb.MongoDBCoonnection;
import mongodb.QueryBls;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class GpsIcIntegrate {
	private static String gps_collection ="gps_11_10_IC";
	private static String ic_collection ="icData";
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance()
			.getMongoData();

	/**
	 * 获得指定车辆指定路线的gps数据
	 * 
	 * @param segmentId
	 *            单程号
	 * @param busselfId
	 *            车辆ID
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public static ArrayList<Document> getGps(int segmentId, String busselfId,
			String startTime, String endTime) {
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb
				.getCollection(gps_collection)
				.find(new Document("$and", Arrays.asList(new Document(
						"segmentId", segmentId), new Document("busselfId",
						busselfId), new Document("arriveTime", new Document(
						"$gt", startTime)), new Document("arriveTime",
						new Document("$lt", endTime)))))
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

	/**
	 * 获得指定线路指定车辆的Ic 数据
	 * 
	 * @param xlbh
	 *            线路
	 * @param qcbh
	 *            汽车编号
	 * @param startTime
	 *            开始时间
	 * @param endTime
	 *            结束时间
	 * @return
	 */
	public static List<Document> getIc(String xlbh, String qcbh,
			String startTime, String endTime) {
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb
				.getCollection(ic_collection)
				.find(new Document("$and", Arrays.asList(new Document("xlbh",
						xlbh), new Document("qcbh", qcbh), new Document("xfsj",
						new Document("$gte", startTime)), new Document("xfsj",
						new Document("$lt", endTime)))))
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
	/**
	 * 获得车辆发车前的刷卡人数
	 * @param segmentId
	 * @param busselfId
	 * @param time
	 * @param flag
	 * @return
	 */
	public static int  getDepartIcNum(int segmentId, String busselfId,String time, int flag){
		final ArrayList<Document> array = new ArrayList<Document>();
		Document document =null;
		if(flag==1) document= new Document("leaveTime",new Document("$lte", time));
		else document= new Document("leaveTime",new Document("$gte", time));
		FindIterable<Document> iter = mongodb
				.getCollection(gps_collection)
				.find(new Document("$and", Arrays.asList( new Document("busselfId",
						busselfId), document)))
				.sort(new Document("arriveTime", -1)).limit(1);
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		if(array.size()==0) return 0;
		document = array.get(0);
		String startTime = document.getString("leaveTime");
		System.out.println("StartTime "+startTime);
		String xlbh= String.format("%06d", QueryBls.getSubInfoId(mongodb, segmentId));
		System.out.println("xlbh "+xlbh);
		List<Document> ic = getIc(xlbh, "0"+busselfId, startTime, time);
		System.out.println("ic num " +ic.size());
		int num =0;
		if(document.getInteger("sngSerialId") == QueryBls.getStationNum(mongodb, document.getInteger("segmentId"))){
		
		return ic.size();
		}
		else{
			for(int i =0 ;i < ic.size(); i++){
				if(Math.abs(Time.getInterBtwTime(ic.get(i).getString("xfsj"), time))
						< Math.abs(Time.getInterBtwTime(ic.get(i).getString("xfsj"), document.getString("leaveTime")))) 
					num++;
			}
			return num;
		}
		
	}

	/**
	 * 获得Ic卡数据之间时间间隔数组
	 * 
	 * @param arrayList
	 * @return
	 */
	public static List<Integer> icInter(List<Document> arrayList) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		Document pre = arrayList.get(0);
		int p = 0;
		arr.add(0);
		for (int i = 1; i < arrayList.size(); i++) {
			Document tmp = arrayList.get(i);
			p += Time.getInterBtwIc(pre, tmp);
			arr.add(p);
			pre = tmp;
		}
		return arr;
	}

	/**
	 * 获得gps数据之间时间间隔数组
	 * 
	 * @param arrayList
	 * @return
	 */
	public static List<Integer> gpsInter(List<Document> arrayList) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		int p = 0;
		Document pre = arrayList.get(0);
		arr.add(0);
		for (int i = 1; i < arrayList.size(); i++) {
			p += Time.getInterBtwTime(pre.getString("arriveTime"),
					pre.getString("leaveTime"));
			arr.add(p);
			Document tmp = arrayList.get(i);
			p += (Time.getInterBtwTime(pre.getString("leaveTime"),
					tmp.getString("arriveTime")));
			arr.add(p);
			pre = tmp;
		}
		return arr;
	}

	
	
	
	/**
	 * 获得刷卡数据与gps数据匹配最小间隔的方案
	 * 
	 * @param gpsInter
	 * @param icInter
	 * @return
	 */
	public static int minInterBteGpsIc(List<Integer> gpsInter,
			List<Integer> icInter) {
		//保存时间间隔
		int inter = 0;
		//保存最小时间间隔
		int min = Integer.MAX_VALUE; 
		//loc 最小时间间隔的匹配开始位置 time 最小时间错位
		int loc = 0, time = 0;
		//timeInter 保存时间错位
		int timeInter = 0;
		//result 保存最小间隔匹配方案
		Map<Integer, Integer> result = null;
		for (int i = 0; i < gpsInter.size() / 2; i++) {
			int iLoc = i;
			inter = 0;
			for (timeInter = -30; timeInter <= 30; timeInter++) {
				iLoc = i;
				inter = 0;
				Map<Integer, Integer> map = new HashMap<Integer, Integer>();
				if (timeInter < 0) {
					inter -= timeInter;
					map.put(i + 1, 1);
				} else {
					inter += Math.min(
							gpsInter.get(i * 2 + 1) - gpsInter.get(i * 2)
									- timeInter, timeInter);
					map.put(i + 1, 1);
				}
				boolean flag = false;
				for (int j = 1; j < icInter.size(); j++) {
					while (iLoc < gpsInter.size() / 2 - 1
							&& icInter.get(j) > gpsInter.get(iLoc * 2 + 1)
									- gpsInter.get(i * 2) - timeInter) {
						iLoc++;

					}
					// System.out.println(iLoc);
					int left = iLoc >= 1 ? Math
							.abs(icInter.get(j)
									- (gpsInter.get(iLoc * 2 - 1)
											- gpsInter.get(i * 2) - timeInter))
							: Math.abs(icInter.get(j)
									- (gpsInter.get(0) - gpsInter.get(i * 2) - timeInter));
					int right = Math
							.min(Math.abs(icInter.get(j)
									- (gpsInter.get(iLoc * 2 + 1)
											- gpsInter.get(i * 2) - timeInter)),
									Math.abs(icInter.get(j)
											- (gpsInter.get(iLoc * 2)
													- gpsInter.get(i * 2) - timeInter)));
					if (left < right) {
						iLoc--;
						inter += left;
						map.put(iLoc + 1,
								map.get(iLoc + 1) == null ? 1 : map
										.get(iLoc + 1) + 1);
					} else {
						inter += right;
						map.put(iLoc + 1,
								map.get(iLoc + 1) == null ? 1 : map
										.get(iLoc + 1) + 1);
					}
				}
				if (min > inter) {
					loc = i;
					time = timeInter;
					result = map;
				}
				min = Math.min(min, inter);
				System.out.println(inter);

			}

		}
		System.out.println("min " + min);
		System.out.println("gpsIc " + result);
		System.out.println("timeInter " + time);
		return loc;
	}

	public static void main(String[] args) {
		String startTime = "2015-11-10 00:00:00";
		String endTime = "2015-11-10 23:59:59";
		int segmentId = 17372101;
		String busselfId = "38704";
		String qcbh = "038704";
		String xlbh = "000046";
		List<Document> arrayList = getGps(segmentId, busselfId, startTime,
				endTime);
		arrayList = arrayList.subList(28, 53);
		for (int i = 0; i < arrayList.size(); i++) {
			System.out.println(arrayList.get(i));
		}
		System.out.println(gpsInter(arrayList));

		List<Document> icList = getIc(xlbh, qcbh, startTime, endTime);
		icList = icList.subList(17, 31);
		for (int i = 0; i < icList.size(); i++)
			System.out.println(icList.get(i));
		System.out.println(icInter(icList));
		System.out.println(minInterBteGpsIc(gpsInter(arrayList),
				icInter(icList)));
		
		System.out.println(getDepartIcNum(segmentId, busselfId, arrayList.get(0).getString("arriveTime"), 1));
	}

}
