package decomposition;



public class Tensor_3order_power {
	private static double p = 0.45;//p< 1/(order-1)
	public static double [] power(double [][][] tensor,int n ){
		double[] v = new double [n];
		double [] rm = new double[n];
		double[][] rm2 = new double[n][n];
		int sum =0,rmNum=0;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(tensor[j][i][i] ==2){
					sum+=tensor[j][i][i];
					tensor[j][i][i] =0;
					rm2[j][i] = 1;
				}
			}
			if(sum == 2*n){
				rm2[i][i] = 2;
				rm[i]=1;
				rmNum++;
				
			}sum=0;
		}
		//System.out.println("rm vector:");
		//for(int i=0;i<n;i++) System.out.print(rm[i]+" ");
		//System.out.println();
		//System.out.println("tensor deal:");
		Tensor_3order.deal(tensor, n, rm2, rmNum, p);
		//Tensor_3order.print(tensor, n);
		double lamda =1e-10;
		double [] z =new double[n];
		v=DealVector.init(rm, n, rmNum);
		//DealVector.print(v, n);
		int ite=0;
		while(DealVector.norm_2(v, z, n)>lamda){
			DealVector.copy(z, v, n);
			v=Tensor_3order.multip_order(tensor, v,rm, n);
			ite++;
			//System.out.println("the ite num: "+ite);
			//DealVector.print(v, n);
			if(ite >50) {
				break;
			}
		}
		//DealVector.print(v, n);
		return v;
	}
	public static void main(String []args){
//		double [][][] tensor = lineTransition.getTensor_3order();
//		int n =20;
//		//Tensor_3order.print(tensor, n);
//		double [] v=power(tensor, 20);
//		DealVector.print(v, n);
	}
}
