package mongodb;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import mysql.DBConnection;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class QueryGpsIc {

	public static void getGpsIc(DB db, String collectionName,String sqlTableName) {
		DBCursor iter = db.getCollection(collectionName).find();
		int num = 0;
		Connection conn;
		// try {
		// conn= DBConnection.getInstance().getConnection();
		// while(iter.hasNext()){
		// DBObject object =iter.next();
		// insert(conn,object,"gpsic_11_10");
		// num++;
		//
		// }
		// } catch (IOException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		//
		// System.out.println(num);
		try {
			conn = DBConnection.getInstance().getConnection();
			insert1(conn, iter, sqlTableName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void insert(Connection conn, DBObject obj, String tableName) {
		String sql = "insert into " + tableName + " values ("
				+ obj.get("segmentId") + "," + obj.get("sngSerialId") + ",\'"
				+ obj.get("busselfId") + "\',\'" + obj.get("stationId")
				+ "\',\'" + obj.get("arriveTime") + "\',\'"
				+ obj.get("leaveTime") + "\'," + obj.get("traffic") + ")";
		System.out.println(sql);

		try {
			conn.createStatement().executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void insert1(Connection conn, DBCursor iter, String tableName) {
		String sql = "insert into " + tableName + " values(?,?,?,?,?,?,?)";
		System.out.println(sql);

		try {
			PreparedStatement pst = conn.prepareStatement(sql);
			conn.setAutoCommit(false);
			int num =0;
			while (iter.hasNext()) {
				DBObject obj = iter.next();
				
				pst.setInt(1, (Integer) obj.get("segmentId"));
				pst.setInt(2, (Integer) obj.get("sngSerialId"));
				pst.setString(3, (String) obj.get("busselfId"));
				pst.setString(4, (String) obj.get("stationId"));
				pst.setString(5, (String) obj.get("arriveTime"));
				pst.setString(6, (String) obj.get("leaveTime"));
				if (obj.get("traffic") != null){
					//System.out.println((Integer)obj.get("traffic"));
					pst.setInt(7, (Integer) obj.get("traffic"));}
				else
					pst.setInt(7, 0);
				pst.addBatch();
				num++;
				if(num == 100000){
					pst.executeBatch();conn.commit();
					num=0;
				}
				
			}
			pst.executeBatch();conn.commit();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		getGpsIc(MongoDBCoonnection.getInstance().getDB(), "gps_12_07_IC","gpsic_12_07");
		getGpsIc(MongoDBCoonnection.getInstance().getDB(), "gps_11_10_IC","gpsic_11_10");
	}

}
