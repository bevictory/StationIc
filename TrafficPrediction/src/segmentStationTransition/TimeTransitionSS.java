package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;

import util.ArrayHelper;
import util.SegmentStationSequence;

public class TimeTransitionSS {
	private double[][][] transTensor;
	private int stateSpace ;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public TimeTransitionSS(){
		
	}
	private boolean isSetTrans = false;
	public double[][][] getTransiton( int segmentId, String stationId, String startTime, String endTime,int mod){
		if(!isSetTrans){
			SegmentStationSequence sequence= new SegmentStationSequence();
			
			List<Integer> array = sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
			//set_arrayRelate(segmentId, stationId, startTime, endTime);
			setStateSpace(ArrayHelper.getMax(array));
			transTensor = new double[stateSpace][stateSpace][stateSpace];
			toTransTensor(array);
			isSetTrans = true;
		}
		return transTensor;
	}
	public void toTransTensor(List<Integer> array) {
		// TODO Auto-generated method stub
		double[][] sum = new double[stateSpace][stateSpace];
		for(int i=1;i<array.size()-1;i++){
			//if(array.get(i)>=stateSpace||array.get(i+1)>=stateSpace) continue;
			int state_pre=array.get(i-1)<stateSpace?array.get(i-1):stateSpace-1,
				state_curr=array.get(i)<stateSpace?array.get(i):stateSpace-1,
				state_next=array.get(i+1)<stateSpace?array.get(i+1):stateSpace-1;
				sum[state_pre][state_curr] += 1;
				transTensor[state_pre][state_curr][state_next] +=1;
				
//			sum[array.get(i-1)][array.get(i)] += 1;
//			tranMatrix[array.get(i-1)][array.get(i)][array.get(i+1)] +=1;
		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
						transTensor[i][j][k] /= sum[i][j];
					// else tranMatrix[i][i] =1;
					else {
						//trans[i][j][k] = 1.0 / ( stateSpace);
						transTensor[i][j][j] =1.0;
					}
				}

			}
		}
	}
}
