package com.traderapist.adp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmaclean.models.Player;
import com.dmaclean.testutils.FantasyTestUtils;

public class TestADPParser {

	private ADPParser parser;
	
	private final String testDataPath = "test_data/adp/";
	
	@Before
	public void setUp() throws Exception {
		parser = new ADPParser();
		FantasyTestUtils.resetTestDatabase();
	}

	@After
	public void tearDown() throws Exception {
		parser = null;
	}

	@Test
	public void testParse_MyFantasyLeague_2001() {
		parser.setSeason(2001);
		
		Connection conn = null;
		try {
			conn = FantasyTestUtils.getTestConnection();
			ArrayList<String[]> results = parser.parse(testDataPath + "adp_2001_myfantasyleague.txt");
			
			// First player
			//1.	Faulk, Marshall STL RB	2.07	1	15	449
			assertTrue(results.get(1)[0].equals("1.") &&
					results.get(1)[1].equals("Faulk") &&
					results.get(1)[2].equals("Marshall") &&
					results.get(1)[3].equals("STL") &&
					results.get(1)[4].equals("RB") &&
					results.get(1)[5].equals("2.07") &&
					results.get(1)[6].equals("1") &&
					results.get(1)[7].equals("15") &&
					results.get(1)[8].equals("449"));
			
			// Player without team
			//27.	Carter, Cris FA WR	33.02	10	252	442
			assertTrue(results.get(27)[0].equals("27.") &&
					results.get(27)[1].equals("Carter") &&
					results.get(27)[2].equals("Cris") &&
					results.get(27)[3].equals("FA") &&
					results.get(27)[4].equals("WR") &&
					results.get(27)[5].equals("33.02") &&
					results.get(27)[6].equals("10") &&
					results.get(27)[7].equals("252") &&
					results.get(27)[8].equals("442"));
			
			// Team
			//63.	Buccaneers, Tampa Bay TBB Def	65.95	1	145	132
			assertTrue(results.get(63)[0].equals("63.") &&
					results.get(63)[1].equals("Buccaneers") &&
					results.get(63)[2].equals("Tampa") &&
					results.get(63)[3].equals("Bay") &&
					results.get(63)[4].equals("TBB") &&
					results.get(63)[5].equals("Def") &&
					results.get(63)[6].equals("65.95") &&
					results.get(63)[7].equals("1") &&
					results.get(63)[8].equals("145") &&
					results.get(63)[9].equals("132"));
			
			// Last player
			//356.	Green, Mike TEN RB	221.42	124	530	33
			assertTrue(results.get(356)[0].equals("356.") &&
					results.get(356)[1].equals("Green") &&
					results.get(356)[2].equals("Mike") &&
					results.get(356)[3].equals("TEN") &&
					results.get(356)[4].equals("RB") &&
					results.get(356)[5].equals("221.42") &&
					results.get(356)[6].equals("124") &&
					results.get(356)[7].equals("530") &&
					results.get(356)[8].equals("33"));
			
			/*
			 * Create the Player objects for each player we're testing.
			 */
			Player faulk = new Player();
			faulk.setName("Marshall Faulk");
			faulk.setPlayerId(1);
			faulk.setPosition("RB");
			faulk.save(conn);
			
			Player carter = new Player();
			carter.setName("Cris Carter");
			carter.setPlayerId(4);
			carter.setPosition("WR");
			carter.save(conn);
			
			Player tampa = new Player();
			tampa.setName("Tampa Bay");
			tampa.setPlayerId(3);
			tampa.setPosition("DEF");
			tampa.save(conn);
			
			Player green = new Player();
			green.setName("Mike Green");
			green.setPlayerId(2);
			green.setPosition("RB");
			green.save(conn);
			
			parser.processMyFantasyLeagueData(results, conn);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			String sql = "select * from average_draft_positions adp left outer join team_memberships t on adp.player_id = t.player_id where adp.player_id = ?";
			
			try {
				/*
				 * Marshall Faulk
				 */
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, faulk.getPlayerId());
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					int adpPlayerId = rs.getInt("adp.player_id");
					int adpSeason = rs.getInt("adp.season");
					double adp = rs.getDouble("adp.adp");
					int tmPlayerId = rs.getInt("t.player_id");
					int tmSeason = rs.getInt("t.season");
					int tmTeam = rs.getInt("t.team_id");
					
					assertTrue(adpPlayerId == faulk.getPlayerId());
					assertTrue(adpSeason == 2001);
					assertTrue(adp == 2.07);
					assertTrue(tmPlayerId == faulk.getPlayerId());
					assertTrue(tmSeason == 2001);
					assertTrue(tmTeam == 14);
				}
				else {
					fail("Unable to find ADP information for Marshall Faulk.");
				}
				
				/*
				 * Cris Carter
				 */
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, carter.getPlayerId());
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					int adpPlayerId = rs.getInt("adp.player_id");
					int adpSeason = rs.getInt("adp.season");
					double adp = rs.getDouble("adp.adp");
					int tmPlayerId = rs.getInt("t.player_id");
					int tmSeason = rs.getInt("t.season");
					int tmTeam = rs.getInt("t.team_id");
					
