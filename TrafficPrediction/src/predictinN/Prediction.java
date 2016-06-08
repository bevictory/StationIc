package predictinN;

public abstract class Prediction {
	public int segmentId;
	public int sngSerialId;
	public String startTime;
	public String endTime;
	Prediction(int segmentId, int sngSerialId, String startTime, String endTime){
		this.segmentId = segmentId;
		this.sngSerialId = sngSerialId;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	public abstract void prediction(String time);

}
