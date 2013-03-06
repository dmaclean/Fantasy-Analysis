package com.traderapist.adp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class ADPParser {

	private static final Logger logger = Logger.getLogger(ADPParser.class.getPackage().getName());
	
//	public static final String TYPE_MY_FANTASY_LEAGUE = "MY_FANTASY_LEAGUE";
	
//	public static final String TYPE_FANTASY_FOOTBALL_CALCULATOR = "FANTASY_FOOTBALL_CALCULATOR";
	
//	private String type;
	
	private int season;
	
	private static HashMap<String, String> teamTranslation = new HashMap<String, String>();
	static {
		teamTranslation.put("ATL", "ATL");
		teamTranslation.put("BUF", "BUF");
		teamTranslation.put("CHI", "CHI");
		teamTranslation.put("CIN", "CIN");
		teamTranslation.put("CLE", "CLE");
		teamTranslation.put("DAL", "DAL");
		teamTranslation.put("DEN", "DEN");
		teamTranslation.put("DET", "DET");
		teamTranslation.put("GBP", "GB");
		teamTranslation.put("TEN", "TEN");
		teamTranslation.put("IND", "IND");
		teamTranslation.put("KCC", "KC");
		teamTranslation.put("OAK", "OAK");
		teamTranslation.put("STL", "STL");
		teamTranslation.put("MIA", "MIA");
		teamTranslation.put("MIN", "MIN");
		teamTranslation.put("NEP", "NE");
		teamTranslation.put("NOS", "NO");
		teamTranslation.put("NYG", "NYG");
		teamTranslation.put("NYJ", "NYJ");
		teamTranslation.put("PHI", "PHI");
		teamTranslation.put("ARI", "ARI");
		teamTranslation.put("PIT", "PIT");
		teamTranslation.put("SDC", "SD");
		teamTranslation.put("SFO", "SF");
		teamTranslation.put("SEA", "SEA");
		teamTranslation.put("TBB", "TB");
		teamTranslation.put("WAS", "WAS");
		teamTranslation.put("CAR", "CAR");
		teamTranslation.put("JAC", "JAC");
		teamTranslation.put("BAL", "BAL");
		teamTranslation.put("HOU", "HOU");
		
	}
	
//	private final String myFantasyLeagueTeamNameRegex = 
//										 "Falcons|Bills|Bears|Bengals|Browns|Cowboys|Broncos|Lions|Packers|" +
//										 "Titans|Colts|Chiefs|Raiders|Rams|Dolphins|Vikings|Patriots|Saints|" +
//										 "Giants|Eagles|Cardinals|Steelers|Chargers|49ers|Seahawks|Buccaneers|" +
//										 "Redskins|Panthers|Jaguars|Ravens|Texans";
	
//	private final String fantasyFootballCalculatorTeamNameRegex = 
//							 "Falcons|Bills|Bears|Bengals|Browns|Cowboys|Broncos|Lions|Packers|" +
//							 "Titans|Colts|Chiefs|Raiders|Rams|Dolphins|Vikings|Patriots|Saints|" +
//							 "Giants|Eagles|Cardinals|Steelers|Chargers|49ers|Seahawks|Buccaneers|" +
//							 "Redskins|Panthers|Jaguars|Ravens|Texans";
	
	public ADPParser() {}
	
	public ADPParser(int season) {
		this.season = season;
	}
	
	public void writeToDatabase(Connection conn, ArrayList<String[]> results) {
//		if(results.get(0)[0].equals(TYPE_MY_FANTASY_LEAGUE)) {
			processMyFantasyLeagueData(results, conn);
//		}
//		else if(results.get(0)[0].equals(TYPE_FANTASY_FOOTBALL_CALCULATOR)) {
//			processFantasyFootballCalculatorData(results, conn);
//		}
	}

	private void processMyFantasyLeagueData(ArrayList<String[]> results, Connection conn) {
//		Pattern p = Pattern.compile(myFantasyLeagueTeamNameRegex);
		
		for(String[] line : results) {
			if(line[0].matches("#")) {
				continue;
			}
			String name = line[2] + " " + line[1].replace(",", "");
			String team = line[3];
			Double adp = Double.MIN_VALUE;
			try {
				adp = Double.parseDouble(line[5]);
			}
			catch(NumberFormatException e) {
				logger.severe(e.getMessage());
			}
			
			/*
			 * Check if we're dealing with a team instead of a player.
			 */
//			Matcher m = p.matcher(name);
			if(/*m.find() &&*/ line.length == 9 && line[4].equals("Def")) {
				name = line[2];
			}
			else if(/*m.find() &&*/ line.length == 10 && line[5].equals("Def")) {
				name = line[2] + " " + line[3];
				team = line[4];
				adp = Double.parseDouble(line[6]);
			}
			
			Object[] playerInfo = getPlayerInfo(conn, name);
			if(playerInfo != null) {
				insertAverageDraftPosition(conn, (Integer) playerInfo[0], season, adp);
				insertTeamMembership(conn, (Integer) playerInfo[0], season, getTranslatedAbbreviation(team));
			}
		}
	}
	
//	private void processFantasyFootballCalculatorData(ArrayList<String[]> results, Connection conn) {
//		Pattern p = Pattern.compile(fantasyFootballCalculatorTeamNameRegex);
//		
//		for(String[] line : results) {
//			if(line[0].matches(TYPE_FANTASY_FOOTBALL_CALCULATOR + "|rank")) {
//				continue;
//			}
//			
//			String name = line[2] + " " + line[3];
//			String team = line[4];
//			
//			/*
//			 * Check if we're dealing with a team instead of a player.
//			 */
//			Matcher m = p.matcher(name);
//			if(m.find() && line.length == 10) {
//				name = line[2];
//			}
//			else if(m.find() && line.length == 11) {
//				name = line[2] + " " + line[3];
//				team = line[5];
//			}
//			
//			Object playerInfo = getPlayerInfo(conn, name);
//			if(playerInfo != null) {
//				
//			}
//		}
//	}
	
	private void insertAverageDraftPosition(Connection conn, int playerId, int season, double adp) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("insert into average_draft_positions (player_id, season, adp) values (?,?,?)");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, season);
			pstmt.setDouble(3, adp);
			
			pstmt.execute();
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void insertTeamMembership(Connection conn, int playerId, int season, String team) {
		if(team == null) {
			logger.severe("Could not find a team association for player " + playerId);
			return;
		}
		
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("insert into team_memberships (player_id, season, team_id) values (?,?,?)");
			pstmt.setInt(1, playerId);
			pstmt.setInt(2, season);
			pstmt.setInt(3, getTeamId(conn, team));
			
			pstmt.execute();
		}
		catch(Exception e) {
			logger.severe("Failed to insert team membership for player " + playerId);
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private int getTeamId(Connection conn, String team) throws Exception {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		int id = -1;
		
		try {
			pstmt = conn.prepareStatement("select id from teams where abbreviation = ?");
			pstmt.setString(1, team);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				id = rs.getInt(1);
			}
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return id;
	}
	
	/**
	 * Retrieve a player's information from the players table.
	 * 
	 * @param conn
	 * @param name
	 * @return
	 */
	private Object[] getPlayerInfo(Connection conn, String name) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from players where name = ?");
			pstmt.setString(1, name);
			
			logger.info("Querying for " + name);
			
			rs = pstmt.executeQuery();
			if(rs.next()) {
				logger.info("Found (" + rs.getInt("id") + ") " + rs.getString("name") + "/" + rs.getString("position") + "\n\n");
				
				int id = rs.getInt("id");
				String playerName = rs.getString("name");
				String position = rs.getString("position");
				
				Object[] o = {id, playerName, position};
				return o;
			}
		
		}
		catch(SQLException e) {
			e.printStackTrace();
		}
		finally {
			try {
				rs.close();
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public String getTranslatedAbbreviation(String myFantasyLeagueAbbreviation) {
		return teamTranslation.get(myFantasyLeagueAbbreviation);
	}
	
	public ArrayList<String[]> parse(String filename) throws FileNotFoundException, IOException {
		File f = new File(filename);
		BufferedReader reader = new BufferedReader(new FileReader(f));
		
		ArrayList<String> contents = new ArrayList<String>();
		String s;
		while((s = reader.readLine()) != null) {
			contents.add(s.replace(",", ""));
		}
		
		ArrayList<String[]> results = new ArrayList<String[]>();
		for(String line : contents) {
			results.add(line.split("\\s"));
//			String name = pieces[2] + " " + pieces[1].replace(",", "");
		}
		
//		type = results.get(0)[0];
		
		return results;
	}
	
	public static void main(String[] args) {
		ADPParser parser = new ADPParser(2001);
		
		Connection conn = null;
		
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "fantasy_yahoo";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "fantasy"; 
		String password = "fantasy";
		
		try {
			Class.forName(driver).newInstance();
			conn = DriverManager.getConnection(url+dbName,userName,password);
			
			ArrayList<String[]> results = parser.parse(args[0]);
			parser.writeToDatabase(conn, results);
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			if(conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public int getSeason() {
		return season;
	}

	public void setSeason(int season) {
		this.season = season;
	}
}
