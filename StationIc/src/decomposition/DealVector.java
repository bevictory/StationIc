package decomposition;

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
	public static double norm_2(double[] v,double[] z ,int n){
		double result=0;
		for(int i=0;i<n;i++){
			result+=Math.pow((v[i]-z[i]),2);
		}
		return Math.sqrt(result);
	}

}
