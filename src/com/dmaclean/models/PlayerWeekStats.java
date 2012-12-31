package com.dmaclean.models;

import java.util.logging.Logger;

import org.json.JSONException;

public class PlayerWeekStats {
	private static final Logger logger = Logger.getLogger(PlayerWeekStats.class.getPackage().getName());
	
	private int playerId;
	private int year;
	private int week;
	private Stat stat;
	
	public PlayerWeekStats() {
		stat = new Stat();
	}
	
	public void parsePlayerWeekStats(String json) throws JSONException {
		// type=week;week={week}
		
	}
	
	public int getPlayerId() {
		return playerId;
	}
	public void setPlayerId(int playerId) {
		this.playerId = playerId;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public int getWeek() {
		return week;
	}
	public void setWeek(int week) {
		this.week = week;
	}
	public Stat getStat() {
		return stat;
	}
	public void setStat(Stat stat) {
		this.stat = stat;
	}
}
