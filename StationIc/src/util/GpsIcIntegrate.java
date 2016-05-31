package util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.management.Query;
import javax.print.Doc;
import javax.swing.ListModel;
import javax.swing.text.NumberFormatter;

import mongodb.MongoDBCoonnection;
import mongodb.QueryBls;
import mongodb.UpdateTraffic;

import org.bson.BSONObject;
import org.bson.Document;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
class SegBus{
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public String getBusselfId() {
		return busselfId;
	}
	public void setBusselfId(String busselfId) {
		this.busselfId = busselfId;
	}
	private int segmentId;
	private String busselfId;
	public SegBus(int segmentId, String busselfId){
		this.segmentId = segmentId;
		this.busselfId = busselfId;
		
	}
}
public class GpsIcIntegrate {
	private static String gps_collection = "gps_12_07_IC";
	private static String ic_collection = "icData_12_07";

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
		if(hasChecked(qcbh))
		{
			int inter =  CheckTime2.get_inter(qcbh);
			startTime  = Time.add(startTime,inter);
			endTime  = Time.add(endTime, inter);
		}
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
	 * 
	 * @param segmentId
	 * @param busselfId
	 * @param time
	 * @param flag
	 * @return
	 */
	public static int getDepartIcNum(int segmentId, String busselfId,
			String time, int flag) {
		final ArrayList<Document> array = new ArrayList<Document>();
		Document document = null;
		if (flag == 1)
			document = new Document("leaveTime", new Document("$lte", time));
		else
			document = new Document("arriveTime", new Document("$gte", time));

		// time= Time.addTime(time,-CheckTime2.get_inter("0"+busselfId));

		FindIterable<Document> iter = mongodb
				.getCollection(gps_collection)
				.find(new Document("$and", Arrays.asList(new Document(
						"busselfId", busselfId), document)))
				.sort(new Document("arriveTime", -1)).limit(1);
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0);
			}
		});
		if (array.size() == 0)
			return 0;
		document = array.get(0);
		String startTime = document.getString("leaveTime");

		System.out.println("StartTime " + startTime);

		String xlbh = String.format("%06d",
				QueryBls.getSubInfoId(mongodb, segmentId));

		System.out.println("xlbh " + xlbh);

		if (Math.abs(CheckTime2.isCheckTime("0" + busselfId) - 1) < 10e-5) {
			int gps_ic_inter = -CheckTime2.get_inter("0" + busselfId);
			startTime = Time.add(startTime, gps_ic_inter);
			time = Time.add(time, gps_ic_inter);

		}

		List<Document> ic = getIc(xlbh, "0" + busselfId, startTime, time);

		System.out.println("ic num " + ic.size());

		int num = 0;
		if (document.getInteger("sngSerialId") == QueryBls.getStationNum(
				mongodb, document.getInteger("segmentId"))) {

			return ic.size();
		} else {
			for (int i = 0; i < ic.size(); i++) {
				if (Math.abs(Time.getInterBtwTime(ic.get(i).getString("xfsj"),
						time)) < Math.abs(Time.getInterBtwTime(ic.get(i)
						.getString("xfsj"), startTime)))
					num++;
			}
			return num;
		}

	}
	public static boolean hasChecked(String qcbh){
		if (Math.abs(CheckTime2.isCheckTime(qcbh) - 1) < 10e-5) {
			return true;
		}else  return false;
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
	 * 使用时间校准方案解决刷卡记录时间间隔在10分钟内的单程
	 * @param gpsList
	 * @param icList
	 * @param segmentId
	 * @param busselfId
	 * @param inter
	 */
	public static void integrateWithInter(List<Document> gpsList,
			List<Document> icList, int segmentId, String busselfId,int inter){
		
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for(int i =0;i< icList.size() ;i++){
			Document document  = icList.get(i);
			String xfsj=Time.add(document.getString("xfsj"),inter);
			int min = Integer.MAX_VALUE;
			int locj=0;
			for(int j=0;j<gpsList.size();j++){
				int dis = Math.min(Math.abs(Time.getInterBtwTime(gpsList.get(j).getString("leaveTime"), xfsj)),
						Math.abs(Time.getInterBtwTime(gpsList.get(j).getString("arriveTime"), xfsj)));
				if(dis<min){
					locj =j;
				}
			}result.put(locj+1,result.get(locj+1)==null?1:result.get(locj+1)+1);
		}
		
		for (int i = 0; i < gpsList.size(); i++) {
			if (result.containsKey(i + 1)) {
				UpdateTraffic.update(MongoDBCoonnection.getMongoDBConn()
						.getDB(), gps_collection,
						gpsList.get(i).getObjectId("_id"), result.get(i + 1));
			}
		}
	}
	/**
	 * 使用最小间隔算法获得刷卡数据与gps数据匹配的方案
	 * 
	 * @param gpsList
	 * @param icList
	 * @return gps车载机与ic刷卡机的时间差 gps - ic
	 */
	public static int minInterBteGpsIc(List<Document> gpsList,
			List<Document> icList, int segmentId, String busselfId) {
		List<Integer> gpsInter = null, icInter = null;

		// 获得gps数据 时间间隔
		gpsInter = gpsInter(gpsList);

		// 获得ic数据 时间间隔
		icInter = icInter(icList);

		// 保存时间间隔
		int inter = 0;

		// 保存最小时间间隔
		int min = Integer.MAX_VALUE;

		// loc 最小时间间隔的匹配开始位置 time 最小时间错位
		int loc = 0, time = 0;

		// timeInter 保存时间错位
		int timeInter = 0;

		// result 保存最小间隔匹配方案
		Map<Integer, Integer> result = null;
		String departTime = gpsList.get(0).getString("arriveTime");
		int beforeDepartIcNum = getDepartIcNum(segmentId, busselfId,
				departTime, 1);
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
//					System.out.println("loc "+i);
//					System.out.println("begin xfsj "+icList.get(0).getString("xfsj"));
//					System.out.println("arriveTime "+ gpsList.get(loc)
//									.getString("arriveTime") );
					time = Time.getInterBtwTime(
							icList.get(0).getString("xfsj"), gpsList.get(loc)
									.getString("arriveTime"))
							+ timeInter;
					
					result = map;result.put(1,
							map.get(1) == null ? beforeDepartIcNum : map.get(1)
									+ beforeDepartIcNum);
				}
				min = Math.min(min, inter);
				//System.out.println(inter);

			}

		}
		System.out.println("min " + min);
		System.out.println("gpsIc " + result);
		System.out.println("timeInter " + time);

		for (int i = 0; i < gpsList.size(); i++) {
			if (result.containsKey(i + 1)) {
				UpdateTraffic.update(MongoDBCoonnection.getMongoDBConn()
						.getDB(), gps_collection,
						gpsList.get(i).getObjectId("_id"), result.get(i + 1));
			}
		}
		
		if (!CheckTime2.isCheck(busselfId)) {
			mongodb.getCollection("checkTime3").insertOne(
					new Document("busselfId", busselfId)
							.append("startTime",
									gpsList.get(0).getString("arriveTime"))
							.append("endTime",
									gpsList.get(gpsList.size()-1).getString(
											"endTime")).append("inter", time)
							.append("prob", 1.0));
		} else {
			double checkTime = CheckTime2.isCheckTime(busselfId);
			time = (int) (time + checkTime) / 2;
			mongodb.getCollection("checkTime").updateOne(
					new Document("busselfId", busselfId),
					new Document("$set",new Document("startTime", gpsList.get(0).getString(
							"arriveTime"))
							.append("endTime",
									gpsList.get(gpsList.size()-1).getString(
											"endTime")).append("inter", time)
							.append("prob", 1.0)));
		}

		return time;
	}

	public static void process(int segmentId, String busselfId,
			String startTime, String endTime) {
		List<Document> gpsList = getGps(segmentId, busselfId, startTime,
				endTime);
		int stationNum = QueryBls.getStationNum(mongodb, segmentId);
		int pre = 0;
		int timeInter = 0;
		List<List<Document>> noDealList = new ArrayList<List<Document>>();
		String xlbh = String.format("%06d",
				QueryBls.getSubInfoId(mongodb, segmentId));
		for (int i = 0; i < gpsList.size(); i++) {

			if (gpsList.get(i).getInteger("sngSerialId") == stationNum
					|| i == gpsList.size() - 1
					|| ((i + 1 < gpsList.size() && gpsList.get(i).getInteger(
							"sngSerialId") > gpsList.get(i + 1).getInteger(
							"sngSerialId")))) {
				List<Document> subGps = null;
				subGps = gpsList.subList(pre, i + 1);
				pre=i+1;
				System.out.println(subGps);

				List<Document> icList = getIc(xlbh, "0" + busselfId, subGps
						.get(0).getString("arriveTime"),
						subGps.get(subGps.size()-1).getString("leaveTime"));
				if (isDeal(icList)) {
					timeInter = minInterBteGpsIc(subGps, icList, segmentId,
							busselfId);
				} else
					noDealList.add(subGps);
			}
		}

		for (int i = 0; i < noDealList.size(); i++) {
			List<Document> icList = getIc(xlbh, "0" + busselfId, Time.add(noDealList.get(i)
					.get(0).getString("arriveTime"),-timeInter),
					Time.add(noDealList.get(i).get(noDealList.get(i).size()-1).getString("leaveTime"),-timeInter));
			integrateWithInter(noDealList.get(i), icList, segmentId, busselfId, timeInter);
		}

	}
	/**
	 * 单程的刷卡记录时间范围不超过10分钟的，则不使用最小间隔法解决，使用时间差校准的方式
	 * @param icList
	 * @return
	 */
	public static boolean isDeal(List<Document> icList) {
		if (icList.size() == 0)
			return false;
		if (Math.abs(Time.getInterBtwTime(
				icList.get(0).getString("xfsj"),
				icList.get(icList.size() - 1).getString("xfsj"))) < 600||icList.size()<3) {
			return false;
		}
		return true;
	}
	public static void update_process(String startTime,String endTime){
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		 StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		//System.out.println(end);
		//end = Time.addDay(start, 1);
		
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0)
			{
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			
			List<SegBus> result = new ArrayList<SegBus>();
			result = getSegBus(start, end);
			for(int j=0; j<result.size();j++){
				SegBus tmp = result.get(j);
				process(tmp.getSegmentId(), tmp.getBusselfId(), start, end);
			}
			System.out.println(start);
			System.out.println(end);
			
			
			
		}
	}
	public static List<SegBus> getSegBus(String startTime,String endTime){
		DB db = MongoDBCoonnection.getInstance().getDB();
		List<DBObject> keyList  = new ArrayList<DBObject>();
		List<DBObject> condList = new ArrayList<DBObject>();
		keyList.add(new BasicDBObject("segmentId",true));
		keyList.add(new BasicDBObject("busselfId",true));
		condList.add(new BasicDBObject("arriveTime",new BasicDBObject("$gte",startTime)));
		condList.add(new BasicDBObject("leaveTime",new BasicDBObject("$lte",endTime)));
		BasicDBList list=(BasicDBList) db.getCollection(gps_collection).group(new BasicDBObject("segmentId",true).append("busselfId",true), 
				new BasicDBObject("arriveTime",new BasicDBObject("$gte",startTime)).append("leaveTime", new BasicDBObject("$lte",endTime)), 
				new BasicDBObject("count",0),
				"function(curr,pre){pre.count+=1;}");
		System.out.println(list.size());
		List<SegBus> result = new ArrayList<SegBus>();
		for(int i=0; i< list.size();i++){
			DBObject obj = (BasicDBObject) list.get(i);
			double c = (Double) obj.get("segmentId");
			BigDecimal bigDecimal = new BigDecimal(c);
			int segmentId = Integer.valueOf(bigDecimal.toPlainString());
			String busselfId = (String) obj.get("busselfId");
			result.add(new SegBus(segmentId, busselfId));
			System.out.println(segmentId);
			//System.out.println(String.valueOf((Double)obj.get("segmentId")));
			System.out.println(busselfId);
		}
		return result;
	}
	public static void main(String[] args) {
		String startTime = "2015-12-07 00:00:00";
		String endTime = "2015-12-13 23:59:59";
		int segmentId = 17372101;
		String busselfId = "38704";
		String qcbh = "038704";
		String xlbh = "000046";
//		List<Document> arrayList = getGps(segmentId, busselfId, startTime,
//				endTime);
//		arrayList = arrayList.subList(28, 53);
//		for (int i = 0; i < arrayList.size(); i++) {
//			System.out.println(arrayList.get(i));
//		}
//		System.out.println(gpsInter(arrayList));
//
//		List<Document> icList = getIc(xlbh, qcbh, startTime, endTime);
//		icList = icList.subList(17, 31);
//		for (int i = 0; i < icList.size(); i++)
//			System.out.println(icList.get(i));
//		System.out.println(icInter(icList));
//		System.out.println(minInterBteGpsIc(arrayList, icList, segmentId,
//				busselfId));
//
//		System.out.println(getDepartIcNum(segmentId, busselfId, arrayList
//				.get(0).getString("arriveTime"), 1));
		
		//process(segmentId, busselfId, startTime, endTime);
			update_process(startTime, endTime);
		//System.out.println(Integer.MAX_VALUE);
	}

}
