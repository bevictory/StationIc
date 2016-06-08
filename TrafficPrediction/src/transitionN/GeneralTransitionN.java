package transitionN;

import java.util.ArrayList;

import com.mongodb.client.MongoDatabase;

import decomposition.Matrix;

import mongodb.GetIcArray;

public class GeneralTransitionN extends Transition {
	private double[][] transition;
	private int mode = 1;
	private int order = 1;
	private double[] para;
	public void setMode(int mode) {
		this.mode = mode;
	}
	public void setOrder(int order){
		this.order = order;
	}

	private boolean isSetTrans = false;
	private void setPara()
	{
		para = new double[order];
//		for(int i=0;i<order ;i++){
//			para[i]=1.0/order;
//		}
		para[0]=0.1;
		para[1]=0.1;
		para[2]=0.8;
	}

	public double[][] getTransiton(int segmentId, int sngSerialId,
			String startTime, String endTime) {
		if (!isSetTrans) {
			setPara();
			ArrayList<Integer> array = getIc(segmentId, sngSerialId, startTime,
					endTime);
			toTransTensor(array,order);
			Matrix.transpose(transition, stateSpace);
			isSetTrans = true;
		}
		return transition;
	}

	public void setSetTrans(boolean isSetTrans) {
		this.isSetTrans = isSetTrans;
	}

	/**
	 * 获得特定线路站点时间段的刷卡数据
	 * 
	 * @param segmentId
	 * @param sngSerialId
	 * @param startTime
	 * @param endTime
	 * @return
	 */
	public ArrayList<Integer> getIc(int segmentId, int sngSerialId,
			String startTime, String endTime) {
		if (mode > 1)
			return GetIcArray.getIcByHour_int(segmentId, sngSerialId,
					startTime, endTime);
		else
			return GetIcArray.getIC_int(segmentId, sngSerialId, startTime,
					endTime);
	}

	public void toTransTensor(ArrayList<Integer> array) {
		transition = new double[stateSpace][stateSpace];

		double[] sum = new double[stateSpace];
		for (int i = 0; i < array.size() - 1; i++) {
			if (array.get(i) / mode >= stateSpace
					&& array.get(i + 1) / mode >= stateSpace) {

				sum[stateSpace - 1] += 1;
				transition[stateSpace - 1][stateSpace - 1] += 1;
			} else if (array.get(i) / mode >= stateSpace) {
				sum[stateSpace - 1] += 1;
				transition[stateSpace - 1][array.get(i + 1)] += 1;
			} else if (array.get(i + 1) / mode >= stateSpace) {
				sum[array.get(i) / mode] += 1;
				transition[array.get(i) / mode][stateSpace - 1] += 1;
			} else {
				sum[array.get(i) / mode] += 1;
				transition[array.get(i) / mode][array.get(i + 1) / mode] += 1;
			}

		}
		for (int i = 0; i < stateSpace; i++) {
			for (int j = 0; j < stateSpace; j++) {
				if (sum[i] > 0)
					transition[i][j] /= sum[i];
				// else transition[i][i] =1;
				else {
					// transition[i][i] =1.0/stateSpace;
					transition[i][i] = 0.0;
				}
			}
		}

	}

	@Override
	public void toTransTensor(ArrayList<Integer> array, int order) {
		// TODO Auto-generated method stub
		transition = new double[stateSpace][stateSpace];
		while (order > 0) {
			double[] sum = new double[stateSpace];
			double [][] transitionN = new double[stateSpace][stateSpace];
			for (int i = 0; i < array.size() - order; i++) {
				if (array.get(i) / mode >= stateSpace
						&& array.get(i + order) / mode >= stateSpace) {

					sum[stateSpace - 1] += 1;
					transitionN[stateSpace - 1][stateSpace - 1] += 1;
				} else if (array.get(i) / mode >= stateSpace) {
					sum[stateSpace - 1] += 1;
					transitionN[stateSpace - 1][array.get(i + order)] += 1;
				} else if (array.get(i + order) / mode >= stateSpace) {
					sum[array.get(i) / mode] += 1;
					transitionN[array.get(i) / mode][stateSpace - 1] += 1;
				} else {
					sum[array.get(i) / mode] += 1;
					transitionN[array.get(i) / mode][array.get(i + order) / mode] += 1;
				}

			}
			for (int i = 0; i < stateSpace; i++) {
				for (int j = 0; j < stateSpace; j++) {
					if (sum[i] > 0)
						transitionN[i][j] /= sum[i];
					// else transition[i][i] =1;
					else {
						 transition[i][i] =1.0/stateSpace;
						//transitionN[i][i] = 0.0;
					}
				}
			}
			Matrix.add(transition, transitionN, stateSpace,para[order-1]);
			order--;
		}
	}

	public static void main(String[] args) {
		int segmentId = 35610028;
		int sngSerialId = 18;
		String startTime = "2015-12-07 06:30:00", endTime = "2015-12-10 09:00:00";
		GeneralTransitionN generalTransitionN = new GeneralTransitionN();
		System.out.println(generalTransitionN.getIc(segmentId, sngSerialId, startTime, endTime));
		

	}

}
