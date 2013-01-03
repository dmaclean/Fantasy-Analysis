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

public class Player {
	private static final Logger logger = Logger.getLogger(Player.class.getPackage().getName());
	
	private JSONArray player;
	
	private int playerId;
	private String name;
	private String position;
	
	public Player(JSONArray player) {
		this.player = player;
	}
	
	public void parsePlayerInfo() throws JSONException {
		try {
			JSONArray playerInfo = player.getJSONArray(0);
			
			for(int i=0; i<playerInfo.length(); i++) {
				try { playerInfo.getJSONObject(i); } catch(JSONException e) { break; }
				
				if(playerInfo.getJSONObject(i).has("player_id"))
					playerId = playerInfo.getJSONObject(i).getInt("player_id");
				else if(playerInfo.getJSONObject(i).has("name"))
					name = playerInfo.getJSONObject(i).getJSONObject("name").getString("full");
				else if(playerInfo.getJSONObject(i).has("display_position"))
					position = playerInfo.getJSONObject(i).getString("display_position");
			}
			
			logger.info("Fetched " + name + " (" + position + " - " + playerId + ")");
		}
		catch(JSONException e) {
			logger.severe("Failed to fetch " + name + " (" + position + " - " + playerId + ")");
			throw new JSONException(e);
		}
	}
	
	public void parsePlayerSeasonStats() throws JSONException {
		JSONObject playerStats = player.getJSONObject(1);
		
	}
	
	public static void retrieveAllPlayersForYear(int year, Connection conn) {
		StHttpRequest httpRequest = new StHttpRequest();
		
		String yahooServer = "http://fantasysports.yahooapis.com/fantasy/v2/league/223.l.431/players;start={start}/stats?format=json";
		// Create final URL
		String url = yahooServer;// + params;
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		int count = -1;
		int start = 0;
		
		try {
			do {
				start = (count == -1) ? 0 : start + count;
				url = yahooServer.replace("{start}", "" + start);
				
				logger.info("sending get request to" + URLDecoder.decode(url, "UTF-8"));
				int responseCode = httpRequest.sendGetRequest(url); 
				
				// Send the request
				if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
					logger.info("Response ");
				} else {
					logger.severe("Error in response due to status code = " + responseCode);
				}
				logger.info(httpRequest.getResponseBody());
				
				JSONObject json = new JSONObject(httpRequest.getResponseBody());
				JSONObject fantasyContent = json.getJSONObject("fantasy_content");
				JSONArray league = fantasyContent.getJSONArray("league");
				JSONObject leagueInfo = league.getJSONObject(0);
				JSONObject players = league.getJSONObject(1);
				JSONObject players2 = players.getJSONObject("players");
				count = players2.getInt("count");
				
				for(int i=0; i<count; i++) {
					JSONObject o = players2.getJSONObject("" + i);
					JSONArray a = o.getJSONArray("player");
	//				JSONArray playerInfo = a.getJSONArray(0);
	//				JSONObject playerStats = a.getJSONObject(1);
	//				System.out.println();
					
					Player player = new Player(a);
					player.parsePlayerInfo();
					player.save(conn);
	//				player.parsePlayerSeasonStats();
				}
				
				System.out.println();
				
			} while(count == 25);
		} catch(UnsupportedEncodingException e) {
			logger.severe("Encoding/Decording error");
		} catch (IOException e) {
			logger.severe("Error with HTTP IO - " + e.getMessage());
		} catch (Exception e) {
			logger.severe(httpRequest.getResponseBody() + " - " + e.getMessage());
		}
	}
	
	/**
	 * Write the player record to the database.
	 * 
	 * @param conn
	 */
	public void save(Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("insert into players (id, name, position) values (?,?,?)");
			pstmt.setInt(1, playerId);
			pstmt.setString(2, name);
			pstmt.setString(3, position);
			
			pstmt.execute();
		} 
		catch (SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			} 
			catch (SQLException e) {
				logger.severe(e.getMessage());
				e.printStackTrace();
			}
		}
	}
	
	public static ArrayList<Integer> getAllPlayerIds(Connection conn) {
		long start = System.currentTimeMillis();
		
		ArrayList<Integer> ids = new ArrayList<Integer>();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select id from players");
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				ids.add(rs.getInt(1));
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
		
		long end = System.currentTimeMillis();
		logger.info("getAllPlayerIds took " + (end-start)/1000.0 + " seconds.");
		
		return ids;
	}

	public JSONArray getPlayer() {
		return player;
	}

	public void setPlayer(JSONArray player) {
		this.player = player;
	}
}
