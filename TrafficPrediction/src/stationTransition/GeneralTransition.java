package stationTransition;


import java.util.List;


import decomposition.Matrix;

import util.ArrayHelper;
import util.SegmentStationSequence;
import util.StationSequence;

public class GeneralTransition {
	private int stateSpace ;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	private int isDayModel=1;
	private List<Integer> array;
	private double[] initState;
	private double[][] transition;
	private int mode =1;
	
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}

	public GeneralTransition(String stationId,String startTime, String endTime,int isDayModel,int mode ,int mod)
	
	{
		this.mode =mode;
		this.isDayModel = isDayModel;
		
		
		StationSequence sequence= new StationSequence();
		if(isDayModel==0)array=sequence.findBydayProcess(stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess( stationId, startTime, endTime, mod);
		setStateSpace(ArrayHelper.getMax(array)/mode+1);
		initState = ArrayHelper.getInitState(array);
	}
	private boolean isSetTrans = false;
	public double[][] getTransiton(){
		if(!isSetTrans){
			
						
			toTransTensor(array);
			Matrix.transpose(transition, stateSpace);
			Matrix.print(transition, stateSpace);
			isSetTrans = true;
		}
		return transition;
	}
	public double[] getInitState() {
		return initState;
	}
	public void setInitState(double[] initState) {
		this.initState = initState;
	}
	public void setSetTrans(boolean isSetTrans) {
		this.isSetTrans = isSetTrans;
	}
	
	public void toTransTensor(List<Integer> array){
		 transition = new double[stateSpace][stateSpace];
		
		 double[] sum = new double[stateSpace];
		for(int i=0;i<array.size()-1;i++){
//			if(array.get(i)/mode>=stateSpace&&array.get(i+1)/mode>=stateSpace) {
//				
//				sum[stateSpace-1] += 1;
//				transition[stateSpace-1][stateSpace-1] +=1;
//			}else if(array.get(i)/mode>=stateSpace){
//				sum[stateSpace-1] += 1;
//				transition[stateSpace-1][array.get(i+1)] +=1;
//			}else if(array.get(i+1)/mode>=stateSpace){
//				sum[array.get(i)/mode] += 1;
//				transition[array.get(i)/mode][stateSpace-1] +=1;
//			}else {
//				sum[array.get(i)/mode] += 1;
//				transition[array.get(i)/mode][array.get(i+1)/mode] +=1;
//			}
			sum[array.get(i)/mode]+=1;
			transition[array.get(i)/mode][array.get(i+1)/mode]+=1;
			
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
