package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

import decomposition.Matrix;
import decomposition.Tensor_3order;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import util.ArrayHelper;
import util.SegmentStationSequence;

public class LineTransitionSS {
	private double[][][] transTensor;
	private boolean isSetTrans = false;
	private List<List<Integer>> array_relate ;
	private List<Integer> relate_segment;
	private List<Integer> array;
	private List<Double> para;
	private int stateSpace ;
	private int mode =1;
	private int mod;
	private int  isDayModel =0;
	public int isDayModel() {
		return isDayModel;
	}
	public void setDayModel(int isDayModel) {
		this.isDayModel = isDayModel;
	}
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public List<Double> getPara() {
		return para;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}

	
	//private boolean isSetTrans = false;
	public LineTransitionSS(int segmentId, String stationId, String startTime, String endTime,int isDaymodel,int mode,int mod){
		this.mod = mod;
		this.mode = mode;
		this.isDayModel = isDaymodel;
		array_relate = new ArrayList<List<Integer>>();
		
		relate_segment = new ArrayList<Integer>();
		para = new ArrayList<Double>();
		SegmentStationSequence sequence= new SegmentStationSequence();
		if(isDayModel==0) array= sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1)array= sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array=sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		set_arrayRelate(segmentId, stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		set_para(array);
	}
	public double[][][] getTransiton(){
		if(!isSetTrans){	
			transTensor = new double[stateSpace][stateSpace][stateSpace];			
			toTransTensor(array);
			//Tensor_3order.print(transTensor, stateSpace);
			//Matrix.print(transTensor[4], stateSpace);
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
			double state =0 ;
			for(int j =0 ; j < array_relate.size(); j++){
				state+= array_relate.get(j).get(i)* para.get(j);
			}
			sum[(int)state/mode ][array.get(i)/mode] += 1;
			transTensor[(int)state/mode ][array.get(i)/mode][array.get(i+1)/mode] += 1;
			
		}
		
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
					{
						//System.out.println("trans "+transTensor[i][j][k]+" "+sum[i][j] +" "+i+" "+j+" "+k);
						transTensor[i][j][k] /= sum[i][j];
						
					}
					
					// else tranMatrix[i][i] =1;
					else {
						//transTensor[i][j][k] = 1.0 / ( stateSpace);
						transTensor[i][j][j] = 2.0;
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
		List<Integer> lineList=QueryBls.getSameStation(MongoDBAssis.getDb(), segmentId,stationId );
		SegmentStationSequence sequence= new SegmentStationSequence();
		//System.out.println("same station line "+lineList.size());
		for(int i=0;i<lineList.size();i++){
			if(isDayModel==1&&!sequence.hasData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			if(isDayModel==2&&!sequence.hasWorkdayData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			List<Integer> array;
			relate_segment.add(lineList.get(i));
			if(isDayModel==0) array= sequence.findBydayProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(lineList.get(i), stationId, startTime, endTime, mod);
			array_relate.add(array);
		}
		//System.out.println("array_relate size "+array_relate.size());
		
		
	}
	public void set_para(List<Integer> array){
		int sum=0;
		List<Integer> dis= new ArrayList<Integer>();
		for(int i=0;i<array_relate.size();i++){
			int d = getDis(array, array_relate.get(i));
			dis.add(d);
			sum+=d;
		}
		//System.out.println("sum " +sum);
		for(int i=0;i<array_relate.size();i++){
			para.add((double)dis.get(i)/sum);
		}
		//System.out.println(para);
	}
	
	public List<Integer> getRelate_segment() {
		return relate_segment;
	}
	public int  getDis(List<Integer> array1, List<Integer> array2){
		int dis =0; 
		//System.out.println(array1.size()+" "+array2.size());
		for(int i=0 ;i <array1.size();i++){
			 dis += Math.abs(Math.pow(array1.get(i)- array2.get(i),2));
		}
		dis= (int) Math.sqrt(dis);
		return dis;
	}
}
