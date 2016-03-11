package prediction;

import mongodb.GetIcArray;
import mongodb.MongoDBAssis;
import decomposition.DealVector;
import decomposition.Matrix;
import transition.GeneralTransition;
import transition.Transition;

public class GeneralPre extends Prediction{
	private GeneralTransition generalTrans ;
	private double[] result ;
	
	GeneralPre(int segmentId, int sngSerialId,String startTime, String endTime){
		super(segmentId, sngSerialId, startTime, endTime);
		generalTrans = new GeneralTransition();
		
	}
	public double[] getResult() {
		return result;
	}
	public void prediction(String time){
		double[] state = new double[Transition.getStateSpace()];
		state[GetIcArray.getIcAtTime(segmentId, sngSerialId, time)]=1.0;
		result = Matrix.multip_vector(generalTrans.getTransiton(segmentId, sngSerialId, startTime, endTime), state, GeneralTransition.getStateSpace());
	}
	public static void main(String []args){
		int segmentId = 35610028;
		int sngSerialId = 3;
		
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		String time =  "2015-12-11 07:40:00";
		GeneralPre pre = new GeneralPre(segmentId, sngSerialId, startTime, endTime);
		
		pre.prediction(time);
		DealVector.print(pre.getResult(), Transition.getStateSpace());
	}

}
