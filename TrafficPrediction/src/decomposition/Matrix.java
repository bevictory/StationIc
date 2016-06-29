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
	public static void add(double[][] matrix1, double [][] matrix2,int n ,double para){
		for(int i=0; i < n; i ++){
			for( int j=0;j<n;j++){
				
				matrix1[i][j] += matrix2[i][j]*para;
				
			}
		}
	}
	public static void add(double[][] matrix1,int n ,int m,double para){
		for(int i=0; i < n; i ++){
			for( int j=0;j<m;j++){
				
				matrix1[i][j] *= para;
				
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
	public static double [][]  init(int n,int m){
		double [][]result = new double[n][m];
		for(int i = 0; i < n; i++){
			for(int j=0;j<m;j++){
				result[i][j]=1.0;
			result[i][j]/=m*n;
			}
		}
		return result;
	
	}
	public static double norm_2(double[][] v,double[][] z ,int n,int m){
		double result=0;
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
			result+=Math.pow((v[i][j]-z[i][j]),2);
			}
		}
		return Math.sqrt(result);
	}
	public static void copy(double[][] z,double[][] v ,int n,int m){
		for(int i = 0; i < n; i++){
			for(int j=0;j<m;j++){
				z[i][j]=v[i][j];
			}
		}
	}
	public static void reset(double[][] z,int n){
		for(int i = 0; i < n; i++){
			for(int j=0;j<n;j++){
				z[i][j]=0;
			}
		}
	}
	/**
	 * ¾ØÕó´òÓ¡
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
