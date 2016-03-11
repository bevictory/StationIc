package decomposition;

public class Tensor_4order {
	public static double[][][] multip_order(double [][][][] tensor_4order, double [] v , int n){
		
		double [][][] tensor_3order  =new double[n][n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int m =0;m<n ; m++){
						tensor_3order[i][j][k] += tensor_4order[m][i][j][k]*v[m];
					}
				}
			}
		}
		
		return tensor_3order;
	}

}
