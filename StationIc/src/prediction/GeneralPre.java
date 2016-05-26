package prediction;

import java.util.ArrayList;

import mongodb.GetIcArray;

import decomposition.DealVector;
import decomposition.Matrix;

import transition.generalTransition;

public class GeneralPre {
	private static int stateSpace =20;
	private static double[] result = new double[stateSpace];
	public static void prediction(double[] state, String startTime, String endTime){
		double [][]transition = new double[stateSpace][stateSpace];
		transition = GetIcArray.getTransmatrix();
		Matrix.transpose(transition, stateSpace);
		Matrix.print(transition, stateSpace);
		result = Matrix.multip_vector(transition, state, stateSpace);
	}
	public static void main(String []args){
		String startTime = "2015-12-07 06:30:00";
		String endTime = "2015-12-11 09:00:00" ;
		result[1]=1.0;
		
		prediction(result,startTime,endTime);
		DealVector.print(result, stateSpace);
	}

}
