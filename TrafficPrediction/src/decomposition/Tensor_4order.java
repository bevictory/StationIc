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
	public static double[][] multip_2order(double[][][][] tensor_4order, double [][] matrix, int cluster_num,int stateSpace){
		double [][]result = new double[cluster_num][stateSpace];
		for(int i= 0; i < cluster_num; i++){
			for(int j =0; j < stateSpace; j++){
				for(int k =0; k < cluster_num; k++){
					for(int m =0;m<stateSpace ; m++){
						result[i][j] += tensor_4order[i][j][k][m]*matrix[k][m];
					}
				}
					
			}
		}
		return result;
	}
	public static double[][] multip_2order_formulti(double[][][][] tensor_4order, double [][] matrix, int cluster_num,int stateSpace){
		double [][]result = new double[cluster_num][stateSpace];
		for(int i= 0; i < cluster_num; i++){
			for(int j =0; j < stateSpace; j++){
				
				for(int k =0; k < cluster_num; k++){
					for(int m =0;m<stateSpace ; m++){//Matrix.print(tensor_4order[i][k], stateSpace);
						result[i][j] += tensor_4order[k][i][m][j]*matrix[k][m];
					}
				}
					
			}
		}
		return result;
	}

}
