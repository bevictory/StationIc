package decomposition;

public class Tensor {
	public static double[][] multip_order(double [][][] tensor, double [] v ,double [] rm, int n){
		
		double [][]matrix  =new double[n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					//if(rm[i]<1&&rm[j]<1) matrix[i][j] += (tensor[k][i][j] * v[k]);
					if(rm[i]<1&&rm[j]<1) matrix[i][j] += (tensor[k][i][j] * v[k]);
				}
			}
		}
		System.out.println("tensor order multip matrix");
		Matrix.print(matrix, n);
		System.out.println("the matrix");
		Matrix.transpose(matrix, n);
		return matrix;
	}
	public static double[][][] multip_order(double [][][][] tensor, double [] v ,double [] rm, int n){
		
		double [][][] tensor_3  =new double[n][n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int m =0;m<n ; m++){
						if(rm[k]<1&&rm[j]<1)tensor_3[i][j][k] += tensor[m][i][j][k]*v[m];
					}
				}
			}
		}
		
		return tensor_3;
	}
	public static double[][][][] multip_order(double [][][][][] tensor, double [] v ,double [] rm, int n){
		
		double [][][][] tensor_4  =new double[n][n][n][n];
//		for(int i = 0; i < 3; i++){
//			for(int j = 0; j<3; j++){
//				for(int k =0; k < 3; k++){
//					for(int l =0;l<n ; l++){
//						System.out.println(l+" "+i+" "+j+" "+k);
//						for(int m=0; m < n ; m++){
//							System.out.print(tensor[l][i][j][k][m]+" ");
//						}
//						System.out.println();
//					}
//				}
//			}
//		}
		System.out.println("multip order 5");
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int l =0;l<n ; l++){
						for(int m=0; m < n ; m++){
							if(rm[k]<1&&rm[l]<1)tensor_4[i][j][k][l] += tensor[m][i][j][k][l]*v[m];
						}
					
					}
				}
			}
		}
//		for(int i = 0; i < 3; i++){
//			for(int j = 0; j<3; j++){
//				for(int k =0; k < n; k++){
//					System.out.println(k+" "+i+" "+j);
//					for(int m =0;m<n ; m++){
//						System.out.print(tensor_4[k][i][j][m]+" ");
//					}
//					System.out.println();
//				}
//			}
//		}
		return tensor_4;
	}
	public static double[][][][][] multip_order(double [][][][][][] tensor, double [] v ,double [] rm, int n){
		
		double [][][][][] tensor_5  =new double[n][n][n][n][n];
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int l =0;l<n ; l++){
						for(int m=0; m < n ; m++){
							for(int q=0;q<n;q++){
								tensor_5[i][j][k][l][m] += tensor[m][i][j][k][l][q]*v[m];
							}
							
						}
					
					}
				}
			}
		}
		
		return tensor_5;
	}
	public static void  multip_order(double [][][][][][] tensor, double [] v ,double [] rm, int n,int num){
		
		if(num ==0){
			for(int i = 0; i < n; i++){
				for(int j = 0; j<n; j++){
					for(int k =0; k < n; k++){
						for(int l =0;l<n ; l++){
							for(int m=0; m < n ; m++){
								for(int q=0;q<n;q++){
									tensor[0][i][j][k][l][m] += tensor[m][i][j][k][l][q]*v[m];
								}
							
							}
					
						}
					}
				}
			}
		}
		else if(num ==1){
			for(int i = 0; i < n; i++){
				for(int j = 0; j<n; j++){
					for(int k =0; k < n; k++){
						for(int l =0;l<n ; l++){
							for(int m=0; m < n ; m++){
								tensor[0][0][i][j][k][l] += tensor[0][m][i][j][k][l]*v[m];
							}
						
						}
					}
				}
			}
		}
		else if(num ==2){
			for(int i = 0; i < n; i++){
				for(int j = 0; j<n; j++){
					for(int k =0; k < n; k++){
						for(int m =0;m<n ; m++){
							tensor[0][0][0][i][j][k] += tensor[0][0][m][i][j][k]*v[m];
						}
					}
				}
			}
		}
		else if(num ==3){
			double [][]matrix  =new double[n][n];
			for(int i = 0; i < n; i++){
				for(int j = 0; j<n; j++){
					for(int k =0; k < n; k++){
						//if(rm[i]<1&&rm[j]<1) matrix[i][j] += (tensor[k][i][j] * v[k]);
						tensor[0][0][0][0][i][j] += (tensor[0][0][0][k][i][j] * v[k]);
					}
				}
			}
			System.out.println("tensor order multip matrix");
			matrix = tensor[0][0][0][0];
			Matrix.print(matrix, n);
			Matrix.transpose(matrix, n);
			tensor[0][0][0][0]=matrix;
		}
		else if(num ==4){
			
		}
		

	}
	public static void deal(double [][][][][] tensor, int n,double [] rm ,double [][][][] rm4,int rmNum,double p){

		
		for(int i = 0; i < n; i++){
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int l =0;l<n ; l++){
						for(int m=0; m < n ; m++){
							if(rm4[i][j][k][l] < 1 ){
								if(rm[l]<1&&rm[m] < 1){
									tensor[i][j][k][l][m] =tensor[i][j][k][l][m]* p + (1-p)/(n - rmNum); 
								}
							}else{
								if( rm[i] < 1 && rm[l] < 1 && rm[m] < 1){
									tensor[i][j][k][l][m]  = 1.0 / ( n - rmNum); 
								}
							}
							
						}
					
					}
				}
			}
		}
		
		//		for(int i = 0; i < n; i++){
//			for(int j = 0; j<n; j++){
//				for(int k =0; k < n; k++){
//					if(rm[i][j] <1){
//						if((rm[k][k] <2 && rm[j][j] <2)) 
//							tensor[i][j][k] = tensor[i][j][k]*p+(1-p)/(n-rmNum);
//						
//					}else {
//						if(rm[i][i] <2&& rm[j][j] <2 &&rm[k][k] <2)
//							
//							tensor[i][j][k] = 1.0/(n-rmNum);
//						//System.out.println("chuli "+tensor[i][j][k]);
//					}
//					
//				}
//			}
//		}
	}

	public static void deal(double[][][][] tensor, int n, double[] rm,
			double[][][] rm3, int rmNum, double p) {

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				for (int k = 0; k < n; k++) {
					for (int l = 0; l < n; l++) {
						
							if (rm3[i][j][k] < 1) {
								if (rm[l] < 1 && rm[k] < 1) {
									tensor[i][j][k][l] = tensor[i][j][k][l]
											* p + (1 - p) / (n - rmNum);
								}
							} else {
								if (rm[i] < 1 && rm[k] < 1 && rm[l] < 1) {
									tensor[i][j][k][l] = 1.0 / (n - rmNum);
								}
							}

						

					}
				}
			}
		}
	}
	
	

}
