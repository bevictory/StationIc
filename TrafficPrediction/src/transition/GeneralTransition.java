package transition;

import java.util.ArrayList;

import com.mongodb.client.MongoDatabase;

import decomposition.Matrix;

import mongodb.GetIcArray;

public class GeneralTransition extends Transition{
	private double[][] transition;

	private boolean isSetTrans = false;
	public double[][] getTransiton( int segmentId, int sngSerialId, String startTime, String endTime){
		if(!isSetTrans){
			ArrayList<Integer> array = GetIcArray.getIC_int( segmentId, sngSerialId, startTime, endTime);
			toTransTensor(array);
			Matrix.transpose(transition, stateSpace);
		}
		return transition;
	}
	public void setSetTrans(boolean isSetTrans) {
		this.isSetTrans = isSetTrans;
	}
	public void toTransTensor(ArrayList<Integer> array){
		 transition = new double[stateSpace][stateSpace];
		
		 double[] sum = new double[stateSpace];
		for(int i=0;i<array.size()-1;i++){
			if(array.get(i)>=stateSpace&&array.get(i+1)>=stateSpace) {
				
				sum[stateSpace-1] += 1;
				transition[stateSpace-1][stateSpace-1] +=1;
			}else if(array.get(i)>=stateSpace){
				sum[stateSpace-1] += 1;
				transition[stateSpace-1][array.get(i+1)] +=1;
			}else if(array.get(i+1)>=stateSpace){
				sum[array.get(i)] += 1;
				transition[array.get(i)][stateSpace-1] +=1;
			}else {
				sum[array.get(i)] += 1;
				transition[array.get(i)][array.get(i+1)] +=1;
			}
			
		}
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				if(sum[i] > 0)transition[i][j] /= sum[i]; 
				//else transition[i][i] =1;
				else {					
						//transition[i][i] =1.0/stateSpace;	
					transition[i][i] =0.0;	
				}
			}
		}
		
	}
	
	
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 3;
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		
	}

}
