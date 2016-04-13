package prediction;

import java.util.ArrayList;

import mongodb.GetIcArray;
import mongodb.MongoDBAssis;
import decomposition.DealVector;
import decomposition.Matrix;
import transition.GeneralTransition;
import transition.Transition;

public class GeneralPre extends Prediction{
	private GeneralTransition generalTrans ;
	private double[] result ;
	private int mode =1;
	public void setMode(int mode) {
		this.mode = mode;
		this.generalTrans.setMode(mode);
	}
	GeneralPre(int segmentId, int sngSerialId,String startTime, String endTime){
		super(segmentId, sngSerialId, startTime, endTime);
		generalTrans = new GeneralTransition();
		
	}
	public ArrayList<Integer> getIc(int segmentId, int sngSerialId, String startTime, String endTime){
		if(mode >1) return GetIcArray.getIcByHour_int(segmentId, sngSerialId, startTime, endTime);
		else return GetIcArray.getIC_int(segmentId, sngSerialId, startTime, endTime);
	}
	public void setSngSerialId( int sngSerialId){
		this.sngSerialId = sngSerialId;
	}
	public double[] getResult() {
		return result;
	}
	public void prediction(String time){
		double[] state = new double[Transition.getStateSpace()];
		state[GetIcArray.getIcAtTime(segmentId, sngSerialId, time)]=1.0;
		result = Matrix.multip_vector(generalTrans.getTransiton(segmentId, sngSerialId, startTime, endTime), state, GeneralTransition.getStateSpace());
	}
	public int prediction(double[] res, int sta){
		double[] state = new double[Transition.getStateSpace()];
		state[sta]=1.0;
		res = Matrix.multip_vector(generalTrans.getTransiton(segmentId, sngSerialId, startTime, endTime), state, GeneralTransition.getStateSpace());
		return DealVector.getMax(res, Transition.getStateSpace());
		
	}
	public ArrayList<Integer> prediction_top2(double[] res, int sta){
		double[] state = new double[Transition.getStateSpace()];
		state[sta]=1.0;
		res = Matrix.multip_vector(generalTrans.getTransiton(segmentId, sngSerialId, startTime, endTime), state, GeneralTransition.getStateSpace());
		return DealVector.getTop(res, Transition.getStateSpace(),2);
		
	}
	public double   acc(String time1, String time2){
		ArrayList<Integer> array = new ArrayList<Integer>();
		ArrayList<Integer> pre = new ArrayList<Integer>();
		array = getIc(segmentId, sngSerialId, time1, time2);
		for( int i =0 ;i < array.size()-1; i++){
			double[] res = new double[Transition.getStateSpace()];
			pre.add(prediction(res, array.get(i)/mode));
		}
		System.out.println(pre);
		int accurrate=0;
		for(int  i =0 ;i< array.size()-1; i++){
			if(pre.get(i).equals(Integer.valueOf(array.get(i+1)/mode))){
				accurrate+=1;
			}
		}
		return ((double)accurrate/(array.size()-1));
	}
	public double   acc_top2(String time1, String time2){
		ArrayList<Integer> array = new ArrayList<Integer>();
		ArrayList<ArrayList<Integer>> pre = new ArrayList<ArrayList<Integer>>();
		array = getIc(segmentId, sngSerialId, time1, time2);
		for( int i =0 ;i < array.size()-1; i++){
			double[] res = new double[Transition.getStateSpace()];
			pre.add(prediction_top2(res, array.get(i)/mode));
		}
		int accurrate=0;
		for(int  i =0 ;i< array.size()-1; i++){
			if(pre.get(i).contains(Integer.valueOf(array.get(i+1)/mode))){
				accurrate+=1;
			}
		}
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
			GeneralPre pre = new GeneralPre(segmentId, sngSerialId, startTime, endTime);
			pre.setMode(3);
			System.out.println(pre.acc(time1, time2));
		}
	}

}
