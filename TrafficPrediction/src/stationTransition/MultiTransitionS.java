package stationTransition;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;


import transition.MultiTransition;

import util.ArrayHelper;

import util.PredictDis;
import util.StateSet;
import util.Station;
import util.StationSequence;

import util.StationInfo;

import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.Tensor_3order;
import decomposition.Tensor_4order;

public class MultiTransitionS {
	private List<List<Integer>> array_cluster = new ArrayList<List<Integer>>();
	private double[][] para = null;
	private double[][][]paraN =null;
	private List<Double> paraList;
	private double[][][][] transition=null;
	private boolean isSetTrans = false;
	private List<String> clusterList;
	public List<String> getClusterList() {
		return clusterList;
	}
	private int mode =1;
	private int stateSpace ;
	private int isDayModel;
	private int mod;
	private List<Integer> array;
	private int order=1;
	private double[][][] tensor;
	private double[][] transMatrix;
	private int minDis=Integer.MAX_VALUE;
	private List<Double> minDisPara ;
	private int setParaTop=1;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getClusterNum(){
		return array_cluster.size()+1;
	}
	public MultiTransitionS( String stationId, String startTime, String endTime,int isDayModel,int mode,int mod) {
		// TODO Auto-generated constructor stub
		this.isDayModel  = isDayModel;
		this.mod = mod;
		this.mode = mode;
		StationSequence sequence= new StationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess(stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess( stationId, startTime, endTime, mod);
		
		
		
		clusterList = new ArrayList<String>();
		getCluster( stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		PredictDis.dealSequence(array, stateSpace);
		for(int i=0;i<array_cluster.size();i++){
			PredictDis.dealSequence(array_cluster.get(i), stateSpace);
		}
		
		
		transition  = new double[array_cluster.size()+1][array_cluster.size()+1][stateSpace][stateSpace];
		setPara();
		getTransition();
		
	}
	public int getMaxState(int length){
		int max=length;
		for(int i=0;i<array_cluster.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_cluster.get(i)));
		}
		
		return max;
	}
	public void setPara(){
		para = new double[array_cluster.size()+1][array_cluster.size()+1];

		for(int i=0;i<array_cluster.size()+1;i++){
			double sum =0;
			List<Double> dis= new ArrayList<Double>();
			for(int j=0;j<array_cluster.size()+1;j++){
				List<Integer> array1=null,array2=null;
				if(i!=0){
					array1=array_cluster.get(i-1);
					
				}if(j!=0) {
					array2 = array_cluster.get(j-1);
				}
				if(i ==0) array1= array;
				if(j==0) array2=array;
				double d= getDis(array1, array2);
				dis .add(d);
				sum+=d;
			}
			for(int j=0;j<array_cluster.size()+1;j++){
				if(j==i){
					para[i][j]=0.5; 
				}else {
					para[i][j]=dis.get(j)/sum*0.5; 
				}
			}
		}
		
		
	}
	
