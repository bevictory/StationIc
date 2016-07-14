package segmentStationPre;

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
import segmentStationTransition.GeneralTransitionSS;
import segmentStationTransition.LineTransitionSS;
import transition.Transition;
import util.ArrayHelper;
import util.SegmentStation;
import util.SegmentStationSequence;

public class LinePreSS {
	private LineTransitionSS lineTrans ;
	private int segmentId;
	private String stationId;
	private String startTime;
	private int isDayModel=1;
	
	
	private String endTime;
	private int mod;
	private double[] result ;
	private int mode =1;
	private int order  =1;
	public void setMode(int mode) {
		this.mode = mode;
		this.lineTrans.setMode(mode);
	}
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public int isDayModel() {
		return isDayModel;
	}
	public void setDayModel(int isDayModel) {
		this.isDayModel = isDayModel;
	}
	public LinePreSS(){
		
		
	}
	public LinePreSS(int segmentId,String stationId, String startTime, String endTime,int isDayModel,int mode, int mod){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode = mode;
		//super(stationId, startTime, endTime,mod);
		lineTrans = new LineTransitionSS(segmentId, stationId, startTime, endTime,isDayModel,mode, mod);
		lineTrans.setDayModel(isDayModel);
		
	}
	public LinePreSS(int segmentId,String stationId, String startTime, String endTime,
			int isDayModel,int mode, int mod,int order){
		this.segmentId = segmentId;
		this.stationId = stationId;
		this.startTime = startTime;
		this.endTime = endTime;
		this.mod = mod;
		this.mode = mode;
		this.order = order;
		this.isDayModel = isDayModel;
		//super(stationId, startTime, endTime,mod);
		lineTrans = new LineTransitionSS(segmentId, stationId, startTime, endTime,isDayModel,mode, mod,order);
		
		
	}
	public double acc_Zeigen(String time1, String time2,int topN){
		double [][][] tensor = lineTrans.getTransiton();
		//Matrix.print(tensor[3], lineTrans.getStateSpace());
		double[] z_eigen = getZ(tensor, lineTrans.getStateSpace());
		DealVector.print(z_eigen, lineTrans.getStateSpace());
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<Integer> array=sequence.findProcess(segmentId,stationId, time1, time2, mod);
		int accurrate=0;
		List<Integer> topN_result=ArrayHelper.getTopN(z_eigen, topN);
		for(int  i =0 ;i< array.size(); i++){
//			if(topN_result.contains(Integer.valueOf(array.get(i)/mode))){
//				accurrate+=1;
//			}
			if(ArrayHelper.isPredic(topN_result, array.get(i)/mode, ArrayHelper.pre)){
				accurrate+=1;
			}
		}
		
		return ((double)accurrate/(array.size()));
	}
	public double[] getZ(double[][][] tensor,int state){
		return Tensor_3order_power.power(tensor, lineTrans.getStateSpace());
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
		int stateSpace = lineTrans.getStateSpace();
		double[] state = new double[stateSpace];
		double[] state_relate = new double[stateSpace];
		double[][][] tensor_3order = lineTrans.getTransiton();
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
	public void set_relate(List<List<Integer>>array_relate,int segmentId,String stationId, String startTime, String endTime){
		List<Integer> lineList=lineTrans.getRelate_segment();
		SegmentStationSequence sequence= new SegmentStationSequence();
		for(int i=0;i<lineList.size();i++){
			List<Integer> array;
			if(isDayModel==0) array= sequence.findBydayProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else array= sequence.findProcess(lineList.get(i),stationId, startTime, endTime, mod);
			array_relate.add(array);
		}
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
		//System.out.println(array);
		//System.out.println(length);
		System.out.println(array.subList(3, array.size()));
		List<Integer> preList = new ArrayList<Integer>();
		double preBias=0.0;
		int accurrate=0;
		for(int  i =2 ;i< length-1; i++){
			double[] res = new double[lineTrans.getStateSpace()];
			double state_r =0;
			for(int j = 0; j < array_relate.size(); j++){
				state_r+=array_relate.get(j).get(i)*lineTrans.getPara().get(j);
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			double [] result=prediction(res,(int)state_r, array.get(i));
			List<Integer> pre_topN= ArrayHelper.getTopN(result, topN);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
//			if(pre_topN.contains(array.get(i+1)/mode)){
//				accurrate+=1;
//			}
			int state = ArrayHelper.getMinDisState(pre_topN, array.get(i+1)/mode)*mode;
			preList.add(state);
			preBias += array.get(i+1)>0?(double)Math.abs(array.get(i+1)-state)/array.get(i+1):
				(double)Math.abs(array.get(i+1)-state)/mode-1;
			if(ArrayHelper.isPredic(pre_topN, array.get(i+1)/mode, ArrayHelper.pre)){
				accurrate+=1;
			}
		}
		System.out.println(preList);
		System.out.println("preBias "+preBias/(length-3));
		return ((double)accurrate/(length-3));
	}
	
	public double  accN(String startTime, String endTime,int topN){
		List<List<Integer>> array_relate = new ArrayList<List<Integer>>();
		List<Integer> array = new ArrayList<Integer>();
		List<Integer> pre = new ArrayList<Integer>();
		set_relate(array_relate, segmentId, stationId, startTime, endTime);

		//array = GetIcArray.getIC_int(segmentId, stationId, startTime, endTime,mod);
		SegmentStationSequence sequence  = new SegmentStationSequence();
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId, stationId, startTime, endTime, mod);
		else array = sequence.findProcess(segmentId, stationId, startTime, endTime, mod);
		int length = array.size();
		int stateSpace =lineTrans.getStateSpace();
		int accurrate =0;
		double preBias =0.0;
		System.out.println(array.subList(order, array.size()));
		List<Integer> preList = new ArrayList<Integer>();
		for(int  i =0 ;i< length-order; i++){
			double[] res = new double[stateSpace];
			List<List<Integer>> list_order = new ArrayList<List<Integer>>();
			
			for (int k = 0; k < order; k++) {
				double state_r = 0;
				for (int j = 0; j < array_relate.size(); j++) {
					state_r += array_relate.get(j).get(i + k) * lineTrans.getParaN()[order-1-k][j];
					
				}
				List<Integer> list = new ArrayList<Integer>();
				list.add((int)state_r);
				list.add(array.get(i+k));
				list_order.add(list);
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=predictionN(res,list_order,order);
			List<Integer> pre_topN= ArrayHelper.getTopN(result, topN);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			
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
		return ((double)accurrate/(length-order));
	}
	public double[] predictionN(double [] result_,List<List<Integer>> list,int order) {
		// TODO Auto-generated method stub
		int stateSpace = lineTrans.getStateSpace();
		double[] state = new double[stateSpace];
		double[] state_relate = new double[stateSpace];
		double [][]multi =new double[stateSpace][stateSpace];
		
//		for(int i=0;i<order;i++){
//			state_relate[] = para.get(order-1-i);
//		}
//		//System.out.println(state_);
//		
//		for(int i=0;i<order;i++){
//			state[list.get(i).get(1)/mode>stateSpace-1?stateSpace-1:list.get(i).get(1)/mode] = para.get(order-1-i);
//		}
		
//		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, stateSpace);
//		Matrix.transpose(matrix,stateSpace);
//		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		for(int i=0;i<order;i++){
			int row = list.get(i).get(0)/mode>stateSpace-1?stateSpace-1:list.get(i).get(0)/mode;
			int col = list.get(i).get(1)/mode>stateSpace-1?stateSpace-1:list.get(i).get(1)/mode;
			multi[row][col] += lineTrans.getPara().get(order-1-i);
		}
		result_ = Tensor_3order.orderMulti_two(lineTrans.getTransiton(), multi, stateSpace);
 		return result_;
	}
	
	public void test_allDay(){
		String startTime = "06:30:00", endTime = "10:00:00";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 09:59:59";
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<BasicDBObject> list=segSta.getSegStaFromAnaly();
		for(int i =0;i<100;i++){
			int seg =list.get(i).getInt("segmentId") ;
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData(seg, sta, startTime, endTime, 30*60)) continue;
			if(QueryBls.getSameStation(MongoDBAssis.getDb(), seg, sta).size() ==0) continue;
			LinePreSS linePreSS  = 
					new LinePreSS(seg, sta, startTime, endTime,2,5, 30*60);
			GeneralPreSS generalPreSS = new GeneralPreSS(seg, sta, startTime, endTime, 2, 5, 30*60);
			double LineAcc = linePreSS.acc(time1, time2,2);
			double line_zAcc = linePreSS.acc_Zeigen(time1, time2, 2);
			double gene_zAcc = generalPreSS.acc_Zeigen(time1, time2, 2);
			double GeneAcc = generalPreSS.acc(time1, time2, 2);
			list.get(i).append("LineAcc", LineAcc);
			list.get(i).append("Line_zAcc", line_zAcc);
			list.get(i).append("GeneAcc", GeneAcc);
			list.get(i).append("Gene_zAcc", gene_zAcc);
			//MongoDBAssis.getDb().getCollection("segStaPre_mode3_").insert(list.get(i));
			System.out.println(list.get(i));
		}
	}
	public void test_morn(){
		String startTime = "06:30:00", endTime = "20:00:00";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 19:59:59";
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<BasicDBObject> list=segSta.getSegStaFromAnaly();
		for(int i =0;i<100;i++){
			int seg =list.get(i).getInt("segmentId") ;
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData(seg, sta, startTime, endTime, 30*60)) continue;
			if(QueryBls.getSameStation(MongoDBAssis.getDb(), seg, sta).size() ==0) continue;
			LinePreSS linePreSS  = 
					new LinePreSS(seg, sta, startTime, endTime,2,5, 30*60);
			GeneralPreSS generalPreSS = new GeneralPreSS(seg, sta, startTime, endTime, 2, 5, 30*60);
			double LineAcc = linePreSS.acc(time1, time2,2);
			double line_zAcc = linePreSS.acc_Zeigen(time1, time2, 2);
			double gene_zAcc = generalPreSS.acc_Zeigen(time1, time2, 2);
			double GeneAcc = generalPreSS.acc(time1, time2, 2);
			list.get(i).append("LineAcc", LineAcc);
			list.get(i).append("Line_zAcc", line_zAcc);
			list.get(i).append("GeneAcc", GeneAcc);
			list.get(i).append("Gene_zAcc", gene_zAcc);
			//MongoDBAssis.getDb().getCollection("segStaPre_mode3_").insert(list.get(i));
			System.out.println(list.get(i));
		}
	}
	public static void  testN(){
		String startTime = "06:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 18:59:59";
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		LinePreSS linePreSS = new LinePreSS(35632502, "12111300000000045323", startTime, endTime,
				2, 4, 15*60,3);
		
		System.out.println("para "+linePreSS.lineTrans.getPara());
		double acc = linePreSS.accN(time1, time2,1);
		System.out.println(linePreSS.acc_Zeigen(time1, time2, 1));
		System.out.println("statespace size "+linePreSS.lineTrans.getStateSpace());
		System.out.println(linePreSS.lineTrans.getRelate_segment());
		System.out.println("line-associated prediction :"+acc);
	}
	public static void  test(){
		String startTime = "06:30:00", endTime = "18:59:59";
		String time1 =  "2015-12-11 06:30:00" ,time2 =  "2015-12-11 18:59:59";
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		LinePreSS linePreSS = new LinePreSS(35632502, "12111300000000045323", startTime, endTime,
				2, 4, 15*60);
		
		System.out.println("para "+linePreSS.lineTrans.getPara());
		double acc = linePreSS.acc(time1, time2,1);
		System.out.println(linePreSS.acc_Zeigen(time1, time2, 1	));
		System.out.println("statespace size "+linePreSS.lineTrans.getStateSpace());
		System.out.println(linePreSS.lineTrans.getRelate_segment());
		System.out.println("line-associated prediction :"+acc);
	}
	public static void testLine_gene(){
		String startTime = "07:00:00", endTime = "18:59:59";
		String time1 =  "2015-12-13 07:00:00" ,time2 =  "2015-12-13 18:59:59";
//		MultiPreSS multiPreSS  = 
//				new MultiPreSS(35632502, "12111300000000045323", startTime, endTime,2,5, 30*60);
		
		
		SegmentStation  segSta = new SegmentStation();
		SegmentStationSequence sequence = new SegmentStationSequence();
		List<BasicDBObject> list=segSta.getSegStaFromAnaly();
		
	
		
		
		for(int i =0;i<100;i++){
			int seg =list.get(i).getInt("segmentId") ;
			String sta =list.get(i).getString("stationId");
			if(!sequence.hasWorkdayData(seg, sta, startTime, endTime, 10*60)) continue;
			if(QueryBls.getSameStation(MongoDBAssis.getDb(), seg, sta).size() ==0) continue;
			LinePreSS	 linePreSS  = 
					new LinePreSS(seg, sta, startTime, endTime,2,5,30*60,3);
			System.out.println(linePreSS.lineTrans.getRelate_segment());
			GeneralPreSS generalPreSS =  new GeneralPreSS(seg, sta, startTime, endTime,2,5, 30*60);
			double lineAcc = linePreSS.acc(time1, time2,1);
			//double multi_zAcc = multiPreSS.acc_Zeigen(time1, time2, 2);
			//double gene_zAcc = generalPreSS.acc_Zeigen(time1, time2, 2);
			double GeneAcc = generalPreSS.acc(time1, time2, 1);
			System.out.println(list.get(i));
			System.out.println("line "+lineAcc+" gene "+GeneAcc);
			//list.get(i).append("MultiAcc", multiAcc);
			//list.get(i).append("multi_zAcc", multi_zAcc);
			//list.get(i).append("GeneAcc", GeneAcc);
			//list.get(i).append("Gene_zAcc", gene_zAcc);
			//MongoDBAssis.getDb().getCollection("segStaMultiPre_mode5_mod30_ouji").insert(list.get(i));
			
		}
	}
	
