package decomposition;

import stationPre.GeneralPreS;
import stationTransition.GeneralTransition;
import mongodb.GetIcArray;

public class GeneralPower {
	private static double p = 0.85;
	public static double[] decomp(double [][]matrix, int n){
		double[] v = new double [n];
		double[] rm = new double[n];
		double[] delta = new double[n];
		int rmNum =0;
		int stateNum=0;
		for(int i=0;i<n;i++){
			if(matrix[i][i] == 2) {
				matrix[i][i] =0;
				rm[i] =1;
				rmNum++;
			}
		}
		stateNum = n- rmNum;
		Matrix.transpose(matrix, n);
		v=DealVector.init(rm, n, rmNum);
		delta = DealVector.delta(rm, n, rmNum, p);
		Matrix.multip_num(matrix, n, p);
		double lamda =1e-10;
		double [] z =new double[n];
		int ite=0;
		while(DealVector.norm_2(v, z, n)>lamda){
			DealVector.copy(z, v, n);
			v=Matrix.multip_vector(matrix, v, n);
			DealVector.add(v, delta, n);
			ite++;
			if(ite >100) {
				break;
			}
		}	
		System.out.println("the ite num: "+ ite);
		return v;
	}
	public static double[] decomp_nodel(double [][]matrix, int n){
		double[] v = new double [n];
		double[] rm = new double[n];
		double[] delta = new double[n];
		int rmNum =0;
		
		
		Matrix.transpose(matrix, n);
		v=DealVector.init(rm, n, rmNum);
		delta = DealVector.delta(rm, n, rmNum, p);
		Matrix.multip_num(matrix, n, p);
		double lamda =1e-10;
		double [] z =new double[n];
		int ite=0;
		while(DealVector.norm_2(v, z, n)>lamda){
			DealVector.copy(z, v, n);
			v=Matrix.multip_vector(matrix, v, n);
			DealVector.add(v, delta, n);
			ite++;
			if(ite >100) {
				break;
			}
		}	
		System.out.println("the ite num: "+ ite);
		return v;
	}
	public static void main(String []args){
		
		String startTime = "2015-11-10 00:00:00", endTime = "2015-11-13 23:59:59";
		String time1 =  "2015-11-16 00:00:00" ,time2 =  "2015-11-16 23:59:59";
		GeneralTransition generalPre  = new GeneralTransition();
		
		
		double [][]matrix=generalPre.getTransiton("12111300000000045252", startTime, endTime, 5*60);
		Matrix.print(matrix, generalPre.getStateSpace());
		Matrix.transpose(matrix,  generalPre.getStateSpace());
		DealVector.print(matrix[0],generalPre.getStateSpace() );
//		double [] v=decomp(matrix,generalPre.getStateSpace());
//		DealVector.print(v, generalPre.getStateSpace());
	}

}
