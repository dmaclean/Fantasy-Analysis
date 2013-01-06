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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dmaclean.constants.FantasyConstants;
import com.dmaclean.parser.StHttpRequest;

public class PlayerSeasonStats {
	
	private static final Logger logger = Logger.getLogger(PlayerSeasonStats.class.getPackage().getName());

	protected int playerId;
	protected int year;
	protected Stat stat;
	
	public PlayerSeasonStats() {
		stat = new Stat();
	}
	
	public void parsePlayerStats(String json) throws JSONException {
		JSONObject root = new JSONObject(json);
		JSONObject fc = root.getJSONObject("fantasy_content");
		JSONArray player = fc.getJSONArray("player");
		JSONObject stats = player.getJSONObject(1);
		JSONArray statsArray = stats.getJSONObject("player_stats").getJSONArray("stats");
		
		for(int i=0; i<statsArray.length(); i++) {
			try { statsArray.getJSONObject(i); } catch(JSONException e) { break; }
			
			JSONObject stat = statsArray.getJSONObject(i).getJSONObject("stat");
			int statId = stat.getInt("stat_id");
			int value = -1;
			try {
				value = stat.getInt("value");
			}
			catch(JSONException e) {
				logger.severe(e.getMessage());
				value = (int) stat.getDouble("value");
			}
			
			this.stat.addValue(statId, value);
		}
		
		System.out.println();
	}
	
	public static void retrieveSeasonStats(int year, int id, Connection conn) {
		PlayerSeasonStats pss = new PlayerSeasonStats();
		pss.setPlayerId(id);
		pss.setYear(year);
		
		/*
		 * Sanity check - do we already have this?
		 */
		if(pss.exists(conn)) {
			logger.info("Season stats (" + year + ") for player " + id + " already exist.");
			return;
		}
		
		StHttpRequest httpRequest = new StHttpRequest();
		
		// Create final URL
		String url = FantasyConstants.URL_SEASON_STATS;
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		try {
			url = url.replace("{player_id}", "" + id);
			url = url.replace("{year}", FantasyConstants.yearToCode.get(year));
			
			int responseCode = -1;
			
			while(responseCode != FantasyConstants.HTTP_RESPONSE_OK) {
				logger.info("sending get request to" + URLDecoder.decode(url, "UTF-8"));
				responseCode = httpRequest.sendGetRequest(url); 
				
				// Send the request
				if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
					logger.info("Response ");
					
					pss.parsePlayerStats(httpRequest.getResponseBody());
					pss.save(conn);
				} else if(responseCode == 999) {
					logger.severe("Error in response due to status code = " + responseCode + ".  Sleeping for 30 minutes.");
					try {
						Thread.sleep(1000*60*30);
					}
					catch(InterruptedException e) {
						logger.severe(e.getMessage());
						e.printStackTrace();
					}
				} else {
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
	
	public static void retrieveSeasonStatsForAllPlayersForYear(int year, Connection conn) {
		ArrayList<Integer> ids = Player.getAllPlayerIds(conn);

		for(Integer id : ids) {
			retrieveSeasonStats(year, id, conn);
		}
	}
	
	public void save(Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			stat.save(conn);
			pstmt = conn.prepareStatement("insert into player_season_stats (player_id, stat_id, year) values (?,?,?)");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, stat.getId());
			pstmt.setInt(3, year);
			
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
			pstmt = conn.prepareStatement("select * from player_season_stats where player_id = ? and year = ?");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, year);
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

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public Stat getStat() {
		return stat;
	}

	public void setStat(Stat stat) {
		this.stat = stat;
	}
}
