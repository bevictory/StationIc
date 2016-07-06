package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;

import util.ArrayHelper;
import util.SegmentStationSequence;
import util.StationSequence;
import decomposition.Matrix;
import decomposition.Tensor_3order;

public class GeneralTransitionSS {
	private int stateSpace ;
	private double[] initState;
	private double[][] transition;
	private double [][][] tensor;
	private int mode =1;
	private int isDayModel =1;
	private int order = 1;
	private List<Integer> array;
	private List<Double> para;
	private int minDis = Integer.MAX_VALUE;
	private List<Double> minDisPara=new ArrayList<Double>();
	private boolean isSetTrans = false;
	
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public int isDayModel() {
		return isDayModel;
	}
	public void setDayModel(int isDayModel) {
		this.isDayModel = isDayModel;
	}
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}
    //普通马尔科夫链的一步预测
    
	public GeneralTransitionSS(int segmentId, String stationId,String startTime, String endTime,int isDayModel,int mode ,int mod)
	
	{
		this.mode =mode;
		this.isDayModel = isDayModel;
		SegmentStationSequence sequence= new SegmentStationSequence();
		
		
		if(isDayModel==0)array=sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		setStateSpace(ArrayHelper.getMax(array)/mode+1);
		initState = ArrayHelper.getInitState(array);
		
		transition = new double[stateSpace][stateSpace];
	}
	
	
	
	/**
	 * 获得指定线路站点在指定时间段内的转移张量,已转置
	 * @return
	 */
	public double[][] getTransiton(){
		if(!isSetTrans){
			
			if(order==1) toTransTensor(array);
			else toTransTensor(array, order);
			//System.out.println("statespace " +stateSpace);
			Matrix.transpose(transition, stateSpace);
			//Matrix.print(transition, stateSpace);
			isSetTrans = true;
		}
		return transition;
	}
	public double[] getInitState() {
		return initState;
	}
	public void setInitState(double[] initState) {
		this.initState = initState;
	}
	public void setSetTrans(boolean isSetTrans) {
		this.isSetTrans = isSetTrans;
	}
	
	public void toTransTensor(List<Integer> array){
		 
		
		 double[] sum = new double[stateSpace];
		 //System.out.println(array.size()+" " +stateSpace);
		for(int i=0;i<array.size()-1;i++){
			sum[array.get(i)/mode]+=1;
			transition[array.get(i)/mode][array.get(i+1)/mode]+=1;
//			if(array.get(i)/mode>=stateSpace&&array.get(i+1)/mode>=stateSpace) {
//				
//				sum[stateSpace-1] += 1;
//				transition[stateSpace-1][stateSpace-1] +=1;
//			}else if(array.get(i)/mode>=stateSpace){
//				sum[stateSpace-1] += 1;
//				transition[stateSpace-1][array.get(i+1)] +=1;
//			}else if(array.get(i+1)/mode>=stateSpace){
//				sum[array.get(i)/mode] += 1;
//				transition[array.get(i)/mode][stateSpace-1] +=1;
//			}else {
//				sum[array.get(i)/mode] += 1;
//				transition[array.get(i)/mode][array.get(i+1)/mode] +=1;
//			}
			
		}
		for(int i=0;i<stateSpace;i++){
			for(int j=0;j<stateSpace;j++){
				if(sum[i] > 0)transition[i][j] /= sum[i]; 
				//else transition[i][i] =1;
				else {					
						//transition[i][i] =1.0/stateSpace;	
					transition[i][j] =1.0/(stateSpace);	
				}
			}
		}
		
	}
	
	//多步转移张量的构建
	
	
	public GeneralTransitionSS(int segmentId, String stationId,String startTime, String endTime,
			int isDayModel,int mode ,int mod,int order)
		
		{
			this.mode =mode;
			this.isDayModel = isDayModel;
			this.order = order;
			SegmentStationSequence sequence= new SegmentStationSequence();
			
			
			if(isDayModel==0)array=sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
			else if(isDayModel==1) array = sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
			else array = sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
			setStateSpace(ArrayHelper.getMax(array)/mode+1);
			initState = ArrayHelper.getInitState(array);
			para = new ArrayList<Double>();
			
			transition = new double[stateSpace][stateSpace];
			tensor = new double[order][stateSpace][stateSpace];
			setParaProcess();
//			para.add(0.50);
//			para.add(0.0);
//			para.add(0.5);
		}
	
	
	public void toTransTensor(List<Integer> array,int order){
		 Matrix.reset(transition, stateSpace);
		Tensor_3order.reset(tensor, order, stateSpace, stateSpace);
		while(order>=1){
			double[] sum = new double[stateSpace];
			int length = array.size();
			for(int i=0 ;i < length -order;i++){
				sum[array.get(i)/mode]+=1;
				tensor[order-1][array.get(i)/mode][array.get(i+order)/mode]+=1;
			}
			for(int i=0;i<stateSpace;i++){
				for(int j=0;j<stateSpace;j++){
					if(sum[i] > 0)tensor[order-1][i][j] /= sum[i]; 
					//else transition[i][i] =1;
					else {					
							//transition[i][i] =1.0/stateSpace;	
						tensor[order-1][i][j] =1.0/(stateSpace);	
					}
				}
			}
			Matrix.add(transition, tensor[order-1], stateSpace,para.get(order-1));
			order --;
		}				
	}
	/**
	 * 多步转移张量的参数构建过程
	 */
	public void setParaProcess(){
		
		
		double []par = new double [order];
		setPara(par, order, 0, 1);
		para.clear();
		para.addAll(minDisPara);
	}
	public  void setPara(double []par,int n,int loc, double sum){
		if(loc ==n-1) {
			par[loc] = sum;
			para.clear();
			for(int i=0;i<n;i++)
				para.add(par[i]);
			
			
			//获得转移张量
			getTransiton();
			
			int dis = getDis();
			isSetTrans=false;
			System.out.println(para);
			System.out.println("dis" +dis);
			System.out.println("mindis "+minDis);
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
	public int  getDis(){
		int  dis =0;
		int length = array.size();
		
		for(int  i =0 ;i< length-order; i++){
			double[] res = new double[getStateSpace()];
			List<Integer> list_order  = new ArrayList<Integer>();
			for(int j = 0; j < order; j++){
				list_order.add(array.get(i+j));
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=prediction(res,list_order);
			List<Integer> pre_topN= ArrayHelper.getTopN(result,2);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			dis += ArrayHelper.getMinDis(pre_topN, array.get(i+order)/mode);
		}
		return dis ;
	}
	public List<Double> getPara() {
		return para;
	}
	public double[] prediction(double [] result_,List<Integer> list) {
		// TODO Auto-generated method stub
		int stateSpace = getStateSpace();
		double[] state = new double[stateSpace];		
		double[][] matrix = getTransiton();
		for(int i=0;i<order;i++){
			state[list.get(i)/mode>stateSpace-1?stateSpace-1:list.get(i)/mode] += para.get(order-1-i);
		}
		//System.out.println(state_);
		
		//Matrix.transpose(matrix,stateSpace);
		result_ = Matrix.multip_vector(matrix, state, stateSpace);
		return result_;
	}
	
}
