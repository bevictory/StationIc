package decomposition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;

public class DealVector {
	public static double []  init(double[] vector,int n,int rmNum){
		double []result = new double[n];
		for(int i = 0; i < n; i++){
			result[i]= 1- vector[i];
			result[i]/=(n-rmNum);
		}
		return result;
		
	}
	public static double[] delta(double [] vector,int n,int rmNum, double p){
		double []result = new double[n];
		for(int i = 0; i < n; i++){
			result[i]= 1- vector[i];
			result[i]*=((1-p)/(n-rmNum));
		}
		return result;
	}
	public static double[] zeros(int n){
		double [] result = new double[n];
		for(int i=0;i<n ;i ++) {
			result[i] =0;
		}
		return result;
	}
	public static void copy(double[] z,double[] v ,int n){
		for(int i=0;i<n;i++){
			z[i]= v[i];
		}
	}
	public static void add(double[] v,double[] delta ,int n){
		for(int i=0;i<n;i++){
			v[i]+=delta[i];
		}
	}
	public static void add(double[] v,double[] v1 ,int n,double para){
		for(int i=0;i<n;i++){
			v[i]+=v1[i]*para;
		}
	}
	public static double norm_2(double[] v,double[] z ,int n){
		double result=0;
		for(int i=0;i<n;i++){
			result+=Math.pow((v[i]-z[i]),2);
		}
		return Math.sqrt(result);
	}
	public static int getMax(double [] v, int n){
		int  max = 0;
		for(int i =0; i < n; i++){
			if(v[i] >v[max]) max = i;
		}
		return max;
	}
	
	public static ArrayList<Integer> getTop(double [] v, int n,int k){
		class compa implements Comparator<Double>{

			@Override
			public int compare(Double arg0, Double arg1) {
				// TODO Auto-generated method stub
				
				if((double)arg0>(double)arg1) return -1;
				else if((double)arg0<(double)arg1)return 1;
				else return 0;
			}

			
		}
		ArrayList<Double> array= new ArrayList<Double>();
		int max=0;
		for(int i =0; i < n; i++){
			array.add(v[i]);
		}
		compa com =new compa();
		array.sort(com);
		ArrayList<Integer> res = new ArrayList<Integer>();
		int j =0;
		while(k > j){
			for(int i =0; i < n; i++){
				if(Math.abs((double)array.get(j)-v[i]) <0.00000001)
				{
					if(!res.contains(i)) res.add(i);
				}
			}
			j++;
		}
		return res;
	}
	public static void print(double [] v, int n){
		double sum=0;
		for(int i=0;i<n;i++){ 
			System.out.printf("%.6f",v[i]);
			sum+=v[i];
			System.out.println();
		}
		System.out.println(sum);
	}
	public static void reset(double [] v, int n){
	
		for(int i=0;i<n;i++){ 
			v[i]=0;
		}
	
	}

}
