/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:GetIcArray.java
 * Package Name:mongodb
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月26日 下午3:14:19
 */
package mongodb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import org.bson.Document;

import com.mongodb.Block;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import util.Time;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月26日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class GetIcArray {
	private static String collectionName = "gps_12_07_IC";
	private static  int stateSpace = 20;
	public static void getIcAsArray(MongoDatabase mongodb,int segmentId, int sngSerialId,
		String startTime, String endTime,final ArrayList<Document> array){
		FindIterable<Document> iter =mongodb.getCollection("gps").find(new Document("$and",Arrays.asList(new Document("segmentId",segmentId),new Document("sngSerialId",1),
			new Document("arriveTime",new Document("$gt",startTime))
			,new Document("leaveTime",new Document("$lte",endTime))))).sort(new Document("arriveTime",1));
		iter.forEach(new Block<Document>(){

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add(new Document("arriveTime",arg0.getString("arriveTime"))
					.append("leaveTime", arg0.getString("leaveTime")).append("traffic", arg0.getInteger("traffic")));
			}
		});	
	}
	public static ArrayList<Document> getIC(MongoDatabase mongodb,int segmentId, int sngSerialId,String startTime,String endTime){
		ArrayList<Document> arr = new ArrayList<Document>();
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0){start = Time.addHours(start, 24);
			end = Time.addHours(end, 24);}
			//System.out.println(start);
			//System.out.println(end);
			getIcAsArray(mongodb, segmentId, sngSerialId, start,end ,arr);
			
		}
		return arr;
	}
	public static void getIcAsArray_int(MongoDatabase mongodb,int segmentId, int sngSerialId,
		String startTime, String endTime,final ArrayList<Integer> array){
		FindIterable<Document> iter =mongodb.getCollection(collectionName).find(new Document("$and",Arrays.asList(new Document("segmentId",segmentId),new Document("sngSerialId",sngSerialId),
			new Document("arriveTime",new Document("$gt",startTime))
			,new Document("leaveTime",new Document("$lte",endTime))))).sort(new Document("arriveTime",1));
		iter.forEach(new Block<Document>(){

			@Override
			public void apply(Document arg0) {
				// TODO Auto-generated method stub
				array.add( arg0.getInteger("traffic"));
			}
		});	
	}
	public static ArrayList<Integer> getIC_int(MongoDatabase mongodb,int segmentId, int sngSerialId,String startTime,String endTime){
		ArrayList<Integer> arr = new ArrayList<Integer>();
		ArrayList<String> arrStart = Time.getDateTime(startTime),arrEnd = Time.getDateTime(endTime);
		String start=startTime,end;
		StringTokenizer str =new StringTokenizer(endTime, " ");
		str.nextToken();
		end =new StringTokenizer(startTime, " ").nextToken()+" "+str.nextToken(); 
		for (int i=0;i<=Time.disDays(arrStart.get(0), arrEnd.get(0));i++){
			if(i>0){start = Time.addHours(start, 24);
			end = Time.addHours(end, 24);}
			//System.out.println(start);
			//System.out.println(end);
			getIcAsArray_int(mongodb, segmentId, sngSerialId, start,end ,arr);
			
		}
		return arr;
	}
	
	public static String getCollectionName() {
		return collectionName;
	}
	public static void setCollectionName(String collectionName) {
		GetIcArray.collectionName = collectionName;
	}
	public static double[][] toTranMatrix(ArrayList<Integer> array){
		double[][] tranMatrix = new double[stateSpace][stateSpace];
		
		 double[] sum = new double[stateSpace];
		for(int i=0;i<array.size()-1;i++){
			if(array.get(i)>=stateSpace&&array.get(i+1)>=stateSpace) {
				
				sum[stateSpace-1] += 1;
				tranMatrix[stateSpace-1][stateSpace-1] +=1;
			}else if(array.get(i)>=stateSpace){
				sum[stateSpace-1] += 1;
				tranMatrix[stateSpace-1][array.get(i+1)] +=1;
			}else if(array.get(i+1)>=stateSpace){
				sum[array.get(i)] += 1;
				tranMatrix[array.get(i)][stateSpace-1] +=1;
			}else {
				sum[array.get(i)] += 1;
				tranMatrix[array.get(i)][array.get(i+1)] +=1;
			}
			
		}
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				if(sum[i] > 0)tranMatrix[i][j] /= sum[i]; 
				//else tranMatrix[i][i] =1;
				else {					
						//tranMatrix[i][i] =1.0/stateSpace;	
					tranMatrix[i][i] =0.0;	
				}
			}
		}
		return tranMatrix;
	}
	public static double [][] getTransmatrix(){
		setCollectionName("gps_12_07_IC");
		String startTime = "2015-12-07 06:30:00";
		String endTime = "2015-12-11 09:00:00" ;
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		ArrayList<Integer> arr = new ArrayList<Integer>();
		MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
		int segmentId;double [][]matrix = new double[stateSpace][stateSpace];
		for(int i=0;i<1;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				int j=3;{
				ArrayList<Integer> array =null;
				array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
				
				matrix = toTranMatrix(array);
				
				//out.println();
				System.out.println(array);
				}
			}			
		}
		return matrix;
	}
	public static void main(String [] args){
		//getTransmatrix();
		setCollectionName("gps_12_07_IC");
		String startTime = "2015-12-07 06:30:00";
		String endTime = "2015-12-11 09:00:00" ;
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		ArrayList<Integer> arr = new ArrayList<Integer>();
		MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
		File file = new File("data.txt");
		PrintWriter out=null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int segmentId;
		for(int i=0;i<1;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				for(int j =3;j<=3;j++){
				ArrayList<Integer> array =null;
				array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
//				double [][]matrix = new double[stateSpace][stateSpace];
//				matrix = toTranMatrix(array);
//				for(int k=0;k<stateSpace;k++){
//					for(int m=0;m<stateSpace;m++){
//						out.print(matrix[k][m]+" ");
//					}
//					out.println();
//				}
				for(int k = 0; k < array.size(); k ++) {
					out.print(array.get(k)+" ");
				}
				//out.println();
				System.out.println(array);
				}
			}			
		}
		out.close();
		//System.out.println(getIC_int(MongoDBCoonnection.getInstance().getMongoData(), 35641294, 7, startTime, endTime));
		
	}
}
