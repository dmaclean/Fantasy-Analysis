package com.dmaclean.models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.dmaclean.constants.FantasyConstants;

public class Stat {
	
	private static final Logger logger = Logger.getLogger(Stat.class.getPackage().getName());

	private HashMap<Integer, Integer> stats;
	
	private int id;
	/*private int gamesPlayed;
	private int passingAttempts;
	private int completions;
	private int incompletePasses;
	private int passingYards;
	private int passingTouchdowns;
	private int interceptions;
	private int sacks;
	private int rushingAttempts;
	private int rushingYards;
	private int rushingTouchdowns;
	private int receptions;
	private int receptionYards;
	private int receptionTouchdowns;
	private int returnYards;
	private int returnTouchdowns;
	private int twoPointConversions;
	private int fumbles;
	private int fumblesLost;
	private int fieldGoals0To19Yards;
	private int fieldGoals20To29Yards;
	private int fieldGoals30To39Yards;
	private int fieldGoals40To49Yards;
	private int fieldGoals50PlusYards;
	private int fieldGoalsMissed0To19Yards;
	private int fieldGoalsMissed20To29Yards;
	private int fieldGoalsMissed30To39Yards;
	private int fieldGoalsMissed40To49Yards;
	private int fieldGoalsMissed50PlusYards;
	private int pointAfterAttemptMade;
	private int pointAfterAttemptMissed;
	private int pointsAllowed;
	private int sack;
	private int interception;
	private int fumbleRecovery;
	private int touchdown;
	private int safety;
	private int blockKick;
	private int tackleSolo;
	private int tackleAssist;
	// sack - duplicate
	// interception - duplicate
	private int fumbleForce;
	// fumleRecovery - duplicate
	private int defensiveTouchdown;
	// safety - duplicate
	private int passDefended;
	// blockKick - duplicate
	// returnYards - duplicate
	private int kickoffAndPuntReturnTouchdowns;
	private int pointsAllowed0;
	private int pointsAllowed1To6;
	private int pointsAllowed7To13;
	private int pointsAllowed14To20;
	private int pointsAllowed21To27;
	private int pointsAllowed28To34;
	private int pointsAllowed35Plus;
	private int offensiveFumbleReturnTd;
	private int pickSixesThrown;
	private int fortyPlusYardCompletions;
	private int fortyPlusYardPassingTouchdowns;
	private int fortyPlusYardRushingAttempts;
	private int fortyPlusYardRushingTouchdowns;
	private int fortyPlusYardReceptions;
	private int fortyPlusYardReceptionTouchdowns;
	private int tacklesForLoss;
	private int turnoverReturnYards;
	private int fourthDownStops;
	// tacklesForLoss - duplicate
	private int defensiveYardsAllowed;
	private int defensiveYardsAllowedNegative;
	private int defensiveYardsAllowed0To99;
	private int defensiveYardsAllowed100To199;
	private int defensiveYardsAllowed200To299;
	private int defensiveYardsAllowed300To399;
	private int defensiveYardsAllowed400To499;
	private int defensiveYardsAllowed500Plus;
	private int threeAndOutsForced;*/
	
	public Stat() {
		stats = new HashMap<Integer, Integer>(78);
		for(int i=0; i<78; i++) {
			stats.put(i, -1);
		}
	}
	
	public void addValue(int statId, int value) {
		stats.put(statId, value);
	}
	
	public int getValue(int statId) {
		return stats.get(statId);
	}
	
