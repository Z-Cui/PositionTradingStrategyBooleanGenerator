package com.example.candle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.patriques.AlphaVantageConnector;
import org.patriques.TimeSeries;
import org.patriques.input.timeseries.*;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.timeseries.Daily;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;

import com.example.candlecore.CandleQueue;
import com.example.candlecore.Candlestick;
import com.example.candlecore.TYPE_TREND;
import com.example.continuouspatterndetector.FourContinuationPatternDetector;
import com.example.continuouspatterndetector.ThreeContinuationPatternDetector;
import com.example.continuouspatterndetector.TwoContinuationPatternDetector;
import com.example.reversalpatterndetector.FiveReversalPatternDetector;
import com.example.reversalpatterndetector.FourReversalPatternDetector;
import com.example.reversalpatterndetector.MultipleReversalPatternDetector;
import com.example.reversalpatterndetector.OneReversalPatternDetector;
import com.example.reversalpatterndetector.TwoReversalPatternDetector;
import com.example.reversalpatterndetector.ThreeReversalPatternDetector;

// packages for technical indicators
import org.patriques.TechnicalIndicators;
import org.patriques.input.technicalindicators.*;
import org.patriques.output.technicalindicators.*;
import org.patriques.output.technicalindicators.data.IndicatorData;

import com.example.indicators.*;
import java.lang.Object.*;
import com.opencsv.CSVWriter;

