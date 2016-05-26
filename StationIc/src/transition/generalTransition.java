/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:SequenceToTransition.java
 * Package Name:transition
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月30日 上午9:36:54
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
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月30日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class generalTransition {
	private static int station = 35;
	private static  int stateSpace = 20;
	
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	public static double[][][][] getTranTensor(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][][][] tensor = new double[4][35][stateSpace][stateSpace];
		for(int i=0;i<4;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				for(int j=1;j<= station;j++){
					if(j<=stationNum){
						ArrayList<Integer> array =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						if(i==1&&j==6) System.out.println(array);
						tensor[i][j-1]=toTranMatrix(array);
					}else {
						//tensor[i][j-1] = unitMatrix();
						tensor[i][j-1] =avMatrix();
					}
				}
				
			}else{
				for(int j=0;j<stateSpace;j++){
					tensor[i][j]=unitMatrix();
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
						//tranMatrix[i][j] =1.0/stateSpace;
					//tranMatrix[i][j] = 1.0;
				}
			}
		}
		return tranMatrix;
	}
	
	public static void saveToFile(String startTime, String endTime){
		double [][][][] tensor = getTranTensor(startTime, endTime);
		File file = new File("tranTensor.txt");
		PrintWriter out=null;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int flag =1;
		for(int i =0; i<4;i++){
			for(int j=0;j<station;j++){
				for(int k =0;k<stateSpace;k++){
					//System.out.println();
					for(int m=0;m<stateSpace;m++){
						//System.out.print(tensor[i][j][k][m]+" ");
						out.println(i+" "+j+" "+k+" "+i+" "+j+" "+m+" "+tensor[i][j][k][m]);
//							if(sum[k] >0) out.println(i+" "+j+" "+k+" "+m+" "+tensor[i][j][k][m]/sum[k]);
//							else {
//								if(k!=m) {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)0.0);
//								}else {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)1);
//								}
								
//							}
						
					}
				}
			}
		}
		out.close();
	}
	public static void sparse(String startTime, String endTime){
		double [][][][] tensor = getTranTensor(startTime, endTime);
		
		int flag =1;
		int num=0;
		for(int i =0; i<4;i++){
			for(int j=0;j<station;j++){
				for(int k =0;k<stateSpace;k++){
					//System.out.println();
					for(int m=0;m<stateSpace;m++){
						if(tensor[i][j][k][m] > 0) num++;
						//System.out.print(tensor[i][j][k][m]+" ");
						//out.println(i+" "+j+" "+k+" "+i+" "+j+" "+m+" "+tensor[i][j][k][m]);
//							if(sum[k] >0) out.println(i+" "+j+" "+k+" "+m+" "+tensor[i][j][k][m]/sum[k]);
//							else {
//								if(k!=m) {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)0.0);
//								}else {
//									out.println(i+" "+j+" "+k+" "+m+" "+(double)1);
//								}
								
//							}
						
					}
				}
			}
		}
		System.out.println("the sparse of general model is: "+(double)num/(4*station*stateSpace*stateSpace));
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
		//saveToFile(startTime, endTime);
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
