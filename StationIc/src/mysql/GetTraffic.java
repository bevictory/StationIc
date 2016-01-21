/**
 * Copyright(C) 2015 普适 Software Technology Co.,Ltd.
 * Project Name:BusLineStation
 * File Name:QueryIc.java
 * Package Name:mysql
 * @Description:一句话描述该文件做什么
 * @author dai.guohui
 * @Date:2015年11月25日 上午9:36:51
 */
package mysql;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bson.Document;

import mysql.QueryIcSql;

/**
  * 类说明
  * @author dai.guohui
  * @version 1.0, 2015年11月25日  每次修改后更新版本号，日期和修改内容
  * @see	[相关类/方法]
  * @since	[产品/模块版本]
  */
public class GetTraffic {
	
	public static int getTraffic(Connection conn,String sql, String qcbh){
		int traffic =0;
		
		
		try {
			
			Statement sta;
			sta = conn.createStatement();
			ResultSet rs =sta.executeQuery(sql+" and qcbh = '0"+qcbh+"'");
			//System.out.println(sql+" and qcbh = '0"+qcbh+"'");
			if(rs.next()) traffic=rs.getInt(1);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return traffic;
		
		
	}
	public static int getTraffic(Connection conn,Document arg0){
		String buselfId = arg0.getString("busselfId");
		int traffic=0;
		String sql1=QueryIcSql.getSql(arg0.getString("arriveTime"), arg0.getString("leaveTime"),"chargeicdetail");
		//String sql1 = QueryIcSql.getSql_table(sql_base, "chargeicdetail");
		//String sql2 = QueryIcSql.getSql_table(sql_base, "freeicdetail");
		traffic+=GetTraffic.getTraffic(conn,sql1, buselfId);
		return traffic;
	}
	public static void main(String []args){
		String sql = "select count(*) from chargeicdetail  where xfrq >= "+"'2015-11-10 00:00:00'"+" and xfrq < "+"'2015-11-11 00:00:00'"+
	"and xfsj >=" +"'04:59:23'"+ "and xfsj <= "+"'05:00:23'";
		try {
			System.out.println(getTraffic( DBConnection.getInstance().getConnection(),sql,"58581"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
