package segmentStationTransition;

import java.util.List;

import util.ArrayHelper;
import util.SegmentStationSequence;
import util.StationSequence;
import decomposition.Matrix;

public class GeneralTransitionSS {
	private int stateSpace ;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}

	private double[] initState;
	private double[][] transition;
	private int mode =1;
	private int isDayModel =1;
	private List<Integer> array;
	public int isDayModel() {
		return isDayModel;
	}
	public void setDayModel(int isDayModel) {
		this.isDayModel = isDayModel;
	}
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}


	private boolean isSetTrans = false;
	public GeneralTransitionSS(int segmentId, String stationId,String startTime, String endTime,int isDayModel,int mode ,int mod)
	
	{
		this.mode =mode;
		this.isDayModel = isDayModel;
		SegmentStationSequence sequence= new SegmentStationSequence();
		
		
		if(isDayModel==0)array=sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		setStateSpace(ArrayHelper.getMax(array)/mode+1);
		initState = ArrayHelper.getInitState(array);
	}
	
	/**
	 * 获得指定线路站点在指定时间段内的转移张量,已转置
	 * @return
	 */
	public double[][] getTransiton(){
		if(!isSetTrans){
			
			toTransTensor(array);
			//System.out.println("statespace " +stateSpace);
			Matrix.transpose(transition, stateSpace);
			//Matrix.print(transition, stateSpace);
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
		 System.out.println(array.size()+" " +stateSpace);
		for(int i=0;i<array.size()-1;i++){
			sum[array.get(i)/mode]+=1;
			transition[array.get(i)/mode][array.get(i+1)/mode]+=1;
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
			
		}
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				if(sum[i] > 0)transition[i][j] /= sum[i]; 
				//else transition[i][i] =1;
				else {					
						//transition[i][i] =1.0/stateSpace;	
					transition[i][j] =1.0/(stateSpace);	
				}
			}
		}
		
	}
}
