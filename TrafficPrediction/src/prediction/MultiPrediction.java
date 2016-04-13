package prediction;

import java.util.ArrayList;
import java.util.Vector;

import decomposition.DealVector;
import decomposition.Tensor_4order;

import mongodb.GetIcArray;
import transition.MultiTransition;
import transition.Transition;

public class MultiPrediction extends Prediction{
	private MultiTransition multiTransition;
	private double [][] result =null;
	private  int mode =1;
	public void setMode(int mode) {
		this.mode = mode;
		this.multiTransition.setMode(mode);
	}
	MultiPrediction(int segmentId, int sngSerialId, String startTime,
			String endTime) {
		super(segmentId, sngSerialId, startTime, endTime);
		multiTransition = new MultiTransition();
		// TODO Auto-generated constructor stub
	}
	public ArrayList<Integer> getIc(int segmentId, int sngSerialId, String startTime, String endTime){
		if(mode >1) return GetIcArray.getIcByHour_int(segmentId, sngSerialId, startTime, endTime);
		else return GetIcArray.getIC_int(segmentId, sngSerialId, startTime, endTime);
	}
	public void setSngSerialId( int sngSerialId){
		this.sngSerialId = sngSerialId;
	}
	@Override
	public void prediction(String time) {
		// TODO Auto-generated method stub
		
	}
	public void prediction(){
		result = Tensor_4order.multip_2order_formulti(multiTransition.getTransition(segmentId, sngSerialId, this.startTime, this.endTime),
				this.result, multiTransition.getClusterNum(), Transition.getStateSpace());
		
	}
	public double   acc(String time1, String time2){
		ArrayList<ArrayList<Integer>> array_relate = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> array = new ArrayList<Integer>();
		ArrayList<Integer> pre = new ArrayList<Integer>();
		array_relate.add(getIc(segmentId, sngSerialId-1, time1, time2));
		array_relate.add(getIc(segmentId, sngSerialId+1,  time1, time2));
		array = getIc(segmentId, sngSerialId,  time1, time2);
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		multiTransition.setTransition(segmentId, sngSerialId, this.startTime, this.endTime);
//		result = new double[multiTransition.getClusterNum()][Transition.getStateSpace()];
//		result[0][array.get(0)/mode] = 1.0;
//		result[1][array_relate.get(0).get(0)/mode] = 1.0;
//		result[2][array_relate.get(1).get(0)/mode] = 1.0;
		for( int i =0 ;i < length-1; i++){
			
			result = new double[multiTransition.getClusterNum()][Transition.getStateSpace()];
			result[0][array.get(i)/mode] = 1.0;
			result[1][array_relate.get(0).get(i)/mode] = 1.0;
			result[2][array_relate.get(1).get(i)/mode] = 1.0;
			prediction();
			pre.add(DealVector.getMax(result[0], Transition.getStateSpace()));
			DealVector.print(result[0], Transition.getStateSpace());
			
		}
		int accurrate=0;
		for(int  i =0 ;i< length-1; i++){
			if(pre.get(i).equals(Integer.valueOf((array.get(i+1)/mode)) )){
				accurrate+=1;
			}
		}
		System.out.println(pre);
		return ((double)accurrate/(array.size()-1));
	}
	public double   acc_top2(String time1, String time2){
		ArrayList<ArrayList<Integer>> array_relate = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> array = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> pre = new ArrayList<ArrayList<Integer>>();
		array_relate.add(getIc(segmentId, sngSerialId-1, time1, time2));
		array_relate.add(getIc(segmentId, sngSerialId+1,  time1, time2));
		array = getIc(segmentId, sngSerialId,  time1, time2);
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		multiTransition.setTransition(segmentId, sngSerialId, this.startTime, this.endTime);
		
		for( int i =0 ;i < length-1; i++){
			
			result = new double[multiTransition.getClusterNum()][Transition.getStateSpace()];
			result[0][array.get(i)/mode] = 1.0;
			result[1][array_relate.get(0).get(i)/mode] = 1.0;
			result[2][array_relate.get(1).get(i)/mode] = 1.0;
			prediction();
			pre.add(DealVector.getTop(result[0], Transition.getStateSpace(),2));
			DealVector.print(result[0], Transition.getStateSpace());
			
		}
		int accurrate=0;
		for(int  i =0 ;i< length-1; i++){
			if(pre.get(i).contains(Integer.valueOf(array.get(i+1)/mode))){
				accurrate+=1;
			}
		}
		System.out.println(pre);
		return ((double)accurrate/(array.size()-1));
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 4;
		
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 09:00:00";
//		GeneralPre pre = new GeneralPre(segmentId, sngSerialId, startTime, endTime);
//		
//		pre.prediction(time);
//		DealVector.print(pre.getResult(), Transition.getStateSpace());
		for(int i=18; i <=18;i++){
			sngSerialId = i;
			MultiPrediction pre = new MultiPrediction(segmentId, sngSerialId, startTime, endTime);
			pre.setMode(3);
			System.out.println(pre.acc_top2(time1, time2));
		}
	}

}
