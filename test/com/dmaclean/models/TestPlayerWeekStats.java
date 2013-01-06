package com.dmaclean.models;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmaclean.testutils.FantasyTestUtils;

public class TestPlayerWeekStats {

	private PlayerWeekStats pws;
	private String weekJson;
	
	@Before
	public void setUp() throws Exception {
		/*
		 * Clear out the test database on each test.
		 */
		FantasyTestUtils.resetTestDatabase();
		
		pws = new PlayerWeekStats();
		if(weekJson == null) {
			BufferedReader br = new BufferedReader(new FileReader(new File("test_data/week.json")));
			StringBuffer sb = new StringBuffer();
			String s = null;
			while( (s = br.readLine()) != null) sb.append(s);
			
			weekJson = sb.toString();
		}
	}

	@After
	public void tearDown() throws Exception {
		pws = null;
	}

	@Test
	public void testGetSaveDelete() {
		pws.setPlayerId(1);
		pws.setWeek(1);
		pws.setYear(2009);
		
		try {
			pws.parsePlayerStats(weekJson);
		}
		catch(JSONException e) {
			fail(e.getMessage());
		}
		
		Connection conn = null;
		
		try {
			conn = FantasyTestUtils.getTestConnection();
			
			// Save our configured PlayerWeekStat object
			pws.save(conn);
			
			// Retrieve our new row in the database and store in another PWS object
			PlayerWeekStats pwsNew = PlayerWeekStats.get(pws.getPlayerId(), pws.getYear(), pws.getWeek(), conn);
			
			
			assertTrue(pwsNew.getPlayerId() == pws.getPlayerId());
			assertTrue(pwsNew.getYear() == pws.getYear());
			assertTrue(pwsNew.getWeek() == pws.getWeek());
			
			for(int i=0; i<78; i++) {
				assertTrue(pwsNew.getStat().getValue(i) == pws.getStat().getValue(i));
			}
			
			pws.delete(conn);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = conn.prepareStatement("select * from player_week_stats");
				rs = pstmt.executeQuery();
				
				assertTrue(!rs.next());
			}
			catch(SQLException e) {
				e.printStackTrace();
				fail(e.getMessage());
			}
			finally {
				try {
					pstmt.close();
				}
				catch(SQLException e) {}
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	public void testExists_False() {
		Connection conn = null;
		
		try {
			conn = FantasyTestUtils.getTestConnection();
			pws.setPlayerId(1);
			pws.setWeek(2);
			pws.setYear(2012);
			
			assertTrue(!pws.exists(conn));
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
			pws.setPlayerId(1);
			pws.setWeek(2);
			pws.setYear(2012);
			pws.save(conn);
			
			PlayerWeekStats pws2 = new PlayerWeekStats();
			pws2.setPlayerId(1);
			pws2.setWeek(2);
			pws2.setYear(2012);
			
			assertTrue(pws2.exists(conn));
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
