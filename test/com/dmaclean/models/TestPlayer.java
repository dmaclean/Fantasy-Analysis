package com.dmaclean.models;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.dmaclean.testutils.FantasyTestUtils;

public class TestPlayer {

	private Player player;
	
	@Before
	public void setUp() throws Exception {
		FantasyTestUtils.resetTestDatabase();

		player = new Player(null);
	}

	@After
	public void tearDown() throws Exception {
		player = null;
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
			
			Player p2 = new Player(null);
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
