package util;

public class SegmentStationTuple {
	private int segmentId;
	private String stationId;
	public int getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(int segmentId) {
		this.segmentId = segmentId;
	}
	public String getStationId() {
		return stationId;
	}
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	
	public SegmentStationTuple(int segmentId, String stationId){
		this.segmentId = segmentId;
		this.stationId = stationId ;
	}

}
