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
	
	/**
	 * The season we are parsing ADP for.
	 */
	private int season;
	
	/**
	 * A translation table between the team abbreviations in the input files and
	 * the abbreviation column in the teams table.  This helps us fetch the correct
	 * row so we can write the correct team_id value into team_memberships.
	 */
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
	
	public ADPParser() {}
	
	public ADPParser(int season) {
		this.season = season;
	}
	
	/**
	 * Processes the ArrayList we got from reading the input file.  This grabs the
	 * relevant data, performs some logic to get the correct player name and team
	 * abbreviation, then creates entries in the average_draft_positions and 
	 * team_memberships tables.
	 * 
	 * @param results		The ArrayList produced from parse().
	 * @param conn			Database connection
	 */
	public void processMyFantasyLeagueData(ArrayList<String[]> results, Connection conn) {
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
			if(line.length == 9 && line[4].equals("Def")) {
				name = line[2];
			}
			else if(line.length == 10 && line[5].equals("Def")) {
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
	
	/**
	 * Insert a row in the average_draft_positions table for the currently-processed player.
	 * 
	 * @param conn			Database connection
	 * @param playerId		The player id (from players table).
	 * @param season		The season we are processing for.
	 * @param adp			The ADP value for this player.
	 */
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
	
	/**
	 * Insert a row in the team_memberships table.  If the team is null (will happen when
	 * a player is listed as a free agent (FA) or some other designation that is not a
	 * valid team) then we will log this and return immediately without writing to the
	 * database.
	 * 
	 * @param conn			Database connection.
	 * @param playerId		The player id (from players table).
	 * @param season		The season we are processing for.
	 * @param team			The team this player belongs to.
	 */
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
	
	/**
	 * Get the id of the row from teams table based on which abbreviation our specified
	 * "team" argument matches.
	 * 
	 * @param conn		Database connection.
	 * @param team		The abbreviation of the team we want the id for.
	 * @return			The primary key of a matching row if one is found.  -1 otherwise.
	 * @throws Exception
	 */
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
	 * @param conn		Database connection.
	 * @param name		The name to look for.
	 * @return			An Object array containing the values { <player id>, <player name>, <player position> }
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
	
	/**
	 * Reads in the input file, parses each line out into String arrays, and adds each to
	 * an ArrayList that will be used in further processing.
	 * 
	 * @param filename		The file (including path) to parse.
	 * @return				An ArrayList containing String[] for each line.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
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
		}
		
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
			parser.processMyFantasyLeagueData(results, conn);
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
