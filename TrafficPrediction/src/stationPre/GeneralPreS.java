package stationPre;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import mongodb.GetIcArray;


import segmentStationPre.GeneralPreSS;
import segmentStationTransition.GeneralTransitionSS;
import stationTransition.GeneralTransition;
import transition.Transition;
import util.ArrayHelper;
import util.SegmentStationSequence;
import util.StationSequence;
import decomposition.DealVector;
import decomposition.GeneralPower;
import decomposition.Matrix;

public class GeneralPreS {
	private GeneralTransition generalTrans ;
	
	private String stationId;
	private String startTime;
	private int isDayModel=1;
	
	public int isDayModel() {
		return isDayModel;
	}
	public void setDayModel(int isDayModel) {
		this.isDayModel = isDayModel;
	}
	private String endTime;
	private int mod;
	private double[] result ;
	private int mode =1;
	public void setMode(int mode) {
		this.mode = mode;
		this.generalTrans.setMode(mode);
	}
	
	public GeneralPreS(String stationId, String startTime, String endTime,int isDayModel,int mode, int mod){
		
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode =mode;
		this.isDayModel = isDayModel;
		//super(stationId, startTime, endTime,mod);
		generalTrans = new GeneralTransition(stationId, startTime, endTime,isDayModel, mode, mod);
		
		
	}
	
	
	
	public double[] getResult() {
		return result;
	}
	public void prediction(){
		double[] state = new double[Transition.getStateSpace()];
		//state[GetIcArray.getIcAtTime(segmentId, sngSerialId, time)]=1.0;
		result = Matrix.multip_vector(generalTrans.getTransiton(), state, generalTrans.getStateSpace());
	}
	/**
	 * 一步转移预测
	 * @param res
	 * @param sta
	 * @return
	 */
	public double[] prediction(double[] res, int sta){
		int stateSpace = generalTrans.getStateSpace();
		double[] state = new double[generalTrans.getStateSpace()];
		state[sta/mode>stateSpace-1?stateSpace-1:sta/mode]=1.0;
		res = Matrix.multip_vector(generalTrans.getTransiton(), state, generalTrans.getStateSpace());
		return res;
		
	}
	
	public int predictionByInitState(double[] res, int sta){
		
		res = Matrix.multip_vector(generalTrans.getTransiton(), res, generalTrans.getStateSpace());
		return DealVector.getMax(res, generalTrans.getStateSpace());
		
	}
	
	public double   acc(String time1, String time2,int topN){
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		StationSequence sequence = new StationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess( stationId, time1, time2, mod);
		else array = sequence.findProcess( stationId,  time1, time2, mod);
		
		//generalTrans.getTransiton(segmentId,stationId, startTime, endTime, mod);
		//double[] res = generalTrans.getInitState();
		//DealVector.print(res, generalTrans.getStateSpace());
		System.out.println("need pre "+array.size());
		System.out.println(array);
		int accurrate=0;
		for( int i =0 ;i < array.size()-1; i++){
			double[] res =null;
			//pre.add(prediction(res, array.get(i)/mode));
			List<Integer> pre_topN=ArrayHelper.getTopN(prediction(res, array.get(i)), topN);
			System.out.println("pre_topN "+pre_topN);
			System.out.println("actual "+array.get(i)/mode);
			if(pre_topN.contains(array.get(i+1)/mode)){
				accurrate+=1;
			}
		}
//		System.out.println("pre "+pre.size());
//		System.out.println(pre);
//		
//		for(int  i =0 ;i< array.size()-1; i++){
//			if(pre.get(i).equals(Integer.valueOf(array.get(i+1)/mode))){
//				accurrate+=1;
//			}
//		}
		//System.out.println(pre);
		return ((double)accurrate/(array.size()-1));
	}
	
	/**
	 * z特征值预测
	 * @param time1
	 * @param time2
	 * @param topN
	 * @return
	 */
	public double acc_Zeigen(String time1, String time2,int topN){
		double [][] matrix = generalTrans.getTransiton();
		double[] z_eigen = getZ(matrix, generalTrans.getStateSpace());
		StationSequence sequence = new StationSequence();
		List<Integer> array=sequence.findProcess(stationId, time1, time2, mod);
		int accurrate=0;
		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen, topN);
		for(int  i =0 ;i< array.size()-1; i++){
			if(topN_result.contains(Integer.valueOf(array.get(i)/mode))){
				accurrate+=1;
			}
		}
		
		return ((double)accurrate/(array.size()));
	}
	/**
	 * 获得z特征值
	 * @param matrix
	 * @param state
	 * @return
	 */
	public double[] getZ(double[][] matrix, int state){
		return GeneralPower.decomp_nodel(matrix, generalTrans.getStateSpace());
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 4;
		
		String startTime = "06:30:00", endTime = "09:29:59";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 09:29:59";
		GeneralPreS generalPre  = new GeneralPreS( "12111300000000045252", startTime, endTime,2,10, 20*60);
		//generalPre.setMode(3);
//		System.out.println(generalPre.acc(time1, time2));
		double[][]matrix =generalPre.generalTrans.getTransiton();
		System.out.println("get matrix");
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println(sequence.findProcess(35621447,"12111300000000045252", time1, time2, 30*60));
		System.out.println(generalPre.acc_Zeigen(time1, time2,2));
		System.out.println(generalPre.acc(time1, time2, 2));
	}
}