	public void get(int id, Connection conn) {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement("select * from stats where id = ?");
			pstmt.setInt(1, id);
			
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				this.id = rs.getInt(1);
				
				for(int i=0; i<78; i++)
					stats.put(i, rs.getInt(i+2));
			}
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
				rs.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void delete(int id, Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("delete from stats where id = ?");
			pstmt.setInt(1, id);
			
			pstmt.execute();
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void save(Connection conn) {
		PreparedStatement pstmt = null;
		
		try {
			pstmt = conn.prepareStatement("insert into stats (" +
					"games_played, passing_attempts, completions, incomplete_passes, passing_yards, passing_touchdowns, interceptions, sacks," +
					"rushing_attempts, rushing_yards, rushing_touchdowns, receptions, reception_yards, reception_touchdowns, return_yards, return_touchdowns," +
					"two_point_conversions, fumbles, fumbles_lost, field_goals_0_19_yards, field_goals_20_29_yards, field_goals_30_39_yards, " +
					"field_goals_40_49_yards, field_goals_50_plus_yards, field_goals_missed_0_19_yards, field_goals_missed_20_29_yards, " +
					"field_goals_missed_30_39_yards, field_goals_missed_40_49_yards, field_goals_missed_50_plus_yards, point_after_attempt_made," +
					"point_after_attempt_missed, points_allowed, sack, interception, fumble_recovery, touchdown, safety, block_kick, tackle_solo, tackle_assist," +
					"sack_player, interception_player, fumble_force, fumble_recovery_player, defensive_touchdown, safety_player, pass_defended, block_kick_player," +
					"return_yards_team, kickoff_and_punt_return_touchdowns, points_allowed_0, points_allowed_1_6, points_allowed_7_13, points_allowed_14_20, " +
					"points_allowed_21_27, points_allowed_28_34, points_allowed_35_plus, offensive_fumble_return_td, pick_sixes_thrown, 40_plus_yard_completions," +
					"40_plus_yard_passing_touchdowns, 40_plus_yard_rushing_attempts, 40_plus_yard_rushing_touchdowns, 40_plus_yard_receptions, " +
					"40_plus_yard_reception_touchdowns, tackles_for_loss, turnover_return_yards, 4th_down_stops, tackles_for_loss_team, defensive_yards_allowed," +
					"defensive_yards_allowed_negative, defensive_yards_allowed_0_99, defensive_yards_allowed_100_199, defensive_yards_allowed_200_299," +
					"defensive_yards_allowed_300_399, defensive_yards_allowed_400_499, defensive_yards_allowed_500_plus, three_and_outs_forced" +
					") " +
					"values(?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?,?,?," +
							"?,?,?,?,?,?,?,?)");
			
			pstmt.setInt(1, stats.get(FantasyConstants.STAT_GAMES_PLAYED));
			pstmt.setInt(2, stats.get(FantasyConstants.STAT_PASSING_ATTEMPTS));
			pstmt.setInt(3, stats.get(FantasyConstants.STAT_COMPLETIONS));
			pstmt.setInt(4, stats.get(FantasyConstants.STAT_INCOMPLETE_PASSES));
			pstmt.setInt(5, stats.get(FantasyConstants.STAT_PASSING_YARDS));
			pstmt.setInt(6, stats.get(FantasyConstants.STAT_PASSING_TOUCHDOWNS));
			pstmt.setInt(7, stats.get(FantasyConstants.STAT_INTERCEPTIONS));
			pstmt.setInt(8, stats.get(FantasyConstants.STAT_SACKS));
			pstmt.setInt(9, stats.get(FantasyConstants.STAT_RUSHING_ATTEMPTS));
			pstmt.setInt(10, stats.get(FantasyConstants.STAT_RUSHING_YARDS));
			pstmt.setInt(11, stats.get(FantasyConstants.STAT_RUSHING_TOUCHDOWNS));
			pstmt.setInt(12, stats.get(FantasyConstants.STAT_RECEPTIONS));
			pstmt.setInt(13, stats.get(FantasyConstants.STAT_RECEPTION_YARDS));
			pstmt.setInt(14, stats.get(FantasyConstants.STAT_RECEPTION_TOUCHDOWNS));
			pstmt.setInt(15, stats.get(FantasyConstants.STAT_RETURN_YARDS));
			pstmt.setInt(16, stats.get(FantasyConstants.STAT_RETURN_TOUCHDOWNS));
			pstmt.setInt(17, stats.get(FantasyConstants.STAT_TWO_POINT_CONVERSIONS));
			pstmt.setInt(18, stats.get(FantasyConstants.STAT_FUMBLES));
			pstmt.setInt(19, stats.get(FantasyConstants.STAT_FUMBLES_LOST));
			pstmt.setInt(20, stats.get(FantasyConstants.STAT_FIELD_GOALS_0_19_YARDS));
			pstmt.setInt(21, stats.get(FantasyConstants.STAT_FIELD_GOALS_20_29_YARDS));
			pstmt.setInt(22, stats.get(FantasyConstants.STAT_FIELD_GOALS_30_39_YARDS));
			pstmt.setInt(23, stats.get(FantasyConstants.STAT_FIELD_GOALS_40_49_YARDS));
			pstmt.setInt(24, stats.get(FantasyConstants.STAT_FIELD_GOALS_50_PLUS_YARDS));
			pstmt.setInt(25, stats.get(FantasyConstants.STAT_FIELD_GOALS_MISSED_0_19_YARDS));
			pstmt.setInt(26, stats.get(FantasyConstants.STAT_FIELD_GOALS_MISSED_20_29_YARDS));
			pstmt.setInt(27, stats.get(FantasyConstants.STAT_FIELD_GOALS_MISSED_30_39_YARDS));
			pstmt.setInt(28, stats.get(FantasyConstants.STAT_FIELD_GOALS_MISSED_40_49_YARDS));
			pstmt.setInt(29, stats.get(FantasyConstants.STAT_FIELD_GOALS_MISSED_50_PLUS_YARDS));
			pstmt.setInt(30, stats.get(FantasyConstants.STAT_POINT_AFTER_ATTEMPT_MADE));
			pstmt.setInt(31, stats.get(FantasyConstants.STAT_POINT_AFTER_ATTEMPT_MISSED));
			pstmt.setInt(32, stats.get(FantasyConstants.STAT_POINTS_ALLOWED));
			pstmt.setInt(33, stats.get(FantasyConstants.STAT_SACK));
			pstmt.setInt(34, stats.get(FantasyConstants.STAT_INTERCEPTION));
			pstmt.setInt(35, stats.get(FantasyConstants.STAT_FUMBLE_RECOVERY));
			pstmt.setInt(36, stats.get(FantasyConstants.STAT_TOUCHDOWN));
			pstmt.setInt(37, stats.get(FantasyConstants.STAT_SAFETY));
			pstmt.setInt(38, stats.get(FantasyConstants.STAT_BLOCK_KICK));
			pstmt.setInt(39, stats.get(FantasyConstants.STAT_TACKLE_SOLO));
			pstmt.setInt(40, stats.get(FantasyConstants.STAT_TACKLE_ASSIST));
			pstmt.setInt(41, stats.get(FantasyConstants.STAT_SACK_PLAYER));
			pstmt.setInt(42, stats.get(FantasyConstants.STAT_INTERCEPTION_PLAYER));
			pstmt.setInt(43, stats.get(FantasyConstants.STAT_FUMBLE_FORCE));
			pstmt.setInt(44, stats.get(FantasyConstants.STAT_FUMBLE_RECOVERY_PLAYER));
			pstmt.setInt(45, stats.get(FantasyConstants.STAT_DEFENSIVE_TOUCHDOWN));
			pstmt.setInt(46, stats.get(FantasyConstants.STAT_SAFETY_PLAYER));
			pstmt.setInt(47, stats.get(FantasyConstants.STAT_PASS_DEFENDED));
			pstmt.setInt(48, stats.get(FantasyConstants.STAT_BLOCK_KICK_PLAYER));
			pstmt.setInt(49, stats.get(FantasyConstants.STAT_RETURN_YARDS_TEAM));
			pstmt.setInt(50, stats.get(FantasyConstants.STAT_KICKOFF_AND_PUNT_RETURN_TOUCHDOWNS));
			pstmt.setInt(51, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_0));
			pstmt.setInt(52, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_1_6));
			pstmt.setInt(53, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_7_13));
			pstmt.setInt(54, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_14_20));
			pstmt.setInt(55, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_21_27));
			pstmt.setInt(56, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_28_34));
			pstmt.setInt(57, stats.get(FantasyConstants.STAT_POINTS_ALLOWED_35_PLUS));
			pstmt.setInt(58, stats.get(FantasyConstants.STAT_OFFENSIVE_FUMBLE_RETURN_TD));
			pstmt.setInt(59, stats.get(FantasyConstants.STAT_PICK_SIXES_THROWN));
			pstmt.setInt(60, stats.get(FantasyConstants.STAT_FORTY_PLUS_YARD_COMPLETIONS));
			pstmt.setInt(61, stats.get(FantasyConstants.STAT_FORTY_PLUS_YARD_PASSING_TOUCHDOWNS));
			pstmt.setInt(62, stats.get(FantasyConstants.STAT_FORTY_PLUS_YARD_RUSHING_ATTEMPTS));
			pstmt.setInt(63, stats.get(FantasyConstants.STAT_FORTY_PLUS_YARD_RUSHING_TOUCHDOWNS));
			pstmt.setInt(64, stats.get(FantasyConstants.STAT_FORTY_PLUS_YARD_RECEPTIONS));
			pstmt.setInt(65, stats.get(FantasyConstants.STAT_FORTY_PLUS_YARD_RECEPTION_TOUCHDOWNS));
			pstmt.setInt(66, stats.get(FantasyConstants.STAT_TACKLES_FOR_LOSS));
			pstmt.setInt(67, stats.get(FantasyConstants.STAT_TURNOVER_RETURN_YARDS));
			pstmt.setInt(68, stats.get(FantasyConstants.STAT_FOURTH_DOWN_STOPS));
			pstmt.setInt(69, stats.get(FantasyConstants.STAT_TACKLES_FOR_LOSS_TEAM));
			pstmt.setInt(70, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED));
			pstmt.setInt(71, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_NEGATIVE));
			pstmt.setInt(72, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_0_99));
			pstmt.setInt(73, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_100_199));
			pstmt.setInt(74, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_200_299));
			pstmt.setInt(75, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_300_399));
			pstmt.setInt(76, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_400_499));
			pstmt.setInt(77, stats.get(FantasyConstants.STAT_DEFENSIVE_YARDS_ALLOWED_500_PLUS));
			pstmt.setInt(78, stats.get(FantasyConstants.STAT_THREE_AND_OUTS_FORCED));
			
			pstmt.execute();
			
			ResultSet newKey = pstmt.getGeneratedKeys();
			if(newKey.next())
				this.id = newKey.getInt(1);
		}
		catch(SQLException e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		catch(Exception e) {
			logger.severe(e.getMessage());
			e.printStackTrace();
		}
		finally {
			try {
				pstmt.close();
			}
			catch(SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}
}
