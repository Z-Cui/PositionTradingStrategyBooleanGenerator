package com.example.indicators;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.patriques.output.technicalindicators.*;
import org.patriques.output.technicalindicators.data.IndicatorData;
import org.patriques.output.technicalindicators.data.STOCHDataFast;
import org.patriques.output.technicalindicators.data.STOCHDataSlow;
import org.patriques.output.timeseries.data.StockData;

public class IndicatorHandler {
	
	public List<EMA200_EMA50_PatternData> EMA200EMA50_IndicatorHandler(
			EMA response_EMA200, EMA response_EMA50, int size_result, List<StockData> priceData) {
		
		List<EMA200_EMA50_PatternData> result = new ArrayList<EMA200_EMA50_PatternData>();
		List<IndicatorData> ema200 = response_EMA200.getData();
		List<IndicatorData> ema50 = response_EMA50.getData();
		
		// copy EMA data to result list
		for (int i = 0; i < size_result+1; i++) {
			IndicatorData e200 = ema200.get(i);
			IndicatorData e50 = ema50.get(i);
			EMA200_EMA50_PatternData tmp = new EMA200_EMA50_PatternData(e200.getDateTime(), e200.getData(), e50.getData());
			result.add(tmp);
		}
		
		// analyze EMA
		for (int i = 0; i < size_result; i++) {
			EMA200_EMA50_PatternData j0 = result.get(i);
			EMA200_EMA50_PatternData j1 = result.get(i+1);
			
			// ema200_up
			if ( j0.getEma200() > j1.getEma200() ) j0.setEma200_up(true);
			// ema200_down
			if ( j0.getEma200() < j1.getEma200() ) j0.setEma200_down(true);
			// ema50_up
			if ( j0.getEma50() > j1.getEma50() ) j0.setEma50_up(true);
			// ema50_down
			if ( j0.getEma50() < j1.getEma50() ) j0.setEma50_down(true);
			// ema200 ema50 golden cross
			if ( j0.getEma50() > j0.getEma200() && j1.getEma50() < j1.getEma200() ) j0.setEma200_ema50_goldencross(true);
			// ema200 ema50 death cross
			if ( j0.getEma50() < j0.getEma200() && j1.getEma50() > j1.getEma200() ) j0.setEma200_ema50_deathcross(true);
			
			// close price breaks above ema50
			if ( j0.getEma50() < priceData.get(i).getClose() && j1.getEma50() > priceData.get(i+1).getClose() )
				j0.setClose_price_break_above_ema50(true);
			// close price breaks below ema200
			if ( j0.getEma200() > priceData.get(i).getClose() && j1.getEma200() < priceData.get(i+1).getClose() )
				j0.setClose_price_break_below_ema200(true);
			
		}
		// only return [size_result] days' EMA200_EMA50 analysis
		result =  result.stream().limit(size_result).collect(Collectors.toList());
		return result;
	}
	
	public List<STOCHRSI_PatternData> STOCHRSI_IndicatorHandler(STOCHRSI responseStochRSI, int size_result) {
		List<STOCHRSI_PatternData> result = new ArrayList<STOCHRSI_PatternData>();
		List<STOCHDataFast> stochRSI = responseStochRSI.getData();
		
		// copy STOCHRSI data to result list
		for (int i = 0; i < size_result+1; i++) {
			STOCHDataFast t = stochRSI.get(i);
			STOCHRSI_PatternData tmp = new STOCHRSI_PatternData(t.getDateTime(), t.getFastK(), t.getFastD());
			result.add(tmp);
		}
		
		// analyze StochRSI
		for (int i = 0; i < size_result; i++) {
			STOCHRSI_PatternData j0 = result.get(i);
			STOCHRSI_PatternData j1 = result.get(i+1);
			
			// if K increases more than 1% than previous day
			if ( j0.getValueK() > j1.getValueK() + 1.0 ) j0.setK_up(true);
			
			// if K decreases more than 1% than previous day
			if ( j0.getValueK() + 1.0 < j1.getValueK() ) j0.setK_down(true);
			
			// if K crosses D
			if ( j0.getValueK() > j0.getValueD() && j1.getValueK() < j1.getValueD() ) j0.setK_cross_d(true);
			if ( j0.getValueK() < j0.getValueD() && j1.getValueK() > j1.getValueD() ) j0.setK_cross_d(true);
			
			// K oversold < 30%
			if ( j0.getValueK() < 30 ) j0.setK_oversold(true);
			
			// K overbought > 70%
			if ( j0.getValueK() > 70 ) j0.setK_overbought(true);
			
		}
		
		// only return [size_result] days' STOCHRSI analysis
		result =  result.stream().limit(size_result).collect(Collectors.toList());
		return result;
	}
	
	
	public List<RSI_PatternData> RSI_IndicatorHander(RSI responseShort, RSI responseLong, int size_result) { 
		
		List<IndicatorData> rsiShort = responseShort.getData();
		List<IndicatorData> rsiLong = responseLong.getData();
		
		List<RSI_PatternData> result = new ArrayList<RSI_PatternData>();
		
		// copy RSI data to result list
		for (int i = 0; i < size_result+2; i++) {	
			// initialize an instance: date, rsiShort, rsiLong
			RSI_PatternData tmp = new RSI_PatternData(rsiShort.get(i).getDateTime(), rsiShort.get(i).getData(), rsiLong.get(i).getData());
			result.add(tmp);
		}
		
		// analyse [size_result] days' RSI
		for (int i = 0; i < size_result; i++) {
			
			RSI_PatternData j0 = result.get(i); // day j
			RSI_PatternData j1 = result.get(i+1); // day j-1
			RSI_PatternData j2 = result.get(i+2); // day j-2
			
			// if RSI > 70, then overbought
			if (j0.getRsiLong() > 70) j0.setOverboughtLong(true);
			if (j0.getRsiShort() > 70) j0.setOverboughtShort(true);
			
			// if RSI < 30, then oversold
			if (j0.getRsiLong() < 30) j0.setOversoldLong(true);
			if (j0.getRsiShort() < 30) j0.setOversoldShort(true);
			
			// if RSI > 50 and j > j-1 and j-1 > j-2, then trendUp
			if (j0.getRsiLong() > 50
					& j0.getRsiLong() > j1.getRsiLong()
					& j1.getRsiLong() > j2.getRsiLong()) j0.setTrendUpLong(true);
			if (j0.getRsiShort() > 50
					& j0.getRsiShort() > j1.getRsiShort()
					& j1.getRsiShort() > j2.getRsiShort()) j0.setTrendUpShort(true);
			
			// if RSI < 50 and j < j-1 and j-1 < j-2, then trendDown
			if (j0.getRsiLong() < 50
					& j0.getRsiLong() < j1.getRsiLong()
					& j1.getRsiLong() < j2.getRsiLong()) j0.setTrendDownLong(true);
			if (j0.getRsiShort() < 50
					& j0.getRsiShort() < j1.getRsiShort()
					& j1.getRsiShort() < j2.getRsiShort()) j0.setTrendDownShort(true);
			
			// case: short RSI rises and crosses long RSI, a bullish trend
			// if Short j > Long j and Short j-1 < Long j-1 and Short j > Short j-1, then shortUpCrossLong
			if (j0.getRsiShort() > j0.getRsiLong()
					& j1.getRsiShort() < j1.getRsiLong()
					& j0.getRsiShort() > j1.getRsiShort()) j0.setShortUpCrossLong(true);
			
			// case: short RSI declines and crosses long RSI, a bearish trend
			// if Short j < Long j and Short j-1 > Long j-1 and Short j < Short j-1, then shortDownCrossLong
			if (j0.getRsiShort() < j0.getRsiLong()
					& j1.getRsiShort() > j1.getRsiLong()
					& j0.getRsiShort() < j1.getRsiShort()) j0.setShortDownCrossLong(true);
		}
		
		// only return [size_result] days' RSI analysis
		result =  result.stream().limit(size_result).collect(Collectors.toList());
		return result;
	}
	
