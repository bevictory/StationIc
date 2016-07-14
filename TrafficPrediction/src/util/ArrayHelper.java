package util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import decomposition.DealVector;

public class ArrayHelper {
//	public static double pre=0.3;
	public static int pre=1;
//	public static double getPre() {
//		return pre;
//	}
//
//	public static void setPre(double pre) {
//		ArrayHelper.pre = pre;
//	}

	public static int getMax(List<Integer> array){
		int max =0;
		for(int i=0 ;i<array.size();i++){
			if(max<Math.abs(array.get(i))){
				max =Math.abs(array.get(i));
			}
		}
		if(PredictDis.isPreDis)return 2*max+1;
		else return max +1;
	}
	
	public static double[] getInitState(List<Integer> array){
		double[] result = new double[getMax(array)];
		
		for(int i =0 ;i<array.size();i++){
			result[array.get(i)]+=1;
		
		}
		for(int i =0 ;i<result.length;i++){
			
			result[i]/=array.size();
		}
		return result;
	}
	public static int getMinDis(List<Integer> array , int state){
		int min =Integer.MAX_VALUE;
		//boolean isNegative =false;
		for(int i=0;i<array.size();i++){
			min = Math.min(Math.abs(state-array.get(i)), min);
//			int dis=0;
//			if(state ==0) dis = array.get(i)>=2?2:array.get(i);
//			else dis = Math.abs(state-array.get(i))/state;
//			if(array.get(i) == 0) dis = 1;
//			min = Math.min(dis , min);
			
//			if(Math.abs(state-array.get(i)) <min){
//				min = Math.abs(state-array.get(i));
//				if(state <array.get(i)) isNegative = true;
//				else isNegative = false;
//			}
		}
		//if(min <=state*pre) return 0;
		  return min;
	}
	
	public static int getMinDisState(List<Integer> array , int state){
		int min =Integer.MAX_VALUE;
		//boolean isNegative =false;
		int loc =0;
		for(int i=0;i<array.size();i++){
//			min = Math.min(Math.abs(state-array.get(i)), min);
			if(Math.abs(state-array.get(i)) <min){
				min = Math.abs(state-array.get(i));
				loc = array.get(i);
			}
		}
		  return loc;
	}
//	public static boolean isPredic(List<Integer> list, int prediction,double mode){
//		int m =(int) (prediction *mode);
//		if(m==0) m =1;
//		for(int i =-m;i<=m;i++){
//			if(list.contains(prediction+i)){
//				//System.out.print((prediction+i)+" ");
//				return true;
//			}
//		}
//		return false;
//	}
	public static boolean isPredic(List<Integer> list, int prediction,int mode){
		
		for(int i =-mode;i<=mode;i++){
			if(list.contains(prediction+i)){
				//System.out.print((prediction+i)+" ");
				return true;
			}
		}
		return false;
	}
	public static void main(String []args){
		String time1 =  "2015-11-16 00:00:00" ,time2 =  "2015-11-16 23:59:59";
		StationSequence s = new StationSequence();
		List<Integer> array = s.findBydayProcess("12111300000000045252", time1, time2, 5*60);
		System.out.println(array);
		int length = getMax(array);
		System.out.println(length);
		DealVector.print(getInitState(array), length);
		
	}
	/**
	 * 从概率数组中获得概率前几位的
	 * @param zeigen
	 * @param topN
	 * @return
	 */
	public static List<Integer> getTopN(double []zeigen,int topN){
		List<Integer> result = new ArrayList<Integer>();
		int[] loc = new int[zeigen.length];
		for(int i=0;i<zeigen.length;i++){
			loc[i] =i;
		}
		for(int i =0;i<topN;i++){
			double max = zeigen[i];
			for(int j=i+1;j<zeigen.length;j++){
				if(zeigen[j]>zeigen[i]) {
					double tmp= zeigen[i];
					zeigen[i]= zeigen[j];
					zeigen[j]=tmp;
					int t=loc[i];
					loc[i]=loc[j];
					loc[j]=t;
					
				}
			}
			result.add(loc[i]);
		}
		return result;
		
	}

}
