package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mongodb.MongoDBCoonnection;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class GpsIcIntegrate {
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getMongoData();
	
	/**
	 * 获得指定车辆指定路线的gps数据
	 * @param segmentId 单程号	
	 * @param busselfId 车辆ID
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	public static ArrayList<Document>  getGps(int segmentId,String busselfId,
			String startTime,String endTime){
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb.getCollection("gps_11_10_IC")
			.find(new Document("$and",
				Arrays.asList(new Document("segmentId",segmentId),new Document("busselfId", busselfId),
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
	/**
	 * 获得指定线路指定车辆的Ic 数据
	 * @param xlbh	线路
	 * @param qcbh 汽车编号	
	 * @param startTime 开始时间
	 * @param endTime 结束时间
	 * @return
	 */
	public static List<Document> getIc(String xlbh,String qcbh,String startTime,String endTime){
		final ArrayList<Document> array = new ArrayList<Document>();
		FindIterable<Document> iter = mongodb.getCollection("icData")
			.find(new Document("$and",
				Arrays.asList(new Document("xlbh", xlbh),new Document("qcbh", qcbh), new Document("xfsj", new Document("$gte", startTime)),
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
	
	/**
	 * 获得Ic卡数据之间时间间隔数组
	 * @param arrayList
	 * @return
	 */
	public static List<Integer> icInter(List<Document> arrayList){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		Document pre =arrayList.get(0);
		int p=0;
		arr.add(0);
		for(int i=1;i<arrayList.size();i++){
			Document tmp =arrayList.get(i);
			p+=Time.getInterBtwIc(pre,tmp );
			arr.add(p);
			pre = tmp;
		}
		return arr;
	}
	/**
	 * 获得gps数据之间时间间隔数组
	 * @param arrayList
	 * @return
	 */
	public static  List<Integer> gpsInter(List<Document> arrayList){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		int p =0;
		Document pre =arrayList.get(0);
		arr.add(0);
		for(int i =1;i<arrayList.size();i++){
			p+=Time.getInterBtwTime(pre.getString("arriveTime"), pre.getString("leaveTime"));
			arr.add(p);
			Document tmp =arrayList.get(i);
			p+=(Time.getInterBtwTime(pre.getString("leaveTime"), tmp.getString("arriveTime")));
			arr.add(p);
			pre = tmp;
		}
		return arr;
	}
	
	public static int minInterBteGpsIc(List<Integer> gpsInter,List<Integer> icInter){
		int inter=0;
		int min =Integer.MAX_VALUE;
		int loc=0;
		for(int i =0;i<gpsInter.size()/2;i++){
			int iLoc=i;
			inter=0;
			boolean flag = false;
			for(int j=1;j<icInter.size();j++){
				while(icInter.get(j)>gpsInter.get(iLoc*2+1)-gpsInter.get(i*2)&&iLoc<gpsInter.size()/2-1){
					iLoc++;
					
				}
				
				int left=iLoc>=1?Math.abs(icInter.get(j)-(gpsInter.get(iLoc*2-1)-gpsInter.get(i*2))):
					Math.abs(icInter.get(j)-(gpsInter.get(0)-gpsInter.get(i*2)));
				int right =Math.min(Math.abs(icInter.get(j)-(gpsInter.get(iLoc*2+1)-gpsInter.get(i*2))),
						Math.abs(icInter.get(j)-(gpsInter.get(iLoc*2)-gpsInter.get(i*2))));
				if(left <right){
					iLoc --;
					inter+=left;
				}
				else inter +=right;								
			}if(min > inter) loc =i;
			min = Math.min(min, inter);System.out.println(inter);
			
		}
		System.out.println(min);
		return loc;
	}
	public static void main(String []args){
		String startTime = "2015-11-10 00:00:00";
		String endTime = "2015-11-10 23:59:59";
		int segmentId =17372101;
		String busselfId ="38704";
		String qcbh ="038704";
		String xlbh="000046";
		List<Document> arrayList = getGps(segmentId, busselfId, startTime, endTime);
		arrayList =  arrayList.subList(28, 53);
		for(int i =0;i<arrayList.size();i++){
			System.out.println(arrayList.get(i));
		}
		System.out.println(gpsInter(arrayList));
		
		List<Document> icList = getIc(xlbh, qcbh, startTime, endTime);
		icList =  icList.subList(17, 31);
		for(int i=0;i<icList.size();i++) System.out.println(icList.get(i));
		System.out.println(icInter(icList));
		System.out.println(minInterBteGpsIc(gpsInter(arrayList), icInter(icList)));
	}

}
