package mongodb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.bson.Document;

import util.Time;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

public class GetIcArray {
	private static String collectionName = "gps_12_07_IC";
	private static MongoDatabase database = MongoDBAssis.getMongoDatabase();
	public static int getIcAtTime(int segmentId, int sngSerialId, String startTime){
		FindIterable<Document> iter = database
				.getCollection(collectionName)
				.find(new Document("$and", Arrays.asList(new Document(
						"segmentId", segmentId), new Document("sngSerialId",
						sngSerialId), new Document("arriveTime", new Document(
						"$gt", startTime)), new Document("leaveTime",
						new Document("$lte", Time.addTime(startTime, 1200))))))
				.sort(new Document("arriveTime", 1));
		if(iter.iterator().hasNext())
			return iter.iterator().next().getInteger("traffic");
		else return -1;
	}
	/**
	 * get ic data and append to array
	 * 
	 * @param mongodb
	 * @param segmentId
	 * @param sngSerialId
	 * @param startTime
	 * @param endTime
	 * @param array
	 */
	public static void getIcAsArray_int( int segmentId,
			int sngSerialId, String startTime, String endTime,
			final ArrayList<Integer> array) {
		FindIterable<Document> iter = database
				.getCollection(collectionName)
				.find(new Document("$and", Arrays.asList(new Document(
						"segmentId", segmentId), new Document("sngSerialId",
						sngSerialId), new Document("arriveTime", new Document(
						"$gt", startTime)), new Document("leaveTime",
						new Document("$lte", endTime)))))
				.sort(new Document("arriveTime", 1));
		iter.forEach(new Block<Document>() {

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(arg0.getInteger("traffic"));
			}
		});
	}

	/**
	 * get ic data of a line and a station
	 * 
	 * @param mongodb
	 * @param segmentId
	 * @param sngSerialId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public static ArrayList<Integer> getIC_int(
			int segmentId, int sngSerialId, String startTime, String endTime) {
		ArrayList<Integer> arr = new ArrayList<Integer>();
		ArrayList<String> arrStart = Time.getDateTime(startTime), arrEnd = Time
				.getDateTime(endTime);
		String start = startTime, end;
		StringTokenizer str = new StringTokenizer(endTime, " ");
		str.nextToken();
		end = new StringTokenizer(startTime, " ").nextToken() + " "
				+ str.nextToken();
		for (int i = 0; i <= Time.disDays(arrStart.get(0), arrEnd.get(0)); i++) {
			if (i > 0) {
				start = Time.addHours(start, 24);
				end = Time.addHours(end, 24);
			}
			//System.out.println(start);
			//System.out.println(end);
			getIcAsArray_int(segmentId, sngSerialId, start, end, arr);

		}
		return arr;
	}
	public static int getIcByHour(int segmentId, int sngSerialId, String startTime, String endTime){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		
		
		
		getIcAsArray_int(segmentId, sngSerialId, startTime, endTime, arr);
		int sum=0;
		for(Integer s :arr){
			sum+=s;
		}
		return sum;
	}
	public static ArrayList<Integer> getIcByHour_int(int segmentId, int sngSerialId, String startTime, String endTime){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		String start = startTime, end;
		StringTokenizer str = new StringTokenizer(endTime, " ");
		str.nextToken();
		end = new StringTokenizer(startTime, " ").nextToken() + " "
				+ str.nextToken();
		int num  = Time.disHalfHours(start, end);
		int numDays = Time.disDays(startTime, endTime)+1;
		end = Time.addHalfHours(start, 1);
		arr.add(getIcByHour(segmentId, sngSerialId, start, end));
		
		//System.out.println(num);
		for(int i=1;i<num*numDays;i++){
			
			if(i%num !=0){
				start = Time.addHalfHours(start, 1);
				end = Time.addHalfHours(end, 1);
				arr.add(getIcByHour(segmentId, sngSerialId, start, end));
			}else {
				start = Time.addDay(start, 1);
				end = Time.addHalfHours(start, 1);
				arr.add(getIcByHour(segmentId, sngSerialId, start, end));
			}
			
		}
		return arr;
	}
	public static void main(String[] args) {
		int segmentId = 35610028;
		int sngSerialId = 2;
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-11 09:00:00";
		for(int i  =2 ; i <= 2; i++){
			sngSerialId = i;
			System.out.println(getIC_int(
				segmentId, sngSerialId, startTime, endTime));
		}
		ArrayList<Integer> arr = new ArrayList<Integer>();
		String start = startTime, end;
		StringTokenizer str = new StringTokenizer(endTime, " ");
		str.nextToken();
		end = new StringTokenizer(startTime, " ").nextToken() + " "
				+ str.nextToken();
		int num  = Time.disHalfHours(start, end);
		int numDays = Time.disDays(startTime, endTime)+1;
		end = Time.addHalfHours(start, 1);
		arr.add(getIcByHour(segmentId, sngSerialId, start, end)/3);
		
		System.out.println(num);
		for(int i=1;i<num*numDays;i++){
			
			if(i%num !=0){
				start = Time.addHalfHours(start, 1);
				end = Time.addHalfHours(end, 1);
				arr.add(getIcByHour(segmentId, sngSerialId, start, end)/3);
			}else {
				start = Time.addDay(start, 1);
				end = Time.addHalfHours(start, 1);
				arr.add(getIcByHour(segmentId, sngSerialId, start, end)/3);
			}
			
		}
		System.out.println(arr);
		//System.out.println(getIcAtTime(segmentId, sngSerialId, startTime));
	}

}
