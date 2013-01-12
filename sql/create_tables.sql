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