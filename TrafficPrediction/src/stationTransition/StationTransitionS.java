package stationTransition;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;

import util.ArrayHelper;
import util.SegmentStationSequence;
import util.Station;
import util.StationInfo;
import util.StationSequence;

import mongodb.GetIcArray;

public class StationTransitionS {
	private double[][][] transTensor;
	private boolean isSetTrans = false;
	private List<List<Integer>> array_relate ;
	private List<Double> para;
	private int stateSpace ;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	public List<Double> getPara() {
		return para;
	}
	private int mode =1;
	private int mod;
	private int isDayModel =1;
	private List<Integer> array;
	private ArrayList<String> relate_station;
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}


	//private boolean isSetTrans = false;
	public StationTransitionS(String stationId, String startTime, String endTime,int isDaymodel,int mode,int mod){
		this.mod = mod;
		this.mode = mode;
		this.isDayModel = isDaymodel;
		array_relate = new ArrayList<List<Integer>>();
		
		relate_station = new ArrayList<String>();
		para = new ArrayList<Double>();
		StationSequence sequence= new StationSequence();
		if(isDayModel==0) array= sequence.findBydayProcess(stationId, startTime, endTime, mod);
		else if(isDayModel==1)array= sequence.findProcess(stationId, startTime, endTime, mod);
		else array=sequence.findWorkDayProcess( stationId, startTime, endTime, mod);
		System.out.println(array);
		set_arrayRelate( stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		set_para();
	}
	public ArrayList<String> getRelate_station() {
		return relate_station;
	}
	public void setRelate_station(ArrayList<String> relate_station) {
		this.relate_station = relate_station;
	}
	public double[][][] getTransiton(){
		if(!isSetTrans){
			
			transTensor = new double[stateSpace][stateSpace][stateSpace];
			toTransTensor(array);
			isSetTrans = true;
		}
		return transTensor;
	}
	
	public void toTransTensor(List<Integer> array) {
		// TODO Auto-generated method stub
		double[][] sum = new double[stateSpace][stateSpace];
		int length = array.size();
		for(int i =0; i < array_relate.size();i++){
			length =array_relate.get(i).size() < length?array_relate.get(i).size() : length;				
		}
		for(int i = 0; i < length-1 ; i ++){
			double state =0 ;
			for(int j =0 ; j < array_relate.size(); j++){
				state+= array_relate.get(j).get(i) * para.get(j);
			}
			sum[(int)state/mode][array.get(i)/mode] += 1;
			transTensor[(int)state/mode][array.get(i)/mode][array.get(i+1)/mode] += 1;
			
		}
		
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					if (sum[i][j] > 0)
						transTensor[i][j][k] /= sum[i][j];
					// else tranMatrix[i][i] =1;
					else {
						//tranMatrix[i][j][k] = 1.0 / ( stateSpace);
						transTensor[i][j][j] = 0.0;
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
	public void set_arrayRelate(String stationId, String startTime, String endTime){
		List<BasicDBObject> nearStationList = StationInfo.getNear(stationId, 1, 200);
		StationSequence sequence = new StationSequence();
		for(int i=0;i<nearStationList.size();i++){
			if(Station.getStationIcSum(nearStationList.get(i).getString("stationId"))<1000||isDayModel==1&&!sequence.hasData(nearStationList.get(i).getString("stationId"), startTime, endTime, mod)) 
				continue;
			if(Station.getStationIcSum(nearStationList.get(i).getString("stationId"))<1000||isDayModel==2&&!sequence.hasWorkdayData(nearStationList.get(i).getString("stationId"), startTime, endTime, mod)) 
				continue;
			List<Integer> array;
			relate_station.add(nearStationList.get(i).getString("stationId"));
			if(isDayModel==0) array= sequence.findBydayProcess(nearStationList.get(i).getString("stationId"), startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(nearStationList.get(i).getString("stationId"), startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(nearStationList.get(i).getString("stationId"),  startTime, endTime, mod);
			System.out.println(array);
			array_relate.add(array);
		}		
	}
	
	public void set_para(){
		double sum=0;
		List<Double> dis= new ArrayList<Double>();
		for(int i=0;i<array_relate.size();i++){
			double d = 1.0/getDis(array, array_relate.get(i));
			dis.add(d);
			sum+=d;
		}
		//System.out.println("sum " +sum);
		for(int i=0;i<array_relate.size();i++){
			para.add((double)dis.get(i)/sum);
		}
		
		//System.out.println(para);
	}
	
	
	public int  getDis(List<Integer> array1, List<Integer> array2){
		int dis =0; 
		//System.out.println(array1.size()+" "+array2.size());
		for(int i=0 ;i <array1.size();i++){
			 dis += Math.abs(array1.get(i)- array2.get(i));
		}
		//dis= (int) Math.sqrt(dis);
		return dis;
	}
}
