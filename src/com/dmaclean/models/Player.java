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
	
//	private JSONArray player;
	
	private int playerId;
	private String name;
	private String position;
	
	private ArrayList<Stat> weekStats;
	private ArrayList<Stat> seasonStats;
	
	public Player() {
		weekStats = new ArrayList<Stat>();
		seasonStats = new ArrayList<Stat>();
	}
	
//	public Player(JSONArray player) {
//		this.player = player;
//	}
	
	public void parsePlayerInfo(JSONArray player) throws JSONException {
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
	
	public static void retrieveAllPlayersForYear(int year, Connection conn) {
		StHttpRequest httpRequest = new StHttpRequest();
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		int count = -1;
		int start = 0;
		
		try {
			do {
				start = (count == -1) ? 0 : start + count;
				// Create final URL
				String url = FantasyConstants.URL_SEASON_ALL_PLAYERS;
				url = url.replace("{start}", "" + start);
				url = url.replace("{league_key}", FantasyConstants.yearToLeagueKey.get(year));
				
				int responseCode = -1;
				while(responseCode != FantasyConstants.HTTP_RESPONSE_OK) {
					logger.info("sending get request to " + URLDecoder.decode(url, "UTF-8"));
					responseCode = httpRequest.sendGetRequest(url); 
					
					// Send the request
					if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
						logger.info("Response - " + httpRequest.getResponseBody());
					} else if(responseCode == 999) {
						logger.severe("Error in response due to status code = " + responseCode + ".  Sleeping 10 minutes");
						try {
							Thread.sleep(FantasyConstants.ERROR_999_TIMEOUT_MILLISECONDS);
						}
						catch(InterruptedException e) {
							logger.severe(e.getMessage());
							e.printStackTrace();
						}
					} else {
						logger.severe("Error in response due to status code = " + responseCode);
					}
				}
				
//				JSONObject json = new JSONObject(httpRequest.getResponseBody());
//				JSONObject fantasyContent = json.getJSONObject("fantasy_content");
//				JSONArray league = fantasyContent.getJSONArray("league");
//				JSONObject players = league.getJSONObject(1);
//				JSONObject players2 = players.getJSONObject("players");
				JSONObject players = Player.parsePlayersList(httpRequest.getResponseBody());
				count = players.getInt("count");
				
				for(int i=0; i<count; i++) {
					JSONObject o = players.getJSONObject("" + i);
					JSONArray a = o.getJSONArray("player");
					
					Player player = new Player();
					player.parsePlayerInfo(a);
					
					if(!player.exists(conn)) {
						player.save(conn);
					}
					else {
						logger.info(player.getName() + " already exists in the database, skipping...");
					}
						
					player.retrieveSeasonStats(year, conn);
					player.retrieveWeeklyStats(year, conn);
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
	
	public void parsePlayerStats(Connection conn, String json, int year, int week) throws JSONException {
		JSONObject root = new JSONObject(json);
		JSONObject fc = root.getJSONObject("fantasy_content");
		JSONArray player = fc.getJSONArray("player");
		JSONObject stats = player.getJSONObject(1);
		JSONArray statsArray = stats.getJSONObject("player_stats").getJSONArray("stats");
		
		for(int i=0; i<statsArray.length(); i++) {
			try { statsArray.getJSONObject(i); } catch(JSONException e) { break; }
			
			Stat currStat = new Stat();
			currStat.setPlayerId(playerId);
			currStat.setSeason(year);
			currStat.setWeek(week);
			
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
			
			currStat.setStatKey(statId);
			currStat.setStatValue(value);
			
			/*
			 * Make sure we haven't already processed this stat.
			 */
			if(currStat.exists(conn)) {
				logger.info("Season stats for " + year + " for player " + playerId + " aleady exist.");
				continue;
			}
			
			currStat.save(conn);
			
			if(week == -1)
				seasonStats.add(currStat);
			else
				weekStats.add(currStat);
		}
		
		System.out.println();
	}
	
	public void retrieveSeasonStats(int year, Connection conn) {
//		Stat seasonStat = new Stat();
//		seasonStat.setPlayerId(playerId);
//		seasonStat.setSeason(year);
//		seasonStat.setWeek(-1);
		
		/*
		 * Sanity check - do we already have this?
		 */
//		if(seasonStat.exists(conn)) {
//			logger.info("Season stats (" + year + ") for player " + id + " already exist.");
//			return;
//		}
		
		StHttpRequest httpRequest = new StHttpRequest();
		
		// Create final URL
		String url = FantasyConstants.URL_SEASON_STATS;
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		try {
			url = url.replace("{player_id}", "" + playerId);
			url = url.replace("{year}", FantasyConstants.yearToCode.get(year));
			
			int responseCode = -1;
			
			while(responseCode != FantasyConstants.HTTP_RESPONSE_OK) {
				logger.info("sending get request to" + URLDecoder.decode(url, "UTF-8"));
				responseCode = httpRequest.sendGetRequest(url); 
				
				// Send the request
				if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
					logger.info("Response ");
					
					parsePlayerStats(conn, httpRequest.getResponseBody(), year, -1);
				} else if(responseCode == 999) {
					logger.severe("Error in response due to status code = " + responseCode + ".  Sleeping for 10 minutes.");
					try {
						Thread.sleep(FantasyConstants.ERROR_999_TIMEOUT_MILLISECONDS);
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
	
//	public void retrieveSeasonStatsForAllPlayersForYear(int year, Connection conn) {
//		ArrayList<Integer> ids = Player.getAllPlayerIds(conn);
//
//		for(Integer id : ids) {
//			Player p = new Player();
//			p
//			retrieveSeasonStats(year, conn);
//		}
//	}
	
	public void retrieveWeeklyStats(int year, Connection conn) {
		StHttpRequest httpRequest = new StHttpRequest();
		
		// Create oAuth Consumer 
		OAuthConsumer consumer = new DefaultOAuthConsumer(FantasyConstants.consumer_key, FantasyConstants.consumer_secret);
		
		// Set the HTTP request correctly
		httpRequest.setOAuthConsumer(consumer);
		
		try {
			int week = 1;
			while(week<18) {
//				PlayerWeekStats pws = new PlayerWeekStats();
//				pws.setPlayerId(id);
//				pws.setYear(year);
//				pws.setWeek(week);
				
				Stat stat = new Stat();
				stat.setPlayerId(playerId);
				stat.setSeason(year);
				stat.setWeek(week);
				
				/*
				 * Sanity check.  Do we need this entry?
				 */
				if(stat.exists(conn)) {
					logger.info("Week " + week + " stats (" + year + ") for player " + playerId + " already exist.");
					week++;
					continue;
				}
				
				// Create final URL
				String url = FantasyConstants.URL_WEEK_STATS;
				url = url.replace("{player_id}", "" + playerId);
				url = url.replace("{year}", FantasyConstants.yearToCode.get(year));
				url = url.replace("{week}", "" + week);
				
				logger.info("sending get request to" + URLDecoder.decode(url, "UTF-8"));
				int responseCode = httpRequest.sendGetRequest(url); 
				
				// Send the request
				if(responseCode == FantasyConstants.HTTP_RESPONSE_OK) {
					parsePlayerStats(conn, httpRequest.getResponseBody(), year, week);
					
					week++;
					
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
						Thread.sleep(FantasyConstants.ERROR_999_TIMEOUT_MILLISECONDS);
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
	
//	public void retrieveWeeklyStatsForAllPlayersForYear(int year, Connection conn) {
//		ArrayList<Integer> ids = Player.getAllPlayerIds(conn);
//
//		for(Integer id : ids) {
//			retrieveWeeklyStats(year, id, conn);
//		}
//	}
	
	public static JSONObject parsePlayersList(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			JSONObject fantasyContent = jsonObject.getJSONObject("fantasy_content");
			JSONArray league = fantasyContent.getJSONArray("league");
			JSONObject players = league.getJSONObject(1);
			JSONObject players2 = players.getJSONObject("players");
			
			return players2;
		}
		catch(JSONException e) {
			e.printStackTrace();
		}
		
		return null;
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
	
	/**
	 * Determines if the player already exists in the database.
	 * 
	 * @param conn
	 * @return
	 */
	public boolean exists(Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from players where id = ?");
			pstmt.setInt(1, playerId);
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

//	public JSONArray getPlayer() {
//		return player;
//	}
//
//	public void setPlayer(JSONArray player) {
//		this.player = player;
//	}

	public int getPlayerId() {
		return playerId;
	}

	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public ArrayList<Stat> getWeekStats() {
		return weekStats;
	}

	public void setWeekStats(ArrayList<Stat> weekStats) {
		this.weekStats = weekStats;
	}

	public ArrayList<Stat> getSeasonStats() {
		return seasonStats;
	}

	public void setSeasonStats(ArrayList<Stat> seasonStats) {
		this.seasonStats = seasonStats;
	}
}
