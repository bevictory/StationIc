package transition;

import java.util.ArrayList;

import mongodb.GetIcArray;

public class LineTransition extends Transition{
	private double[][][] transTensor;
	private boolean isSetTrans = false;
	private ArrayList<ArrayList<Integer>> array_relate;
	private ArrayList<Double> para;
	public double[][][] getTransiton( int segmentId, int sngSerialId, String startTime, String endTime){
		if(!isSetTrans){
			ArrayList<Integer> array = GetIcArray.getIC_int( segmentId, sngSerialId, startTime, endTime);
			toTransTensor(array);
			
		}
		return transTensor;
	}
	@Override
	public void toTransTensor(ArrayList<Integer> array) {
		// TODO Auto-generated method stub
		double[][] sum = new double[stateSpace][stateSpace];
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		for(int i = 0; i < length-1 ; i ++){
			int state =0 ;
			for(int j =0 ; j < array_relate.size(); j++){
				state+= array_relate.get(j).get(i) * para.get(j);
			}
			sum[state][array.get(i)] += 1;
			transTensor[state][array.get(i)][array.get(i+1)] += 1;
			
		}
		
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
						transTensor[i][j][k] /= sum[i][j];
					// else tranMatrix[i][i] =1;
					else {
						//tranMatrix[i][j][k] = 1.0 / ( stateSpace);
						transTensor[i][j][j] = 0.0;
					}
				}

			}
		}
	}

}
