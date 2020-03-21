package com.example.indicators;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.patriques.input.technicalindicators.*;
import org.patriques.output.technicalindicators.*;
import org.patriques.output.technicalindicators.data.IndicatorData;
import org.patriques.output.technicalindicators.data.MACDData;

public class IndicatorHandler {
	
	// Function: RSI_IndicatorHander(RSI response_RSI)
	// Input:    TechnicalIndicatorResponse response_RSI
	// Output:   
	public List<RSI_PatternData> RSI_IndicatorHander(RSI responseShort, RSI responseLong, int size_result) { 
		
		List<IndicatorData> rsiShort = responseShort.getData();
		List<IndicatorData> rsiLong = responseLong.getData();
		
		List<IndicatorData> rsiShort2 =  rsiShort.stream().limit(size_result+2).collect(Collectors.toList());
		List<IndicatorData> rsiLong2 =  rsiLong.stream().limit(size_result+2).collect(Collectors.toList());
		List<RSI_PatternData> result = new ArrayList<RSI_PatternData>();
		
		// copy rsi data to result list
		for (int i = 0; i < size_result+2; i++) {	
			// initialize an instance: date, rsiShort, rsiLong
			RSI_PatternData tmp = new RSI_PatternData(rsiShort2.get(i).getDateTime(), rsiShort2.get(i).getData(), rsiLong2.get(i).getData());
			result.add(tmp);
		}
		
		// analyse [size_result] days' RSI
		for (int i = 0; i < size_result; i++) {
			
			// if rsi > 70, then overbought
			if (result.get(i).getRsiLong() > 70) result.get(i).setOverboughtLong(true);
			if (result.get(i).getRsiShort() > 70) result.get(i).setOverboughtShort(true);
			
			// if rsi < 30, then oversold
			if (result.get(i).getRsiLong() < 30) result.get(i).setOversoldLong(true);
			if (result.get(i).getRsiShort() < 30) result.get(i).setOversoldShort(true);
			
			// if rsi > 50 and j > j-1 and j-1 > j-2, then trendUp
			if (result.get(i).getRsiLong() > 50
					& result.get(i).getRsiLong() > result.get(i+1).getRsiLong()
					& result.get(i+1).getRsiLong() > result.get(i+2).getRsiLong()) result.get(i).setTrendUpLong(true);
			if (result.get(i).getRsiShort() > 50
					& result.get(i).getRsiShort() > result.get(i+1).getRsiShort()
					& result.get(i+1).getRsiShort() > result.get(i+2).getRsiShort()) result.get(i).setTrendUpShort(true);
			
			// if rsi < 50 and j < j-1 and j-1 < j-2, then trendDown
			if (result.get(i).getRsiLong() < 50
					& result.get(i).getRsiLong() < result.get(i+1).getRsiLong()
					& result.get(i+1).getRsiLong() < result.get(i+2).getRsiLong()) result.get(i).setTrendDownLong(true);
			if (result.get(i).getRsiShort() < 50
					& result.get(i).getRsiShort() < result.get(i+1).getRsiShort()
					& result.get(i+1).getRsiShort() < result.get(i+2).getRsiShort()) result.get(i).setTrendDownShort(true);
			
			// case: short RSI rises and crosses long RSI, a bullish trend
			// if Short j > Long j and Short j-1 < Long j-1 and Short j > Short j-1, then shortUpCrossLong
			if (result.get(i).getRsiShort() > result.get(i).getRsiLong()
					& result.get(i+1).getRsiShort() < result.get(i+1).getRsiLong()
					& result.get(i).getRsiShort() > result.get(i+1).getRsiShort()) result.get(i).setShortUpCrossLong(true);
			
			// case: short RSI declines and crosses long RSI, a bearish trend
			// if Short j < Long j and Short j-1 > Long j-1 and Short j < Short j-1, then shortDownCrossLong
			if (result.get(i).getRsiShort() < result.get(i).getRsiLong()
					& result.get(i+1).getRsiShort() > result.get(i+1).getRsiLong()
					& result.get(i).getRsiShort() < result.get(i+1).getRsiShort()) result.get(i).setShortDownCrossLong(true);
		}
		
		// only return [size_result] days' RSI analysis
		result =  result.stream().limit(size_result).collect(Collectors.toList());
		return result;
	}
	
	
}
