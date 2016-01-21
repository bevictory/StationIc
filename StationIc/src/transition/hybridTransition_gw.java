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
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import com.mongodb.client.MongoDatabase;

import mongodb.GetIcArray;
import mongodb.MongoDBCoonnection;
import mongodb.QueryBls;

/**
 * 类说明
 * @author dai.guohui
 * @version 1.0, 2015年12月17日 每次修改后更新版本号，日期和修改内容
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class hybridTransition_gw {
	private static int lineNum =4;
	private static int station = 35;
	private static int stateSpace = 35;
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getMongoData();

	public static double[][][][][][] getTranTensor_hybrid(int loca,String startTime, String endTime) {
		ArrayList<String> segment = new ArrayList<String>();
		segment.add("35610028");
		segment.add("35557702");
		segment.add("35632502");
		segment.add("35641294");
		int stationNum;
		int segmentId;
		double[][][][][][] tensor = new double[1][station][stateSpace][lineNum][stateSpace][stateSpace];
		
		
		int i = 0;
		segmentId = Integer.valueOf(segment.get(loca));
		stationNum = QueryBls.getStationNum(mongodb, segmentId);
		int loc;
		if (stationNum <= station)
			loc = stationNum;
		else
			loc = station;
		for (int j = 1; j <= station; j++) {
			if (j <= stationNum) {
				ArrayList<Integer> array = GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
				for (int k = 0; k < segment.size(); k++) {
					System.out.println(k+" line");
					for (int m = 1; m <= station; m++) {
						
						if (k == loca && m == j) {
							for(int n =0;n<array.size()-1;n++){
								int state_1 = array.get(n)<stateSpace?array.get(n):stateSpace-1,
									state_2 = array.get(n+1)<stateSpace?array.get(n+1):stateSpace-1;
									tensor[i][j-1][state_1][k][m-1][state_2]+=1;
									//								tensor[i][j-1][array.get(n)][k][m-1][array.get(n+1)]+=1;
							}

						}else{
							if (m <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k)))) {

								ArrayList<Integer> array_other = GetIcArray.getIC_int(mongodb,
									Integer.valueOf(segment.get(k)), m, startTime, endTime);
								int length = array.size()>array_other.size()?array_other.size():array.size();
								for(int n =0;n<length-1;n++){
									int state_1 = array.get(n)<stateSpace?array.get(n):stateSpace-1,
										state_2 = array_other.get(n+1)<stateSpace?array_other.get(n+1):stateSpace-1;
										tensor[i][j-1][state_1][k][m-1][state_2]+=1;
//									tensor[i][j-1][array.get(n)][k][m-1][array_other.get(n+1)]+=1;
								}
							} else {
								
							}
						}
					}
				}
			} else {
				tensor[i][j-1]=av();
			}

		}
		System.out.println("get tensor "+loca);
		return tensor;
	}
	public static double[][][][] av() {
		double[][][][] arr = new double[station][lineNum][station][stateSpace];
		for (int i = 0; i < station; i++) {
			for (int j = 0; j < lineNum; j++) {
				for (int k = 0; k < station; k++) {
					for (int m = 0; m < stateSpace; m++) {
					arr[i][j][k][m] = 1.0 /( lineNum*station*stateSpace);
					}
				}
			}
		}
		return arr;
	}
	public static double[][][] av_() {
		double[][][] arr = new double[lineNum][stateSpace][stateSpace];
		{
			for (int j = 0; j < lineNum; j++) {
				for (int k = 0; k < station; k++) {
					for (int m = 0; m < stateSpace; m++) {
					arr[j][k][m] = 1.0 /( lineNum*station*stateSpace);
					}
				}
			}
		}
		return arr;
	}
	

	

	public static void saveToFile_hybrid(String startTime, String endTime) {

		for (int loca = 0; loca < lineNum; loca++) {
			double[][][][][][] tensor = getTranTensor_hybrid(loca, startTime, endTime);
			ArrayList<String> segment = new ArrayList<String>();
			segment.add("35610028");
			segment.add("35557702");
			segment.add("35632502");
			segment.add("35641294");
			int stationNum;
			int segmentId;
			
			double[][][] sum = new double [4][stateSpace][stateSpace];
			int i = 0;
			segmentId = Integer.valueOf(segment.get(loca));
			stationNum = QueryBls.getStationNum(mongodb, segmentId);			
			for (int j = 1; j <= station; j++) {
				if (j <= stationNum) {
					ArrayList<Integer> array = GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
					for (int k = 0; k < stateSpace; k++) {
						for (int l = 0; l < lineNum; l++) {
							System.out.println(l+" line sum");
							for (int m = 1; m <= station; m++) {
								if (l == loca && m == j) {
									for (int n = 0; n < stateSpace; n++) {
										sum[i][j-1][k]=sum[i][j-1][k]+tensor[i][j-1][k][l][m-1][n];
									}
								} else {
									if (m <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(l)))) {

										for (int n = 0; n < stateSpace; n++) {
											sum[i][j-1][k]=sum[i][j-1][k]+tensor[i][j-1][k][l][m-1][n];
										}
									} else {
										for (int n = 0; n < stateSpace; n++) {
											sum[i][j-1][k]=sum[i][j-1][k]+tensor[i][j-1][k][l][m-1][n];
										}
									}
								}
							}
						}
					}
				} else {
					
				}

			}
			
			for (int j = 1; j <= station; j++) {
				if (j <= stationNum) {
					//ArrayList<Integer> array = GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime);
					for (int k = 0; k < stateSpace; k++) {
						for (int l = 0; l < lineNum; l++) {
							System.out.println(l+" line prob");
							for (int m = 1; m <= station; m++) {
								if (k == loca && m == j) {
									for (int n = 0; n < stateSpace; n++) 
										if(sum[i][j-1][k]>0) tensor[i][j-1][k][l][m-1][n]/=sum[i][j-1][k];
										else tensor[i][j-1][k] =av_();
								} else {
									if (m <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(l)))) {

										for (int n = 0; n < stateSpace; n++) 
											if(sum[i][j-1][k]>0) tensor[i][j-1][k][l][m-1][n]/=sum[i][j-1][k];
											else tensor[i][j-1][k] =av_();
									} else {
										for (int n = 0; n < stateSpace; n++) 
											if(sum[i][j-1][k]>0) tensor[i][j-1][k][l][m-1][n]/=sum[i][j-1][k];
											else tensor[i][j-1][k] =av_();
									}
								}
							}
						}
					}
				} else {
					
				}

			}
			int flag = 1;
			{
				for (int j = 0; j < station; j++) {
					File file = new File(".\\trans_\\trans_" + loca + "_" + j + ".txt");
					System.out.println(loca + "_" + j +" write to file");
					PrintWriter out = null;
					try {
						out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					for (int k = 0; k < stateSpace; k++) {
						// System.out.println();
						for (int m = 0; m < lineNum; m++) {
							for (int n = 0; n < station; n++) {
								for (int l = 0; l < stateSpace; l++) {

									// System.out.print(tensor[i][j][k][m]+" ");
									out.println(loca + " " + j + " " + k + " " + m + " " + n + " " + l + " "
										+ tensor[0][j][k][m][n][l]);
								}
							}

						}
					}
					out.close();
				}
			}
		}

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
		saveToFile_hybrid(startTime, endTime);
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
