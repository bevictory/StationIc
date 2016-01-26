/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:stationTransition.java
 * Package Name:transition
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月17日 上午9:50:57
 */
package transition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import com.mongodb.client.MongoDatabase;

import mongodb.GetIcArray;
import mongodb.MongoDBCoonnection;
import mongodb.QueryBls;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年12月17日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class stationTransition {
	private static int lineNum =4;
	private static int station = 35;
	private static  int stateSpace = 20;
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	public static double[][][][][] getTranTensor_station(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][][][][] tensor = new double[lineNum][station][stateSpace][stateSpace][stateSpace];
		for(int i=0;i<lineNum;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				int loc ;
				if(stationNum <=station ) 
					loc =stationNum; 
				else
					loc =station;
				for(int j=1;j<= station;j++){
					if(j==1&&j<=loc-2){
						ArrayList<Integer> array =null,array2 =null,array3 =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						array2=GetIcArray.getIC_int(mongodb, segmentId, j+1, startTime, endTime);
						array3=GetIcArray.getIC_int(mongodb, segmentId, j+2, startTime, endTime);
						tensor[i][j-1]=toTrans_station(array,array2,array3);
					}else if(j>1&&j<=loc-1){
						ArrayList<Integer> array =null,array2 =null,array3 =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						array2=GetIcArray.getIC_int(mongodb, segmentId, j-1, startTime, endTime);
						array3=GetIcArray.getIC_int(mongodb, segmentId, j+1, startTime, endTime);
						tensor[i][j-1]=toTrans_station(array,array2,array3);
					}else if(j==loc) {
						ArrayList<Integer> array =null,array2 =null,array3 =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						array2=GetIcArray.getIC_int(mongodb, segmentId, j-2, startTime, endTime);
						array3=GetIcArray.getIC_int(mongodb, segmentId, j-1, startTime, endTime);
						tensor[i][j-1]=toTrans_station(array,array2,array3);
					}else
					{
						tensor[i][j-1]=avTrans_station();
					}
					
					
				}
				
			}
		}
		return tensor;
	}
	public static double[][][] avTrans_station(){
		double [][][] arr = new double[stateSpace][stateSpace][stateSpace];
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				for (int k = 0; k < stateSpace; k++) {
				arr[i][j][k] = 1.0/(stateSpace);
				}
			}
		}
		return arr;
	}
	public static double[][][] toTrans_station(ArrayList<Integer> array,ArrayList<Integer> array2,ArrayList<Integer> array3){
		double[][][] tranMatrix = new double[stateSpace][stateSpace][stateSpace];
		
		 double[][] sum = new double[stateSpace][stateSpace];
		int length =0;
		if(array.size() < array2.size()){
			if(array.size() <array3.size()){
				length = array.size();
			}else length = array3.size();
		}else{
			if(array2.size() <array3.size()){
				length = array2.size();
			}else length = array3.size();
		}
		for(int i=0;i<length-1;i++){
			int station_pre=array2.get(i)<stateSpace?array2.get(i):stateSpace-1,
				state_curr=array.get(i)<stateSpace?array.get(i):stateSpace-1,
				state_next=array.get(i+1)<stateSpace?array.get(i+1):stateSpace-1,
				station_next=array3.get(i)<stateSpace?array3.get(i):stateSpace-1;
			sum[(station_pre+station_next)/2][state_curr]+=1;
			tranMatrix[(station_pre+station_next)/2][state_curr][state_next]+=1;
		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
						tranMatrix[i][j][k] /= sum[i][j];
					// else tranMatrix[i][i] =1;
					else {
						//tranMatrix[i][j][k] = 1.0 / ( stateSpace);
						tranMatrix[i][j][j] = 2.0;
					}
				}

			}
		}
//		for(int i=1;i<array.size()-1;i++){
//			if(array.get(i)>=matrixSize||array.get(i+1)>=matrixSize) continue;
//			sum[array.get(i-1)] += 1;
//			tranMatrix[array.get(i-1)][array.get(i)][array.get(i+1)] +=1;
//		}
//		for (int i = 0; i < matrixSize; i++) {
//			for (int j = 0; j < matrixSize; j++) {
//				for (int k = 0; k < matrixSize; k++) {
//					if (sum[i] > 0)
//						tranMatrix[i][j][k] /= sum[i];
//					// else tranMatrix[i][i] =1;
//					else {
//						tranMatrix[i][j][k] = 1.0 / (matrixSize * matrixSize);
//					}
//				}
//
//			}
//		}
		return tranMatrix;
	}
	public static void saveToFile_station(String startTime, String endTime){
		double [][][][][] tensor = getTranTensor_station(startTime, endTime);
		
		
		int flag =1;
		for(int i =0; i<lineNum;i++){
			for(int j=0;j<station;j++){
				File file = new File(".\\trans_station\\trans_"+i+"_"+j+".txt");
				PrintWriter out=null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int k =0;k<stateSpace;k++){
					//System.out.println();
					for(int m=0;m<stateSpace;m++){
						for(int n=0;n<stateSpace;n++){
						//System.out.print(tensor[i][j][k][m]+" ");
						out.println(k+" "+m+" "+n+" "+tensor[i][j][k][m][n]);
						}
					}
				}
				out.close();
			}
		}
		
	}
	public static void sparse(String startTime, String endTime){
		double [][][][][] tensor = getTranTensor_station(startTime, endTime);
		
		
		int flag =1;
		int num =0;
		for(int i =0; i<lineNum;i++){
			for(int j=0;j<station;j++){
				
				for(int k =0;k<stateSpace;k++){
					//System.out.println();
					for(int m=0;m<stateSpace;m++){
						for(int n=0;n<stateSpace;n++){
							if(tensor[i][j][k][m][n]>0) num++;
						}
					}
				}
				
			}
		}
		System.out.println("the sparse of station-associate model is: "+(double)num/(4*station*stateSpace*stateSpace*stateSpace));
	}
	public static double [][][] getTensor_3order(){
		String startTime = "2015-11-10 06:30:00";
		String endTime = "2015-11-12 09:00:00" ;
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		ArrayList<Integer> arr = new ArrayList<Integer>();
		MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
		int segmentId;double [][][]tensor = new double[stateSpace][stateSpace][stateSpace];
		for(int i=0;i<1;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				int j=3;{
				ArrayList<Integer> array =null, array2 = null, array3 = null;
				array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
				
				
				array2=GetIcArray.getIC_int(mongodb, segmentId, j-1, startTime, endTime);
				array3=GetIcArray.getIC_int(mongodb, segmentId, j+1, startTime, endTime);
				tensor = toTrans_station(array, array2, array3);
				//out.println();
				System.out.println(array);
				}
			}			
		}
		return tensor;
	}
	public static void main(String[] args){

		String startTime = "2015-11-10 06:30:00";
		String endTime = "2015-11-16 09:00:00" ;
		//saveToFile_station(startTime, endTime);
		sparse(startTime, endTime);
	}

}
