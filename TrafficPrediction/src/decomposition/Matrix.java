package decomposition;

public class Matrix {
	/**
	 * ����ת��
	 * @param matrix ����
	 * @param n �����ά��
	 */
	public static void  transpose(double [][] matrix , int n){
		for(int i=0; i < n; i ++){
			for( int j=i+1;j<n;j++){
				double t= matrix[i][j];
				matrix[i][j] = matrix[j][i];
				matrix[j][i] = t;
			}
		}
		
	}
	public static void add(double[][] matrix1, double [][] matrix2,int n ,double para){
		for(int i=0; i < n; i ++){
			for( int j=0;j<n;j++){
				
				matrix1[i][j] += matrix2[i][j]*para;
				
			}
		}
	}
	/**
	 * ��������
	 * @param matrix ����
	 * @param n	����ά��
	 * @param p 
	 */
	public static void multip_num(double [][]matrix ,int n,double p){
		for(int i=0; i < n; i ++){
			for( int j=0;j<n;j++){
				
				matrix[i][j] *= p;
			}
		}
	}
	/**
	 * ����ģ��
	 * @param matrix
	 * @param v
	 * @param n 
	 * @return	
	 */
	public static double[] multip_vector(double [][]matrix ,double [] v,int n){
		double[] result = new double[n]; 
		for(int i=0; i < n; i ++){
			for( int j=0;j<n;j++){
				result[i]+=(matrix[i][j]*v[j]);
				
			}
		}
		return result;
	}
	/**
	 * �����ӡ
	 * @param matrix
	 * @param n
	 */
	public static void print(double [][] matrix,int n){
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				
				System.out.printf("%.4f",matrix[i][j]);
				System.out.print(" ");
			}
			System.out.println();
		}
	}

}
