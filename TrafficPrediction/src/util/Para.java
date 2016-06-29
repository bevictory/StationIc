package util;

import java.util.Arrays;

public class Para {
	public static void setPara(double []para,int n,int loc, double sum){
		if(loc ==n-1) {
			para[loc] = sum;
			for(int i=0;i<n;i++)
			  System.out.print(para[i]+" ");
			System.out.println();
			return ;
		}
		for(double p=0.0;sum-p >10e-5; p+=0.01){
			para[loc]=p;
			setPara(para,n, loc+1, sum- p);
		}
	}
	public static void main(String []args){
		double []para = new double [3];
		setPara(para, 3, 0, 1);
	}
}