	public static void main(String []args){
		int segmentId = 36371609;
		int sngSerialId = 4;
		
		testN();
		test();
		//testLine_gene();
		
//		LinePreSS linePreSS  = 
//				new LinePreSS(35638505, "12111300000000045314", startTime, endTime,2,4, 30*60);
		
		
		
		//linePreSS.setMode(3);
//		generalPre.setMode(30);
//		System.out.println(generalPre.acc(time1, time2));
		//double[][][]matrix =linePreSS.lineTrans.getTransiton();
		//System.out.println("get matrix");
		//DealVector.print(generalPre.getZ(matrix, generalPre.generalTrans.getStateSpace()),generalPre.generalTrans.getStateSpace());
		//SegmentStationSequence sequence = new SegmentStationSequence();
		//System.out.println("prediction");
		//System.out.println(sequence.findProcess(36371609,"12111300000000045252", time1, time2, 20*60));
		
		
//		System.out.println(linePreSS.acc_Zeigen(time1, time2, 2));
//		System.out.println("para "+linePreSS.lineTrans.getPara());
//		double acc = linePreSS.acc(time1, time2,2);
//		System.out.println("statespace size "+linePreSS.lineTrans.getStateSpace());
//		System.out.println(linePreSS.lineTrans.getRelate_segment());
//		System.out.println("line-associated prediction :"+acc);
	}
}
