package mongodb;

public class GpsIc {
	private int segmentId;
	private int sngSerialId;
	private int stationId;
	private String arriveTime;
	private String leaveTime;
	private int traffic;
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public int getSngSerialId() {
		return sngSerialId;
	}
	public void setSngSerialId(int sngSerialId) {
		this.sngSerialId = sngSerialId;
	}
	public int getStationId() {
		return stationId;
	}
	public void setStationId(int stationId) {
		this.stationId = stationId;
	}
	public String getArriveTime() {
		return arriveTime;
	}
	public void setArriveTime(String arriveTime) {
		this.arriveTime = arriveTime;
	}
	public String getLeaveTime() {
		return leaveTime;
	}
	public void setLeaveTime(String leaveTime) {
		this.leaveTime = leaveTime;
	}
	public int getTraffic() {
		return traffic;
	}
	public void setTraffic(int traffic) {
		this.traffic = traffic;
	}
	

}
