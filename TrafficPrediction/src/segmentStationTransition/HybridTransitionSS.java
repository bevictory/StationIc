package segmentStationTransition;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.BasicDBObject;

import mongodb.MongoDBAssis;
import mongodb.QueryBls;

import util.ArrayHelper;
import util.SegmentStation;
import util.SegmentStationSequence;
import util.SegmentStationTuple;
import util.Station;
import util.StationInfo;
import util.StationSequence;

public class HybridTransitionSS {
	private double[][][][][] transTensor;
	private boolean isSetTrans = false;
	private List<List<Integer>> array_station;
	private List<List<Integer>> array_line;
	private List<Integer> relate_segment;
	private List<Integer> array;
	private List<Double> para_station;
	private List<Double> para_line;
	private int stateSpace ;
	private int isDayModel =1;
	private int mod;
	public int getStateSpace() {
		return stateSpace;
	}
	public void setStateSpace(int stateSpace) {
		this.stateSpace = stateSpace;
	}
	
	private int mode =1;
	private List<SegmentStationTuple> relate_station;
	
	public void setMode(int mode) {
		this.mode = mode;
		this.stateSpace= this.stateSpace/mode+1;
	}


	//private boolean isSetTrans = false;
	public HybridTransitionSS(int segmentId, String stationId, String startTime, String endTime,int isDayModel,int mode,int mod){
		this.isDayModel  = isDayModel;
		this.mod = mod;
		this.mode = mode;
		array_station = new ArrayList<List<Integer>>();
		array_line = new ArrayList<List<Integer>>();
		para_station = new ArrayList<Double>();		
		para_line = new ArrayList<Double>();
		relate_segment = new ArrayList<Integer>();
		relate_station = new ArrayList<SegmentStationTuple>();
		SegmentStationSequence sequence= new SegmentStationSequence();
		if(isDayModel==0) array= sequence.findBydayProcess(segmentId,stationId, startTime, endTime, mod);
		else if(isDayModel==1)array= sequence.findProcess(segmentId,stationId, startTime, endTime, mod);
		else array=sequence.findWorkDayProcess(segmentId, stationId, startTime, endTime, mod);
		set_arrayRelate(segmentId, stationId, startTime, endTime);
		setStateSpace(getMaxState(ArrayHelper.getMax(array))/mode+1);
		set_para();
		
	}
	public double[][][][][] getTransiton(){
		if(!isSetTrans){
			
			transTensor = new double[stateSpace][stateSpace][stateSpace][stateSpace][stateSpace];
			
			toTransTensor(array);
			isSetTrans = true;
		}
		return transTensor;
	}
	
	public void toTransTensor(List<Integer> array) {
		// TODO Auto-generated method stub
		double[][][][] sum = new double[stateSpace][stateSpace][stateSpace][stateSpace];
		int length = array.size();
		for (int i = 0; i < array_station.size(); i++) {
			length = array_station.get(i).size() < length ? array_station
					.get(i).size() : length;
		}
		for (int i = 0; i < array_line.size(); i++) {
			length = array_line.get(i).size() < length ? array_line.get(i)
					.size() : length;
		}
		for (int i = 1; i < length - 1; i++) {
			double state_station = 0;
			double state_line = 0;
			for (int j = 0; j < array_station.size(); j++) {
				state_station += array_station.get(j).get(i)
						* para_station.get(j);
			}
			for (int j = 0; j < array_line.size(); j++) {
				state_line += array_line.get(j).get(i) * para_line.get(j);
			}
			sum[array.get(i - 1)/mode][(int)state_station/mode][(int)state_line/mode][array.get(i)/mode] += 1;
			transTensor[array.get(i - 1)/mode][(int)state_station/mode][(int)state_line/mode][array.get(i)/mode]
					[array.get(i + 1)/mode] += 1;
		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				for (int k = 0; k < stateSpace; k++) {
					for (int m = 0; m < stateSpace; m++) {
						for (int n = 0; n < stateSpace; n++) {
							if (sum[i][j][k][m] > 0)
								transTensor[i][j][k][m][n] /= sum[i][j][k][m];
							// else tranMatrix[i][i] =1;
							else {
								// tranMatrix[i][j][k][m][n] = 1.0 / (stateSpace
								// );
								transTensor[i][j][k][m][m] = 2.0;
							}
						}
					}

				}

			}
		}
	}
	public int getMaxState(int length){
		int max=length;
		for(int i=0;i<array_line.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_line.get(i)));
		}
		for(int i=0;i<array_station.size();i++){
		    max=	Math.max(max ,ArrayHelper.getMax(array_station.get(i)));
		}
		return max;
	}
	public void set_arrayRelate(int segmentId,String stationId, String startTime, String endTime){
		List<Integer> lineList=QueryBls.getSameStation(MongoDBAssis.getDb(), segmentId,stationId );
		//System.out.println(lineList);
		SegmentStationSequence sequence= new SegmentStationSequence();
		//System.out.println("same station line "+lineList.size());
		for(int i=0;i<lineList.size();i++){
			if(isDayModel==1&&!sequence.hasData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			if(isDayModel==2&&!sequence.hasWorkdayData(lineList.get(i), stationId, startTime, endTime, mod)) continue;
			List<Integer> array;
			relate_segment.add(lineList.get(i));
			if(isDayModel==0) array= sequence.findBydayProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(lineList.get(i),stationId, startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(lineList.get(i), stationId, startTime, endTime, mod);
			array_line.add(array);
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
			relate_station.add(new SegmentStationTuple(segment, station));
			//relate_segment.add(segment);
			if(isDayModel==0) array= sequence.findBydayProcess(segment,station, startTime, endTime, mod);
			else if(isDayModel==1)array= sequence.findProcess(segment,station, startTime, endTime, mod);
			else array=sequence.findWorkDayProcess(segment,station,  startTime, endTime, mod);
			//System.out.println(array);
			array_station.add(array);
		}
		
		
	}
	public void set_para(){
		int sum=0;
		List<Integer> dis= new ArrayList<Integer>();
		for(int i=0;i<array_station.size();i++){
			int d = getDis(array, array_station.get(i));
			dis.add(d);
			sum+=d;
		}
		//System.out.println("sum " +sum);
		for(int i=0;i<array_station.size();i++){
			para_station.add((double)dis.get(i)/sum);
		}
		//System.out.println(para);
		
		sum=0;
		dis.clear();
		for(int i=0;i<array_line.size();i++){
			int d = getDis(array, array_line.get(i));
			dis.add(d);
			sum+=d;
		}
		//System.out.println("sum " +sum);
		for(int i=0;i<array_line.size();i++){
			para_line.add((double)dis.get(i)/sum);
		}
	}
	public int  getDis(List<Integer> array1, List<Integer> array2){
		int dis =0; 
		//System.out.println(array1.size()+" "+array2.size());
		for(int i=0 ;i <array1.size();i++){
//			 dis += Math.abs(Math.pow(array1.get(i)- array2.get(i),2));
			dis += Math.abs(array1.get(i)- array2.get(i));
		}
		//dis= (int) Math.sqrt(dis);
		return dis;
	}
	
	public List<Double> getPara_station() {
		return para_station;
	}
	public List<Double> getPara_line() {
		return para_line;
	}
	public List<Integer> getRelate_segment() {
		return relate_segment;
	}
	public List<SegmentStationTuple> getRelate_station() {
		return relate_station;
	}
	
}
