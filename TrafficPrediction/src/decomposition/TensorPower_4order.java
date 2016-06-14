package decomposition;



public class TensorPower_4order {
	private static double p = 0.3;//p< 1/(order-1)
	public static double [] power(double [][][][] tensor,int n ){
		double[] v = new double [n];
		double [] rm = new double[n];
		double[][] rm2 = new double[n][n];
		double [][][] rm3 = new double[n][n][n];
		double [][] matrix = new double[n][n];
		double [][][] tensor_3 = new double[n][n][n];
		
		int sum =0,rmNum=0;
//		for(int i=0;i<n;i++){
//			for(int j=0;j<n;j++){
//				if(tensor[j][i][i] ==2){
//					sum+=tensor[j][i][i];
//					tensor[j][i][i] =0;
//					rm2[j][i] = 1;
//				}
//			}
//			if(sum == 2*n){
//				rm2[i][i] = 2;
//				rm[i]=1;
//				rmNum++;
//				
//			}sum=0;
//		}
		
		
			for(int j = 0; j<n; j++){
				for(int k =0; k < n; k++){
					for(int l =0;l<n ; l++){
						if( tensor[l][k][j][j] == 2){
							sum += 2;
							tensor[l][k][j][j] = 0;
							rm3[l][k][j] =1;
						}
							
							
					}
					
				}
			
			if(sum  == 2*n*n*n){
				rm[j] = 1;
				rmNum++;
			}sum=0;
			
		}
		System.out.println("rm vector:");
		for(int i=0;i<n;i++) System.out.print(rm[i]+" ");
		System.out.println();

		System.out.println("tensor deal:");
		Tensor.deal(tensor, n, rm, rm3, rmNum, p);
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
		double lamda =1e-10;
		double [] z =new double[n];
		v=DealVector.init(rm, n, rmNum);
		//DealVector.print(v, n);
		int ite=0;
		while(DealVector.norm_2(v, z, n)>lamda){
			DealVector.copy(z, v, n);
			for(int i=1 ;i < 4; i ++){
				if(i==0){
					//tensor_5 = Tensor.multip_order(tensor, v, rm, rmNum);
				}else if(i == 1){
					tensor_3 = Tensor.multip_order(tensor, v, rm, n);
				}else if(i == 2){
					
					matrix = Tensor.multip_order(tensor_3, v, rm, n);
				}else if(i ==3) {
					v = Matrix.multip_vector(matrix, v, n);
				}
			}
			ite++;
			System.out.println("the ite num: "+ite);
			DealVector.print(v, n);
			if(ite >100) {
				break;
			}
		}
		return v;
	}
	public static void main(String []args){
//		double [][][][][] tensor = hybridTransition.getTensor_5order();
//		int n =20;
//		//Tensor_3order.print(tensor, n);
//		double [] v=power(tensor, 20);
//		DealVector.print(v, n);
	}

}
