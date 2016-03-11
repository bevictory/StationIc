package prediction;

import java.util.ArrayList;

import decomposition.DealVector;
import decomposition.Matrix;
import decomposition.Tensor_3order;
import mongodb.GetIcArray;

import transition.StationTransition;
import transition.Transition;

public class StationPre extends Prediction{
	private double[] result;
	private StationTransition stationTrans;
	StationPre(int segmentId, int sngSerialId, String startTime, String endTime) {
		super(segmentId, sngSerialId, startTime, endTime);
		// TODO Auto-generated constructor stub
		stationTrans = new StationTransition();
	}
	@Override
	public void prediction(String time) {
		// TODO Auto-generated method stub
		double[] state = new double[Transition.getStateSpace()];
		double[] state_relate = new double[Transition.getStateSpace()];
		double[][][] tensor_3order = stationTrans.getTransiton(segmentId, sngSerialId, startTime, endTime);
		state_relate[get_relateState(time)] = 1.0;
		state[GetIcArray.getIcAtTime(segmentId, sngSerialId, time)]=1.0;
		
		double [][] matrix=Tensor_3order.orderMulti_one(tensor_3order, state_relate, Transition.getStateSpace());
		Matrix.transpose(matrix, Transition.getStateSpace());
		result = Matrix.multip_vector(matrix, state, Transition.getStateSpace());
	}
	public int get_relateState(String time){
		ArrayList<Integer> array = new ArrayList<Integer>();
		array.add(GetIcArray.getIcAtTime(segmentId, sngSerialId-1, time));
		array.add(GetIcArray.getIcAtTime(segmentId, sngSerialId+1, time));
		int state =0;
		for(int i = 0; i < array.size(); i++){
			state+=array.get(i)*stationTrans.getPara().get(i);
		}
		return state;
	}
	public double[] getResult() {
		return result;
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 3;
		
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		String time =  "2015-12-11 08:40:00";
		StationPre pre = new StationPre(segmentId, sngSerialId, startTime, endTime);
		
		pre.prediction(time);
		DealVector.print(pre.getResult(), Transition.getStateSpace());
	}
	
	

}