	public int  getDis(List<Integer> array1, List<Integer> array2){
		int dis =0; 
		//System.out.println(array1.size()+" "+array2.size());
		for(int i=0 ;i <array1.size();i++){
			 dis += Math.abs(Math.pow(array1.get(i)- array2.get(i),2));
			// dis += Math.abs((array1.get(i)- array2.get(i)));
		}
		dis= (int) Math.sqrt(dis);
		return dis;
	}
	public void getCluster( String stationId, String startTime, String endTime){
		List<BasicDBObject> nearStationList = StationInfo.getNear(stationId, 1, 200);
		StationSequence sequence = new StationSequence();
		int num =0;
		for(int i=0;i<nearStationList.size();i++){
			if(Station.getStationIcSum(nearStationList.get(i).getString("stationId"))<1000||isDayModel==1&&!sequence.hasData(nearStationList.get(i).getString("stationId"), startTime, endTime, mod)) 
				continue;
			if(Station.getStationIcSum(nearStationList.get(i).getString("stationId"))<1000||isDayModel==2&&!sequence.hasWorkdayData(nearStationList.get(i).getString("stationId"), startTime, endTime, mod)) 
				continue;
			
			List<Integer> array;
			clusterList.add(nearStationList.get(i).getString("stationId"));
			
			if(isDayModel==0) array= sequence.findBydayProcess(nearStationList.get(i).getString("stationId"), startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(nearStationList.get(i).getString("stationId"), startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(nearStationList.get(i).getString("stationId"),  startTime, endTime, mod);
			//System.out.println(array);
			array_cluster.add(array);
			num++;
			if(num>=2) break;
		}
	}
	
	
	public double[][][][] getTransition(){
		if(!isSetTrans){
			//transition = new double[stateSpace][stateSpace][stateSpace][stateSpace];
			
			if (order<1)toTransTensor(array);
			else  toTransTensorN(array, order);
			isSetTrans = true;
		}
		
		return transition;
	}

	public void toTransTensor(List<Integer> array) {
		// TODO Auto-generated method stub
		transition[0][0] = toMatrix(array, array,para[0][0]);
		for(int i=0 ;i < array_cluster.size();i++){
			transition[0][i+1] = toMatrix(array, array_cluster.get(i),para[0][i+1]);
			transition[i+1][0] = toMatrix(array_cluster.get(i), array,para[i+1][0] );
		}
		for( int i =0 ;i < array_cluster.size();i++){
			for(int j = 0 ;j < array_cluster.size(); j++){
				transition[i+1][j+1] = toMatrix(array_cluster.get(i),array_cluster.get(j),para[i+1][j+1]);
				//Matrix.print(transition[i+1][j+1], Transition.getStateSpace());
				
			}
		}
//		Matrix.print(transition[0][0], Transition.getStateSpace());
//		System.out.println();
//		Matrix.print(transition[1][0], Transition.getStateSpace());
//		System.out.println();
//		Matrix.print(transition[2][0], Transition.getStateSpace());
		
	}
	public double[][] toMatrix(List<Integer> array1, List<Integer> array2,double para){
		double[][] matrix  = new double [stateSpace][stateSpace];
		int length = array1.size()> array2.size()? array2.size():array1.size();
		double sum[] = new double[stateSpace];
		for(int i = 0; i < length-1 ; i ++){
			sum[array1.get(i)/mode]+=1;
			matrix[array1.get(i)/mode][array2.get(i+1)/mode] +=1;
		}
		for(int i = 0; i < stateSpace ; i ++){
			for(int j = 0 ;j< stateSpace; j++){
				if(sum[i] > 0){
					matrix[i][j] /= sum[i];
					matrix[i][j]*= para;
				}
				else{
					matrix[i][j]= para/stateSpace;
				}
			
			}
			
		}
		return matrix;
	}
	
	
	//多步转移张量建立
	
	public MultiTransitionS(String stationId, String startTime, String endTime,
			int isDayModel,int mode,int mod,int order) {
		// TODO Auto-generated constructor stub
		this.isDayModel  = isDayModel;
		this.mod = mod;
		this.mode = mode;
		this.order = order;
		StationSequence sequence= new StationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess(stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess( stationId, startTime, endTime, mod);
		
		
		
		clusterList = new ArrayList<String>();
		getCluster( stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		PredictDis.dealSequence(array, stateSpace);
		for(int i=0;i<array_cluster.size();i++){
			PredictDis.dealSequence(array_cluster.get(i), stateSpace);
		}
		
		
		transition  = new double[array_cluster.size()+1][array_cluster.size()+1][stateSpace][stateSpace];
		paraN = new double[order][getClusterNum()][getClusterNum()];
		paraList = new ArrayList<Double>();
		minDisPara = new ArrayList<Double>();
		setParaProcess(order);
		setParaProcessN(order);
//		paraList.clear();
//		paraList.add(0.72);
//		paraList.add(0.2800);
//		paraList.add(0.00);
		getTransition();
		
	}
	public void toTransTensorN(List<Integer> array,int order) {
		Tensor_4order.reset_array(transition, array_cluster.size()+1, stateSpace);
		while (order >= 1) {
			
			 Matrix.add(transition[0][0], toMatrix(array, array,
						paraN[order - 1][0][0],order), stateSpace, paraList.get(order-1));
			// System.out.println(paraN[order - 1][0][0]);
			// Matrix.print(transition[0][0], stateSpace);
			// System.out.println("transiton[0][0] "+order);
			for (int i = 0; i < array_cluster.size(); i++) {
//				transition[0][i + 1] = toMatrix(array, array_cluster.get(i),
//						paraN[order - 1][0][i + 1]);
//				transition[i + 1][0] = toMatrix(array_cluster.get(i), array,
//						paraN[order - 1][i + 1][0],order);
				
				 Matrix.add(transition[0][i + 1] , toMatrix( array,array_cluster.get(i),
					paraN[order - 1][i + 1][0],order), 
						 stateSpace, paraList.get(order-1));
				 Matrix.add(transition[i + 1][0], toMatrix(array_cluster.get(i), array,
							paraN[order - 1][0][i + 1],order), 
								 stateSpace, paraList.get(order-1));
			}
			for (int i = 0; i < array_cluster.size(); i++) {
				for (int j = 0; j < array_cluster.size(); j++) {
//					transition[i + 1][j + 1] = toMatrix(array_cluster.get(i),
//							array_cluster.get(j),
//							paraN[order - 1][i + 1][j + 1],order);
					
					Matrix.add(transition[i + 1][j+1], toMatrix(array_cluster.get(i),
						array_cluster.get(j),paraN[order - 1][j + 1][i + 1],order),
								 stateSpace, paraList.get(order-1));
					
					// Matrix.print(transition[i+1][j+1],
					// Transition.getStateSpace());

				}
			}
			
			
			order--;
		}
		//Matrix.print(transition[0][0], stateSpace);
		
	}
	
	public double[][] getTransition(int Sequenceloc, int order){
		if(!isSetTrans){
			if(Sequenceloc==0) toTransTensor(array,Sequenceloc,order);
			else {
				toTransTensor(array_cluster.get(Sequenceloc-1),Sequenceloc,order);
			}
			isSetTrans = true;
		} 
		return transMatrix;
	}
	//paraN[order][loc][i] 指示i 到loc的参数
	public void toTransTensor(List<Integer> array,int loc,int order) {
		// TODO Auto-generated method stub
		
		Tensor_3order.reset(tensor, array_cluster.size()+1, stateSpace, stateSpace);
		tensor[0] = toMatrix(this.array,array ,paraList.get(0), order);
		
		//Matrix.add(transMatrix, tensor[loc], stateSpace, paraN[order-1][loc][0]);
		for(int i=0;i<array_cluster.size();i++){
			tensor[i+1] = toMatrix(array_cluster.get(i),array, paraList.get(i+1),order);
			//Matrix.add(transMatrix, tensor[i+1], stateSpace, paraN[order-1][loc][i+1]);
		}								
		
	}
	public void setParaProcessN(int order){
		minDis = Integer.MAX_VALUE;
		double []par = new double [order];
//		System.out.println("setParaProcessN "+order);
		setParaN(par, order, 0, 1,order);
		paraList.clear();
		paraList.addAll(minDisPara);
	}
	public  void setParaN(double []par,int n,int loc, double sum,int order){
		if(loc ==n-1) {
//			System.out.println("setParaN "+n);
			par[loc] = sum;
			paraList.clear();
			for(int i=0;i<n;i++)
				paraList.add(par[i]);
			
			//System.out.println(para);
			//获得转移张量
			getTransition();
			int dis = getDisN(order);
			isSetTrans=false;
//			System.out.println("paraList "+paraList);
//			System.out.println("dis "+dis);
//			System.out.println("minDis "+minDis);
			if(minDis> dis){
				minDis = dis; 
				minDisPara.clear();				
				minDisPara.addAll(paraList);
				//System.out.println(minDisPara);
			}
			return ;
		}
		for(double p=0.0;sum-p >10e-5; p+=0.01){
			par[loc]=p;
			setParaN(par,n, loc+1, sum- p,order);
		}
	}
	
	public double [][] prediction(double [][]result){
		result = Tensor_4order.multip_2order_formulti(transition,
				result, getClusterNum(),stateSpace);
		return result;
		
	}
	public int getDisN(int order){
		
		
		int length = array.size();
		
		//System.out.println(length);
		int dis=0;
		double[][] result = new double[getClusterNum()][stateSpace];
		for(int  i =0 ;i< length-order; i++){

//			
			Matrix.reset(result, getClusterNum(), stateSpace);
			for (int k = 0; k < order; k++) {

				for (int j = 0; j < getClusterNum(); j++) {
					if (j == 0)
//						result[0][array.get(i+k) / mode > stateSpace - 1 ? stateSpace - 1
//								: array.get(i+k) / mode] += paraList.get(order - 1
//								- k);
						StateSet.setState(result[0], stateSpace, array.get(i+k) / mode > stateSpace - 1 ? stateSpace - 1
								: array.get(i+k) / mode, paraList.get(order - 1- k));
					else
						StateSet.setState(result[j], stateSpace, array_cluster.get(j-1).get(i+k) / mode > stateSpace - 1 ? stateSpace - 1
								: array_cluster.get(j-1).get(i+k) / mode, paraList.get(order - 1- k));
//						result[j][array_cluster.get(j-1).get(i+k) / mode > stateSpace - 1 ? stateSpace - 1
//								: array_cluster.get(j-1).get(i+k) / mode] +=  paraList.get(order - 1
//										- k);
				}
			}
			result=prediction(result);
			List<Integer> pre_topN= ArrayHelper.getTopN(result[0], setParaTop);
			//DealVector.print(result, multiTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			dis+= ArrayHelper.getMinDis(pre_topN, array.get(i+order)/mode);
//			if(ArrayHelper.isPredic(pre_topN, array.get(i+order)/mode, ArrayHelper.pre)){
//				dis-=1;
//			}
		}
		
		
		return dis;
	}
	
	public List<Double> getParaList() {
		return paraList;
	}
	public void setParaProcess(int order){
		for(int i=1;i<=order;i++){
			//for(int j= 0; j < array_cluster.size() +1;i++){
				minDis = Integer.MAX_VALUE;
				setPara(0, i);
				for(int j=0;j<getClusterNum();j++){
					paraN[i-1][0][j] = minDisPara.get(j); 
				}
//				System.out.println(minDisPara);
				
//				paraN[order-1][0][0] = 1;
//				paraN[order-1][0][1] = 0;
//				paraN[order-1][0][2] = 0;
			//}
		}
	}
	public void setPara(int sequenceLoc,int order){
		int lenght = array_cluster.size();
		tensor = new double[lenght+1][stateSpace][stateSpace];
		
		setPara(paraN[order-1][sequenceLoc], array_cluster.size()+1, 0, 1, sequenceLoc, order);
	}
	public  void setPara(double []par,int n,int loc, double sum,int sequenceLoc,int order){
		if(loc ==n-1) {
			//System.out.println("setPara "+n);
			par[loc] = sum;
			paraList.clear();
			for(int i=0;i<n;i++)
				paraList.add(par[i]);
			
			//System.out.println(para);
			getTransition(sequenceLoc,order);
			int dis = getDis(sequenceLoc,order);
//			System.out.println("paraList "+paraList);
//				System.out.println("dis "+dis);
			isSetTrans=false;
			//System.out.println(dis);
			if(minDis> dis){
				
				minDis = dis; 
				minDisPara.clear();				
				minDisPara.addAll(paraList);
				//System.out.println(minDisPara);
			}
			return ;
		}
		for(double p=0.0;sum-p >10e-5; p+=0.01){
			par[loc]=p;
			setPara(par,n, loc+1, sum- p,sequenceLoc,order);
		}
	}
	public void setSetTrans(boolean isSetTrans) {
		this.isSetTrans = isSetTrans;
	}
	public int  getDis(int sequenceLoc,int order){
		int  dis =0;
		List<Integer> array = sequenceLoc ==0?this.array:array_cluster.get(sequenceLoc-1);
		int length = array.size();
		
		for(int  i =0 ;i< length-order; i++){
			double[] res = new double[getStateSpace()];
			List<Integer> list_cluter  = new ArrayList<Integer>();
			list_cluter.add(this.array.get(i));
			for(int j = 0; j < array_cluster.size(); j++){
				list_cluter.add(array_cluster.get(j).get(i));
			}
			//System.out.println("pre state_r "+(int)state_r/mode+" "+array.get(i)/mode);
			//
			
			double [] result=prediction(res,list_cluter,sequenceLoc,order);
			
//			for(int j=0;j<stateSpace;j++){
//				System.out.printf("%.4f",result[j]);
//				System.out.print(" ");
//			}
//			System.out.println();
			List<Integer> pre_topN= ArrayHelper.getTopN(result,setParaTop);
			//DealVector.print(result, lineTrans.getStateSpace());
			//System.out.println("pre_topN "+pre_topN);
			//System.out.println("actual "+array.get(i+1)/mode);
			dis += ArrayHelper.getMinDis(pre_topN, array.get(i+order)/mode);
//			if(ArrayHelper.isPredic(pre_topN, array.get(i+order)/mode, ArrayHelper.pre)){
//				dis-=1;
//			}
//			if(ArrayHelper.isPredic(pre_topN, array.get(i+order), ArrayHelper.pre)){
//				dis-=1;
//			}
		}
		return dis ;
	}
	public double[] prediction(double [] result_,List<Integer> list, int sequenceLoc, int order) {
		// TODO Auto-generated method stub
		int stateSpace = getStateSpace();
		double[] state = new double[stateSpace];		
		double[][] matrix = getTransition(sequenceLoc, order);
		for(int i=0;i<array_cluster.size()+1;i++){
			DealVector.reset(state, stateSpace);
			
			
			//state[list.get(i)/mode>stateSpace-1?stateSpace-1:list.get(i)/mode] =1.0;
			StateSet.setState(state, stateSpace, list.get(i)/mode>stateSpace-1?stateSpace-1:list.get(i)/mode);
			DealVector.add(result_, Matrix.order_vector(tensor[i], state, stateSpace), stateSpace, 1);
			//Matrix.print(tensor[i], stateSpace);
			
		}
			
		//System.out.println(state_);
		
		//Matrix.transpose(matrix,stateSpace);
		
		return result_;
	}
	public double[][] toMatrix(List<Integer> array1, List<Integer> array2,double para,int order){
		double[][] matrix  = new double [stateSpace][stateSpace];
		int length = array1.size()> array2.size()? array2.size():array1.size();
		double sum[] = new double[stateSpace];
		for(int i = 0; i < length-order ; i ++){
			sum[array1.get(i)/mode]+=1;
			matrix[array1.get(i)/mode][array2.get(i+order)/mode] +=1;
		}
		for(int i = 0; i < stateSpace ; i ++){
			for(int j = 0 ;j< stateSpace; j++){
				if(sum[i] > 0){
					matrix[i][j] /= sum[i];
					matrix[i][j] *= para;
				}else {
					matrix[i][j] = para /stateSpace;
				}
			
			}
			
		}
		return matrix;
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 3;
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		MultiTransition mul = new MultiTransition();
		mul.getTransition(segmentId, sngSerialId, startTime, endTime);
		
	}
}
