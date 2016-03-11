package decomposition;

public class Tensor_3order {
	/**
	 * 3阶张量一次单模乘
	 * @param tensor_3order
	 * @param v 模乘向量
	 * @param n 维度
	 * @return 矩阵
	 */
	public static double[][] orderMulti_one(double [][][] tensor_3order, double [] v, int n){
		double [][]matrix  =new double[n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					 matrix[i][j] += (tensor_3order[k][i][j] * v[k]);
				}
			}
		}
		return matrix;
	}
	/**
	 * 3阶张量的打印
	 * @param tensor
	 * @param n
	 */
	public static void print(double [][][] tensor,int n ){
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					System.out.print(tensor[i][j][k]+" ");
				}
				System.out.println();
			}
		}
	}

}
