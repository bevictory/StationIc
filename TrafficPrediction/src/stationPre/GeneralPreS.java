package stationPre;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.mongodb.BasicDBObject;

import mongodb.GetIcArray;


import segmentStationPre.GeneralPreSS;
import segmentStationTransition.GeneralTransitionSS;
import stationTransition.GeneralTransition;
import transition.Transition;
import util.ArrayHelper;
import util.PredictDis;
import util.SegmentStationSequence;
import util.StateSet;
import util.Station;
import util.StationInfo;
import util.StationSequence;
import decomposition.DealVector;
import decomposition.GeneralPower;
import decomposition.Matrix;

public class GeneralPreS {
	private GeneralTransition generalTrans ;
	
	private String stationId;
	private String startTime;
	private int isDayModel=1;
	private int order =1;
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
public GeneralPreS(String stationId, String startTime, String endTime,int isDayModel,
		int mode, int mod,int order){
		
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode =mode;
		this.order = order;
		this.isDayModel = isDayModel;
		//super(stationId, startTime, endTime,mod);
		generalTrans = new GeneralTransition(stationId, startTime, endTime,isDayModel, mode, mod,order);
		
		
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
		//state[sta/mode>stateSpace-1?stateSpace-1:sta/mode]=1.0;
		StateSet.setState(state, stateSpace, sta/mode>stateSpace-1?stateSpace-1:sta/mode);
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
		PredictDis.dealSequence(array, generalTrans.getStateSpace());
		//generalTrans.getTransiton(segmentId,stationId, startTime, endTime, mod);
		//double[] res = generalTrans.getInitState();
		//DealVector.print(res, generalTrans.getStateSpace());
		//System.out.println("need pre "+array.size());
		System.out.println(array.subList(3, array.size()));
		List<Integer> preList = new ArrayList<Integer>();
		double preBias =0.0;
		int accurrate=0;
		
		for( int i =2 ;i < array.size()-1; i++){
			double[] res =null;
			//pre.add(prediction(res, array.get(i)/mode));
			List<Integer> pre_topN=ArrayHelper.getTopN(prediction(res, array.get(i)), topN);
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i)/mode);
			int state=ArrayHelper.getMinDisState(pre_topN, array.get(i+1)/mode)*mode;
			preList.add(state);
			if(PredictDis.isPreDis){
				preBias += array.get(i+1)>0?(double)Math.abs(array.get(i+1)-state)/array.get(i+1):
				(double)Math.abs(array.get(i+1)-state)/mode-1;
			}
			else preBias += array.get(i+1)>0?(double)Math.abs(array.get(i+1)-state)/array.get(i+1):
				(double)Math.abs(array.get(i+1)-state)/mode-1;
			if(ArrayHelper.isPredic(pre_topN, array.get(i+1)/mode, ArrayHelper.pre)){
				accurrate+=1;
			}
		}
		System.out.println(preList);
