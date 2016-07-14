package segmentStationPre;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import com.mongodb.BasicDBObject;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.TensorMultiPower_4order;
import decomposition.TensorPower_5order;
import decomposition.Tensor_3order;
import decomposition.Tensor_3order_power;
import decomposition.Tensor_4order;
import decomposition.Tensor_5order;
import segmentStationTransition.GeneralTransitionSS;

import segmentStationTransition.MultiTransitionSS;


import transition.MultiTransition;
import transition.Transition;
import util.ArrayHelper;
import util.SegmentStation;
import util.SegmentStationSequence;
import util.SegmentStationTuple;

public class MultiPreSS {
	private MultiTransitionSS multiTrans ;
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
	private int order=1;
	public void setMode(int mode) {
		this.mode = mode;
		this.multiTrans.setMode(mode);
	}
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	
	public MultiPreSS(int segmentId, String stationId,int mode, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		
		this.mod = mod;
		this.mode = mode;
		
	}
	public MultiPreSS(int segmentId,String stationId, String startTime, String endTime,int isDayModel,int mode, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode = mode;
		//super(stationId, startTime, endTime,mod);
		multiTrans = new MultiTransitionSS(segmentId, stationId, startTime, endTime,isDayModel,mode, mod);
		//multiTrans.setDayModel(isDayModel);
		
	}
	public MultiPreSS(int segmentId,String stationId, String startTime, String endTime,
			int isDayModel,int mode, int mod, int order){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode = mode;
		this.order = order;
		//super(stationId, startTime, endTime,mod);
		multiTrans = new MultiTransitionSS(segmentId, stationId, startTime, endTime,isDayModel,mode, mod,order);
		//multiTrans.setDayModel(isDayModel);
		
	}
//	public double acc_Zeigen(String time1, String time2,int topN){
//		double [][][][] tensor = multiTrans.getTransition();
//		double[] z_eigen = getZ(tensor, multiTrans.getStateSpace());
//		SegmentStationSequence sequence = new SegmentStationSequence();
//		List<Integer> array=sequence.findProcess(segmentId,stationId, time1, time2, 20*60);
//		int accurrate=0;
//		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen, topN);
//		for(int  i =0 ;i< array.size(); i++){
//			if(topN_result.contains(Integer.valueOf(array.get(i)/mode))){
//				accurrate+=1;
//			}
//		}
//		
//		return ((double)accurrate/(array.size()));
//	}
	public double[] getZ(double[][][][][] tensor,int state){
		return TensorPower_5order.power(tensor, multiTrans.getStateSpace());
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
		int stateSpace = multiTrans.getStateSpace();
		
		double[][][][] tensor_4order = multiTrans.getTransition();
		
		return result_;
	}
	
	public double[] getResult() {
		return result;
	}
	public void set_relate(List<List<Integer>>array_relate,int segmentId,String stationId, String startTime, String endTime){
		
		SegmentStationSequence sequence= new SegmentStationSequence();
		
		List<SegmentStationTuple> stationList = multiTrans.getClusterList();
		for(int i=0;i<stationList.size();i++){
			int segment = stationList.get(i).getSegmentId();
			String station = stationList.get(i).getStationId();
			List<Integer> array;
			if(isDayModel==0) array= sequence.findBydayProcess(segment,station, startTime, endTime, mod);
			else array= sequence.findProcess(segment,station, startTime, endTime, mod);
			array_relate.add(array);
		}
	}
	
	public double[][] prediction(double [][]result){
		result = Tensor_4order.multip_2order_formulti(multiTrans.getTransition(),
				result, multiTrans.getClusterNum(),multiTrans.getStateSpace());
		return result;
		
	}
	public double acc(String startTime, String endTime,int topN){
		List<List<Integer>> array_relate = new ArrayList<List<Integer>>();
		
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		set_relate(array_relate, segmentId, stationId, startTime, endTime);

		//array = GetIcArray.getIC_int(segmentId, stationId, startTime, endTime,mod);
		SegmentStationSequence sequence  = new SegmentStationSequence();
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId, stationId, startTime, endTime, mod);
		else array = sequence.findProcess(segmentId, stationId, startTime, endTime, mod);
		int length = array.size();
		
