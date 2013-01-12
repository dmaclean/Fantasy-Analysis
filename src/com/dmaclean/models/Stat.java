package com.dmaclean.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

public class Stat {
	
	private static final Logger logger = Logger.getLogger(Stat.class.getPackage().getName());

	private int id;
	
	private int playerId;
	
	private int season;
	
	private int week;
	
	private int statKey;
	
	private int statValue;
	
	public Stat() {
		
	}
	
	public void get(int id, Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from stats where id = ?");
			pstmt.setInt(1, id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				this.id = rs.getInt(1);
				playerId = rs.getInt(2);
				season = rs.getInt(3);
				week = rs.getInt(4);
				statKey = rs.getInt(5);
				statValue = rs.getInt(6);
			}
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
				rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void delete(int id, Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("delete from stats where id = ?");
			pstmt.setInt(1, id);
			
			pstmt.execute();
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void save(Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("insert into stats (player_id, season, week, stat_key, stat_value) " +
					"values(?,?,?,?,?)");
			
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, season);
			pstmt.setInt(3, week);
			pstmt.setInt(4, statKey);
			pstmt.setInt(5, statValue);
			
			pstmt.execute();
			
			ResultSet newKey = pstmt.getGeneratedKeys();
			if(newKey.next())
				this.id = newKey.getInt(1);
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean exists(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from stats where " +
					"player_id = ? and season = ? and week = ? and stat_key = ? and stat_value = ?");
			
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, season);
			pstmt.setInt(3, week);
			pstmt.setInt(4, statKey);
			pstmt.setInt(5, statValue);
			
			rs = pstmt.executeQuery();

			return rs.next();
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
				rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
		
		return false;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getStatKey() {
		return statKey;
	}

	public void setStatKey(int key) {
		this.statKey = key;
	}

	public int getStatValue() {
		return statValue;
	}

	public void setStatValue(int value) {
		this.statValue = value;
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}

	public int getWeek() {
		return week;
	}

	public void setWeek(int week) {
		this.week = week;
	}
}
