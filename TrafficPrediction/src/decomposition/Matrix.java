package decomposition;

public class Matrix {
	/**
	 * ¾ØÕó×ªÖÃ
	 * @param matrix ¾ØÕó
	 * @param n ¾ØÕóµÄÎ¬Êı
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
	/**
	 * ¾ØÕóÊı³Ë
	 * @param matrix ¾ØÕó
	 * @param n	¾ØÕóÎ¬Êı
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
	 * ¾ØÕóÄ£³Ë
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
	 * ¾ØÕó´òÓ¡
	 * @param matrix
	 * @param n
	 */
	public static void print(double [][] matrix,int n){
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				System.out.print(matrix[i][j]+" ");
			}
			System.out.println();
		}
	}

}
