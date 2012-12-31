package com.dmaclean.testutils;

import java.sql.Connection;
import java.sql.DriverManager;

public class FantasyTestUtils {
	/**
	 * 
	 * 
	 * @return
	 */
	public static Connection getTestConnection() {
		Connection conn = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "fantasy_yahoo_test";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "fantasy"; 
		String password = "fantasy";
		  
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return conn;
	}
}