import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SpringBootApplication
public class TradingPatternFinderApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(TradingPatternFinderApplication.class, args);
	    String apiKey = "L2XQBCB0BV31ZGF1";
	    int timeout = 3000;
	    AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
	    
	    // api for time series
	    TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
	    // api for technical indicators
	    TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);
	    
	    try {
	    	// Stock Code: Microsoft MSFT
	    	String stockCode = "MSFT";
	    	
	    	// Analyse recent 30 days
	    	int size_result = 30;
	    	
	    	// priceData consists of 100 days daily close, open, high, low prices
	    	Daily response = stockTimeSeries.daily(stockCode, OutputSize.COMPACT);
	    	List<StockData> priceData = response.getStockData();
	    	// for (int i = 0; i < 5; i++) System.out.println(priceData.get(i).getClose());
	    	
	    	// Interface to technical indicators
	    	IndicatorHandler indicatorHandler = new IndicatorHandler();
	    	
	    	// ---------------------------------------------------------------------------------------
	    	// StochRSI Query: 14-days close-price StochRSI, K=21, D=21
	    	STOCHRSI response_StochRSI = technicalIndicators.stochrsi(stockCode,
	    			org.patriques.input.technicalindicators.Interval.DAILY,
	    			org.patriques.input.technicalindicators.TimePeriod.of(14),
	    			org.patriques.input.technicalindicators.SeriesType.CLOSE,
	    			org.patriques.input.technicalindicators.FastKPeriod.of(21),
	    			org.patriques.input.technicalindicators.FastDPeriod.of(21),
	    			org.patriques.input.technicalindicators.FastDMaType.SMA);
	    	
	    	List<STOCHRSI_PatternData> StochRsiResult = indicatorHandler.STOCHRSI_IndicatorHandler(response_StochRSI, size_result);
	    	
	    	// ---------------------------------------------------------------------------------------
	    	// EMA50 and EMA200
	    	EMA response_EMA50 = technicalIndicators.ema(stockCode, 
	    			org.patriques.input.technicalindicators.Interval.DAILY,
	    			org.patriques.input.technicalindicators.TimePeriod.of(50),
	    			org.patriques.input.technicalindicators.SeriesType.CLOSE);
	    	EMA response_EMA200 = technicalIndicators.ema(stockCode, 
	    			org.patriques.input.technicalindicators.Interval.DAILY,
	    			org.patriques.input.technicalindicators.TimePeriod.of(200),
	    			org.patriques.input.technicalindicators.SeriesType.CLOSE);
	    	
	    	List<EMA200_EMA50_PatternData> EMA200EMA50Result = indicatorHandler
	    			.EMA200EMA50_IndicatorHandler(response_EMA200, response_EMA50, size_result, priceData);
	    	
	    	// wrote to csv file for prolog strategy testing
	    	String filename_csv = "./positionTradingStrategyBooleanTable_" + stockCode + ".csv";
	    	try {
	            // create CSVWriter object filewriter object as parameter
	    		FileWriter outputfile = new FileWriter(filename_csv); 
	            CSVWriter writer = new CSVWriter(outputfile, ',', 
	            		CSVWriter.NO_QUOTE_CHARACTER,
	            		CSVWriter.DEFAULT_ESCAPE_CHARACTER,
	            		CSVWriter.DEFAULT_LINE_END); 
	            
	            // adding header to csv 
	            String[] header = { "datetime", "ema200", "ema50", "ema200_up", "ema200_down", "ema50_up", "ema50_down",
	            		"golden_cross", "death_cross", "close_price_cross_above_ema50", "close_price_cross_below_ema200",
	            		"datetime2", "k", "d", "k_up", "k_down", "k_overbought", "k_oversold",  "k_cross_d"}; 
	            writer.writeNext(header); 
	            for (int i = 0; i < size_result; i++) {
	            	STOCHRSI_PatternData j0_stochrsi = StochRsiResult.get(i);
	            	EMA200_EMA50_PatternData j0_ema = EMA200EMA50Result.get(i);
	            	writer.writeNext(ArrayUtils.addAll(j0_stochrsi.toArray(), j0_ema.toArray()));
	            }
	            // closing writer connection 
	            writer.close(); 
	        } 
	        catch (IOException e) { 
	            e.printStackTrace(); 
	        } 
	    	
	    	// Visualize result : 5 days StochRSI and 5 days EMA200_EMA50
	    	System.out.println("---------------------------------------------------------------------------");
	    	for (int i = 0; i < 5; i++) {
	    		
	    		STOCHRSI_PatternData j0_stochrsi = StochRsiResult.get(i);
	    		System.out.println(j0_stochrsi.getDatetime() + " StochRSI K:" + j0_stochrsi.getValueK() + " D:" + j0_stochrsi.getValueD());
	    		System.out.println("K_up:" + j0_stochrsi.isK_up() + " | K_down:" + j0_stochrsi.isK_down() + 
	    				" | KcrossD:" + j0_stochrsi.isK_cross_d() + " | K_oversold:" + j0_stochrsi.isK_oversold() +
	    				" | K_overbought:" + j0_stochrsi.isK_overbought());
	    		
	    		EMA200_EMA50_PatternData j0_ema = EMA200EMA50Result.get(i);
	    		System.out.println(j0_ema.getDatetime() + " EMA200:" + j0_ema.getEma200() + " EMA50:" + j0_ema.getEma50());
	    		System.out.println("EMA200_up:" + j0_ema.isEma200_up() + " | EMA200_down:" + j0_ema.isEma200_down() +
	    				" | EMA50_up:" + j0_ema.isEma50_up() + " | EMA50_down:" + j0_ema.isEma50_down());
	    		System.out.println("GoldenCross:" + j0_ema.isEma200_ema50_goldencross() +
	    				" | DeathCross:" + j0_ema.isEma200_ema50_deathcross() +
	    				" | ClosePriceCrossAboveEMA50:" + j0_ema.isClose_price_break_above_ema50() +
	    				" | ClosePriceCrossBelowEMA200:" + j0_ema.isClose_price_break_below_ema200());
	    		
	    		System.out.println("-----");
	    		System.out.println(Arrays.toString(j0_stochrsi.toArray()));
	    		System.out.println(Arrays.toString(j0_ema.toArray()));
	    		System.out.println("---------------------------------------------------------------------------");
	    		
	    	}
	    	
	    	//private static final String CSV_SEPARATOR = ",";
	    	
	    	
	    	
	    	/*
	    	// Due to same name issue between org.patriques.input.timeseries.Interval and org.patriques.input.technicalindicators.Interval, 
	    	// Please use full name of the package
	    	IntraDay response = stockTimeSeries.intraDay("MSFT", 
	    			org.patriques.input.timeseries.Interval.ONE_MIN, 
	    			OutputSize.COMPACT);
	    	Daily response = stockTimeSeries.daily("MSFT", OutputSize.COMPACT);
	    	Map<String, String> metaData = response.getMetaData();
	    	System.out.println("Information: " + metaData.get("1. Information"));
	    	System.out.println("Stock: " + metaData.get("2. Symbol"));
	    	
	    	List<StockData> stockData = response.getStockData();
	    	stockData.forEach(stock -> {
	    		Candlestick test = new  Candlestick( stock.getOpen(), stock.getHigh(), stock.getLow(), stock.getClose(), stock.getDateTime(),
	    				CandleQueue.getTrendFromMa(stock.getClose(), 5));
	    		CandleQueue.addCandlestick(test);
	    		System.out.println(test);
	    		
	    		HashMap<String, Boolean> patterns = OneReversalPatternDetector.patternReversalCheking(null);
	    		if(CandleQueue.getQueueSize() > 1) {
	    			TwoReversalPatternDetector.patternRevesalChecking(patterns);
	    			TwoContinuationPatternDetector.patternReversalCheking(patterns);
	    			}
	    		if (CandleQueue.getQueueSize() > 2) {
	    			ThreeReversalPatternDetector.patternReversalCheking(patterns);
	    			ThreeContinuationPatternDetector.patternReversalCheking(patterns); 
	    			}
	    		if (CandleQueue.getQueueSize() > 3) {
	    			FourReversalPatternDetector.patternReversalCheking(patterns);
	    			FourContinuationPatternDetector.patternReversalCheking(patterns);
	    			}
	    		if (CandleQueue.getQueueSize() > 4) {
	    			FiveReversalPatternDetector.patternReversalCheking(patterns);
	    			}
	    		MultipleReversalPatternDetector.patternReversalCheking(patterns);
	    		
	    		for ( Map.Entry<String, Boolean> entry : patterns.entrySet()) {
	    			String key = entry.getKey();
	    			Boolean tab = entry.getValue();
	    			
	    			if(tab) 
	    				System.out.println(key+" : "+tab);
	    			// do something with key and/or tab
	    			}
	    		System.out.println(patterns.size());
	    		});
	    	*/
	    	
	    	/*
	    	// ---------------------------------------------------------------------------------------
	    	// RSI Query: 14-days(period) close-price daily RSI
	    	RSI response_RSI_long = technicalIndicators.rsi(stockCode, 
	    			org.patriques.input.technicalindicators.Interval.DAILY, 
	    			TimePeriod.of(10), SeriesType.CLOSE);
	    	RSI response_RSI_short = technicalIndicators.rsi(stockCode, 
	    			org.patriques.input.technicalindicators.Interval.DAILY, 
	    			TimePeriod.of(5), SeriesType.CLOSE);
	    	
	    	// Analyse recent 30 days
	    	int size_result = 30;
	    	
	    	// RSI result of day i consists of following booleans : TrendUpLong, TrendUpShort, TrendDownLong, 
	    	// TrendDownShort, OverboughtLong, OverboughtShort, OversoldLong, OversoldShort, ShortUpCrossLong, ShortDownCrossLong
	    	List<RSI_PatternData> rsiResult = indicatorHandler.RSI_IndicatorHander(response_RSI_short, response_RSI_long, size_result);
	    	// e.g. rsiResult.get(0).isTrendUpLong() returns true if the last trading day's long-term trend goes up.
	    	
	    	// Visualize result: 3 days RSI
	    	for (int i = 0; i < 3; i++) {
	    		RSI_PatternData j0 = rsiResult.get(i); 
	    		System.out.println(j0.getDatetime()+" RSI_Long:"+j0.getRsiLong()+" RSI_Short:"+j0.getRsiShort());
	    		System.out.println("TrendUpLong, TrendDownLong, TrendUpShort, TrendDownShort, "
	    				+ "OverboughtLong, OverboughtShort, OversoldLong, OversoldShort, ShortUpCrossLong, ShortDownCrossLong");
	    		System.out.println(j0.isTrendUpLong()+"         "+j0.isTrendDownLong()+"           "
	    				+ j0.isTrendUpShort()+"          "+j0.isTrendDownShort()+"           "+j0.isOverboughtLong()
	    				+ "           "+j0.isOverboughtShort()+"           "+j0.isOversoldLong()+"           "+j0.isOversoldShort()
	    				+ "           "+j0.isShortUpCrossLong()+"             "+j0.isShortDownCrossLong());
	    	}
	    	
	    	// ---------------------------------------------------------------------------------------
	    	// STOCH Query
	    	STOCHF response_STOCH_fast = technicalIndicators.stochf(stockCode, org.patriques.input.technicalindicators.Interval.DAILY, 
	    			FastKPeriod.of(14), FastDPeriod.of(3), FastDMaType.SMA);
	    	STOCH response_STOCH_slow = technicalIndicators.stoch(stockCode, org.patriques.input.technicalindicators.Interval.DAILY, 
	    			FastKPeriod.of(14), SlowKPeriod.of(3), SlowDPeriod.of(3), SlowKMaType.SMA, SlowDMaType.SMA);
	    	// Analyse recent 30 days
	    	int size_result = 30;
	    	
	    	// STOCH result of day i consists of following booleans : Overbought, Oversold, 
	    	// fastKDownCrossfastD, slowKDownCrossSlowD, fastKUpCrossfastD, slowKUpCrossslowD
	    	List<STOCH_PatternData> stochResult = indicatorHandler.STOCH_IndicatorHandler(response_STOCH_fast, response_STOCH_slow, size_result);
	    	// e.g. stochResult.get(0).isOversold() returns true if the last trading day's STOCH < 20.
	    	
	    	// Visualize result: 3 days STOCH
	    	for (int i = 0; i < 3; i++) {
	    		STOCH_PatternData j0 = stochResult.get(i);
	    		System.out.println(j0.getDatetime()+" fastK:"+j0.getFastK()+" fastD/slowK:"+j0.getSlowK()+" slowD:"+j0.getSlowD());
	    		System.out.println("Overbought, Oversold, fastKDownCrossfastD, slowKDownCrossSlowD, fastKUpCrossfastD, slowKUpCrossslowD");
	    		System.out.println(j0.isOverbought()+"        "+j0.isOversold()+"       "+j0.isFastKDownCrossfastD()+"                 "
	    				+j0.isSlowKDownCrossSlowD()+"                 "+j0.isFastKUpCrossfastD()+"               "+j0.isSlowKUpCrossslowD());
	    	}
	    	
	    	// ---------------------------------------------------------------------------------------
	    	// Williams Query
	    	WILLR response_WILLR = technicalIndicators.willr(stockCode, org.patriques.input.technicalindicators.Interval.DAILY, TimePeriod.of(14));
	    	List<WILLR_PatternData> willrResult = indicatorHandler.WILLR_IndicatorHandler(response_WILLR, size_result);
	    	
	    	// Visualize result: 3 days Williams %R
	    	for (int i = 0; i < 3; i++) {
	    		WILLR_PatternData j0 = willrResult.get(i);
	    		System.out.println(j0.getDatetime()+" "+j0.getWillr()+" overbought:"+j0.isOverbought()+" oversold:"+j0.isOversold());
	    	}
	    	*/
	    	
	    	} catch (AlphaVantageException e) {
	    		System.out.println("something went wrong");
	    	}
	    }
}