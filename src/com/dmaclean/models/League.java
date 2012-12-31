package com.dmaclean.models;

public class League {
	private String leagueKey;
	private int leagueId;
	private String name;
	
	public League() {
		
	}
	
	public String getLeagueKey() {
		return leagueKey;
	}
	public void setLeagueKey(String leagueKey) {
		this.leagueKey = leagueKey;
	}
	public int getLeagueId() {
		return leagueId;
	}
	public void setLeagueId(int leagueId) {
		this.leagueId = leagueId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
