package transitionN;

import java.util.ArrayList;

public abstract class Transition {
	public static int stateSpace =20;
	public static int getStateSpace(){
		return stateSpace;
	}
	public abstract void toTransTensor(ArrayList<Integer> array,int order);
	
}
