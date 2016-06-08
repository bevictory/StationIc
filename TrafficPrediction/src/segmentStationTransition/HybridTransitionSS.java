package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;

import util.ArrayHelper;
import util.SegmentStationSequence;

public class HybridTransitionSS {
	private double[][][][][] transTensor;
	private boolean isSetTrans = false;
	private List<List<Integer>> array_station;
	private List<List<Integer>> array_line;
	private List<Double> para_station;
	private List<Double> para_line;
	private int stateSpace ;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	
	private int mode =1;
	
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}


	//private boolean isSetTrans = false;
	public HybridTransitionSS(){
		array_station = new ArrayList<List<Integer>>();
		array_line = new ArrayList<List<Integer>>();
		para_station = new ArrayList<Double>();		
		para_line = new ArrayList<Double>();
		
	}
	public double[][][][][] getTransiton( int segmentId, String stationId, String startTime, String endTime,int mod){
		if(!isSetTrans){
			SegmentStationSequence sequence= new SegmentStationSequence();
			
			List<Integer> array = sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
			set_arrayRelate(segmentId, stationId, startTime, endTime);
			setStateSpace(getMaxState(ArrayHelper.getMax(array)));
			transTensor = new double[stateSpace][stateSpace][stateSpace][stateSpace][stateSpace];
			set_para();
			toTransTensor(array);
			isSetTrans = true;
		}
		return transTensor;
	}
	
	public void toTransTensor(List<Integer> array) {
		// TODO Auto-generated method stub
		double[][][][] sum = new double[stateSpace][stateSpace][stateSpace][stateSpace];
		int length = array.size();
		for (int i = 0; i < array_station.size(); i++) {
			length = array_station.get(i).size() < length ? array_station
					.get(i).size() : length;
		}
		for (int i = 0; i < array_line.size(); i++) {
			length = array_line.get(i).size() < length ? array_line.get(i)
					.size() : length;
		}
		for (int i = 1; i < length - 1; i++) {
			int state_station = 0;
			int state_line = 0;
			for (int j = 0; j < array_station.size(); j++) {
				state_station += array_station.get(j).get(i)
						* para_station.get(j);
			}
			for (int j = 0; j < array_line.size(); j++) {
				state_line += array_line.get(j).get(i) * para_line.get(j);
			}
			sum[array.get(i - 1)][state_station][state_line][array.get(i)] += 1;
			transTensor[array.get(i - 1)][state_station][state_line][array
					.get(i)][array.get(i + 1)] += 1;
		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					for (int m = 0; m < stateSpace; m++) {
						for (int n = 0; n < stateSpace; n++) {
							if (sum[i][j][k][m] > 0)
								transTensor[i][j][k][m][n] /= sum[i][j][k][m];
							// else tranMatrix[i][i] =1;
							else {
								// tranMatrix[i][j][k][m][n] = 1.0 / (stateSpace
								// );
								transTensor[i][j][k][m][m] = 0.0;
							}
						}
					}

				}

			}
		}
	}
	public int getMaxState(int length){
		int max=length;
		for(int i=0;i<array_line.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_line.get(i)));
		}
		for(int i=0;i<array_station.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_station.get(i)));
		}
		return max;
	}
	public void set_arrayRelate(int segmentId,String stationId, String startTime, String endTime){
		
		
		
	}
	public void set_para(){
		
	}
}