		//System.out.println(length);
		int accurrate=0;
		for(int  i =0 ;i< length-1; i++){
//			double[] res = new double[multiTrans.getStateSpace()];
//			double state_l =0;
//			double state_s =0;
//			for(int j = 0; j < array_line.size(); j++){
//				state_l+=array_line.get(j).get(i)*multiTrans.getPara_line().get(j);
//			}
//			for(int j = 0; j < array_station.size(); j++){
//				state_s+=array_station.get(j).get(i)*multiTrans.getPara_station().get(j);
//			}
//			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
//			//
//			double [] result=prediction(res,array.get(i-1),(int)state_l, (int)state_s,array.get(i));
//			
			double[][] result = new double[multiTrans.getClusterNum()][multiTrans.getStateSpace()];
//			result[0][array.get(i)/mode] = 1.0;
//			result[1][array_relate.get(0).get(i)/mode] = 1.0;
//			result[2][array_relate.get(1).get(i)/mode] = 1.0;
			for(int j=0;j<multiTrans.getClusterNum();j++){
				if(j==0)result[0][array.get(i)/mode>multiTrans.getStateSpace()-1?multiTrans.getStateSpace()-1:array.get(i)/mode]=1.0;
				else result[j][array_relate.get(j-1).get(i)/mode>multiTrans.getStateSpace()-1?multiTrans.getStateSpace()-1:array_relate.get(j-1).get(i)/mode]=1.0;
			}
			result=prediction(result);
			List<Integer> pre_topN= ArrayHelper.getTopN(result[0], topN);
			//DealVector.print(result, multiTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			if(pre_topN.contains(array.get(i+1)/mode)){
				accurrate+=1;
			}
		}
		
		
		return ((double)accurrate/(length-1));
	}
	public double accN(String startTime, String endTime,int topN){
		List<List<Integer>> array_relate = new ArrayList<List<Integer>>();
		
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		set_relate(array_relate, segmentId, stationId, startTime, endTime);

		//array = GetIcArray.getIC_int(segmentId, stationId, startTime, endTime,mod);
		SegmentStationSequence sequence  = new SegmentStationSequence();
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId, stationId, startTime, endTime, mod);
		else array = sequence.findProcess(segmentId, stationId, startTime, endTime, mod);
		int length = array.size();
		System.out.println(array.subList(order, length));
		List<Integer> preList = new ArrayList<Integer>();
		//System.out.println(length);
		int accurrate=0;
		double[][] result = new double[multiTrans.getClusterNum()][multiTrans.getStateSpace()];
		int stateSpace = multiTrans.getStateSpace();
		double preBias=0.0;
		for(int  i =0 ;i< length-order; i++){
		
			Matrix.reset(result, multiTrans.getClusterNum(),stateSpace);
			
			for (int k = 0; k < order; k++) {

				for (int j = 0; j < multiTrans.getClusterNum(); j++) {
					if (j == 0)
						result[0][array.get(i+k) / mode > stateSpace - 1 ? stateSpace - 1
								: array.get(i+k) / mode] += multiTrans.getParaList().get(order - 1
								- k);
					else
						result[j][array_relate.get(j-1).get(i+k) / mode > stateSpace - 1 ? stateSpace - 1
								: array_relate.get(j-1).get(i+k) / mode] +=  multiTrans.getParaList().get(order - 1
										- k);
				}
			}
			
			
			//Matrix.print(result, 3,stateSpace);
			//System.out.println();
			result=prediction(result);
			//Matrix.print(result, 3,stateSpace);
			List<Integer> pre_topN= ArrayHelper.getTopN(result[0], topN);
			//preList.add(ArrayHelper.getMinDisState(pre_topN, array.get(i+order)/mode)*mode);
			int state = ArrayHelper.getMinDisState(pre_topN, array.get(i+order)/mode)*mode;
			preList.add(state);
			preBias += array.get(i+order)>0?(double)Math.abs(array.get(i+order)-state)/array.get(i+order):
				(double)Math.abs(array.get(i+order)-state)/mode-1;
			
			if(ArrayHelper.isPredic(pre_topN, array.get(i+order)/mode, ArrayHelper.pre)){
				accurrate+=1;
			}
			
		}
		System.out.println(preList);
		System.out.println("preBias "+preBias/(length-order));
		System.out.println("accurate "+accurrate);
		
		return ((double)accurrate/(length-order));
	}
	public double acc_Zeigen(String time1, String time2,int topN){
		double [][][][] tensor = multiTrans.getTransition();
		double[][] z_eigen = getZ(tensor, multiTrans.getStateSpace());
		//DealVector.print(z_eigen, lineTrans.getStateSpace());
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<Integer> array=sequence.findProcess(segmentId,stationId, time1, time2, mod);
		int accurrate=0;
		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen[0], topN);
		for(int  i =0 ;i< array.size(); i++){
			if(topN_result.contains(Integer.valueOf(array.get(i)/mode))){
				accurrate+=1;
			}
		}
		
		return ((double)accurrate/(array.size()));
	}
	public double[][] getZ(double[][][][] tensor,int state){
		return TensorMultiPower_4order.power(tensor, multiTrans.getClusterNum(),multiTrans.getStateSpace());
	}
	
	public static void test(){
		String startTime = "06:30:00", endTime = "10:00:00";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 09:59:59";
//		MultiPreSS multiPreSS  = 
//				new MultiPreSS(35632502, "12111300000000045323", startTime, endTime,2,5, 30*60);
		
		
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<BasicDBObject> list=segSta.getSegStaFromAnaly();
		
	
		
		
		for(int i =0;i<100;i++){
			int seg =list.get(i).getInt("segmentId") ;
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData(seg, sta, startTime, endTime, 30*60)) continue;
			if(QueryBls.getSameStation(MongoDBAssis.getDb(), seg, sta).size() ==0) continue;
			MultiPreSS multiPreSS  = 
					new MultiPreSS(seg, sta, startTime, endTime,2,5, 30*60);
			System.out.println(multiPreSS.multiTrans.getClusterNum());
			GeneralPreSS generalPreSS =  new GeneralPreSS(seg, sta, startTime, endTime,2,5, 30*60);
			double multiAcc = multiPreSS.acc(time1, time2,2);
			double multi_zAcc = multiPreSS.acc_Zeigen(time1, time2, 2);
			double gene_zAcc = generalPreSS.acc_Zeigen(time1, time2, 2);
			double GeneAcc = generalPreSS.acc(time1, time2, 2);
			list.get(i).append("MultiAcc", multiAcc);
			list.get(i).append("multi_zAcc", multi_zAcc);
			list.get(i).append("GeneAcc", GeneAcc);
			list.get(i).append("Gene_zAcc", gene_zAcc);
			MongoDBAssis.getDb().getCollection("segStaMultiPre_mode5_mod30_ouji").insert(list.get(i));
			System.out.println(list.get(i));
		}
	}
	public static void testN(){
		String startTime = "06:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 18:59:59";
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		
		MultiPreSS multiPreSS  = 
				new MultiPreSS(35632502, "12111300000000045323", startTime, endTime,2,4, 15*60,3);
		
		System.out.println("para "+multiPreSS.multiTrans.getParaList());
		
		
		
		double acc = multiPreSS.accN(time1, time2,1);
		System.out.println("multi prediction :"+acc);
		
		System.out.println(multiPreSS.acc_Zeigen(time1, time2, 1));
		System.out.println("statespace size "+multiPreSS.multiTrans.getStateSpace());
		System.out.println("clusterNum  "+multiPreSS.multiTrans.getClusterNum());
		//System.out.println(linePreSS.lineTrans.getRelate_segment());
		
		//Matrix.print(multiPreSS.multiTrans.getTransition()[0][0],multiPreSS.multiTrans.getStateSpace() );
		//double [][]matrix = multiPreSS.multiTrans.getTransition()[0][0];
	}
	public static void testMulti_gene(){
		String startTime = "06:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 18:59:59";
//		MultiPreSS multiPreSS  = 
//				new MultiPreSS(35632502, "12111300000000045323", startTime, endTime,2,5, 30*60);
		
		
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<BasicDBObject> list=segSta.getSegStaFromAnaly();
		
	
		
		
		for(int i =0;i<1;i++){
			int seg =list.get(i).getInt("segmentId") ;
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData(seg, sta, startTime, endTime, 15*60)
					||!sequence.hasData(seg, sta, time1, time2, 15*60)) continue;
			if(QueryBls.getSameStation(MongoDBAssis.getDb(), seg, sta).size() ==0) continue;
			int mode =4;
			GeneralPreSS generalPreSS =  new GeneralPreSS( seg,sta, startTime, endTime,2,mode, 15*60);
			
			LinePreSS LinePreSS = new LinePreSS(seg,sta, startTime, endTime, 2, mode, 15*60);
			LinePreSS LinePreSS_3 = new LinePreSS(seg,sta, startTime, endTime, 2, mode, 15*60,3);
			MultiPreSS multiPreSS  = 
					new MultiPreSS(seg, sta, startTime, endTime,2,mode, 15*60,1);
			MultiPreSS multiPreSS_3  = 
					new MultiPreSS( seg,sta, startTime, endTime,2,mode, 15*60,3);
			
			System.out.println(multiPreSS.multiTrans.getClusterNum());
			int topN=1;
			double GeneAcc = generalPreSS.acc(time1, time2, topN);
			System.out.println("general "+GeneAcc);
			double staAcc = LinePreSS.acc(time1, time2, topN);
			System.out.println("staweight "+staAcc);
			double staAcc3order = LinePreSS_3.accN(time1, time2, topN);
			System.out.println("staweightStep "+staAcc3order);
			double multiAcc = multiPreSS.accN(time1, time2,topN);
			System.out.println("multi "+multiAcc);
			double multiAcc3order = multiPreSS_3.accN(time1, time2,topN);
			System.out.println("multiStep "+multiAcc3order);
			//MongoDBAssis.getDb().getCollection("segStaMultiPre_mode5_mod30_ouji").insert(list.get(i));
			//System.out.println(list.get(i));
		}
	}
	public static void main(String []args){
		int segmentId = 36371609;
		int sngSerialId = 4;
		testMulti_gene();
		
		
		//linePreSS.setMode(3);
//		generalPre.setMode(30);
//		System.out.println(generalPre.acc(time1, time2));
		//double[][][]matrix =linePreSS.multiTrans.getTransiton();
		//System.out.println("get matrix");
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println("prediction");
		//System.out.println(sequence.findProcess(36371609,"12111300000000045252", time1, time2, 20*60));
		
		
		//System.out.println(linePreSS.acc_Zeigen(time1, time2, 2));
		//System.out.println("para "+linePreSS.multiTrans.getPara());
//		System.out.println(multiPreSS.acc(time1, time2,2));
	}
}
