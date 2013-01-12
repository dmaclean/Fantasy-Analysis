package com.dmaclean.models;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmaclean.constants.FantasyConstants;
import com.dmaclean.testutils.FantasyTestUtils;

public class TestStat {

	private Stat stat;
	
	@Before
	public void setUp() throws Exception {
		FantasyTestUtils.resetTestDatabase();
		
		stat = new Stat();
	}

	@After
	public void tearDown() throws Exception {
		stat = null;
	}
	
	@Test
	public void testGetSaveDeleteExists() {
		Connection conn = FantasyTestUtils.getTestConnection();
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			Player p = new Player();
			p.setName("Dan MacLean");
			p.setPlayerId(1);
			p.setPosition("QB");
			p.save(conn);
			
			Stat s = new Stat();
			s.setId(1);
			s.setPlayerId(p.getPlayerId());
			s.setSeason(2001);
			s.setWeek(-1);
			s.setStatKey(1);
			s.setStatValue(10);
			assertTrue(!s.exists(conn));
			s.save(conn);
			assertTrue(s.exists(conn));
			
			Stat s2 = new Stat();
			s2.get(s.getId(), conn);
			
			assertTrue(s2.getId() == s.getId());
			assertTrue(s2.getPlayerId() == s.getPlayerId());
			assertTrue(s2.getSeason() == s.getSeason());
			assertTrue(s2.getWeek() == s.getWeek());
			assertTrue(s2.getStatKey() == s.getStatKey());
			assertTrue(s2.getStatValue() == s.getStatValue());
			
			s.delete(s.getId(), conn);
			
			pstmt = conn.prepareStatement("select * from stats where id = ?");
			pstmt.setInt(1, s2.getId());
			rs = pstmt.executeQuery();
			
			assertTrue(!rs.next());
			
		}
		catch(Exception e) {
			fail(e.getMessage());
		}
		finally {
			try {
				pstmt.close();
				rs.close();
				conn.close();
			}
			catch(SQLException e) {}
		}
	}

}
