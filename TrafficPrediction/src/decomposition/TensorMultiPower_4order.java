package decomposition;



public class TensorMultiPower_4order {
	private static double p = 0.85;//p< 1/(order-1)
	public static double [][] power(double [][][][] tensor,int n, int m){
		double[][] v = new double [n][m];
		
		

		double lamda =1e-10;
		double [][] z =new double[n][m];
		v=Matrix.init(n, m);
		//DealVector.print(v, n);
		int ite=0;
		Tensor.deal(tensor, n, m, p);
		while(Matrix.norm_2(v, z, n,m)>lamda){
			//DealVector.copy(z, v, n);
			Matrix.copy(z, v, n, m);
			v=Tensor_4order.multip_2order_formulti(tensor, v, n, m);
			Matrix.add(v, n, m, 1-p);
			ite++;
			//System.out.println("the ite num: "+ite);
			//DealVector.print(v, n);
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
