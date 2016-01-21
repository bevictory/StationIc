/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:hybridPrediction.java
 * Package Name:transition
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年12月22日 下午8:08:51
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
  * @version 1.0, 2015年12月22日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class hybridPrediction {
	private static int lineNum =4;
	private static int station = 35;
	private static int stateSpace = 35;
	private static int loca =6;
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();

	public static double[][][] getPre_hybrid2(String startTime, String endTime) {
		ArrayList<String> segment = new ArrayList<String>();
		segment.add("35610028");
		segment.add("35557702");
		segment.add("35632502");
		segment.add("35641294");
		int stationNum;
		int segmentId;
		double [][][] tensor = new double[lineNum][station][4];
		
			for(int i=0;i<4;i++) {
				
				segmentId = Integer.valueOf(segment.get(i));
				stationNum = QueryBls.getStationNum(mongodb, segmentId);
				int loc;
				if (stationNum <= station)
					loc = stationNum;
				else
					loc = station;
				for (int j = 1; j <= station; j++) {
					if (j == 1 && j <= loc - 2) {
						ArrayList<Integer> array = null, array2 = null, array3 = null;
						ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));

						// arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j + 1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j + 2, startTime, endTime));
						for (int k = 0; k < segment.size(); k++) {
							if (k != i) {
								if (j <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))))
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)), j, startTime,
										endTime));
								else
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)),
										QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))), startTime,
										endTime));
							}
						}
						tensor[i][j-1][0]=(arr.get(1).get(loca)<stateSpace?arr.get(1).get(loca):stateSpace-1 + arr.get(2).get(loca)<stateSpace?arr.get(2).get(loca):stateSpace-1)/ 2;
						tensor[i][j-1][1]=(arr.get(3).get(loca)<stateSpace?arr.get(3).get(loca):stateSpace-1 + arr.get(4).get(loca)<stateSpace?arr.get(4).get(loca):stateSpace-1 
							+ arr.get(5).get(loca)<stateSpace?arr.get(5).get(loca):stateSpace-1) / 3;
						tensor[i][j-1][2]=arr.get(0).get(loca)<stateSpace?arr.get(0).get(loca):stateSpace-1;
						tensor[i][j-1][3]=arr.get(0).get(loca+1)<stateSpace?arr.get(0).get(loca+1):stateSpace-1;
//						tensor[i][j-1][0]=arr.get(0).get((arr.get(1).get(loca) + arr.get(2).get(loca))
//							/ 2);
//						tensor[i][j-1][1]=arr.get(0).get((arr.get(3).get(loca) + arr.get(4).get(loca) + arr.get(5).get(loca)) / 3);
//						tensor[i][j-1][2]=arr.get(0).get(loca);
//						tensor[i][j-1][3]=arr.get(0).get(loca+1);
						//tensor[i][j - 1] = toTranMatrix_hybrid(arr);
					} else if (j > 1 && j <= loc - 1) {
						ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));

						// arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j - 1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j + 1, startTime, endTime));
						for (int k = 0; k < segment.size(); k++) {
							if (k != i) {
								if (j <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))))
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)), j, startTime,
										endTime));
								else
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)),
										QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))), startTime,
										endTime));
							}
						}
						tensor[i][j-1][0]=(arr.get(1).get(loca)<stateSpace?arr.get(1).get(loca):stateSpace-1 + arr.get(2).get(loca)<stateSpace?arr.get(2).get(loca):stateSpace-1)/ 2;
						tensor[i][j-1][1]=(arr.get(3).get(loca)<stateSpace?arr.get(3).get(loca):stateSpace-1 + arr.get(4).get(loca)<stateSpace?arr.get(4).get(loca):stateSpace-1 
							+ arr.get(5).get(loca)<stateSpace?arr.get(5).get(loca):stateSpace-1) / 3;
						tensor[i][j-1][2]=arr.get(0).get(loca)<stateSpace?arr.get(0).get(loca):stateSpace-1;
						tensor[i][j-1][3]=arr.get(0).get(loca+1)<stateSpace?arr.get(0).get(loca+1):stateSpace-1;
//						tensor[i][j-1][0]=arr.get(0).get((arr.get(1).get(loca) + arr.get(2).get(loca))
//							/ 2);
//						tensor[i][j-1][1]=arr.get(0).get((arr.get(3).get(loca) + arr.get(4).get(loca) + arr.get(5).get(loca)) / 3);
//						tensor[i][j-1][2]=arr.get(0).get(loca);
//						tensor[i][j-1][3]=arr.get(0).get(loca+1);
						//tensor[i][j - 1] = toTranMatrix_hybrid(arr);
					} else if (j == loc) {
						ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));

						// arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j - 2, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j - 1, startTime, endTime));
						for (int k = 0; k < segment.size(); k++) {
							if (k != i) {
								if (j <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))))
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)), j, startTime,
										endTime));
								else
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)),
										QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))), startTime,
										endTime));
							}
						}
						tensor[i][j-1][0]=(arr.get(1).get(loca)<stateSpace?arr.get(1).get(loca):stateSpace-1 + arr.get(2).get(loca)<stateSpace?arr.get(2).get(loca):stateSpace-1)/ 2;
						tensor[i][j-1][1]=(arr.get(3).get(loca)<stateSpace?arr.get(3).get(loca):stateSpace-1 + arr.get(4).get(loca)<stateSpace?arr.get(4).get(loca):stateSpace-1 
							+ arr.get(5).get(loca)<stateSpace?arr.get(5).get(loca):stateSpace-1) / 3;
						tensor[i][j-1][2]=arr.get(0).get(loca)<stateSpace?arr.get(0).get(loca):stateSpace-1;
						tensor[i][j-1][3]=arr.get(0).get(loca+1)<stateSpace?arr.get(0).get(loca+1):stateSpace-1;
//						tensor[i][j-1][0]=arr.get(0).get((arr.get(1).get(loca) + arr.get(2).get(loca))
//							/ 2);
//						tensor[i][j-1][1]=arr.get(0).get((arr.get(3).get(loca) + arr.get(4).get(loca) + arr.get(5).get(loca)) / 3);
//						tensor[i][j-1][2]=arr.get(0).get(loca);
//						tensor[i][j-1][3]=arr.get(0).get(loca+1);
						
					} else {
						
					}

				}

			}

		

		return tensor;
	}
	public static void saveToFile_hybrid2(String startTime, String endTime){
		double [][][] tensor = getPre_hybrid2(startTime, endTime);
		File file_pre = new File(".\\pre_hybrid2\\prediction.txt");
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
				File file = new File(".\\pre_hybrid2\\init_"+i+"_"+j+".txt");
				PrintWriter out=null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
//				for(int k =0;k<matrixSize;k++){
//					for(int m=0;m<matrixSize;m++){
						out.println((int)tensor[i][j][0]+" "+(int)tensor[i][j][1]+" "+(int)tensor[i][j][2]+" "+f);
						out_pre.println(i+" "+j+" "+(int)tensor[i][j][3]);
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
		saveToFile_hybrid2(startTime, endTime);
		//double [][][][] arr =new double[4][43][30][30];
		//saveToFile(arr);
	}
}
