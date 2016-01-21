/**
 * Copyright(C) 2015 ���� Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:SequenceToTransition.java
 * Package Name:transition
 * @Description:һ�仰�������ļ���ʲô
 * @author dai.guohui
 * @Date:2015��11��30�� ����9:36:54
 */
package transition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.bson.Document;

import com.mongodb.client.MongoDatabase;

import mongodb.GetIcArray;
import mongodb.MongoDBCoonnection;
import mongodb.QueryBls;

/**
  * ��˵��
  * @author dai.guohui
  * @version 1.0, 2015��11��30��  ÿ���޸ĺ���°汾�ţ����ں��޸�����
  * @see	[�����/����]
  * @since	[��Ʒ/ģ��汾]
  */
public class SequenceToTransition3 {
	private static int lineNum =4;
	private static int station = 35;
	private static  int stateSpace = 35;
	private static int loca =5;
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	public static double[][] getTranTensor(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][] tensor = new double[4][35];
		for(int i=0;i<lineNum;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				for(int j=1;j<= station;j++){
					if(j<=stationNum){
						ArrayList<Integer> array =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						tensor[i][j-1]=array.get(loca)<=stateSpace?array.get(loca):stateSpace-1;
						//tensor[i][j-1]=toTranMatrix(array);
					}else {
						//tensor[i][j-1] = unitMatrix();
						//tensor[i][j-1] =avMatrix();
					}
				}
				
			}
		}
		return tensor;
	}
	public static double[][][] getTranTensor_time(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][][] tensor = new double[4][stateSpace][3];
		for(int i=0;i<lineNum;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				for(int j=1;j<= station;j++){
					if(j<=stationNum){
						ArrayList<Integer> array =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						tensor[i][j-1][0]=array.get(loca)<=stateSpace?array.get(loca):stateSpace-1;
						tensor[i][j-1][1]=array.get(loca+1)<=stateSpace?array.get(loca+1):stateSpace-1;
						tensor[i][j-1][2]=array.get(loca+2)<=stateSpace?array.get(loca+2):stateSpace-1;
						//tensor[i][j-1]=toTranMatrix(array);
					}else {
						//tensor[i][j-1] = unitMatrix();
						//tensor[i][j-1] =avMatrix();
					}
				}
				
			}
		}
		return tensor;
	}
	public static double[][] avMatrix(){
		double [][] arr = new double[stateSpace][stateSpace];
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				arr[i][j] = 1.0/stateSpace;
			}
		}
		return arr;
	}
	public static double[][] unitMatrix(){
		double [][] arr = new double[stateSpace][stateSpace];
		for(int i=0;i<stateSpace;i++){
			arr[i][i] =1;
		}
		return arr;
	}
	public static double[][] toTranMatrix(ArrayList<Integer> array){
		double[][] tranMatrix = new double[stateSpace][stateSpace];
		
		 double[] sum = new double[stateSpace];
		for(int i=0;i<array.size()-1;i++){
			if(array.get(i)>=stateSpace||array.get(i+1)>=stateSpace) continue;
			sum[array.get(i)] += 1;
			tranMatrix[array.get(i)][array.get(i+1)] +=1;
		}
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				if(sum[i] > 0)tranMatrix[i][j] /= sum[i]; 
				//else tranMatrix[i][i] =1;
				else {					
						tranMatrix[i][j] =1.0/stateSpace;					
				}
			}
		}
		return tranMatrix;
	}
	public static void saveToFile(String startTime, String endTime){
		double [][] tensor = getTranTensor(startTime, endTime);
		File file = new File("traffic"+loca+".txt");
		PrintWriter out=null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int flag =1;double f=1.0;
		for(int i =0; i<4;i++){
			for(int j=0;j<35;j++){
//				for(int k =0;k<matrixSize;k++){
//					for(int m=0;m<matrixSize;m++){
						out.println(i+" "+j+" "+(int)tensor[i][j]+" "+f);
				//System.out.println(i+" "+j+" "+tensor[i][j]);
//							if(sum[k] >0) out.println(i+" "+j+" "+k+" "+m+" "+tensor[i][j][k][m]/sum[k]);
//							else {
//								if(k!=m) {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)0.0);
//								}else {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)1);
//								}
								
//							}
						
//					}
//				}
			}
		}
		out.close();
	}
	public static void saveToFile_time(String startTime, String endTime){
		double [][][] tensor = getTranTensor_time(startTime, endTime);
		File file_pre = new File(".\\pre_time\\prediction.txt");
		PrintWriter out_pre=null;
		try {
			out_pre = new PrintWriter(new BufferedWriter(new FileWriter(file_pre)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int flag =1;double f=1.0;
		for(int i =0; i<4;i++){
			for(int j=0;j<35;j++){
				File file = new File(".\\pre_time\\init_"+i+"_"+j+".txt");
				PrintWriter out=null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				for(int k =0;k<matrixSize;k++){
//					for(int m=0;m<matrixSize;m++){
						out.println((int)tensor[i][j][0]+" "+(int)tensor[i][j][1]+" "+f);
						out_pre.println(i+" "+j+" "+(int)tensor[i][j][2]);
				//System.out.println(i+" "+j+" "+tensor[i][j]);
//							if(sum[k] >0) out.println(i+" "+j+" "+k+" "+m+" "+tensor[i][j][k][m]/sum[k]);
//							else {
//								if(k!=m) {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)0.0);
//								}else {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)1);
//								}
								
//							}
						
//					}
//				}
						out.close();
						
			}
		}
		out_pre.close();
		
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
		String endTime = "2015-11-10 09:00:00" ;
		saveToFile_time(startTime, endTime);
		//double [][][][] arr =new double[4][43][30][30];
		//saveToFile(arr);
	}

	
}