//		System.out.println("pre "+pre.size());
//		System.out.println(pre);
//		
//		for(int  i =0 ;i< array.size()-1; i++){
//			if(pre.get(i).equals(Integer.valueOf(array.get(i+1)/mode))){
//				accurrate+=1;
//			}
//		}
		//System.out.println(pre);
		System.out.println("preBias "+preBias/(array.size()-3));
		return ((double)accurrate/(array.size()-3));
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
		DealVector.print(z_eigen, generalTrans.getStateSpace());
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
	
	public double[] predictionN(double [] result_,List<Integer> list) {
		// TODO Auto-generated method stub
		int stateSpace = generalTrans.getStateSpace();
		double[] state = new double[stateSpace];		
		double[][] matrix = generalTrans.getTransiton();
		for(int i=0;i<order;i++){
			//state[list.get(i)/mode>stateSpace-1?stateSpace-1:list.get(i)/mode] += generalTrans.getPara().get(order-1-i);
			StateSet.setState(state, stateSpace, list.get(i)/mode>stateSpace-1?stateSpace-1:list.get(i)/mode, 
					generalTrans.getPara().get(order-1-i));
		}
		//System.out.println(state_);
		
		//Matrix.transpose(matrix,stateSpace);
		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		return result_;
	}
	public double accN(String time1, String time2,int topN){
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		StationSequence sequence = new StationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess( stationId, time1, time2, mod);
		else array = sequence.findProcess( stationId, time1, time2, mod);
		
		PredictDis.dealSequence(array, generalTrans.getStateSpace());
		
		System.out.println(array.subList(order, array.size()));
		List<Integer> preList = new ArrayList<Integer>();
		double preBias =0.0;		
		int accurrate=0;
		for( int i =0 ;i < array.size()-order; i++){			
			
			double[] res = null;
			List<Integer> list_order  = new ArrayList<Integer>();
			for(int j = 0; j < order; j++){
				list_order.add(array.get(i+j));
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=predictionN(res,list_order);
			List<Integer> pre_topN= ArrayHelper.getTopN(result, topN);
			//System.out.println(pre_topN);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			int state=ArrayHelper.getMinDisState(pre_topN, array.get(i+order)/mode)*mode;
			preList.add(state);
			preBias += array.get(i+order)>0?(double)Math.abs(array.get(i+order)-state)/array.get(i+order):
				(double)Math.abs(array.get(i+order)-state)/mode-1;
			if(ArrayHelper.isPredic(pre_topN, array.get(i+order)/mode, ArrayHelper.pre)){
				accurrate+=1;
			}
			
		}
		System.out.println(preList);
		System.out.println("preBias "+preBias/(array.size()-order));
		System.out.println("accurrate "+accurrate);
		return ((double)accurrate/(array.size()-order));
	}
	
	public double acc_ZeigenN(String time1, String time2,int topN){
		double [][] matrix = generalTrans.getTransiton();
		double[] z_eigen = getZ(matrix, generalTrans.getStateSpace());
		//DealVector.print(z_eigen, generalTrans.getStateSpace());
		StationSequence sequence = new StationSequence();
		List<Integer> array=sequence.findProcess(stationId, time1, time2,10*60);
		int accurrate=0;
		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen, topN);
		for(int  i =0 ;i< array.size()-1; i++){
			if(ArrayHelper.isPredic(topN_result, array.get(i)/mode, ArrayHelper.pre)){
				accurrate+=1;
			}
		}
		
		return ((double)accurrate/(array.size()));
	}
	
	public static void test(){
		String startTime = "15:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-11 15:30:00" ,time2 =  "2015-12-11 18:59:59";
		GeneralPreS generalPre  = new GeneralPreS( "12111300000000045323", startTime, endTime,2,1, 30*60);
		//generalPre.setMode(3);
//		System.out.println(generalPre.acc(time1, time2));
		double[][]matrix =generalPre.generalTrans.getTransiton();
		//Matrix.print(matrix, generalPre.generalTrans.getStateSpace());
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println(sequence.findProcess(35621447,"12111300000000045252", time1, time2, 30*60));
		
		double acc=generalPre.acc(time1, time2, 1);
		//double acc_e=generalPre.acc_Zeigen(time1, time2,1);
		System.out.println(generalPre.generalTrans.getStateSpace());
		//System.out.println(acc_e);
		System.out.println("prediction accurate:"+acc);
		//Matrix.print(generalPre.generalTrans.getTransiton(), generalPre.generalTrans.getStateSpace());
	}
	public static void testN(){
		String startTime = "15:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-11 15:30:00" ,time2 =  "2015-12-11 18:59:59";
		GeneralPreS generalPre  = new GeneralPreS( "12111300000000045323", 
				startTime, endTime,2,1, 30*60,3);
		//generalPre.setMode(3);
//		System.out.println(generalPre.acc(time1, time2));
		double[][]matrix =generalPre.generalTrans.getTransiton();
		
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println(sequence.findProcess(35621447,"12111300000000045252", time1, time2, 30*60));
		
		double acc=generalPre.accN(time1, time2, 1);
		//double acc_e=generalPre.acc_ZeigenN(time1, time2,2);
		System.out.println(generalPre.generalTrans.getPara());
		System.out.println(generalPre.generalTrans.getStateSpace());
		//System.out.println(acc_e);
		System.out.println("prediction accurate:"+acc);
	}
	
	public static void test_gene(){
		String startTime = "15:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-10 15:30:00" ,time2 =  "2015-12-11 18:59:59";
//		MultiPreSS multiPreSS  = 
//				new MultiPreSS(35632502, "12111300000000045323", startTime, endTime,2,5, 30*60);
		
		
		Station station = new Station();
		StationSequence sequence = new StationSequence();
		List<BasicDBObject> list=Station.getStaFromAnaly();
		
	
		
		
		for(int i =0;i<100;i++){
			System.out.println("i "+i);
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData( sta, startTime, endTime, 10*60)) continue;
			if(StationInfo.getNear(sta, 1, 200).size() ==0) continue;
			
			GeneralPreS generalPreSS =  new GeneralPreS( sta, startTime, endTime,2,1, 10*60);
			
			//double multi_zAcc = multiPreSS.acc_Zeigen(time1, time2, 2);
			//double gene_zAcc = generalPreSS.acc_Zeigen(time1, time2, 2);
			double GeneAcc = generalPreSS.acc(time1, time2, 2);
			System.out.println(" gene "+GeneAcc);
			//list.get(i).append("MultiAcc", multiAcc);
			//list.get(i).append("multi_zAcc", multi_zAcc);
			//list.get(i).append("GeneAcc", GeneAcc);
			//list.get(i).append("Gene_zAcc", gene_zAcc);
			//MongoDBAssis.getDb().getCollection("segStaMultiPre_mode5_mod30_ouji").insert(list.get(i));
			//System.out.println(list.get(i));
		}
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 4;
		testN();test();
		
	}
}
