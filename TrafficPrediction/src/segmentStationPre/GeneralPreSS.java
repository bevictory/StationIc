package segmentStationPre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import segmentStationTransition.GeneralTransitionSS;
import stationPre.GeneralPreS;
import stationTransition.GeneralTransition;
import transition.Transition;
import util.ArrayHelper;
import util.SegmentStationSequence;
import util.StationSequence;
import decomposition.DealVector;
import decomposition.GeneralPower;
import decomposition.Matrix;

public class GeneralPreSS {
	public GeneralTransitionSS generalTrans ;
	private int segmentId;
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
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public GeneralPreSS(int segmentId,String stationId, String startTime, String endTime,int isDayModel,int mode, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode =mode;
		this.isDayModel = isDayModel;
		//super(stationId, startTime, endTime,mod);
		generalTrans = new GeneralTransitionSS(segmentId, stationId, startTime, endTime,isDayModel, mode, mod);
		generalTrans.setDayModel(isDayModel);
		
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
	public ArrayList<Integer> prediction_top2(double[] res, int sta){
		double[] state = new double[generalTrans.getStateSpace()];
		state[sta]=1.0;
		res = Matrix.multip_vector(generalTrans.getTransiton(), state, generalTrans.getStateSpace());
		return DealVector.getTop(res, generalTrans.getStateSpace(),2);
		
	}
	public double   acc(String time1, String time2,int topN){
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		SegmentStationSequence sequence = new SegmentStationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId, stationId, time1, time2, mod);
		else array = sequence.findProcess(segmentId, stationId, time1, time2, mod);
		
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
	public double   acc_top2(String time1, String time2){
		
		ArrayList<ArrayList<Integer>> pre = new ArrayList<ArrayList<Integer>>();
		List<Integer> array = new ArrayList<Integer>();
		
		SegmentStationSequence sequence = new SegmentStationSequence();
		generalTrans.getTransiton();
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId, stationId, time1, time2, mod);
		else array = sequence.findProcess(segmentId, stationId,  time1, time2, mod);
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
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<Integer> array=sequence.findProcess(segmentId,stationId, time1, time2, 20*60);
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
		GeneralPreSS generalPre  = new GeneralPreSS(36371609, "12111300000000045252", startTime, endTime,2,3, 20*60);
		//generalPre.setMode(3);
//		System.out.println(generalPre.acc(time1, time2));
		double[][]matrix =generalPre.generalTrans.getTransiton();
		System.out.println("get matrix");
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println(sequence.findProcess(35621447,"12111300000000045252", time1, time2, 30*60));
		System.out.println(generalPre.acc_Zeigen(time1, time2,1));
		System.out.println(generalPre.acc(time1, time2, 2));
	}
}
