package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;

import util.ArrayHelper;
import util.SegmentStationSequence;

public class LineStationSS {
	private double[][][] transTensor;
	private boolean isSetTrans = false;
	private List<List<Integer>> array_relate ;
	private List<Double> para;
	private int stateSpace ;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public List<Double> getPara() {
		return para;
	}
	private int mode =1;
	
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}


	//private boolean isSetTrans = false;
	public LineStationSS(){
		array_relate = new ArrayList<List<Integer>>();
		
		
		para = new ArrayList<Double>();
		
	}
	public double[][][] getTransiton( int segmentId, String stationId, String startTime, String endTime,int mod){
		if(!isSetTrans){
			SegmentStationSequence sequence= new SegmentStationSequence();
			
			List<Integer> array = sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
			set_arrayRelate(segmentId, stationId, startTime, endTime);
			setStateSpace(getMaxState(ArrayHelper.getMax(array)));
			transTensor = new double[stateSpace][stateSpace][stateSpace];set_para();
			toTransTensor(array);
			isSetTrans = true;
		}
		return transTensor;
	}
	
	public void toTransTensor(List<Integer> array) {
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
	public int getMaxState(int length){
		int max=length;
		for(int i=0;i<array_relate.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_relate.get(i)));
		}
		return max;
	}
	public void set_arrayRelate(int segmentId,String stationId, String startTime, String endTime){
		
		
		
	}
	public void set_para(){
		para.add(0.5);
		para.add(0.5);
	}
}
