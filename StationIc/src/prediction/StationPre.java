package prediction;

import java.util.ArrayList;

import transition.stationTransition;

import com.mongodb.client.MongoDatabase;

import mongodb.GetIcArray;
import mongodb.MongoDBCoonnection;
import mongodb.QueryBls;
import decomposition.DealVector;
import decomposition.Matrix;

public class StationPre {
	private static int stateSpace = 20;
	private static double[] result = new double[stateSpace];
	private static MongoDatabase mongodb = MongoDBCoonnection.getInstance()
			.getRemoteMongoDatabase2();

	public static double[][][] getTransition(int lineNo, int stationNo,
			String startTime, String endTime) {

		ArrayList<String> segment = new ArrayList<String>();
		segment.add("35610028");
		segment.add("35557702");
		segment.add("35632502");
		segment.add("35641294");
		int stationNum;
		int segmentId;
		double[][][] tensor = new double[stateSpace][stateSpace][stateSpace];

		segmentId = lineNo;
		stationNum = QueryBls.getStationNum(mongodb, segmentId);
		// int loc ;
		// if(stationNum <=station )
		// loc =stationNum;
		// else
		// loc =station;

		if (stationNo == 1 && stationNo <= stationNum - 2) {
			ArrayList<Integer> array = null, array2 = null, array3 = null;
			array = GetIcArray.getIC_int(mongodb, segmentId, stationNo,
					startTime, endTime);
			array2 = GetIcArray.getIC_int(mongodb, segmentId, stationNo,
					startTime, endTime);
			array3 = GetIcArray.getIC_int(mongodb, segmentId, stationNo,
					startTime, endTime);
			tensor = stationTransition.toTrans_station(array, array2, array3);
		} else if (stationNo > 1 && stationNo <= stationNum - 1) {
			ArrayList<Integer> array = null, array2 = null, array3 = null;
			array = GetIcArray.getIC_int(mongodb, segmentId, stationNo,
					startTime, endTime);
			array2 = GetIcArray.getIC_int(mongodb, segmentId, stationNo - 1,
					startTime, endTime);
			array3 = GetIcArray.getIC_int(mongodb, segmentId, stationNo + 1,
					startTime, endTime);
			tensor = stationTransition.toTrans_station(array, array2, array3);
		} else if (stationNo == stationNum) {
			ArrayList<Integer> array = null, array2 = null, array3 = null;
			array = GetIcArray.getIC_int(mongodb, segmentId, stationNo,
					startTime, endTime);
			array2 = GetIcArray.getIC_int(mongodb, segmentId, stationNo - 2,
					startTime, endTime);
			array3 = GetIcArray.getIC_int(mongodb, segmentId, stationNo - 1,
					startTime, endTime);
			tensor = stationTransition.toTrans_station(array, array2, array3);
		} else {

		}

		return tensor;
	}

	public static void prediction(int weight,int state,int lineNo, int stationNo, String startTime,
			String endTime) {
		double[][][] transition = new double[stateSpace][stateSpace][stateSpace];
		transition = getTransition(lineNo, stationNo, startTime, endTime);
		for(int i=0;i<stateSpace;i++){
			result[i] = transition[weight][state][i]; 
		}
	}

	public static void main(String[] args) {
		String startTime = "2015-12-07 06:30:00";
		String endTime = "2015-12-11 09:00:00";
		//result[1] = 1.0;

		//prediction(result, startTime, endTime);
		DealVector.print(result, stateSpace);
	}

}
