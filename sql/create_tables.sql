drop table players;
create table players (
	id int not null primary key,
	name varchar(100) not null,
	position varchar(5) not null
);

drop table stats;
create table stats (
	id int auto_increment primary key,
	player_id int not null,
	season int not null,
	week int not null,
	stat_key int not null,
	stat_value int not null,
	foreign key (player_id) references player(id)
);
create index stat_key_idx on stats(stat_key);

drop table fantasy_points;
create table fantasy_points (
	id int auto_increment primary key,
	player_id int not null,
	system varchar(100) not null,
	season int not null,
	week int not null,
	points int not null
);

drop table teams;
create table teams(
	id int auto_increment primary key,
	city varchar(20) not null,
	name varchar(20) not null,
	abbreviation varchar(3) not null
);
insert into teams (city, name, abbreviation) values ('Atlanta','Falcons','ATL');
insert into teams (city, name, abbreviation) values ('Buffalo','Bills','BUF');
insert into teams (city, name, abbreviation) values ('Chicago','Bears','CHI');
insert into teams (city, name, abbreviation) values ('Cincinnati','Bengals','CIN');
insert into teams (city, name, abbreviation) values ('Cleveland','Browns','CLE');
insert into teams (city, name, abbreviation) values ('Dallas','Cowboys','DAL');
insert into teams (city, name, abbreviation) values ('Denver','Broncos','DEN');
insert into teams (city, name, abbreviation) values ('Detroit','Lions','DET');
insert into teams (city, name, abbreviation) values ('Green Bay','Packers','GB');
insert into teams (city, name, abbreviation) values ('Tennessee','Titans','TEN');
insert into teams (city, name, abbreviation) values ('Indianapolis','Colts','IND');
insert into teams (city, name, abbreviation) values ('Kansas City','Chiefs','KC');
insert into teams (city, name, abbreviation) values ('Oakland','Raiders','OAK');
insert into teams (city, name, abbreviation) values ('St. Louis','Rams','STL');
insert into teams (city, name, abbreviation) values ('Miami','Dolphins','MIA');
insert into teams (city, name, abbreviation) values ('Minnesota','Vikings','MIN');
insert into teams (city, name, abbreviation) values ('New England','Patriots','NE');
insert into teams (city, name, abbreviation) values ('New Orleans','Saints','NO');
insert into teams (city, name, abbreviation) values ('New York','Giants','NYG');
insert into teams (city, name, abbreviation) values ('New York','Jets','NYJ');
insert into teams (city, name, abbreviation) values ('Philadelphia','Eagles','PHI');
insert into teams (city, name, abbreviation) values ('Arizona','Cardinals','ARI');
insert into teams (city, name, abbreviation) values ('Pittsburgh','Steelers','PIT');
insert into teams (city, name, abbreviation) values ('San Diego','Chargers','SD');
insert into teams (city, name, abbreviation) values ('San Francisco','49ers','SF');
insert into teams (city, name, abbreviation) values ('Seattle','Seahawks','SEA');
insert into teams (city, name, abbreviation) values ('Tampa Bay','Buccaneers','TB');
insert into teams (city, name, abbreviation) values ('Washington','Redskins','WAS');
insert into teams (city, name, abbreviation) values ('Carolina','Panthers','CAR');
insert into teams (city, name, abbreviation) values ('Jacksonville','Jaguars','JAC');
insert into teams (city, name, abbreviation) values ('Baltimore','Ravens','BAL');
insert into teams (city, name, abbreviation) values ('Houston','Texans','HOU');

drop table team_memberships;
create table team_memberships (
	id int auto_increment primary key,
	player_id int not null,
	season int not null,
	team_id int not null,
	version int,
	foreign key (player_id) references player(id),
	foreign key (team_id) references teams(id)
);
create index player_season_team_idx on team_memberships(player_id, season, team_id);

drop table average_draft_positions;
create table average_draft_positions (
	id int auto_increment primary key,
	player_id int not null,
	season int not null,
	adp double not null,
	version int,
	foreign key (player_id) references player(id)
);
create index adp_season_idx on average_draft_positions(season)