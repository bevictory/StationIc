package stationTransition;

import java.util.ArrayList;
import java.util.List;

import mongodb.GetIcArray;
import mongodb.MongoDBAssis;
import mongodb.QueryBls;
import transition.MultiTransition;
import transition.Transition;
import util.SegmentStationSequence;
import util.StationSequence;
import util.tuple;
import decomposition.Matrix;

public class MultiTransitionS {
	private List<List<Integer>> array_cluster = new ArrayList<List<Integer>>();
	private double[][] para = null;
	private double[][][][] transition=null;
	private boolean isSetTrans = false;
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
	public MultiTransitionS( String stationId, String startTime, String endTime,int isDayModel,int mode,int mod) {
		// TODO Auto-generated constructor stub
		this.isDayModel  = isDayModel;
		this.mod = mod;
		this.mode = mode;
		StationSequence sequence= new StationSequence();
		
		if(isDayModel==0) array = sequence.findBydayProcess(stationId, startTime, endTime, mod);
		else if(isDayModel==1) array = sequence.findProcess(stationId, startTime, endTime, mod);
		else array = sequence.findWorkDayProcess( stationId, startTime, endTime, mod);
		
		
	}
	public ArrayList<Integer> getIc(int segmentId, int sngSerialId, String startTime, String endTime){
		if(mode >1) return GetIcArray.getIcByHour_int(segmentId, sngSerialId, startTime, endTime);
		else return GetIcArray.getIC_int(segmentId, sngSerialId, startTime, endTime);
	}
	public void setPara(){
		para = new double[array_cluster.size()+1][array_cluster.size()+1];
		for(int i = 0 ; i < array_cluster.size()+1; i++){
			for(int j = 0 ; j < array_cluster.size()+1; j++){
				para[i][j] = ((double)1.0/(array_cluster.size()+1)) ;
			}
		}
		para[0][0]=0.8;
		para[1][0]=0.1;
		para[2][0]=0.1;
		transition  = new double[array_cluster.size()+1][array_cluster.size()+1][stateSpace][stateSpace];
		
	}
	public void getCluster(int segmentId, int sngSerialId, String startTime, String endTime){
		ArrayList<tuple> array =QueryBls.getSameStation(MongoDBAssis.getDb(), segmentId, sngSerialId);
		if(array.size()>1)
		{
			System.out.println("different line same station");
			array_cluster.add(getIc( array.get(0).getLineId(), array.get(0).getSngSerialId(), startTime, endTime));
			array_cluster.add(getIc( array.get(1).getLineId(), array.get(1).getSngSerialId(), startTime, endTime));
		}else
		{
			array_cluster.add(getIc( segmentId, sngSerialId-1, startTime, endTime));
			array_cluster.add(getIc( segmentId, sngSerialId+1, startTime, endTime));
		}
	
		System.out.println(getClusterNum());
		setPara();
	}
	public  void  setTransition(int segmentId, int sngSerialId, String startTime, String endTime){
		if(!isSetTrans){
			getCluster(segmentId,sngSerialId, startTime, endTime);
			ArrayList<Integer> array = getIc( segmentId, sngSerialId, startTime, endTime);
			toTransTensor(array);
			isSetTrans = true;
		}
	}
	
	
	public double[][][][] getTransition(int segmentId,String stationId , String startTime, String endTime,int mod){
		if(!isSetTrans){
			int sngSerialId =QueryBls.getSngrialId(MongoDBAssis.getDb(), segmentId, stationId);
			getCluster(segmentId,sngSerialId, startTime, endTime);
			
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
		Matrix.print(transition[0][0], Transition.getStateSpace());
		System.out.println();
		Matrix.print(transition[1][0], Transition.getStateSpace());
		System.out.println();
		Matrix.print(transition[2][0], Transition.getStateSpace());
		
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