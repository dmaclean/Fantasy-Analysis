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
		stat = new Stat();
	}

	@After
	public void tearDown() throws Exception {
		stat = null;
	}

	@Test
	public void testAddValue() {
		stat.addValue(FantasyConstants.STAT_BLOCK_KICK, 1);
		
		assertTrue(stat.getValue(FantasyConstants.STAT_BLOCK_KICK) == 1);
	}

	@Test
	public void testGetSave() {
		Connection conn = null;
		
		try {
			conn = FantasyTestUtils.getTestConnection();
			
			stat.addValue(FantasyConstants.STAT_BLOCK_KICK, 1);
			stat.save(conn);
			
			Stat newStat = new Stat();
			newStat.get(stat.getId(), conn);
			
			assertTrue(newStat.getId() == stat.getId());
			for(int i=0; i<78; i++) {
				if(i == FantasyConstants.STAT_BLOCK_KICK) {
					assertTrue(newStat.getValue(i) == 1);
				}
				else {
					assertTrue(newStat.getValue(i) == -1);
				}
			}
			
			stat.delete(stat.getId(), conn);
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = conn.prepareStatement("select * from stats");
				rs = pstmt.executeQuery();
				
				assertTrue(!rs.next());
			}
			catch(SQLException e) {
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
