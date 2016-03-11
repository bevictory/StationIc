package decomposition;

public class Tensor_5order {
public static double[][][][] multip_order(double [][][][][] tensor_5order, double [] v , int n){
		
		double [][][][] tensor_4order  =new double[n][n][n][n];

		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int l =0;l<n ; l++){
						for(int m=0; m < n ; m++){
							tensor_4order[i][j][k][l] += tensor_5order[m][i][j][k][l]*v[m];
						}
					
					}
				}
			}
		}

		return tensor_4order;
	}
}
