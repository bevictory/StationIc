package stationPre;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mongodb.GetIcArray;


import stationTransition.GeneralTransition;
import transition.Transition;
import util.StationSequence;
import decomposition.DealVector;
import decomposition.Matrix;

public class GeneralPreS {
	private GeneralTransition generalTrans ;
	private String stationId;
	private String startTime;
	private String endTime;
	private int mod;
	private double[] result ;
	private int mode =1;
	public void setMode(int mode) {
		this.mode = mode;
		this.generalTrans.setMode(mode);
	}
	public GeneralPreS(String stationId, String startTime, String endTime, int mod){
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		//super(stationId, startTime, endTime,mod);
		generalTrans = new GeneralTransition();
		
	}
	
	
	
	public double[] getResult() {
		return result;
	}
	public void prediction(){
		double[] state = new double[Transition.getStateSpace()];
		//state[GetIcArray.getIcAtTime(segmentId, sngSerialId, time)]=1.0;
		result = Matrix.multip_vector(generalTrans.getTransiton(stationId, startTime, endTime,mod), state, generalTrans.getStateSpace());
	}
	/**
	 * Ò»²½×ªÒÆÔ¤²â
	 * @param res
	 * @param sta
	 * @return
	 */
	public int prediction(double[] res, int sta){
		double[] state = new double[generalTrans.getStateSpace()];
		state[sta]=1.0;
		res = Matrix.multip_vector(generalTrans.getTransiton(stationId, startTime, endTime,mod), state, generalTrans.getStateSpace());
		return DealVector.getMax(res, generalTrans.getStateSpace());
		
	}
	
	public int predictionByInitState(double[] res, int sta){
		
		res = Matrix.multip_vector(generalTrans.getTransiton(stationId, startTime, endTime,mod), res, generalTrans.getStateSpace());
		return DealVector.getMax(res, generalTrans.getStateSpace());
		
	}
	public ArrayList<Integer> prediction_top2(double[] res, int sta){
		double[] state = new double[generalTrans.getStateSpace()];
		state[sta]=1.0;
		res = Matrix.multip_vector(generalTrans.getTransiton(stationId, startTime, endTime,mod), state, generalTrans.getStateSpace());
		return DealVector.getTop(res, generalTrans.getStateSpace(),2);
		
	}
	public double   acc(String time1, String time2){
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		StationSequence sequence = new StationSequence();
		
		array = sequence.findBydayProcess(stationId, time1, time2, mod);
		//array = sequence.findProcess(stationId, time1, time2, mod);
		
		generalTrans.getTransiton(stationId, startTime, endTime, mod);
		//double[] res = generalTrans.getInitState();
		//DealVector.print(res, generalTrans.getStateSpace());
		System.out.println("need pre "+array.size());
		System.out.println(array);
		
		for( int i =0 ;i < array.size()-1; i++){
			double[] res =null;
			pre.add(prediction(res, array.get(i)/mode));
		}
		System.out.println("pre "+pre.size());
		System.out.println(pre);
		int accurrate=0;
		for(int  i =0 ;i< array.size()-1; i++){
			if(pre.get(i).equals(Integer.valueOf(array.get(i+1)/mode))){
				accurrate+=1;
			}
		}
		//System.out.println(pre);
		return ((double)accurrate/(array.size()-1));
	}
	public double   acc_top2(String time1, String time2){
		
		ArrayList<ArrayList<Integer>> pre = new ArrayList<ArrayList<Integer>>();
		List<Integer> array = new ArrayList<Integer>();
		
		StationSequence sequence = new StationSequence();
		generalTrans.getTransiton(stationId, startTime, endTime, mod);
		array = sequence.findBydayProcess(stationId, time1, time2, mod);
		//array = sequence.findProcess(stationId, time1, time2, mod);
		System.out.println(array);
		//double[] res = generalTrans.getInitState();
		for( int i =0 ;i < array.size()-1; i++){
			double[] res = new double[generalTrans.getStateSpace()];
			pre.add(prediction_top2(res, array.get(i)/mode));
		}
		int accurrate=0;
		for(int  i =0 ;i< array.size()-1; i++){
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
		
		String startTime = "2015-12-07 00:00:00", endTime = "2015-12-10 23:59:59";
		String time1 =  "2015-12-11 00:00:00" ,time2 =  "2015-12-11 23:59:59";
		GeneralPreS generalPre  = new GeneralPreS("12111300000000045252", startTime, endTime, 60*60);
		generalPre.setMode(30);
		System.out.println(generalPre.acc(time1, time2));
	}
}
