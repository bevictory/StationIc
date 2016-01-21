package decomposition;

public class TensorPower_3order {
	private static double p = 0.45;//p< 1/(order-1)
	public static double [] power(double [][][] tensor,int n ){
		double[] v = new double [n];
		double[] rm = new double[n];
		int sum =0,rmNum=0;
		for(int i=0;i<n;i++){
			for(int j=0;j<n;j++){
				if(tensor[j][i][i] ==2){
					sum+=tensor[j][i][i];
					tensor[j][i][i] =0;
				}
			}
			if(sum == 2*n){
				rm[i] =1;
				rmNum++;
				sum=0;
			}
		}
		Tensor_3order.deal(tensor, n, rm, rmNum, p);
		double lamda =1e-10;
		double [] z =new double[n];
		int ite=0;
		while(DealVector.norm_2(v, z, n)>lamda){
			DealVector.copy(z, v, n);
			v=Tensor_3order.multip_order(tensor, v, n);
			ite++;
			if(ite >100) {
				break;
			}
		}
		return v;
	}

}
