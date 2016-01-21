/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:hybridTransition.java
 * Package Name:transition
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月17日 上午10:11:38
 */
package transition;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

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
public class hybridTransition {
	private static  int matrixSize = 35;
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	public static double[][][][][][][] getTranTensor_hybrid(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][][][][][][] tensor = new double[4][35][matrixSize][matrixSize][matrixSize][matrixSize][matrixSize];
		for(int i=0;i<4;i++){
			if(i<segment.size()){
				
				
				
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				int loc ;
				if(stationNum <=35 ) 
					loc =stationNum; 
				else
					loc =35;
				for(int j=1;j<= 35;j++){
					if(j==1&&j<=loc-2){
						ArrayList<Integer> array =null,array2 =null,array3 =null;
						ArrayList<ArrayList<Integer>> arr= new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						
						
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j+1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j+2, startTime, endTime));
						for(int k=0;k<segment.size();k++){
							if(k!=i) {
								if(j <QueryBls.getStationNum(mongodb,Integer.valueOf(segment.get(k)) ))
									arr.add(GetIcArray.getIC_int(mongodb,Integer.valueOf(segment.get(k)), j, startTime, endTime));
								else 
									arr.add(GetIcArray.getIC_int(mongodb,Integer.valueOf(segment.get(k)), QueryBls.getStationNum(mongodb,Integer.valueOf(segment.get(k)) ), startTime, endTime));
							}
						}
						tensor[i][j-1]=toTranMatrix_hybrid(arr);
					}else if(j>1&&j<=loc-1){
						ArrayList<ArrayList<Integer>> arr= new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						
						
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j+1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j+2, startTime, endTime));
						for(int k=0;k<segment.size();k++){
							if(k!=i) {
								if(j <QueryBls.getStationNum(mongodb,Integer.valueOf(segment.get(k)) ))
									arr.add(GetIcArray.getIC_int(mongodb,Integer.valueOf(segment.get(k)), j, startTime, endTime));
								else 
									arr.add(GetIcArray.getIC_int(mongodb,Integer.valueOf(segment.get(k)), QueryBls.getStationNum(mongodb,Integer.valueOf(segment.get(k)) ), startTime, endTime));
							}
						}
						tensor[i][j-1]=toTranMatrix_hybrid(arr);
					}else {
						ArrayList<ArrayList<Integer>> arr= new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						
						
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j+1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j+2, startTime, endTime));
						for(int k=0;k<segment.size();k++){
							if(k!=i) {
								if(j <QueryBls.getStationNum(mongodb,Integer.valueOf(segment.get(k)) ))
									arr.add(GetIcArray.getIC_int(mongodb,Integer.valueOf(segment.get(k)), j, startTime, endTime));
								else 
									arr.add(GetIcArray.getIC_int(mongodb,Integer.valueOf(segment.get(k)), QueryBls.getStationNum(mongodb,Integer.valueOf(segment.get(k)) ), startTime, endTime));
							}
						}
						tensor[i][j-1]=toTranMatrix_hybrid(arr);
					}
					
					
				}
				
				
					
					
				}
				
			}
		
		return tensor;
	}
	public static double[][][] avMatrix_time(){
		double [][][] arr = new double[matrixSize][matrixSize][matrixSize];
		for(int i=0;i<matrixSize;i++){
			for(int j=0;j<matrixSize;j++){
				for (int k = 0; k < matrixSize; k++) {
				arr[i][j][k] = 1.0/(matrixSize*matrixSize);
				}
			}
		}
		return arr;
	}
	public static double[][][][][] toTranMatrix_hybrid(ArrayList<ArrayList<Integer>> array){
		double[][][][][] tranMatrix = new double[matrixSize][matrixSize][matrixSize][matrixSize][matrixSize];
		
		 double[][][][] sum = new double[matrixSize][matrixSize][matrixSize][matrixSize];
		 int []len={} ;
		 
		 for(int i=0;i<6;i++){
			 len[i]=(array.get(i).size());
		 }
		 Arrays.sort(len);
		 int length = len[0];
		for(int i=1;i<length;i++){
			
			sum[array.get(0).get(i-1)][(array.get(1).get(i)+array.get(2).get(i))/2][(array.get(3).get(i)+array.get(4).get(i)+array.get(5).get(i))/3][array.get(0).get(i)] += 1;
			tranMatrix[array.get(0).get(i-1)][(array.get(1).get(i)+array.get(2).get(i))/2][(array.get(3).get(i)+array.get(4).get(i)+array.get(5).get(i))/3][array.get(0).get(i)][array.get(0).get(i+1)] +=1;
		}
		for (int i = 0; i < matrixSize; i++) {
			for (int j = 0; j < matrixSize; j++) {
				for (int k = 0; k < matrixSize; k++) {
					for (int m = 0; m < matrixSize; m++) {
						for (int n = 0; n < matrixSize; n++) {
							if (sum[i][j][k][m] > 0)
								tranMatrix[i][j][k][m][n] /= sum[i][j][k][m];
							// else tranMatrix[i][i] =1;
							else {
								tranMatrix[i][j][k][m][n] = 1.0 / (matrixSize * matrixSize);
							}
						}
					}
					
				}

			}
		}
		return tranMatrix;
	}
	public static void saveToFile_hybrid(String startTime, String endTime){
		double [][][][][][][] tensor = getTranTensor_hybrid(startTime, endTime);
		
		
		int flag =1;
		for(int i =0; i<4;i++){
			for(int j=0;j<35;j++){
				File file = new File(".\\trans_hybrid\\trans_"+i+"_"+j+".txt");
				PrintWriter out=null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for(int k =0;k<matrixSize;k++){
					//System.out.println();
					for(int m=0;m<matrixSize;m++){
						for(int n=0;n<matrixSize;n++){
						//System.out.print(tensor[i][j][k][m]+" ");
						out.println(i+" "+j+" "+k+" "+m+" "+n+" "+tensor[i][j][k][m][n]);
						}
						
						
					}
				}
				out.close();
			}
		}
		
	}

}
