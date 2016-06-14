package decomposition;

public class Tensor_3order {
	public static void deal(double [][][] tensor, int n,double [][] rm ,int rmNum,double p){
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					if(rm[i][j] <1){
						if((rm[k][k] <2 && rm[j][j] <2)) 
							tensor[i][j][k] = tensor[i][j][k]*p+(1-p)/(n-rmNum);
						
					}else {
						if(rm[i][i] <2&& rm[j][j] <2 &&rm[k][k] <2)
							
							tensor[i][j][k] = 1.0/(n-rmNum);
						//System.out.println("chuli "+tensor[i][j][k]);
					}
					
				}
			}
		}
	}
	public static double[] multip_order(double [][][] tensor, double [] v ,double [] rm, int n){
		double []result = new double [n];
		double [][]matrix  =new double[n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					//if(rm[i]<1&&rm[j]<1) matrix[i][j] += (tensor[k][i][j] * v[k]);
					if(rm[i]<1&&rm[j]<1) matrix[i][j] += (tensor[k][i][j] * v[k]);
				}
			}
		}
		//System.out.println("tensor order multip matrix");
		//Matrix.print(matrix, n);
		Matrix.transpose(matrix, n);
		
		result= Matrix.multip_vector(matrix, v, n);
		return result;
	}
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
