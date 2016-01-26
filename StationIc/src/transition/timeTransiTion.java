/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:timeTransiTion.java
 * Package Name:transition
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月17日 上午10:09:47
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
public class timeTransiTion {
	private static int lineNum =4;
	private static int station = 35;
	private static  int stateSpace = 35;
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	public static double[][][][][] getTranTensor_time(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][][][][] tensor = new double[4][35][stateSpace][stateSpace][stateSpace];
		for(int i=0;i<lineNum;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				for(int j=1;j<= station;j++){
					if(j<=stationNum){
						ArrayList<Integer> array =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						tensor[i][j-1]=toTrans_time(array);
					}else {
						//tensor[i][j-1] = unitMatrix();
						tensor[i][j-1] =avTrans_time();
					}
				}
				
			}
		}
		return tensor;
	}
	public static double[][][] avTrans_time(){
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
	public static double[][][] toTrans_time(ArrayList<Integer> array){
		double[][][] trans = new double[stateSpace][stateSpace][stateSpace];
		
		 double[][] sum = new double[stateSpace][stateSpace];
		for(int i=1;i<array.size()-1;i++){
			//if(array.get(i)>=stateSpace||array.get(i+1)>=stateSpace) continue;
			int state_pre=array.get(i-1)<stateSpace?array.get(i-1):stateSpace-1,
				state_curr=array.get(i)<stateSpace?array.get(i):stateSpace-1,
				state_next=array.get(i+1)<stateSpace?array.get(i+1):stateSpace-1;
				sum[state_pre][state_curr] += 1;
				trans[state_pre][state_curr][state_next] +=1;
				
//			sum[array.get(i-1)][array.get(i)] += 1;
//			tranMatrix[array.get(i-1)][array.get(i)][array.get(i+1)] +=1;
		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
						trans[i][j][k] /= sum[i][j];
					// else tranMatrix[i][i] =1;
					else {
						trans[i][j][k] = 1.0 / ( stateSpace);
					}
				}

			}
		}
		return trans;
	}
	public static void saveToFile_time(String startTime, String endTime){
		double [][][][][] tensor = getTranTensor_time(startTime, endTime);
		
		
		int flag =1;
		for(int i =0; i<lineNum;i++){
			for(int j=0;j<station;j++){
				File file = new File(".\\trans_time\\trans_"+i+"_"+j+".txt");
				PrintWriter out=null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int k=0;k<stateSpace;k++){
					for(int m=0;m<stateSpace;m++){
						for(int n=0;n<stateSpace;n++){
							out.println(k+" "+m+" "+n+" "+tensor[i][j][k][m][n]);
						}
					}
				}
//				for(int k =0;k<matrixSize;k++){
//					//System.out.println();
//					for(int m=0;m<matrixSize;m++){
//						for(int n=0;n<matrixSize;n++){
//						//System.out.print(tensor[i][j][k][m]+" ");
//						out.println(k+" "+m+" "+n+" "+tensor[i][j][k][m][n]);
//						}
////							if(sum[k] >0) out.println(i+" "+j+" "+k+" "+m+" "+tensor[i][j][k][m]/sum[k]);
////							else {
////								if(k!=m) {
////									out.println(i+" "+j+" "+k+" "+m+" "+(double)0.0);
////								}else {
////									out.println(i+" "+j+" "+k+" "+m+" "+(double)1);
////								}
//								
////							}
//						
//					}
//				}
				out.close();
			}
		}
		
	}
	public static void sparse(String startTime, String endTime){
		double [][][][][] tensor = getTranTensor_time(startTime, endTime);
		
		
		int flag =1;
		int num =0;
		for(int i =0; i<lineNum;i++){
			for(int j=0;j<station;j++){
				
				for(int k=0;k<stateSpace;k++){
					for(int m=0;m<stateSpace;m++){
						for(int n=0;n<stateSpace;n++){
							if(tensor[i][j][k][m][n]>0) num++;
						}
					}
				}
			
			}
		}
		System.out.println("the sparse of time-associate model is: "+(double)num/(4*station*stateSpace*stateSpace*stateSpace));
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
				
				
				
				tensor = toTrans_time(array);
				//out.println();
				System.out.println(array);
				}
			}			
		}
		return tensor;
	}
	public static void main(String[] args){
//		int[] arr = {0,1,4,5,8,1,2,3,4,8,7,4,0,5,6};
//		ArrayList<Integer> array = new ArrayList<Integer>();
//		for(int i=0;i<arr.length;i++){
//			array.add(arr[i]);
//		}
//		System.out.println(array);
//		System.out.println(Arrays.deepToString(toTranMatrix(array)));
		
		String startTime = "2015-11-10 06:30:00";
		String endTime = "2015-11-16 09:00:00" ;
		//saveToFile_time(startTime, endTime);
		sparse(startTime, endTime);
//		File file = new File(".\\trans\\trans"+0+"_"+0+".txt");
//		try {
//			file.createNewFile();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		//double [][][][] arr =new double[4][43][30][30];
		//saveToFile(arr);
	}


}
