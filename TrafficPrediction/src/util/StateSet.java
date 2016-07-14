package util;

import decomposition.DealVector;
import decomposition.Matrix;

public class StateSet {
	private static double bias = 0.1;
	private static double delta = 0.8;
	private static int step =0;
	public static void setState(double []vector,int stateSpace, int state){
		
		int dis = (int)(state*bias);
		if(dis ==0&& bias-0>10e-5) dis =step>state?state:step;
		int length = vector.length;
		
		int dis_begin = -dis;
		if(state ==0) dis_begin = 0;
		if(PredictDis.isPreDis){
			dis =(int)( Math.abs(state-(stateSpace-2)/2)*bias);
			if(state-dis<0) dis_begin =state;
			else dis_begin =-dis;
		}
		int dis_end = (state+dis)>stateSpace-1?stateSpace-1-state:dis;
		int len = dis_end -dis_begin+1;
		for(int i=dis_begin;i<=dis_end;i++){
			vector[state+i]+= 1.0/len*delta;
			if(i==0 ) vector[state] += 1-delta;
		}
	}
	public static void setState(double []vector,int stateSpace, int state,double para){
		int dis = (int)(state*bias);if(dis ==0&& bias-0>10e-5) dis =step>state?state:step;
		int length = vector.length;
		int dis_begin = -dis;if(state ==0) dis_begin = 0;
		if(PredictDis.isPreDis){
			dis =(int)( Math.abs(state-(stateSpace-2)/2)*bias);
			if(state-dis<0) dis_begin =state;
			else dis_begin =-dis;
		}
		int dis_end = (state+dis)>stateSpace-1?stateSpace-1-state:dis;
		
		int len = dis_end -dis_begin+1;
		for(int i=dis_begin;i<=dis_end;i++){
			vector[state+i]+= 1.0/len*delta;
			if(i==0 ) vector[state] += 1-delta;
		}
	}
	public static void setState(double [][]matrix,int stateSpace, int state_r,int state,double para){
		int dis_r = (int)(state_r*bias);if(dis_r ==0&& bias-0>10e-5) dis_r =step>state_r?state_r:step;
		int dis = (int)(state*bias);if(dis ==0&& bias-0>10e-5) dis =step>state?state:step;
		int dis_r_begin = -dis_r;if(state_r ==0) dis_r_begin = 0;
		int dis_begin = -dis;if(state ==0) dis_begin = 0;
		if(PredictDis.isPreDis){
			dis =(int)( Math.abs(state-(stateSpace-2)/2)*bias);
			dis_r =(int)( Math.abs(state_r-(stateSpace-2)/2)*bias);
			if(state-dis<0) dis_begin = state;
			else dis_begin =-dis;
			if(state_r-dis_r<0) dis_r_begin = state_r;
			else dis_r_begin =-dis_r;
		}
		int dis_r_end = (state_r+dis_r)>stateSpace-1?stateSpace-1-state_r:dis_r;
		
		int dis_end = (state+dis)>stateSpace-1?stateSpace-1-state:dis;
		int len =( dis_end-dis_begin +1);
		for(int i=dis_r_begin;i<=dis_r_end;i++){
			for(int j=dis_begin;j<=dis_end;j++){
				matrix[state_r+i][state+j] +=1.0/len*para*delta;
				if(j==0) matrix[state_r+i][state+j] += (1-delta)*para;
			}
		}
	}
	public static void main(String []args){
		int stateSpace=30;
		double []vector = new double [stateSpace];
		double [][]matrix = new double [stateSpace][stateSpace];
		setState(vector, stateSpace, 25);
		DealVector.print(vector, stateSpace);
		
		setState(matrix, stateSpace, 5, 25, 0.2);
		Matrix.print(matrix, stateSpace);
		setState(matrix, stateSpace, 5, 18, 0.8);
		Matrix.print(matrix, stateSpace);
		
	}
	
	

}
