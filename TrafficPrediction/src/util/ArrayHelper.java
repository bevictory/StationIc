package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import decomposition.DealVector;

public class ArrayHelper {
	public static int getMax(List<Integer> array){
		int max =0;
		for(int i=0 ;i<array.size();i++){
			if(max<array.get(i)){
				max = array.get(i);
			}
		}
		return max+1;
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
	public static void main(String []args){
		String time1 =  "2015-11-16 00:00:00" ,time2 =  "2015-11-16 23:59:59";
		StationSequence s = new StationSequence();
		List<Integer> array = s.findBydayProcess("12111300000000045252", time1, time2, 5*60);
		System.out.println(array);
		int length = getMax(array);
		System.out.println(length);
		DealVector.print(getInitState(array), length);
		
	}

}
