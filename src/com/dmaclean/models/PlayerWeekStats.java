package com.dmaclean.models;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
	
	public static void retrieveWeeklyStatsForAllPlayersForYear(int year, Connection conn) {
		ArrayList<Integer> ids = Player.getAllPlayerIds(conn);
		
		StHttpRequest httpRequest = new StHttpRequest();
		
		String yahooServer = "http://fantasysports.yahooapis.com/fantasy/v2/player/{year}.p.{player_id}/stats;type=week;week={week}?format=json";
		// Create final URL
		String url = yahooServer;// + params;
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		ArrayList<ArrayList<Integer>> idsWeeks = new ArrayList<ArrayList<Integer>>();
		for(int id=0; id<ids.size(); id++) {
			if(ids.get(id)<8835) continue;
			for(int week=1; week<18; week++) {
				ArrayList<Integer> l = new ArrayList<Integer>(2);
				l.add(ids.get(id));
				l.add(week);
				idsWeeks.add(l);
			}
		}
		
		try {
//			for(Integer id : ids) {
//				for(int week=1; week<18; week++) {
			while(!idsWeeks.isEmpty()) {
				int id = idsWeeks.get(0).get(0);
				int week = idsWeeks.get(0).get(1);
				
					url = yahooServer.replace("{player_id}", "" + id);
					url = url.replace("{year}", FantasyConstants.yearToCode.get(year));
					url = url.replace("{week}", "" + week);
					
					logger.info("sending get request to" + URLDecoder.decode(url, "UTF-8"));
					int responseCode = httpRequest.sendGetRequest(url); 
					
					// Send the request
					if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
						logger.info("Response ");
						
						PlayerWeekStats pws = new PlayerWeekStats();
						pws.setPlayerId(id);
						pws.setYear(year);
						pws.setWeek(week);
						pws.parsePlayerStats(httpRequest.getResponseBody());
						pws.save(conn);
						
						idsWeeks.remove(0);
						
						try {
							Thread.sleep(1000*2);
						}
						catch(InterruptedException e) {
							e.printStackTrace();
						}
					}
					else if(responseCode == 999) {
						logger.severe("Error in response due to status code = " + responseCode + ".  Sleeping for 10 minutes.");
						
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
//				}
			}
		} catch(UnsupportedEncodingException e) {
			logger.severe("Encoding/Decording error");
		} catch (IOException e) {
			logger.severe("Error with HTTP IO - " + e.getMessage());
		} catch (Exception e) {
			logger.severe(httpRequest.getResponseBody() + " - " + e.getMessage());
		}
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
	
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
}
