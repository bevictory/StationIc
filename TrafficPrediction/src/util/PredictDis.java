package util;

import java.util.List;

public class PredictDis {
	public static boolean isPreDis =false;
	public static void dealSequence(List<Integer> list, int max){
		
		if(!isPreDis) return ;
		int dis = (max-2)/2;
		for(int i=0;i<list.size();i++){
			int elem = list.get(i);
			elem += dis;
			list.set(i, elem);
		}
	}
	public static List<Integer> back(List<Integer> list, int max){
		
		if(!isPreDis) return list;
		int dis = (max-2)/2;
		for(int i=0;i<list.size();i++){
			int elem = list.get(i);
			elem -= dis;
			list.set(i, elem);
		}
		return list;
	}

}
