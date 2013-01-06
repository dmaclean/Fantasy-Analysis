package com.dmaclean.models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import com.dmaclean.constants.FantasyConstants;
import com.dmaclean.parser.StHttpRequest;

public class PlayerWeekStats extends PlayerSeasonStats {
	private static final Logger logger = Logger.getLogger(PlayerWeekStats.class.getPackage().getName());
	
	private int week;
	
	public PlayerWeekStats() {
		super();
	}
	
	public static void retrieveWeeklyStats(int year, int id, Connection conn) {
		StHttpRequest httpRequest = new StHttpRequest();
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		try {
			int week = 1;
			while(week<18) {
				PlayerWeekStats pws = new PlayerWeekStats();
				pws.setPlayerId(id);
				pws.setYear(year);
				pws.setWeek(week);
				
				/*
				 * Sanity check.  Do we need this entry?
				 */
				if(pws.exists(conn)) {
					logger.info("Week " + week + " stats (" + year + ") for player " + id + " already exist.");
					week++;
					continue;
				}
				
				// Create final URL
				String url = FantasyConstants.URL_WEEK_STATS;
				url = url.replace("{player_id}", "" + id);
				url = url.replace("{year}", FantasyConstants.yearToCode.get(year));
				url = url.replace("{week}", "" + week);
				
				logger.info("sending get request to" + URLDecoder.decode(url, "UTF-8"));
				int responseCode = httpRequest.sendGetRequest(url); 
				
				// Send the request
				if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
					logger.info("Response ");
					
					pws.parsePlayerStats(httpRequest.getResponseBody());
					pws.save(conn);
					
					week++;
					
					try {
						Thread.sleep(1000*2);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
				else if(responseCode == 999) {
					logger.severe("Error in response due to status code = " + responseCode + ".  Sleeping for 30 minutes.");
					
					try {
						Thread.sleep(1000*60*30);
					}
					catch(InterruptedException e) {
						e.printStackTrace();
					}
				}
				else {
					logger.severe("Error in response due to status code = " + responseCode);
				}
				logger.info(httpRequest.getResponseBody());
			}
		} catch(UnsupportedEncodingException e) {
			logger.severe("Encoding/Decording error");
		} catch (IOException e) {
			logger.severe("Error with HTTP IO - " + e.getMessage());
		} catch (Exception e) {
			logger.severe(httpRequest.getResponseBody() + " - " + e.getMessage());
		}
	}
	
	public static void retrieveWeeklyStatsForAllPlayersForYear(int year, Connection conn) {
		ArrayList<Integer> ids = Player.getAllPlayerIds(conn);

		for(Integer id : ids) {
			retrieveWeeklyStats(year, id, conn);
		}
	}
	
	/**
	 * Retrieves the PlayerWeekStats object from the database that matches the id/year/week combo.
	 * 
	 * @param id		Player id
	 * @param year		Season
	 * @param week		Week
	 * @param conn
	 * @return
	 */
	public static PlayerWeekStats get(int id, int year, int week, Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PlayerWeekStats pws = new PlayerWeekStats();
		
		try {
			pstmt = conn.prepareStatement("select * from player_week_stats where player_id = ? and year = ? and week = ?");
			pstmt.setInt(1, id);
			pstmt.setInt(2, year);
			pstmt.setInt(3, week);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				pws.setPlayerId(rs.getInt("player_id"));
				pws.setYear(rs.getInt("year"));
				pws.setWeek(rs.getInt("week"));
				
				Stat stat = new Stat();
				stat.get(rs.getInt("stat_id"), conn);
				
				pws.setStat(stat);
			}
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
				rs.close();
			}
			catch(SQLException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
		}
		
		return pws;
	}
	
	public void save(Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			stat.save(conn);
			pstmt = conn.prepareStatement("insert into player_week_stats (player_id, stat_id, year, week) values (?,?,?,?)");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, stat.getId());
			pstmt.setInt(3, year);
			pstmt.setInt(4, week);
			
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
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public void delete(Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			stat.delete(stat.getId(), conn);
			pstmt = conn.prepareStatement("delete from player_week_stats where player_id = ? and year = ? and week = ?");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, year);
			pstmt.setInt(3, week);
			
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
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public boolean exists(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from player_week_stats where player_id = ? and year = ? and week = ?");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, year);
			pstmt.setInt(3, week);
			rs = pstmt.executeQuery();
			
			return rs.next();
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
				rs.close();
			}
			catch(SQLException e) {	}
		}
		
		return false;
	}
	
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
}
