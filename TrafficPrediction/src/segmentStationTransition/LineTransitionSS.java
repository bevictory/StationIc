package segmentStationTransition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor.DiscardOldestPolicy;

import org.bson.types.MaxKey;

import decomposition.Matrix;
import decomposition.Tensor_3order;
import decomposition.Tensor_4order;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import segmentStationPre.LinePreSS;
import util.ArrayHelper;
import util.SegmentStationSequence;

public class LineTransitionSS {
	private double[][][] transTensor;
	private boolean isSetTrans = false;
	private List<List<Integer>> array_relate ;
	private List<Integer> relate_segment;
	private List<Integer> array;
	private List<Double> para;
	private double [][] paraN;private int order =1;
	private int stateSpace ;
	private int mode =1;
	private int mod;
	private int  isDayModel =0;
	
	private int minDis = Integer.MAX_VALUE;
	private List<Double> minDisPara=new ArrayList<Double>();
	private double[][][][] tensor;
	public int isDayModel() {
		return isDayModel;
	}
	public void setDayModel(int isDayModel) {
		this.isDayModel = isDayModel;
	}
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public List<Double> getPara() {
		return para;
	}
	
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}

	
	//private boolean isSetTrans = false;
	public LineTransitionSS(int segmentId, String stationId, String startTime, String endTime,int isDaymodel,int mode,int mod){
		this.mod = mod;
		this.mode = mode;
		this.isDayModel = isDaymodel;
		array_relate = new ArrayList<List<Integer>>();
		
		relate_segment = new ArrayList<Integer>();
		para = new ArrayList<Double>();
		SegmentStationSequence sequence= new SegmentStationSequence();
		if(isDayModel==0) array= sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1)array= sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array=sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		set_arrayRelate(segmentId, stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		transTensor = new double[stateSpace][stateSpace][stateSpace];	
		set_para(array);
		//setParaProcess();
	}
	
	public double[][][] getTransiton(){
		if(!isSetTrans){	
					
			if(order==1) toTransTensor(array);
			else toTransTensorN(array, order);
			//Tensor_3order.print(transTensor, stateSpace);
			//Matrix.print(transTensor[4], stateSpace);
			isSetTrans = true;
		}
		return transTensor;
	}
	
	public void toTransTensor(List<Integer> array) {
		// TODO Auto-generated method stub
		Tensor_3order.reset(transTensor, stateSpace, stateSpace, stateSpace);
		double[][] sum = new double[stateSpace][stateSpace];
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		for(int i = 0; i < length-1 ; i ++){
			double state =0 ;
			for(int j =0 ; j < array_relate.size(); j++){
				state+= array_relate.get(j).get(i)* para.get(j);
			}
			sum[(int)state/mode ][array.get(i)/mode] += 1;
			transTensor[(int)state/mode ][array.get(i)/mode][array.get(i+1)/mode] += 1;
			
		}
		
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
					{
						//System.out.println("trans "+transTensor[i][j][k]+" "+sum[i][j] +" "+i+" "+j+" "+k);
						transTensor[i][j][k] /= sum[i][j];
						
					}
					
					// else tranMatrix[i][i] =1;
					else {
						//transTensor[i][j][k] = 1.0 / ( stateSpace);
						transTensor[i][j][j] = 2.0;
					}
				}

			}
		}
	}
	
	public int getMaxState(int length){
		int max=length;
		for(int i=0;i<array_relate.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_relate.get(i)));
		}
		return max;
	}
	public void set_arrayRelate(int segmentId,String stationId, String startTime, String endTime){
		List<Integer> lineList=QueryBls.getSameStation(MongoDBAssis.getDb(), segmentId,stationId );
		SegmentStationSequence sequence= new SegmentStationSequence();
		//System.out.println("same station line "+lineList.size());
		//System.out.println(lineList.size());
		int length =lineList.size();//>4? 6:lineList.size();
		int num =0;
		for(int i=0;i<length;i++){
			if(isDayModel==1&&!sequence.hasData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			if(isDayModel==2&&!sequence.hasWorkdayData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			if(++num >4) break;
			List<Integer> array;
			relate_segment.add(lineList.get(i));
			if(isDayModel==0) array= sequence.findBydayProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(lineList.get(i), stationId, startTime, endTime, mod);
			array_relate.add(array);
		}
		//System.out.println("array_relate size "+array_relate.size());
		
		
	}
	public void set_para(List<Integer> array){
		int sum=0;
		List<Integer> dis= new ArrayList<Integer>();
		for(int i=0;i<array_relate.size();i++){
			int d = getDis(array, array_relate.get(i));
			dis.add(d);
			sum+=d;
		}
		//System.out.println("sum " +sum);
		for(int i=0;i<array_relate.size();i++){
			para.add((double)dis.get(i)/sum);
		}
		//System.out.println(para);
		
	}
	public void setParaProcess(){
		if(array_relate.size() ==0) return;
		System.out.println(array_relate.size() );
		double []par = new double [array_relate.size()];
		setPara(par, array_relate.size(), 0, 1);
		para.clear();
		para.addAll(minDisPara);
	}
	public int  getDis(){
		int  dis =0;
		int length = array.size();
		
		for(int  i =0 ;i< length-1; i++){
			double[] res = new double[getStateSpace()];
			double state_r =0;
			for(int j = 0; j < array_relate.size(); j++){
				state_r+=array_relate.get(j).get(i)*para.get(j);
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=prediction(res,(int)state_r, array.get(i));
			List<Integer> pre_topN= ArrayHelper.getTopN(result, 1);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			dis += ArrayHelper.getMinDis(pre_topN, array.get(i+1)/mode);
		}
		return dis ;
	}
	public double[] prediction(double [] result_,int state_r, int state_) {
		// TODO Auto-generated method stub
		int stateSpace = getStateSpace();
		double[] state = new double[stateSpace];
		double[] state_relate = new double[stateSpace];
		double[][][] tensor_3order = getTransiton();
		state_relate[state_r/mode>stateSpace-1?stateSpace-1:state_r/mode] = 1.0;
		//System.out.println(state_);
		
		state[state_/mode>stateSpace-1?stateSpace-1:state_/mode]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, stateSpace);
		Matrix.transpose(matrix,stateSpace);
		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		return result_;
	}
	public  void setPara(double []par,int n,int loc, double sum){
		if(loc ==n-1) {
			par[loc] = sum;
			para.clear();
			for(int i=0;i<n;i++)
				para.add(par[i]);
			//System.out.println(para);
			getTransiton();
			int dis = getDis();
			isSetTrans=false;
			//System.out.println(dis);
			if(minDis> dis){
				minDis = dis; 
				minDisPara.clear();				
				minDisPara.addAll(para);
				//System.out.println(minDisPara);
			}
			return ;
		}
		for(double p=0.0;sum-p >10e-5; p+=0.01){
			par[loc]=p;
			setPara(par,n, loc+1, sum- p,order);
		}
	}
	public List<Integer> getRelate_segment() {
		return relate_segment;
	}
	public int  getDis(List<Integer> array1, List<Integer> array2){
		int dis =0; 
		//System.out.println(array1.size()+" "+array2.size());
		for(int i=0 ;i <array1.size();i++){
			 //dis += Math.abs(Math.pow(array1.get(i)- array2.get(i),2));
			 dis += Math.abs(array1.get(i)- array2.get(i));
		}
		//dis= (int) Math.sqrt(dis);
		return dis;
	}
	
	//多步转移张量构建过程
	public LineTransitionSS(int segmentId, String stationId, String startTime, String endTime,
			int isDaymodel,int mode,int mod,int order){
		this.mod = mod;
		this.mode = mode;
		this.order = order;
		this.isDayModel = isDaymodel;
		array_relate = new ArrayList<List<Integer>>();
		
		relate_segment = new ArrayList<Integer>();
		para = new ArrayList<Double>();
		SegmentStationSequence sequence= new SegmentStationSequence();
		if(isDayModel==0) array= sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1)array= sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array=sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		set_arrayRelate(segmentId, stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		transTensor = new double[stateSpace][stateSpace][stateSpace];
		tensor = new double [order][stateSpace][stateSpace][stateSpace];
		paraN = new double[order][array_relate.size()];
		System.out.println( "order "+order);
		setParaProcessN(order);
	}
	public double[][][] getTransiton(int order){
		if(!isSetTrans){	
						
			toTransTensor(array,order);
			//Tensor_3order.print(transTensor, stateSpace);
			//Matrix.print(transTensor[4], stateSpace);
			isSetTrans = true;
		}
		return transTensor;
	}
	public void toTransTensor(List<Integer> array,int order) {
		// TODO Auto-generated method stub
		Tensor_3order.reset(transTensor, stateSpace, stateSpace, stateSpace);
		double[][] sum = new double[stateSpace][stateSpace];
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		for(int i = 0; i < length-order ; i ++){
			double state =0 ;
			for(int j =0 ; j < array_relate.size(); j++){
				state+= array_relate.get(j).get(i)* para.get(j);
			}
			sum[(int)state/mode ][array.get(i)/mode] += 1;
			transTensor[(int)state/mode ][array.get(i)/mode][array.get(i+order)/mode] += 1;
			
		}
		
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
					{
						//System.out.println("trans "+transTensor[i][j][k]+" "+sum[i][j] +" "+i+" "+j+" "+k);
						transTensor[i][j][k] /= sum[i][j];
						
					}
					
					// else tranMatrix[i][i] =1;
					else {
						//transTensor[i][j][k] = 1.0 / ( stateSpace);
						transTensor[i][j][j] = 2.0;
					}
				}

			}
		}
	}
	public void setParaProcess(int order){
		if(array_relate.size() ==0) return;
		System.out.println(array_relate.size() );
		minDis = Integer.MAX_VALUE;
		setPara(paraN[order-1], array_relate.size(), 0, 1,order);
		for(int i=0 ;i< array_relate.size();i++){
			paraN[order-1][i] = (double)minDisPara.get(i);
		}
	}
	public int  getDis(int order){
		int  dis =0;
		int length = array.size();
		
		for(int  i =0 ;i< length-order; i++){
			double[] res = new double[getStateSpace()];
			double state_r =0;
			for(int j = 0; j < array_relate.size(); j++){
				state_r+=array_relate.get(j).get(i)*paraN[order-1][j];
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=prediction(res,(int)state_r, array.get(i));
			List<Integer> pre_topN= ArrayHelper.getTopN(result, 1);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			dis += ArrayHelper.getMinDis(pre_topN, array.get(i+order)/mode);
		}
		return dis ;
	}
	public double[] prediction(double [] result_,int state_r, int state_,int order) {
		// TODO Auto-generated method stub
		int stateSpace = getStateSpace();
		double[] state = new double[stateSpace];
		double[] state_relate = new double[stateSpace];
		double[][][] tensor_3order = getTransiton(order);
		state_relate[state_r/mode>stateSpace-1?stateSpace-1:state_r/mode] = 1.0;
		//System.out.println(state_);
		
		state[state_/mode>stateSpace-1?stateSpace-1:state_/mode]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, stateSpace);
		Matrix.transpose(matrix,stateSpace);
		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		return result_;
	}
	public  void setPara(double []par,int n,int loc, double sum,int order){
		if(loc ==n-1) {
			par[loc] = sum;
			para.clear();
			for(int i=0;i<n;i++)
				para.add(par[i]);
			//System.out.println(para);
			getTransiton(order);
			int dis = getDis(order);
			isSetTrans=false;
			//System.out.println(dis);
			if(minDis> dis){
				minDis = dis; 
				minDisPara.clear();				
				minDisPara.addAll(para);
				//System.out.println(minDisPara);
			}
			return ;
		}
		for(double p=0.0;sum-p >10e-5; p+=0.01){
			par[loc]=p;
			setPara(par,n, loc+1, sum- p);
		}
	}
	
	
	public void toTransTensorN(List<Integer> array,int order) {
		// TODO Auto-generated method stub
		
		Tensor_4order.reset(tensor, order, stateSpace);
		Tensor_3order.reset(transTensor, stateSpace, stateSpace, stateSpace);
		
		while(order>=1){
			
			//设置参数的过程
			double[][] sum = new double[stateSpace][stateSpace];
			
			int length = array.size();
			
			for(int i = 0; i < length-order ; i ++){
				double state =0 ;
				for(int j =0 ; j < array_relate.size(); j++){
					state+= array_relate.get(j).get(i)* paraN[order-1][j];
				}
				sum[(int)state/mode ][array.get(i)/mode] += 1;
				tensor[order-1][(int)state/mode ][array.get(i)/mode][array.get(i+order)/mode] += 1;
				
			}
			
			for (int i = 0; i < stateSpace; i++) {
				for (int j = 0; j < stateSpace; j++) {
					for (int k = 0; k < stateSpace; k++) {
						if (sum[i][j] > 0)
						{
							//System.out.println("trans "+transTensor[i][j][k]+" "+sum[i][j] +" "+i+" "+j+" "+k);
							tensor[order-1][i][j][k] /= sum[i][j];
							
						}
						
						// else tranMatrix[i][i] =1;
						else {
							//transTensor[i][j][k] = 1.0 / ( stateSpace);
							tensor[order-1][i][j][j] = 2.0;
						}
					}

				}
			}
			Tensor_3order.add(transTensor, tensor[order-1], stateSpace, para.get(order-1));
			
			order--;
		}
		
		
	}
	public void setParaProcessN(int order){
		if(array_relate.size() ==0) return;
		
		for(int i=1 ;i<= order;i++){
			setParaProcess(order);
		}
		minDis = Integer.MAX_VALUE;
		double []par = new double [order];
		System.out.println("setParaProcessN "+order);
		setParaN(par, order, 0, 1,order);
		para.clear();
		
		para.addAll(minDisPara);
		System.out.println("para size "+para.size());
	}
	public int  getDisN(int order){
		int  dis =0;
		int length = array.size();
		
		for(int  i =0 ;i< length-order; i++){
			double[] res = new double[getStateSpace()];
			List<List<Integer>> list_order = new ArrayList<List<Integer>>();
			
			for (int k = 0; k < order; k++) {
				double state_r = 0;
				for (int j = 0; j < array_relate.size(); j++) {
					state_r += array_relate.get(j).get(i + k) * paraN[order-1-k][j];
					
				}
				List<Integer> list = new ArrayList<Integer>();
				list.add((int)state_r);
				list.add(array.get(i+k));
				list_order.add(list);
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=predictionN(res,list_order,order);
			List<Integer> pre_topN= ArrayHelper.getTopN(result, 1);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			dis += ArrayHelper.getMinDis(pre_topN, array.get(i+order)/mode);
		}
		return dis ;
	}
	public double[] predictionN(double [] result_,List<List<Integer>> list,int order) {
		// TODO Auto-generated method stub
		int stateSpace = getStateSpace();
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
			multi[row][col] += para.get(order-1-i);
		}
		result_ = Tensor_3order.orderMulti_two(transTensor, multi, stateSpace);
 		return result_;
	}
	public double[][] getParaN() {
		return paraN;
	}
	public  void setParaN(double []par,int n,int loc, double sum,int order){
		if(loc ==n-1) {
			System.out.println("setParaN "+n);
			par[loc] = sum;
			para.clear();
			for(int i=0;i<n;i++)
				para.add(par[i]);
			//System.out.println(para);
			//获得转移张量
			getTransiton();
			int dis = getDisN(order);
			isSetTrans=false;
			//System.out.println(dis);
			if(minDis> dis){
				minDis = dis; 
				minDisPara.clear();				
				minDisPara.addAll(para);
				//System.out.println(minDisPara);
			}
			return ;
		}
		for(double p=0.0;sum-p >10e-5; p+=0.01){
			par[loc]=p;
			setParaN(par,n, loc+1, sum- p,order);
		}
	}
	
	
	public static void main(String []args){
		String startTime = "06:30:00", endTime = "10:00:00";
		LineTransitionSS lineTransitionSS = new 
				LineTransitionSS(35632502 , "12111300000000045323", startTime, endTime,2,4, 30*60);
		lineTransitionSS.setParaProcess();
		System.out.println(lineTransitionSS.minDisPara);
		System.out.println();
		System.out.println("加权线路选择为：");
		for(int i =0;i<lineTransitionSS.relate_segment.size();i++)
			System.out.println(QueryBls.getLineName(MongoDBAssis.getDb(),lineTransitionSS.getRelate_segment().get(i)));
	}
}
