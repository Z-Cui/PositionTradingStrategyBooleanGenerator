package com.example.indicators;

import java.time.LocalDateTime;

public class EMA200_EMA50_PatternData {
	
	private LocalDateTime datetime;
	private double ema200;
	private double ema50;
	
	private boolean ema200_up;
	private boolean ema200_down;
	private boolean ema50_up;
	private boolean ema50_down;
	
	// ema50 rises and crosses ema200
	private boolean ema200_ema50_goldencross;
	// ema50 drops and crosses ema200
	private boolean ema200_ema50_deathcross;
	// close price breaks above ema50 line
	private boolean close_price_break_above_ema50;
	// close price breaks below ema200 line
	private boolean close_price_break_below_ema200;
	
	public EMA200_EMA50_PatternData(LocalDateTime datetime, double ema200, double ema50) {
		super();
		this.datetime = datetime;
		this.ema200 = ema200;
		this.ema50 = ema50;
		this.ema200_up = false;
		this.ema200_down = false;
		this.ema50_up = false;
		this.ema50_down = false;
		this.ema200_ema50_goldencross = false;
		this.ema200_ema50_deathcross = false;
		this.close_price_break_above_ema50 = false;
		this.close_price_break_below_ema200 = false;
	}
	public LocalDateTime getDatetime() {
		return datetime;
	}
	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}
	public double getEma200() {
		return ema200;
	}
	public void setEma200(double ema200) {
		this.ema200 = ema200;
	}
	public double getEma50() {
		return ema50;
	}
	public void setEma50(double ema50) {
		this.ema50 = ema50;
	}
	public boolean isEma200_up() {
		return ema200_up;
	}
	public void setEma200_up(boolean ema200_up) {
		this.ema200_up = ema200_up;
	}
	public boolean isEma200_down() {
		return ema200_down;
	}
	public void setEma200_down(boolean ema200_down) {
		this.ema200_down = ema200_down;
	}
	public boolean isEma50_up() {
		return ema50_up;
	}
	public void setEma50_up(boolean ema50_up) {
		this.ema50_up = ema50_up;
	}
	public boolean isEma50_down() {
		return ema50_down;
	}
	public void setEma50_down(boolean ema50_down) {
		this.ema50_down = ema50_down;
	}
	public boolean isEma200_ema50_goldencross() {
		return ema200_ema50_goldencross;
	}
	public void setEma200_ema50_goldencross(boolean ema200_ema50_goldencross) {
		this.ema200_ema50_goldencross = ema200_ema50_goldencross;
	}
	public boolean isEma200_ema50_deathcross() {
		return ema200_ema50_deathcross;
	}
	public void setEma200_ema50_deathcross(boolean ema200_ema50_deathcross) {
		this.ema200_ema50_deathcross = ema200_ema50_deathcross;
	}
	public boolean isClose_price_break_above_ema50() {
		return close_price_break_above_ema50;
	}
	public void setClose_price_break_above_ema50(boolean close_price_break_above_ema50) {
		this.close_price_break_above_ema50 = close_price_break_above_ema50;
	}
	
	public boolean isClose_price_break_below_ema200() {
		return close_price_break_below_ema200;
	}
	public void setClose_price_break_below_ema200(boolean close_price_break_below_ema200) {
		this.close_price_break_below_ema200 = close_price_break_below_ema200;
	}
	
}
