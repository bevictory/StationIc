package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;

import mongodb.GetIcArray;
import mongodb.MongoDBAssis;
import mongodb.QueryBls;
import transition.MultiTransition;
import transition.Transition;
import util.ArrayHelper;
import util.SegmentStation;
import util.SegmentStationSequence;
import util.SegmentStationTuple;
import util.StationInfo;
import util.tuple;
import decomposition.Matrix;

public class MultiTransitionSS {
	private List<List<Integer>> array_cluster = new ArrayList<List<Integer>>();
	private double[][] para = null;
	private double[][][][] transition=null;
	private boolean isSetTrans = false;
	private List<SegmentStationTuple> clusterList;
	public List<SegmentStationTuple> getClusterList() {
		return clusterList;
	}
	private int mode =1;
	private int stateSpace ;
	private int isDayModel;
	private int mod;
	private List<Integer> array;
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
	public MultiTransitionSS(int segmentId, String stationId, String startTime, String endTime,int isDayModel,int mode,int mod) {
		// TODO Auto-generated constructor stub
		this.isDayModel  = isDayModel;
		this.mod = mod;
		this.mode = mode;
		SegmentStationSequence sequence= new SegmentStationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		
		
		clusterList = new ArrayList<SegmentStationTuple>();
		getCluster(segmentId, stationId, startTime, endTime);
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
				double d= 1.0/getDis(array1, array2);
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
		}
		dis= (int) Math.sqrt(dis);
		return dis;
	}
	public void getCluster(int segmentId, String stationId, String startTime, String endTime){
		List<Integer> lineList=QueryBls.getSameStation(MongoDBAssis.getDb(), segmentId,stationId );
		//System.out.println(lineList);
		SegmentStationSequence sequence= new SegmentStationSequence();
		//System.out.println("same station line "+lineList.size());
		for(int i=0;i<lineList.size();i++){
			if(isDayModel==1&&!sequence.hasData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			if(isDayModel==2&&!sequence.hasWorkdayData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			List<Integer> array;
			clusterList.add(new SegmentStationTuple(lineList.get(i), stationId));
			if(isDayModel==0) array= sequence.findBydayProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(lineList.get(i), stationId, startTime, endTime, mod);
			array_cluster.add(array);
		}
		
		
		List<BasicDBObject> nearStationList = StationInfo.getNear_segsta(stationId, 1, 200);
		//StationSequence sequence = new StationSequence();
		for(int i=0;i<nearStationList.size();i++){
			int segment = nearStationList.get(i).getInt("segmentId");
			String station = nearStationList.get(i).getString("stationId");
			if(SegmentStation.getIcSum(segment,station)<1000||isDayModel==1&&!sequence.hasData(segment,station, startTime, endTime, mod)) 
				continue;
			if(SegmentStation.getIcSum(segment,station)<1000||isDayModel==2&&!sequence.hasWorkdayData(segment,station, startTime, endTime, mod)) 
				continue;
			List<Integer> array;
			clusterList.add(new SegmentStationTuple(segment, station));
			//relate_segment.add(segment);
			if(isDayModel==0) array= sequence.findBydayProcess(segment,station, startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(segment,station, startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(segment,station,  startTime, endTime, mod);
			//System.out.println(array);
			array_cluster.add(array);
		}
	}
	
	
	public double[][][][] getTransition(){
		if(!isSetTrans){
			//transition = new double[stateSpace][stateSpace][stateSpace][stateSpace];
			transition  = new double[array_cluster.size()+1][array_cluster.size()+1][stateSpace][stateSpace];
			toTransTensor(array);
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
