/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:StationIc
 * File Name:timePrediction.java
 * Package Name:transition
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月23日 下午2:56:25
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
  * @version 1.0, 2015年12月23日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class timePrediction {
	private static int lineNum =4;
	private static int station = 35;
	private static  int stateSpace = 35;
	private static int loca =5;
	private static MongoDatabase mongodb= MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();
	public static double[][][] getTranTensor_time(String startTime, String endTime){
		ArrayList<String> segment =new ArrayList<String>(); 
		segment.add("35610028");segment.add("35557702");segment.add("35632502");segment.add("35641294");
		int stationNum; int segmentId;
		double [][][] tensor = new double[4][station][3];
		for(int i=0;i<lineNum;i++){
			if(i<segment.size()){
				segmentId =Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb,segmentId );
				for(int j=1;j<= station;j++){
					if(j<=stationNum){
						ArrayList<Integer> array =null;
						array=GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
						tensor[i][j-1][0]=array.get(loca)<stateSpace?array.get(loca):stateSpace-1;
						tensor[i][j-1][1]=array.get(loca+1)<stateSpace?array.get(loca+1):stateSpace-1;
						tensor[i][j-1][2]=array.get(loca+2)<stateSpace?array.get(loca+2):stateSpace-1;
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
