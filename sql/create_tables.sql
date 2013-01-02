drop table player;
create table players (
	id int not null primary key,
	name varchar(100) not null,
	position varchar(5) not null
);

drop table player_season_stats;
create table player_season_stats (
	player_id int not null,
	stat_id int not null,
	year int not null,
	foreign key (stat_id) references stats(id)
);
CREATE UNIQUE INDEX player_season_stats_idx ON player_season_stats (player_id, stat_id, year);

drop table player_week_stats;
create table player_week_stats (
	player_id int not null,
	stat_id int not null,
	year int not null,
	week int not null
);
CREATE UNIQUE INDEX player_week_stats_idx ON player_week_stats (player_id, stat_id, year, week);

drop table stats;
create table stats (
	id int auto_increment primary key,
	games_played int,
	passing_attempts int,
	completions int,
	incomplete_passes int,
	passing_yards int,
	passing_touchdowns int,
	interceptions int,
	sacks int,
	rushing_attempts int,
	rushing_yards int,
	rushing_touchdowns int,
	receptions int,
	reception_yards int,
	reception_touchdowns int,
	return_yards int,
	return_touchdowns int,
	two_point_conversions int,
	fumbles int,
	fumbles_lost int,
	field_goals_0_19_yards int,
	field_goals_20_29_yards int,
	field_goals_30_39_yards int,
	field_goals_40_49_yards int,
	field_goals_50_plus_yards int,
	field_goals_missed_0_19_yards int,
	field_goals_missed_20_29_yards int,
	field_goals_missed_30_39_yards int,
	field_goals_missed_40_49_yards int,
	field_goals_missed_50_plus_yards int,
	point_after_attempt_made int,
	point_after_attempt_missed int,
	points_allowed int,
	sack int,
	interception int,
	fumble_recovery int,
	touchdown int,
	safety int,
	block_kick int,
	tackle_solo int,
	tackle_assist int,
	sack_player int,
	interception_player int,
	fumble_force int,
	fumble_recovery_player int,
	defensive_touchdown int,
	safety_player int,
	pass_defended int,
	block_kick_player int,
	return_yards_team int,
	kickoff_and_punt_return_touchdowns int,
	points_allowed_0 int,
	points_allowed_1_6 int,
	points_allowed_7_13 int,
	points_allowed_14_20 int,
	points_allowed_21_27 int,
	points_allowed_28_34 int,
	points_allowed_35_plus int,
	offensive_fumble_return_td int,
	pick_sixes_thrown int,
	40_plus_yard_completions int,
	40_plus_yard_passing_touchdowns int,
	40_plus_yard_rushing_attempts int,
	40_plus_yard_rushing_touchdowns int,
	40_plus_yard_receptions int,
	40_plus_yard_reception_touchdowns int,
	tackles_for_loss int,
	turnover_return_yards int,
	4th_down_stops int,
	tackles_for_loss_team int,
	defensive_yards_allowed int,
	defensive_yards_allowed_negative int,
	defensive_yards_allowed_0_99 int,
	defensive_yards_allowed_100_199 int,
	defensive_yards_allowed_200_299 int,
	defensive_yards_allowed_300_399 int,
	defensive_yards_allowed_400_499 int,
	defensive_yards_allowed_500_plus int,
	three_and_outs_forced int
);