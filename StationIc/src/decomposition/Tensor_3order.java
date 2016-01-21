package decomposition;

public class Tensor_3order {
	public static void deal(double [][][] tensor, int n,double [] rm ,int rmNum,double p){
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					if(rm[j] !=1)tensor[i][j][k] = tensor[i][j][k]*p+(1-p)/(n-rmNum);
				}
			}
		}
	}
	public static double[] multip_order(double [][][] tensor, double [] v , int n){
		double []result = new double [n];
		double [][]matrix  =new double[n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					matrix[i][j] += (tensor[k][i][j] * v[k]);
				}
			}
		}
		Matrix.transpose(matrix, n);
		
		result= Matrix.multip_vector(matrix, v, n);
		return result;
	}

}
