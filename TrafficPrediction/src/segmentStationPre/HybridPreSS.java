package segmentStationPre;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.TensorPower_5order;
import decomposition.Tensor_3order;
import decomposition.Tensor_3order_power;
import decomposition.Tensor_4order;
import decomposition.Tensor_5order;
import segmentStationTransition.GeneralTransitionSS;
import segmentStationTransition.HybridTransitionSS;

import transition.Transition;
import util.ArrayHelper;
import util.SegmentStationSequence;
import util.SegmentStationTuple;

public class HybridPreSS {
	private HybridTransitionSS hybridTrans ;
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
		this.hybridTrans.setMode(mode);
	}
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	
	public HybridPreSS(int segmentId, String stationId,int mode, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		
		this.mod = mod;
		this.mode = mode;
		
	}
	public HybridPreSS(int segmentId,String stationId, String startTime, String endTime,int isDayModel,int mode, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode = mode;
		//super(stationId, startTime, endTime,mod);
		hybridTrans = new HybridTransitionSS(segmentId, stationId, startTime, endTime,isDayModel,mode, mod);
		//hybridTrans.setDayModel(isDayModel);
		
	}
	public double acc_Zeigen(String time1, String time2,int topN){
		double [][][][][] tensor = hybridTrans.getTransiton();
		double[] z_eigen = getZ(tensor, hybridTrans.getStateSpace());
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<Integer> array=sequence.findProcess(segmentId,stationId, time1, time2, 20*60);
		int accurrate=0;
		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen, topN);
		for(int  i =0 ;i< array.size(); i++){
			if(topN_result.contains(Integer.valueOf(array.get(i)/mode))){
				accurrate+=1;
			}
		}
		
		return ((double)accurrate/(array.size()));
	}
	public double[] getZ(double[][][][][] tensor,int state){
		return TensorPower_5order.power(tensor, hybridTrans.getStateSpace());
	}
	/**
	 * 预测 传入的参数都是原始的数据
	 * @param result_
	 * @param state_r
	 * @param state_
	 * @return
	 */
	public double[] prediction(double [] result_,int preState,int state_l, int state_s,int state_) {
		// TODO Auto-generated method stub
		int stateSpace = hybridTrans.getStateSpace();
		double[] state = new double[stateSpace];
		double[] state_line = new double[stateSpace];
		double[] state_pre = new double[stateSpace];
		double[] state_station = new double[stateSpace];
		double[][][][][] tensor_5order = hybridTrans.getTransiton();
		state_line[state_l/mode>stateSpace-1?stateSpace-1:state_l/mode] = 1.0;
		state_station[state_s/mode>stateSpace-1?stateSpace-1:state_s/mode] = 1.0;
		state_pre[preState/mode>stateSpace-1?stateSpace-1:preState/mode] = 1.0;
		
		
		state[state_/mode>stateSpace-1?stateSpace-1:state_/mode]=1.0;
		
		double [][][][] tensor_4order=Tensor_5order.multip_order(tensor_5order, state_pre, stateSpace);
		double [][][] tensor_3order=Tensor_4order.multip_order(tensor_4order, state_station, stateSpace);
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_line, stateSpace);
		Matrix.transpose(matrix,stateSpace);
		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		return result_;
	}
	
	public double[] getResult() {
		return result;
	}
	public void set_relate(List<List<Integer>>array_line,List<List<Integer>>array_station,int segmentId,String stationId, String startTime, String endTime){
		List<Integer> lineList=hybridTrans.getRelate_segment();
		SegmentStationSequence sequence= new SegmentStationSequence();
		for(int i=0;i<lineList.size();i++){
			List<Integer> array;
			System.out.println(lineList.get(i));
			if(isDayModel==0) array= sequence.findBydayProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else array= sequence.findProcess(lineList.get(i),stationId, startTime, endTime, mod);
			array_line.add(array);
		}
		
		List<SegmentStationTuple> stationList = hybridTrans.getRelate_station();
		for(int i=0;i<stationList.size();i++){
			int segment = stationList.get(i).getSegmentId();
			String station = stationList.get(i).getStationId();
			List<Integer> array;
			if(isDayModel==0) array= sequence.findBydayProcess(segment,station, startTime, endTime, mod);
			else array= sequence.findProcess(segment,station, startTime, endTime, mod);
			array_station.add(array);
		}
	}
	public double acc(String startTime, String endTime,int topN){
		List<List<Integer>> array_line = new ArrayList<List<Integer>>();
		List<List<Integer>> array_station = new ArrayList<List<Integer>>();
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		set_relate(array_line, array_station,segmentId, stationId, startTime, endTime);

		//array = GetIcArray.getIC_int(segmentId, stationId, startTime, endTime,mod);
		SegmentStationSequence sequence  = new SegmentStationSequence();
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId, stationId, startTime, endTime, mod);
		else array = sequence.findProcess(segmentId, stationId, startTime, endTime, mod);
		int length = array.size();
		
		//System.out.println(length);
		int accurrate=0;
		for(int  i =1 ;i< length-1; i++){
			double[] res = new double[hybridTrans.getStateSpace()];
			double state_l =0;
			double state_s =0;
			for(int j = 0; j < array_line.size(); j++){
				state_l+=array_line.get(j).get(i)*hybridTrans.getPara_line().get(j);
			}
			for(int j = 0; j < array_station.size(); j++){
				state_s+=array_station.get(j).get(i)*hybridTrans.getPara_station().get(j);
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			double [] result=prediction(res,array.get(i-1),(int)state_l, (int)state_s,array.get(i));
			List<Integer> pre_topN= ArrayHelper.getTopN(result, topN);
			//DealVector.print(result, hybridTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
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
		HybridPreSS linePreSS  = 
				new HybridPreSS(35632502, "12111300000000045323", startTime, endTime,2,4, 30*60);
		//linePreSS.setMode(3);
//		generalPre.setMode(30);
//		System.out.println(generalPre.acc(time1, time2));
		//double[][][]matrix =linePreSS.hybridTrans.getTransiton();
		//System.out.println("get matrix");
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println("prediction");
		//System.out.println(sequence.findProcess(36371609,"12111300000000045252", time1, time2, 20*60));
		
		
		System.out.println(linePreSS.acc_Zeigen(time1, time2, 2));
		//System.out.println("para "+linePreSS.hybridTrans.getPara());
		System.out.println(linePreSS.acc(time1, time2,2));
	}
}
