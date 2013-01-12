package com.dmaclean.models;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmaclean.testutils.FantasyTestUtils;

public class TestPlayer {

	private Player player;
	
	@Before
	public void setUp() throws Exception {
		FantasyTestUtils.resetTestDatabase();

		player = new Player();
	}

	@After
	public void tearDown() throws Exception {
		player = null;
	}
	
	@Test
	public void testParsePlayerInfo() {
		Connection conn = null;
		
		try {
			JSONObject players = Player.parsePlayersList(FantasyTestUtils.readTestFile("players_list.json"));
//			int count = players.getInt("count");
			JSONObject o = players.getJSONObject("" + 0);
			JSONArray a = o.getJSONArray("player");
			
			conn = FantasyTestUtils.getTestConnection();
			
			Player p = new Player();
			p.parsePlayerInfo(a);
			
			assertTrue(p.getPlayerId() == 549);
			assertTrue(p.getName().equals("John Carney"));
			assertTrue(p.getPosition().equals("K"));
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
			try {
				conn.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void testParsePlayerStats() {
		Connection conn = FantasyTestUtils.getTestConnection();
		
		Player p = new Player();
		
		try {
			p.parsePlayerStats(conn, FantasyTestUtils.readTestFile("season_stats.json"), 2001, -1);
			ArrayList<Stat> stats  = p.getSeasonStats();
			
			assertTrue(p.getWeekStats().isEmpty());
			
			int i=0;
			for(Stat s : stats) {
				
				assertTrue(s.exists(conn));
				assertTrue(s.getPlayerId() == p.getPlayerId());
				assertTrue(s.getSeason() == 2001);
				assertTrue(s.getWeek() == -1);
				
				if(i==0) {
					assertTrue(s.getStatKey() == 0);
					assertTrue(s.getStatValue() == 15);
				}
				else if(i==1) {
					assertTrue(s.getStatKey() == 1);
					assertTrue(s.getStatValue() == 514);
				}
				else if(i==2) {
					assertTrue(s.getStatKey() == 2);
					assertTrue(s.getStatValue() == 363);
				}
				else if(i==3) {
					assertTrue(s.getStatKey() == 3);
					assertTrue(s.getStatValue() == 151);
				}
				else if(i==4) {
					assertTrue(s.getStatKey() == 4);
					assertTrue(s.getStatValue() == 4388);
				}
				else if(i==5) {
					assertTrue(s.getStatKey() == 5);
					assertTrue(s.getStatValue() == 34);
				}
				else if(i==6) {
					assertTrue(s.getStatKey() == 6);
					assertTrue(s.getStatValue() == 11);
				}
				else if(i==7) {
					assertTrue(s.getStatKey() == 7);
					assertTrue(s.getStatValue() == 20);
				}
				else if(i==8) {
					assertTrue(s.getStatKey() == 8);
					assertTrue(s.getStatValue() == 22);
				}
				else if(i==9) {
					assertTrue(s.getStatKey() == 9);
					assertTrue(s.getStatValue() == 33);
				}
				else if(i==10) {
					assertTrue(s.getStatKey() == 10);
					assertTrue(s.getStatValue() == 2);
				}
				else if(i==11) {
					assertTrue(s.getStatKey() == 11);
					assertTrue(s.getStatValue() == 1);
				}
				else if(i==12) {
					assertTrue(s.getStatKey() == 12);
					assertTrue(s.getStatValue() == -4);
				}
				else if(i==13) {
					assertTrue(s.getStatKey() == 13);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==14) {
					assertTrue(s.getStatKey() == 14);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==15) {
					assertTrue(s.getStatKey() == 15);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==16) {
					assertTrue(s.getStatKey() == 16);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==17) {
					assertTrue(s.getStatKey() == 17);
					assertTrue(s.getStatValue() == 9);
				}
				else if(i==18) {
					assertTrue(s.getStatKey() == 18);
					assertTrue(s.getStatValue() == 6);
				}
				else if(i==19) {
					assertTrue(s.getStatKey() == 57);
					assertTrue(s.getStatValue() == 0);
				}
				
				i++;
			}
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	/**
	 * This tests for making sure the location of exists() check is correct.  If done without
	 * first setting the stat_key and stat_value then if 0-0 comes up then the stat will be
	 * flagged as existing on all subsequent stats.
	 */
	@Test
	public void testParsePlayerStats_FirstStatZero() {
		Connection conn = FantasyTestUtils.getTestConnection();
		
		Player p = new Player();
		
		try {
			p.parsePlayerStats(conn, FantasyTestUtils.readTestFile("season_stats_first_stat_zero.json"), 2001, -1);
			ArrayList<Stat> stats  = p.getSeasonStats();
			
			assertTrue(p.getSeasonStats().size() == 20);
			assertTrue(p.getWeekStats().isEmpty());
			
			int i=0;
			for(Stat s : stats) {
				
				assertTrue(s.exists(conn));
				assertTrue(s.getPlayerId() == p.getPlayerId());
				assertTrue(s.getSeason() == 2001);
				assertTrue(s.getWeek() == -1);
				
				if(i==0) {
					assertTrue(s.getStatKey() == 0);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==1) {
					assertTrue(s.getStatKey() == 1);
					assertTrue(s.getStatValue() == 514);
				}
				else if(i==2) {
					assertTrue(s.getStatKey() == 2);
					assertTrue(s.getStatValue() == 363);
				}
				else if(i==3) {
					assertTrue(s.getStatKey() == 3);
					assertTrue(s.getStatValue() == 151);
				}
				else if(i==4) {
					assertTrue(s.getStatKey() == 4);
					assertTrue(s.getStatValue() == 4388);
				}
				else if(i==5) {
					assertTrue(s.getStatKey() == 5);
					assertTrue(s.getStatValue() == 34);
				}
				else if(i==6) {
					assertTrue(s.getStatKey() == 6);
					assertTrue(s.getStatValue() == 11);
				}
				else if(i==7) {
					assertTrue(s.getStatKey() == 7);
					assertTrue(s.getStatValue() == 20);
				}
				else if(i==8) {
					assertTrue(s.getStatKey() == 8);
					assertTrue(s.getStatValue() == 22);
				}
				else if(i==9) {
					assertTrue(s.getStatKey() == 9);
					assertTrue(s.getStatValue() == 33);
				}
				else if(i==10) {
					assertTrue(s.getStatKey() == 10);
					assertTrue(s.getStatValue() == 2);
				}
				else if(i==11) {
					assertTrue(s.getStatKey() == 11);
					assertTrue(s.getStatValue() == 1);
				}
				else if(i==12) {
					assertTrue(s.getStatKey() == 12);
					assertTrue(s.getStatValue() == -4);
				}
				else if(i==13) {
					assertTrue(s.getStatKey() == 13);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==14) {
					assertTrue(s.getStatKey() == 14);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==15) {
					assertTrue(s.getStatKey() == 15);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==16) {
					assertTrue(s.getStatKey() == 16);
					assertTrue(s.getStatValue() == 0);
				}
				else if(i==17) {
					assertTrue(s.getStatKey() == 17);
					assertTrue(s.getStatValue() == 9);
				}
				else if(i==18) {
					assertTrue(s.getStatKey() == 18);
					assertTrue(s.getStatValue() == 6);
				}
				else if(i==19) {
					assertTrue(s.getStatKey() == 57);
					assertTrue(s.getStatValue() == 0);
				}
				
				i++;
			}
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testExists_False() {
		Connection conn = null;
		
		try {
			conn = FantasyTestUtils.getTestConnection();
			player.setPlayerId(1);
			
			assertTrue(!player.exists(conn));
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
			try {
				conn.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testExists_True() {
		Connection conn = null;
		
		try {
			conn = FantasyTestUtils.getTestConnection();
			player.setPlayerId(1);
			player.setName("test");
			player.setPosition("QB");
			player.save(conn);
			
			Player p2 = new Player();
			p2.setPlayerId(1);
			
			assertTrue(p2.exists(conn));
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
			try {
				conn.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
