package util;

public class tuple{
	private int lineId;
	private int sngSerialId;
	public tuple(int lineid, int sngSerialId) {
		// TODO Auto-generated constructor stub
		this.sngSerialId= sngSerialId;
		this.lineId = lineid;
	}
	public String toString(){
		return lineId+" "+this.sngSerialId;
			}
	public int getLineId() {
		return lineId;
	}
	public int getSngSerialId() {
		return sngSerialId;
	}
}
