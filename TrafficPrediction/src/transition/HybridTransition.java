package transition;

import java.util.ArrayList;

public class HybridTransition extends Transition {
	private double[][][][][] transTensor;
	private boolean isSetTrans = false;
	private ArrayList<ArrayList<Integer>> array_station;
	private ArrayList<ArrayList<Integer>> array_line;
	private ArrayList<Double> para_station;
	private ArrayList<Double> para_line;

	@Override
	public void toTransTensor(ArrayList<Integer> array) {
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
			int state_station = 0;
			int state_line = 0;
			for (int j = 0; j < array_station.size(); j++) {
				state_station += array_station.get(j).get(i)
						* para_station.get(j);
			}
			for (int j = 0; j < array_line.size(); j++) {
				state_line += array_line.get(j).get(i) * para_line.get(j);
			}
			sum[array.get(i - 1)][state_station][state_line][array.get(i)] += 1;
			transTensor[array.get(i - 1)][state_station][state_line][array
					.get(i)][array.get(i + 1)] += 1;
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
								transTensor[i][j][k][m][m] = 0.0;
							}
						}
					}

				}

			}
		}
	}

}
