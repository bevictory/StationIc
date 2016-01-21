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
public class SequenceToTransition {
	private static  int matrixSize = 30;
	private static double[] sum = new double[30];
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getMongoData();
	public static double[][][][] getTranTensor(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		//segment.add("35610028");segment.add("35557702");
		segment.add("35632502");
		//segment.add("35641294");
		int stationNum; int segmentId;
		double [][][][] tensor = new double[4][43][matrixSize][matrixSize];
		for(int i=0;i<segment.size();i++){
			segmentId =Integer.valueOf(segment.get(i));
		    stationNum = QueryBls.getStationNum(mongodb,segmentId );
			for(int j=1;j<= 1;j++){
				ArrayList<Integer> array =null;
				array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
				tensor[i][j-1]=toTranMatrix(array);
			}
		}
		return tensor;
	}
	
	public static double[][] toTranMatrix(ArrayList<Integer> array){
		double[][] tranMatrix = new double[matrixSize][matrixSize];
		
		
		for(int i=0;i<array.size()-1;i++){
			sum[array.get(i)] += 1;
			tranMatrix[array.get(i)][array.get(i+1)] +=1;
		}
		for(int i=0;i<matrixSize;i++){
			for(int j=0;j<matrixSize;j++){
				if(sum[i] > 0);//tranMatrix[i][j] /= sum[i]; 
				else tranMatrix[i][i] =1;
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
		for(int i =0; i<1;i++){
			for(int j=0;j<1;j++){
				for(int k =0;k<matrixSize;k++){
					System.out.println();
					for(int m=0;m<matrixSize;m++){
						System.out.print(tensor[i][j][k][m]+" ");
						//out.println(i+" "+j+" "+k+" "+m+" "+tensor[i][j][k][m]);
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
	public static void main(String[] args){
//		int[] arr = {0,1,4,5,8,1,2,3,4,8,7,4,0,5,6};
//		ArrayList<Integer> array = new ArrayList<Integer>();
//		for(int i=0;i<arr.length;i++){
//			array.add(arr[i]);
//		}
//		System.out.println(array);
//		System.out.println(Arrays.deepToString(toTranMatrix(array)));
		
		String startTime = "2015-11-12 06:30:00";
		String endTime = "2015-11-12 09:00:00" ;
		saveToFile(startTime, endTime);
		//double [][][][] arr =new double[4][43][30][30];
		//saveToFile(arr);
	}

	
}
