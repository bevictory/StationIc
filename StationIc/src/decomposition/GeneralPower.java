package decomposition;

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
		double [][]matrix=GetIcArray.getTransmatrix();
		for(int i=0;i<20;i++){
			for(int j=0;j<20;j++){
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println();
		}
		double [] v=decomp_nodel(matrix,20);
		for(int i=0;i<20;i++){ 
			System.out.print(v[i]+" ");
		}
	}

}
