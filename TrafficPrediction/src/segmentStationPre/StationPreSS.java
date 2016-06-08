package segmentStationPre;

import java.util.ArrayList;

import mongodb.GetIcArray;
import prediction.StationPre;
import segmentStationTransition.GeneralTransitionSS;
import segmentStationTransition.StationTransitionSS;
import transition.StationTransition;
import transition.Transition;
import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.Tensor_3order;
import decomposition.Tensor_3order_power;

public class StationPreSS {
	private StationTransitionSS stationTrans ;
	private int segmentId;
	private String stationId;
	private String startTime;
	private String endTime;
	private int mod;
	private double[] result ;
	private int mode =1;
	public void setMode(int mode) {
		this.mode = mode;
		this.stationTrans.setMode(mode);
	}
	public StationPreSS(int segmentId,String stationId, String startTime, String endTime, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		//super(stationId, startTime, endTime,mod);
		stationTrans = new StationTransitionSS();
		
	}
	public void prediction(String time) {
		// TODO Auto-generated method stub
		double[] state = new double[Transition.getStateSpace()];
		double[] state_relate = new double[Transition.getStateSpace()];
		double[][][] tensor_3order = stationTrans.getTransiton(segmentId, stationId, startTime, endTime,mod);
		state_relate[get_relateState(time)] = 1.0;
		//state[GetIcArray.getIcAtTime(segmentId, stationId, time)]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, Transition.getStateSpace());
		Matrix.transpose(matrix, Transition.getStateSpace());
		result = Matrix.multip_vector(matrix, state, Transition.getStateSpace());
	}
	public int prediction(double [] result_,int state_r, int state_) {
		// TODO Auto-generated method stub
		double[] state = new double[Transition.getStateSpace()];
		double[] state_relate = new double[Transition.getStateSpace()];
		double[][][] tensor_3order = stationTrans.getTransiton(segmentId, stationId, this.startTime, this.endTime,mod);
		state_relate[state_r] = 1.0;
		state[state_]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, Transition.getStateSpace());
		Matrix.transpose(matrix, Transition.getStateSpace());
		result_ = Matrix.multip_vector(matrix, state, Transition.getStateSpace());
		return DealVector.getMax(result_, Transition.getStateSpace());
	}
	public int get_relateState(String time){
		ArrayList<Integer> array = new ArrayList<Integer>();
		
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


		//array = GetIcArray.getIC_int(segmentId, stationId, startTime, endTime,mod);
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
		
		double[] p = Tensor_3order_power.power(stationTrans.getTransiton(segmentId, stationId, this.startTime, this.endTime,mod), Transition.getStateSpace());
		
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
			//StationPre pre = new StationPre(segmentId, sngSerialId, startTime, endTime);
			//System.out.println(pre.acc(time1, time2));
		}
//		System.out.println(pre.acc(time1,time2));
//		DealVector.print(pre.getResult(), Transition.getStateSpace());
	}
}
