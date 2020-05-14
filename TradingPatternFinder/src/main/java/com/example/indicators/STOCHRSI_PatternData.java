package com.example.indicators;

import java.time.LocalDateTime;

public class STOCHRSI_PatternData {
	
	private LocalDateTime datetime;
	private double valueK;
	private double valueD;
	
	private boolean k_up;
	private boolean k_down;
	private boolean k_cross_d;
	private boolean k_oversold;
	private boolean k_overbought;
	
	public STOCHRSI_PatternData(LocalDateTime dt, double k, double d) {
		this.datetime = dt;
		this.valueK = k;
		this.valueD = d;
		this.k_up = false;
		this.k_down = false;
		this.k_cross_d = false;
		this.k_oversold = false;
		this.k_overbought = false;
	}
	
	public String[] toArray() {
		String[] result = new String[8];
		result[0] = this.datetime.toString();
		result[1] = String.valueOf(this.valueK);
		result[2] = String.valueOf(this.valueD);
		result[3] = String.valueOf(this.k_up);
		result[4] = String.valueOf(this.k_down);
		result[5] = String.valueOf(this.k_overbought);
		result[6] = String.valueOf(this.k_oversold);
		result[7] = String.valueOf(this.k_cross_d);
		return result;
	}
	
	public LocalDateTime getDatetime() {
		return datetime;
	}
	public void setDatetime(LocalDateTime datetime) {
		this.datetime = datetime;
	}
	public double getValueK() {
		return valueK;
	}
	public void setValueK(double valueK) {
		this.valueK = valueK;
	}
	public double getValueD() {
		return valueD;
	}
	public void setValueD(double valueD) {
		this.valueD = valueD;
	}
	public boolean isK_up() {
		return k_up;
	}
	public void setK_up(boolean k_up) {
		this.k_up = k_up;
	}
	public boolean isK_down() {
		return k_down;
	}
	public void setK_down(boolean k_down) {
		this.k_down = k_down;
	}
	public boolean isK_cross_d() {
		return k_cross_d;
	}
	public void setK_cross_d(boolean k_cross_d) {
		this.k_cross_d = k_cross_d;
	}
	public boolean isK_oversold() {
		return k_oversold;
	}
	public void setK_oversold(boolean k_oversold) {
		this.k_oversold = k_oversold;
	}
	
	public boolean isK_overbought() {
		return k_overbought;
	}
	public void setK_overbought(boolean k_overbought) {
		this.k_overbought = k_overbought;
	}
	

}
