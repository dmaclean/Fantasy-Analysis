package com.dmaclean.models;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestPlayerSeasonStats {

	private PlayerSeasonStats pss;
	
	@Before
	public void setUp() throws Exception {
		pss = new PlayerSeasonStats();
	}

	@After
	public void tearDown() throws Exception {
		pss = null;
	}

	@Test
	public void testParsePlayerSeasonStats() {
		String json = "{\"fantasy_content\":{\"xml:lang\":\"en-US\",\"yahoo:uri\":\"/fantasy/v2/player/223.p.100001/stats\",\"player\":[[{\"player_key\":\"223.p.100001\"},{\"player_id\":\"100001\"},{\"name\":{\"full\":\"Atlanta\",\"first\":\"Atlanta\",\"last\":\"\",\"ascii_first\":\"Atlanta\",\"ascii_last\":\"\"}},{\"editorial_player_key\":\"nfl.p.100001\"},{\"editorial_team_key\":\"nfl.t.1\"},{\"editorial_team_full_name\":\"Atlanta Falcons\"},{\"editorial_team_abbr\":\"Atl\"},{\"bye_weeks\":{\"week\":\"4\"}},{\"uniform_number\":false},{\"display_position\":\"DEF\"},{\"headshot\":{\"url\":\"http://l.yimg.com/iu/api/res/1.2/rHwmff7kVS0f_S1amFMEwQ--/YXBwaWQ9eXZpZGVvO2NoPTIxNTtjcj0xO2N3PTE2NDtkeD0xO2R5PTE7Zmk9dWxjcm9wO2g9NjA7cT0xMDA7dz00Ng--/http://l.yimg.com/a/i/us/sp/v/blank_player2.gif\",\"size\":\"small\"},\"image_url\":\"http://l.yimg.com/iu/api/res/1.2/rHwmff7kVS0f_S1amFMEwQ--/YXBwaWQ9eXZpZGVvO2NoPTIxNTtjcj0xO2N3PTE2NDtkeD0xO2R5PTE7Zmk9dWxjcm9wO2g9NjA7cT0xMDA7dz00Ng--/http://l.yimg.com/a/i/us/sp/v/blank_player2.gif\"},{\"is_undroppable\":\"0\"},{\"position_type\":\"DT\"},{\"eligible_positions\":[{\"position\":\"DEF\"}]},[],[]],{\"player_stats\":{\"0\":{\"coverage_type\":\"season\",\"season\":\"2009\"},\"stats\":[{\"stat\":{\"stat_id\":\"0\",\"value\":\"16\"}},{\"stat\":{\"stat_id\":\"31\",\"value\":\"313\"}},{\"stat\":{\"stat_id\":\"32\",\"value\":\"28.0\"}},{\"stat\":{\"stat_id\":\"33\",\"value\":\"15\"}},{\"stat\":{\"stat_id\":\"34\",\"value\":\"13\"}},{\"stat\":{\"stat_id\":\"35\",\"value\":\"3\"}},{\"stat\":{\"stat_id\":\"36\",\"value\":\"0\"}},{\"stat\":{\"stat_id\":\"37\",\"value\":\"3\"}},{\"stat\":{\"stat_id\":\"48\",\"value\":\"1606\"}},{\"stat\":{\"stat_id\":\"49\",\"value\":\"0\"}},{\"stat\":{\"stat_id\":\"50\",\"value\":\"0\"}},{\"stat\":{\"stat_id\":\"51\",\"value\":\"1\"}},{\"stat\":{\"stat_id\":\"52\",\"value\":\"4\"}},{\"stat\":{\"stat_id\":\"53\",\"value\":\"4\"}},{\"stat\":{\"stat_id\":\"54\",\"value\":\"2\"}},{\"stat\":{\"stat_id\":\"55\",\"value\":\"4\"}},{\"stat\":{\"stat_id\":\"56\",\"value\":\"1\"}}]}}],\"time\":\"54.160833358765ms\",\"copyright\":\"Data provided by Yahoo! and STATS, LLC\",\"refresh_rate\":\"90\"}}";
		
		try {
			pss.parsePlayerSeasonStats(json);
			
			assertTrue(pss.getStat() != null);
		} catch (JSONException e) {
			fail(e.getMessage());
		}
	}

}