					assertTrue(adpPlayerId == carter.getPlayerId());
					assertTrue(adpSeason == 2001);
					assertTrue(adp == 33.02);
					assertTrue(tmPlayerId == 0);
					assertTrue(tmSeason == 0);
					assertTrue(tmTeam == 0);
				}
				else {
					fail("Unable to find ADP information for Cris Carter.");
				}
				
				/*
				 * Tampa Bay
				 */
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, tampa.getPlayerId());
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					int adpPlayerId = rs.getInt("adp.player_id");
					int adpSeason = rs.getInt("adp.season");
					double adp = rs.getDouble("adp.adp");
					int tmPlayerId = rs.getInt("t.player_id");
					int tmSeason = rs.getInt("t.season");
					int tmTeam = rs.getInt("t.team_id");
					
					assertTrue(adpPlayerId == tampa.getPlayerId());
					assertTrue(adpSeason == 2001);
					assertTrue(adp == 70.82);
					assertTrue(tmPlayerId == tampa.getPlayerId());
					assertTrue(tmSeason == 2001);
					assertTrue(tmTeam == 27);
				}
				else {
					fail("Unable to find ADP information for Tampa Bay.");
				}
				
				pstmt.close();
				rs.close();
				
				/*
				 * Mike Green
				 */
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, green.getPlayerId());
				rs = pstmt.executeQuery();
				
				if(rs.next()) {
					int adpPlayerId = rs.getInt("adp.player_id");
					int adpSeason = rs.getInt("adp.season");
					double adp = rs.getDouble("adp.adp");
					int tmPlayerId = rs.getInt("t.player_id");
					int tmSeason = rs.getInt("t.season");
					int tmTeam = rs.getInt("t.team_id");
					
					assertTrue(adpPlayerId == green.getPlayerId());
					assertTrue(adpSeason == 2001);
					assertTrue(adp == 221.42);
					assertTrue(tmPlayerId == green.getPlayerId());
					assertTrue(tmSeason == 2001);
					assertTrue(tmTeam == 10);
				}
				else {
					fail("Unable to find ADP information for Mike Green.");
				}
			}
			catch(SQLException e ) {
				fail(e.getMessage());
			}
			finally {
				try {
					pstmt.close();
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		catch(IOException e) {
			fail(e.getMessage());
		}
		finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testTeamTranslation() {
		String[] value = {
				"ATL", "BUF", "CHI", "CIN", "CLE", "DAL", "DEN", "DET", "GBP", "TEN",
				"IND", "KCC", "OAK", "STL", "MIA", "MIN", "NEP", "NOS", "NYG", "NYJ",
				"PHI", "ARI", "PIT", "SDC", "SFO", "SEA", "TBB", "WAS", "CAR", "JAC",
				"BAL", "HOU"
		};
		String[] result = {
				"ATL", "BUF", "CHI", "CIN", "CLE", "DAL", "DEN", "DET", "GB", "TEN",
				"IND", "KC", "OAK", "STL", "MIA", "MIN", "NE", "NO", "NYG", "NYJ",
				"PHI", "ARI", "PIT", "SD", "SF", "SEA", "TB", "WAS", "CAR", "JAC",
				"BAL", "HOU"
		};
		
		for(int i=0; i<value.length; i++) {
			assertTrue(parser.getTranslatedAbbreviation(value[i]).equals(result[i]));
		}
	}
}
