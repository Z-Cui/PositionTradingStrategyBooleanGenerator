package com.example.candle;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.patriques.AlphaVantageConnector;
import org.patriques.TechnicalIndicators;
import org.patriques.TimeSeries;
import org.patriques.input.technicalindicators.SeriesType;
import org.patriques.input.technicalindicators.TimePeriod;
//import org.patriques.input.timeseries.Interval;
import org.patriques.input.technicalindicators.Interval;
import org.patriques.input.timeseries.OutputSize;
import org.patriques.output.AlphaVantageException;
import org.patriques.output.technicalindicators.RSI;
import org.patriques.output.technicalindicators.data.IndicatorData;
import org.patriques.output.timeseries.Daily;
import org.patriques.output.timeseries.IntraDay;
import org.patriques.output.timeseries.data.StockData;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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

import com.example.indicators.*;

@SpringBootApplication
public class TradingPatternFinderApplication {

	public static void main(String[] args) {
		SpringApplication.run(TradingPatternFinderApplication.class, args);
	    String apiKey = "GGKBONYCY3BTIB5J";
	    int timeout = 3000;
	    AlphaVantageConnector apiConnector = new AlphaVantageConnector(apiKey, timeout);
	    TimeSeries stockTimeSeries = new TimeSeries(apiConnector);
	    
	    // api for technical indicators
	    TechnicalIndicators technicalIndicators = new TechnicalIndicators(apiConnector);
	    
	    
	    try {
	      
	      // Due to same name issue between org.patriques.input.timeseries.Interval and org.patriques.input.technicalindicators.Interval, 
	      // I recommend we use daily trading data to avoid this API package issue. That's why I replaced the query by [Daily] funciton.
	      // IntraDay response = stockTimeSeries.intraDay("MSFT", Interval.ONE_MIN, OutputSize.COMPACT);
	      Daily response = stockTimeSeries.daily("MSFT", OutputSize.COMPACT);
	      Map<String, String> metaData = response.getMetaData();
	      System.out.println("Information: " + metaData.get("1. Information"));
	      System.out.println("Stock: " + metaData.get("2. Symbol"));
	      
	      List<StockData> stockData = response.getStockData();
	      stockData.forEach(stock -> {
	    	  Candlestick test = new  Candlestick( stock.getOpen(), stock.getHigh(), stock.getLow(), stock.getClose(), stock.getDateTime(),
	    			  CandleQueue.getTrendFromMa(stock.getClose(), 5));
	    	  CandleQueue.addCandlestick(test);
	    	  //System.out.println(test);
	    	  
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
	    		    System.out.println(key);
	    		    // do something with key and/or tab
	    		}
	      });
	      
	      // Interface to technical indicators
	      IndicatorHandler indicatorHandler = new IndicatorHandler();
	      
	      String stockCode = "MSFT";
	      // Query: 14-days(period) close-price daily RSI
	      RSI response_RSI_long = technicalIndicators.rsi(stockCode, Interval.DAILY, TimePeriod.of(10), SeriesType.CLOSE);
	      RSI response_RSI_short = technicalIndicators.rsi(stockCode, Interval.DAILY, TimePeriod.of(5), SeriesType.CLOSE);
	      
	      // Analyse RSI of recent 30 days
	      int size_result = 30;
	      List<RSI_PatternData> rsiResult = indicatorHandler.RSI_IndicatorHander(response_RSI_short, response_RSI_long, size_result);
	      
	      // Visualize result
	      for (int i = 0; i < size_result; i++) {
	    	  //System.out.println("--- Date, RSI Long, RSI Short");
		      System.out.println(rsiResult.get(i).getDatetime()+" Long:"+rsiResult.get(i).getRsiLong()+" Short:"+rsiResult.get(i).getRsiShort());
		      System.out.println("TrendUpLong, TrendDownLong, TrendUpShort, TrendDownShort, "
		      		+ "OverboughtLong, OverboughtShort, OversoldLong, OversoldShort, ShortUpCrossLong, ShortDownCrossLong");
		      System.out.println(rsiResult.get(i).isTrendUpLong()+" "+rsiResult.get(i).isTrendDownLong()+" "
		      		+ rsiResult.get(i).isTrendUpShort()+" "+rsiResult.get(i).isTrendDownShort()+" "+rsiResult.get(i).isOverboughtLong()
		      		+ " "+rsiResult.get(i).isOverboughtShort()+" "+rsiResult.get(i).isOversoldLong()+" "+rsiResult.get(i).isOversoldShort()
		      		+ " "+rsiResult.get(i).isShortUpCrossLong()+" "+rsiResult.get(i).isShortDownCrossLong());
	      }
	      
	    } catch (AlphaVantageException e) {
	      System.out.println("something went wrong");
	    }
	}

}