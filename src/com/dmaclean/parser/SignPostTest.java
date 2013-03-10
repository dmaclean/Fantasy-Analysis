package com.dmaclean.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.logging.Logger;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.basic.DefaultOAuthConsumer;

import org.json.JSONArray;
import org.json.JSONObject;

import com.dmaclean.models.Player;
import com.dmaclean.models.TeamMembership;


/**
 * Sample code to use Yahoo! Search BOSS
 * 
 * Please include the following libraries 
 * 1. Apache Log4j
 * 2. oAuth Signpost
 * 
 * @author xyz
 */
public class SignPostTest {

//	private static HashMap<String, String> yearCodeMap;
//	static {
//		yearCodeMap = new HashMap<String, String>();
//		yearCodeMap.put("2009", "223");
//	}
	
	private static final Logger logger = Logger.getLogger(SignPostTest.class.getPackage().getName());
// 714409
	
	/**
	 * Get all players in the league
	 */
//	protected static String yahooServer = "http://fantasysports.yahooapis.com/fantasy/v2/league/223.l.431/players;start={start}/stats?format=json";
	
	/**
	 * Get player stats for week 1 of 2009 season.
	 */
//	protected static String yahooServer = "http://fantasysports.yahooapis.com/fantasy/v2/player/223.p.5479/stats;type=week;week=1?format=json";
	protected static String yahooServer = "http://fantasysports.yahooapis.com/fantasy/v2/player/79.p.70/stats;type=week;week=1?format=json";
	
	/**
	 * Get NFL stat categories.
	 */
//	protected static String yahooServer = "http://fantasysports.yahooapis.com/fantasy/v2/game/nfl/stat_categories";
	
	
	// Please provide your consumer key here
	private static String consumer_key = "dj0yJmk9Y2RLeUNiTkdPeUxTJmQ9WVdrOVozZzRjSGhETTJNbWNHbzlOemt6TnpNMk1nLS0mcz1jb25zdW1lcnNlY3JldCZ4PWYy";

	// Please provide your consumer secret here
	private static String consumer_secret = "86712de54c4479c386538e80520269970a7db725";
	
	/** The HTTP request object used for the connection */
	private static StHttpRequest httpRequest = new StHttpRequest();
	
	/** Encode Format */
	private static final String ENCODE_FORMAT = "UTF-8";
	
	/** Call Type */
	private static final String callType = "web";
	
	private static final int HTTP_STATUS_OK = 200;

/**
 * 
 * @return
 */
	public int returnHttpData() throws UnsupportedEncodingException, Exception{
		if(this.isConsumerKeyExists() && this.isConsumerSecretExists()) {
		
			// Start with call Type
			//String params = callType;
			
			// Add query
			//params = params.concat("?q=");
			
			// Encode Query string before concatenating
			//params = params.concat(URLEncoder.encode(this.getSearchString(), "UTF-8"));
			
			// Create final URL
			String url = yahooServer;// + params;
			
			// Create oAuth Consumer 
			OAuthConsumer consumer = new DefaultOAuthConsumer(consumer_key, consumer_secret);
			
			// Set the HTTP request correctly
			httpRequest.setOAuthConsumer(consumer);
			
			try {
				url = url.replace("{start}", "0");
				
				logger.info("sending get request to" + URLDecoder.decode(url, ENCODE_FORMAT));
				int responseCode = httpRequest.sendGetRequest(url); 
				
				// Send the request
				if(responseCode == HTTP_STATUS_OK) {
					logger.info("Response ");
				} else {
					logger.severe("Error in response due to status code = " + responseCode);
				}
				logger.info(httpRequest.getResponseBody());
				
//				PlayerSeasonStats pss = new PlayerSeasonStats();
//				pss.parsePlayerStats(httpRequest.getResponseBody());
				
				if(1==1) return 1;
				
				JSONObject json = new JSONObject(httpRequest.getResponseBody());
				JSONObject fantasyContent = json.getJSONObject("fantasy_content");
				JSONArray league = fantasyContent.getJSONArray("league");
				JSONObject leagueInfo = league.getJSONObject(0);
				JSONObject players = league.getJSONObject(1);
				JSONObject players2 = players.getJSONObject("players");
				int count = players2.getInt("count");
				
				for(int i=0; i<count; i++) {
					JSONObject o = players2.getJSONObject("" + i);
					JSONArray a = o.getJSONArray("player");
//					JSONArray playerInfo = a.getJSONArray(0);
//					JSONObject playerStats = a.getJSONObject(1);
//					System.out.println();
					
					Player player = new Player();
					
					player.parsePlayerInfo(a);
					
				}
				
				System.out.println();
				
			} catch(UnsupportedEncodingException e) {
				logger.severe("Encoding/Decording error");
			} catch (IOException e) {
				logger.severe("Error with HTTP IO: " + e.getMessage());
			} catch (Exception e) {
				logger.severe(httpRequest.getResponseBody() + ": " + e.getMessage());
				return 0;
			}
		} else {
			logger.severe("Key/Secret does not exist");
		}
		return 1;
	}

private String getSearchString() {
return "Yahoo";
}

private boolean isConsumerKeyExists() {
if(consumer_key.isEmpty()) {
logger.severe("Consumer Key is missing. Please provide the key");
return false;
}
return true;
}

private boolean isConsumerSecretExists() {
if(consumer_secret.isEmpty()) {
logger.severe("Consumer Secret is missing. Please provide the key");
return false;
}
return true;
}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		BasicConfigurator.configure();
	
		try {
			SignPostTest signPostTest = new SignPostTest();
			
			signPostTest.returnHttpData();
			
			Connection conn = null;
			String url = "jdbc:mysql://localhost:3306/";
			String dbName = "fantasy_yahoo";
			String driver = "com.mysql.jdbc.Driver";
			String userName = "fantasy"; 
			String password = "fantasy";
			
			boolean autoCommit = false;
			  
			try {
				Class.forName(driver).newInstance();
				conn = DriverManager.getConnection(url+dbName,userName,password);
				
				autoCommit = conn.getAutoCommit();
				if(autoCommit) {
					conn.setAutoCommit(false);
				}
				logger.info("Connected to the database");
				
//				Player.retrieveAllPlayersForYear(2009, conn);
//				PlayerSeasonStats.retrieveSeasonStatsForAllPlayersForYear(2009, conn);
//				PlayerWeekStats.retrieveWeeklyStatsForAllPlayersForYear(2009, conn);
				
//				Player.retrieveAllPlayersForYear(2003, conn);
				
//				TeamMembership.retrieveTeamMemberships(conn, 2001);
				
//				conn.commit();
			} catch (Exception e) {
				logger.severe(e.getMessage());
			}
			finally {
				if(autoCommit)
					conn.setAutoCommit(true);
				conn.close();
				logger.info("Disconnected from database");
			}
		} catch (Exception e) {
			logger.info("Error - " + e.getMessage());
		}
	}

}