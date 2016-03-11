package decomposition;

public class Tensor_3order {
	/**
	 * 3������һ�ε�ģ��
	 * @param tensor_3order
	 * @param v ģ������
	 * @param n ά��
	 * @return ����
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
	 * 3�������Ĵ�ӡ
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
