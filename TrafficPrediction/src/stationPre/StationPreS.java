package stationPre;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.mongodb.BasicDBObject;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.Tensor_3order;
import decomposition.Tensor_3order_power;
import segmentStationPre.GeneralPreSS;
import segmentStationPre.LinePreSS;
import segmentStationTransition.GeneralTransitionSS;
import segmentStationTransition.LineTransitionSS;
import stationTransition.StationTransitionS;
import transition.Transition;
import util.ArrayHelper;
import util.SegmentStation;
import util.SegmentStationSequence;
import util.Station;
import util.StationInfo;
import util.StationSequence;

public class StationPreS {
	private StationTransitionS stationTrans;
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
		this.stationTrans.setMode(mode);
	}
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	
	public StationPreS(String stationId,int mode, int mod){
		
		this.stationId = stationId;
		
		this.mod = mod;
		this.mode = mode;
		
	}
	public StationPreS(String stationId, String startTime, String endTime,int isDayModel,int mode, int mod){
		
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode = mode;
		//super(stationId, startTime, endTime,mod);
		stationTrans = new StationTransitionS( stationId, startTime, endTime,isDayModel,mode, mod);
		//stationTrans.setDayModel(isDayModel);
		
	}
	public double acc_Zeigen(String time1, String time2,int topN){
		double [][][] tensor = stationTrans.getTransiton();
		double[] z_eigen = getZ(tensor, stationTrans.getStateSpace());
		StationSequence sequence = new StationSequence();
		List<Integer> array=sequence.findProcess(stationId, time1, time2, mod);
		
		int accurrate=0;
		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen, topN);
		for(int  i =0 ;i< array.size(); i++){
			if(topN_result.contains(Integer.valueOf(array.get(i)/mode))){
				accurrate+=1;
			}
		}
		
		return ((double)accurrate/(array.size()));
	}
	public double[] getZ(double[][][] tensor,int state){
		return Tensor_3order_power.power(tensor, stationTrans.getStateSpace());
	}
	/**
	 * 预测 传入的参数都是原始的数据
	 * @param result_
	 * @param state_r
	 * @param state_
	 * @return
	 */
	public double[] prediction(double [] result_,int state_r, int state_) {
		// TODO Auto-generated method stub
		int stateSpace = stationTrans.getStateSpace();
		double[] state = new double[stateSpace];
		double[] state_relate = new double[stateSpace];
		double[][][] tensor_3order = stationTrans.getTransiton();
		state_relate[state_r/mode>stateSpace-1?stateSpace-1:state_r/mode] = 1.0;
		//System.out.println(state_);
		
		state[state_/mode>stateSpace-1?stateSpace-1:state_/mode]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, stateSpace);
		Matrix.transpose(matrix,stateSpace);
		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		return result_;
	}
	
	public double[] getResult() {
		return result;
	}
	public void set_relate(List<List<Integer>>array_relate,String stationId, String startTime, String endTime){
		List<String> stationList=stationTrans.getRelate_station();
		StationSequence sequence= new StationSequence();
		for(int i=0;i<stationList.size();i++){
			List<Integer> array;
			if(isDayModel==0) array= sequence.findBydayProcess(stationList.get(i),startTime, endTime, mod);
			else array= sequence.findProcess(stationList.get(i), startTime, endTime, mod);
			array_relate.add(array);
			//System.out.println(array);
		}
	}
	public double acc(String startTime, String endTime,int topN){
		List<List<Integer>> array_relate = new ArrayList<List<Integer>>();
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		set_relate(array_relate, stationId, startTime, endTime);

		//array = GetIcArray.getIC_int(segmentId, stationId, startTime, endTime,mod);
		StationSequence sequence  = new StationSequence();
		if(isDayModel==0) array = sequence.findBydayProcess(stationId, startTime, endTime, mod);
		else array = sequence.findProcess(stationId, startTime, endTime, mod);
		int length = array.size();
		//System.out.println(array);
		//System.out.println(length);
		int accurrate=0;
		for(int  i =0 ;i< length-1; i++){
			double[] res = new double[stationTrans.getStateSpace()];
			double state_r =0;
			for(int j = 0; j < array_relate.size(); j++){
				state_r+=array_relate.get(j).get(i)*stationTrans.getPara().get(j);
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			double [] result=prediction(res,(int)state_r, array.get(i));
			List<Integer> pre_topN= ArrayHelper.getTopN(result, topN);
			//DealVector.print(result, lineTrans.getStateSpace());
		   // System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			if(pre_topN.contains(array.get(i+1)/mode)){
				accurrate+=1;
			}
		}
		
		
		return ((double)accurrate/(length-1));
	}
	public static void main(String []args){
		int segmentId = 36371609;
		int sngSerialId = 4;
		
		String startTime = "06:30:00", endTime = "10:00:00";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 09:59:59";
		
		Station  station = new Station();
		StationSequence sequence = new StationSequence();
		List<BasicDBObject> list=Station.getStaFromAnaly();
		for(int i =0;i<100;i++){
			//int seg =list.get(i).getInt("segmentId") ;
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData( sta, startTime, endTime, 10*60)) continue;
			if(StationInfo.getNear(sta, 1, 200).size()==0) continue;
			StationPreS stationPreS  = 
					new StationPreS(sta, startTime, endTime,2,5, 10*60);
			GeneralPreS generalPreS = new GeneralPreS( sta, startTime, endTime, 2, 5, 10*60);
			double StaAcc = stationPreS.acc(time1, time2,2);
			double sta_zAcc = stationPreS.acc_Zeigen(time1, time2, 2);
			double gene_zAcc = generalPreS.acc_Zeigen(time1, time2, 2);
			double GeneAcc = generalPreS.acc(time1, time2, 2);
			list.get(i).append("StaAcc", StaAcc);
			list.get(i).append("Sta_zAcc", sta_zAcc);
			list.get(i).append("GeneAcc", GeneAcc);
			list.get(i).append("Gene_zAcc", gene_zAcc);
			MongoDBAssis.getDb().getCollection("staPre_mode5_mod10").insert(list.get(i));
			System.out.println(list.get(i));
		}
		
		
		
//		StationPreS stationPreS  = 
//				new StationPreS("12111300000000045391", startTime, endTime,2,8, 30*60);
		//linePreSS.setMode(3);
//		generalPre.setMode(30);
//		System.out.println(generalPre.acc(time1, time2));
		//double[][][]matrix =linePreSS.lineTrans.getTransiton();
		//System.out.println("get matrix");
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println("prediction");
		//System.out.println(sequence.findProcess(36371609,"12111300000000045252", time1, time2, 20*60));
		
//		System.out.println(stationPreS.stationTrans.getRelate_station());
//		System.out.println(stationPreS.acc_Zeigen(time1, time2, 2));
//		System.out.println("para "+stationPreS.stationTrans.getPara());
//		System.out.println(stationPreS.acc(time1, time2,2));
	}
}
