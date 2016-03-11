package transition;

import java.util.ArrayList;

public class TimeTransition extends Transition{
	private double[][][] transTensor;
	@Override
	public void toTransTensor(ArrayList<Integer> array) {
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