	public List<STOCH_PatternData> STOCH_IndicatorHandler(STOCHF response_STOCH_fast, STOCH response_STOCH_slow, int size_result) {
		
		List<STOCHDataFast> stochFast = response_STOCH_fast.getData();
		List<STOCHDataSlow> stochSlow = response_STOCH_slow.getData();
		
		List<STOCH_PatternData> result = new ArrayList<STOCH_PatternData>();
		
		// copy STOCH data to result list
		for (int i = 0; i < size_result+1; i++) {
			// initialize an instance: date, fastk, fastd(also called slowk), slowd
			STOCH_PatternData tmp = new STOCH_PatternData(stochFast.get(i).getDateTime(), stochFast.get(i).getFastK(), 
					stochSlow.get(i).getSlowK(), stochSlow.get(i).getSlowD());
			result.add(tmp);
		}
		
		// analyze STOCH
		for (int i = 0; i < size_result; i++) {
			
			STOCH_PatternData j0 = result.get(i); // day j
			STOCH_PatternData j1 = result.get(i+1); // day j-1
			
			// if fastK > 80, then overbought
			if (j0.getFastK() > 80) j0.setOverbought(true);
			
			// if fastK < 20, then oversold
			if (j0.getFastK() < 20) j0.setOversold(true);
			
			// if fastK goes down and crosses fastD(slowK), then sharpTrendDown
			if (j0.getFastK() < j1.getFastK()
					& j0.getFastK() < j0.getSlowK()
					& j1.getFastK() > j1.getSlowK())
				j0.setFastKDownCrossfastD(true);
			
			// if slowK goes down and crosses slowD, then smoothTrendDown
			if (j0.getSlowK() < j1.getSlowK()
					& j0.getSlowK() < j0.getSlowD()
					& j1.getSlowK() > j1.getSlowD())
				j0.setSlowKDownCrossSlowD(true);
			
			// if fastK goes up and crosses fastD(slowK), then sharpTrendUp
			if (j0.getFastK() > j1.getFastK()
					& j0.getFastK() > j0.getSlowK()
					& j1.getFastK() < j1.getSlowK())
				j0.setFastKUpCrossfastD(true);
			
			// if slowK goes up and crosses slowD, then smoothTrendUp
			if (j0.getSlowK() > j1.getSlowK()
					& j0.getSlowK() > j0.getSlowD()
					& j1.getSlowK() < j1.getSlowD())
				j0.setSlowKUpCrossslowD(true);
		}
		
		// only return [size_result] days' RSI analysis
		result =  result.stream().limit(size_result).collect(Collectors.toList());
		return result;
	}
	
	public List<WILLR_PatternData> WILLR_IndicatorHandler(WILLR response_WILLR, int size_result){
	//public List<WILLR_PatternData> WILLR_IndicatorHandler(List<IndicatorData> willrData, int size_result){
		
		List<IndicatorData> willrData = response_WILLR.getData();
		
		//System.out.println(" ");
		
		List<WILLR_PatternData> result = new ArrayList<WILLR_PatternData>();
		
		for (int i = 0; i < size_result; i++) {
			WILLR_PatternData tmp = new WILLR_PatternData(willrData.get(i).getDateTime(), willrData.get(i).getData());
			if (-20 < willrData.get(i).getData()) tmp.setOverbought(true);
			if (willrData.get(i).getData() < -80) tmp.setOversold(true);
			result.add(tmp);
		}
		
		return result;
	}
	
}
