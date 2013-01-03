package com.dmaclean.testutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

public class FantasyTestUtils {
	
	private static final Logger logger = Logger.getLogger(FantasyTestUtils.class.getPackage().getName());
	
	/**
	 * Create a JDBC connection to the test database.
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
	
	/**
	 * Run the create_tables.sql script to reset the test database tables.
	 */
	public static void resetTestDatabase() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		long start = System.currentTimeMillis();
		
		try {
			BufferedReader r = new BufferedReader(new FileReader(new File("sql/create_tables.sql")));
			ArrayList<String> commands = new ArrayList<String>();
			StringBuffer sb = new StringBuffer();
			String s = null;
			
			while( (s = r.readLine()) != null) {
				sb.append(s);
				
				// We've reached the end of a command.
				if(s.contains(";")) {
					commands.add(sb.toString());
					sb = new StringBuffer();
				}
			}
			
			conn = FantasyTestUtils.getTestConnection();
			
			for(String command : commands) {
				pstmt = conn.prepareStatement(command);
				pstmt.execute();
			}
		}
		catch(Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				conn.close();
				pstmt.close();
			}
			catch(SQLException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
		}
		
		long end = System.currentTimeMillis();
		logger.info("Reset test database in " + (end-start)/1000.0 + " seconds.");
	}
	
	public static void main(String[] args) {
		FantasyTestUtils.resetTestDatabase();
	}
}
