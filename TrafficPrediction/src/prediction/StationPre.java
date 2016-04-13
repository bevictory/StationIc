package prediction;

import java.util.ArrayList;

import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.Tensor_3order;
import decomposition.Tensor_3order_power;
import mongodb.GetIcArray;

import transition.StationTransition;
import transition.Transition;

public class StationPre extends Prediction{
	private double[] result;
	private StationTransition stationTrans;
	StationPre(int segmentId, int sngSerialId, String startTime, String endTime) {
		super(segmentId, sngSerialId, startTime, endTime);
		// TODO Auto-generated constructor stub
		stationTrans = new StationTransition();
		this.segmentId= segmentId;
		this.sngSerialId = sngSerialId;
	}
	@Override
	public void prediction(String time) {
		// TODO Auto-generated method stub
		double[] state = new double[Transition.getStateSpace()];
		double[] state_relate = new double[Transition.getStateSpace()];
		double[][][] tensor_3order = stationTrans.getTransiton(segmentId, sngSerialId, startTime, endTime);
		state_relate[get_relateState(time)] = 1.0;
		state[GetIcArray.getIcAtTime(segmentId, sngSerialId, time)]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, Transition.getStateSpace());
		Matrix.transpose(matrix, Transition.getStateSpace());
		result = Matrix.multip_vector(matrix, state, Transition.getStateSpace());
	}
	public int prediction(double [] result_,int state_r, int state_) {
		// TODO Auto-generated method stub
		double[] state = new double[Transition.getStateSpace()];
		double[] state_relate = new double[Transition.getStateSpace()];
		double[][][] tensor_3order = stationTrans.getTransiton(segmentId, sngSerialId, this.startTime, this.endTime);
		state_relate[state_r] = 1.0;
		state[state_]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, Transition.getStateSpace());
		Matrix.transpose(matrix, Transition.getStateSpace());
		result_ = Matrix.multip_vector(matrix, state, Transition.getStateSpace());
		return DealVector.getMax(result_, Transition.getStateSpace());
	}
	public int get_relateState(String time){
		ArrayList<Integer> array = new ArrayList<Integer>();
		array.add(GetIcArray.getIcAtTime(segmentId, sngSerialId-1, time));
		array.add(GetIcArray.getIcAtTime(segmentId, sngSerialId+1, time));
		int state =0;
		for(int i = 0; i < array.size(); i++){
			state+=array.get(i)*stationTrans.getPara().get(i);
		}
		return state;
	}
	public double[] getResult() {
		return result;
	}
	public double acc(String startTime, String endTime){
		ArrayList<ArrayList<Integer>> array_relate = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> array = new ArrayList<Integer>();
		ArrayList<Integer> pre = new ArrayList<Integer>();
		array_relate.add(GetIcArray.getIC_int(segmentId, sngSerialId-1, startTime, endTime));
		array_relate.add(GetIcArray.getIC_int(segmentId, sngSerialId+1, startTime, endTime));
		array = GetIcArray.getIC_int(segmentId, sngSerialId, startTime, endTime);
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		//System.out.println(length);
		for(int  i =0 ;i< length; i++){
			double[] res = new double[Transition.getStateSpace()];
			int state_r =0;
			for(int j = 0; j < array_relate.size(); j++){
				state_r+=array_relate.get(j).get(i)*stationTrans.getPara().get(j);
			}
			pre.add(prediction(res,state_r, array.get(i)));
		}
		int accurrate=0;
		for(int  i =0 ;i< length; i++){
			if(array.get(i).equals(pre.get(i))){
				accurrate+=1;
			}
		}
		return ((double)accurrate/length);
	}
	public double acc_stable(String startTime, String endTime){
		ArrayList<ArrayList<Integer>> array_relate = new ArrayList<ArrayList<Integer>>();
		ArrayList<Integer> array = new ArrayList<Integer>();
		ArrayList<Integer> pre = new ArrayList<Integer>();
		array_relate.add(GetIcArray.getIC_int(segmentId, sngSerialId-1, startTime, endTime));
		array_relate.add(GetIcArray.getIC_int(segmentId, sngSerialId+1, startTime, endTime));
		array = GetIcArray.getIC_int(segmentId, sngSerialId, startTime, endTime);
		double[] p = Tensor_3order_power.power(stationTrans.getTransiton(segmentId, sngSerialId, this.startTime, this.endTime), Transition.getStateSpace());
		
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		System.out.println(length);
		for(int  i =0 ;i< length; i++){
			double[] result = new double[Transition.getStateSpace()];
			int state_r =0;
			for(int j = 0; j < array_relate.size(); j++){
				state_r+=array_relate.get(j).get(i)*stationTrans.getPara().get(j);
			}
			pre.add(prediction(result,state_r, array.get(i)));
		}
		int accurrate=0;
		for(int  i =0 ;i< length; i++){
			if(array.get(i).equals(pre.get(i))){
				accurrate+=1;
			}
		}
		return ((double)accurrate/length);
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 4;
		
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		String time1 =  "2015-12-11 06:30:00",time2 =  "2015-12-11 09:00:00"; 
//		StationPre pre = new StationPre(segmentId, sngSerialId, startTime, endTime);
		for(int i=2; i <=7;i++){
			sngSerialId = i;
			StationPre pre = new StationPre(segmentId, sngSerialId, startTime, endTime);
			System.out.println(pre.acc(time1, time2));
		}
//		System.out.println(pre.acc(time1,time2));
//		DealVector.print(pre.getResult(), Transition.getStateSpace());
	}
	
	

}
