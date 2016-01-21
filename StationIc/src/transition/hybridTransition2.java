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
 * @version 1.0, 2015年12月17日 每次修改后更新版本号，日期和修改内容
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class hybridTransition2 {
	private static int lineNum =4;
	private static int station = 35;
	private static int stateSpace = 35;
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance().getRemoteMongoDatabase2();

	public static double[][][][][][] getTranTensor_hybrid(int loca,String startTime, String endTime) {
		ArrayList<String> segment = new ArrayList<String>();
		segment.add("35610028");
		segment.add("35557702");
		segment.add("35632502");
		segment.add("35641294");
		int stationNum;
		int segmentId;
		double[][][][][][] tensor = new double[1][station][stateSpace][stateSpace][stateSpace][stateSpace];
		
			if (true) {
				int i=0;
				segmentId = Integer.valueOf(segment.get(loca));
				stationNum = QueryBls.getStationNum(mongodb, segmentId);
				int loc;
				if (stationNum <= 35)
					loc = stationNum;
				else
					loc = 35;
				for (int j = 1; j <= 35; j++) {
					if (j == 1 && j <= loc - 2) {
						ArrayList<Integer> array = null, array2 = null, array3 = null;
						ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));

						// arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j + 1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j + 2, startTime, endTime));
						for (int k = 0; k < segment.size(); k++) {
							if (k != loca) {
								if (j <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))))
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)), j, startTime,
										endTime));
								else
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)),
										QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))), startTime,
										endTime));
							}
						}
						tensor[i][j - 1] = toTrans_hybrid(arr);
					} else if (j > 1 && j <= loc - 1) {
						ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));

						// arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j - 1, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j + 1, startTime, endTime));
						for (int k = 0; k < segment.size(); k++) {
							if (k != loca) {
								if (j <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))))
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)), j, startTime,
										endTime));
								else
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)),
										QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))), startTime,
										endTime));
							}
						}
						tensor[i][j - 1] = toTrans_hybrid(arr);
					} else if (j == loc) {
						ArrayList<ArrayList<Integer>> arr = new ArrayList<ArrayList<Integer>>();
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));

						// arr.add(GetIcArray.getIC_int(mongodb, segmentId, j, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j - 2, startTime, endTime));
						arr.add(GetIcArray.getIC_int(mongodb, segmentId, j - 1, startTime, endTime));
						for (int k = 0; k < segment.size(); k++) {
							if (k != loca) {
								if (j <= QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))))
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)), j, startTime,
										endTime));
								else
									arr.add(GetIcArray.getIC_int(mongodb, Integer.valueOf(segment.get(k)),
										QueryBls.getStationNum(mongodb, Integer.valueOf(segment.get(k))), startTime,
										endTime));
							}
						}
						tensor[i][j - 1] = toTrans_hybrid(arr);
					} else {
						tensor[i][j - 1] = avMatrix_hybrid();
					}

				}

			}

		

		return tensor;
	}

	public static double[][][][] avMatrix_hybrid() {
		double[][][][] arr = new double[stateSpace][stateSpace][stateSpace][stateSpace];
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					for (int m = 0; m < stateSpace; m++) {
					arr[i][j][k][m] = 1.0 /( stateSpace);
					}
				}
			}
		}
		return arr;
	}

	public static double[][][][] toTrans_hybrid(ArrayList<ArrayList<Integer>> array) {
		double[][][][] tranMatrix = new double[stateSpace][stateSpace][stateSpace][stateSpace];

		double[][][] sum = new double[stateSpace][stateSpace][stateSpace];
		int[] len = new int[6];

		for (int i = 0; i < 6; i++) {
			len[i] = (array.get(i).size());
		}
		Arrays.sort(len);
		int length = len[0];
		for (int i = 1; i < length-1; i++) {
			int	station_pre=array.get(1).get(i)<stateSpace?array.get(1).get(i):stateSpace-1,
				station_next=array.get(2).get(i)<stateSpace?array.get(2).get(i):stateSpace-1,
			    station_1=array.get(3).get(i)<stateSpace?array.get(3).get(i):stateSpace-1,
				station_2=array.get(4).get(i)<stateSpace?array.get(4).get(i):stateSpace-1,
				station_3=array.get(5).get(i)<stateSpace?array.get(5).get(i):stateSpace-1,
				state_curr=array.get(0).get(i)<stateSpace?array.get(0).get(i):stateSpace-1,
				state_next=array.get(0).get(i+1)<stateSpace?array.get(0).get(i+1):stateSpace-1;
			sum[(station_pre+station_next)/2][ (station_1+station_2+station_3)/3][state_curr] += 1;
			tranMatrix[(station_pre+station_next)/2][ (station_1+station_2+station_3)/3][state_curr][state_next] += 1;
//			sum[ (array.get(1).get(i) + array.get(2).get(i))
//				/ 2][ (array.get(3).get(i) + array.get(4).get(i) + array.get(5).get(i)) / 3][array.get(0).get(i)] += 1;
//			tranMatrix[ (array.get(1).get(i) + array.get(2).get(i))
//				/ 2][ (array.get(3).get(i) + array.get(4).get(i) + array.get(5).get(i)) / 3][array.get(0).get(i)][array
//					.get(0).get(i + 1)] += 1;
		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					for (int m = 0; m < stateSpace; m++) {

						if (sum[i][j][k] > 0)
							tranMatrix[i][j][k][m] /= sum[i][j][k];
						// else tranMatrix[i][i] =1;
						else {
							tranMatrix[i][j][k][m] = 1.0 / ( stateSpace);

						}
					}

				}

			}
		}
		return tranMatrix;
	}

	public static void saveToFile_hybrid(String startTime, String endTime) {
		for(int loca=0;loca<lineNum;loca++){
		double[][][][][][] tensor = getTranTensor_hybrid(loca,startTime, endTime);

		int flag = 1;
		 {
			for (int j = 0; j < station; j++) {
				File file = new File(".\\trans_hybrid\\trans_" + loca + "_" + j + ".txt");
				PrintWriter out = null;
				try {
					out = new PrintWriter(new BufferedWriter(new FileWriter(file)));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				for (int k = 0; k < stateSpace; k++) {
					// System.out.println();
					for (int m = 0; m < stateSpace; m++) {
						for (int n = 0; n < stateSpace; n++) {
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
	public static void sparse(String startTime, String endTime) {
		int num=0;
		for(int loca=0;loca<lineNum;loca++){
		double[][][][][][] tensor = getTranTensor_hybrid(loca,startTime, endTime);
		
		int flag = 1;
		 {
			for (int j = 0; j < station; j++) {
				
				for (int k = 0; k < stateSpace; k++) {
					// System.out.println();
					for (int m = 0; m < stateSpace; m++) {
						for (int n = 0; n < stateSpace; n++) {
							for (int l = 0; l < stateSpace; l++) {
								if(tensor[0][j][k][m][n][l]>0) num++;
								// System.out.print(tensor[i][j][k][m]+" ");
								
							}
						}

					}
				}
				
			}
		}
		}
		System.out.println("the sparse of hybrid model is: "+(double)num/(4*station*stateSpace*stateSpace*stateSpace*stateSpace));

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
		//saveToFile_hybrid(startTime, endTime);
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
