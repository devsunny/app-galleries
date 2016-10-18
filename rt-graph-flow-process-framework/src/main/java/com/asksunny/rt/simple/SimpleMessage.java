package com.asksunny.rt.simple;

import java.time.LocalDate;
import java.time.LocalTime;

import com.asksunny.rt.FlowMessage;

public class SimpleMessage implements FlowMessage {

	private String channelId;
	private String symbol;
	private double askPrice;
	private double bidPrice;
	private int askSize;
	private int bidSize;
	private LocalDate quoteDate;
	private LocalTime quoteTime;

	SimpleMessage(String channelId, String symbol, double askPrice, double bidPrice, int askSize, int bidSize,
			LocalDate quoteDate, LocalTime quoteTime) {
		super();
		this.channelId = channelId;
		this.symbol = symbol;
		this.askPrice = askPrice;
		this.bidPrice = bidPrice;
		this.askSize = askSize;
		this.bidSize = bidSize;
		this.quoteDate = quoteDate;
		this.quoteTime = quoteTime;
	}

	public SimpleMessage() {
	}

	@Override
	public String getChannelId() {
		return channelId;
	}

	@Override
	public String getMessageId() {
		return symbol;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public double getAskPrice() {
		return askPrice;
	}

	public void setAskPrice(double askPrice) {
		this.askPrice = askPrice;
	}

	public double getBidPrice() {
		return bidPrice;
	}

	public void setBidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
	}

	public int getAskSize() {
		return askSize;
	}

	public void setAskSize(int askSize) {
		this.askSize = askSize;
	}

	public int getBidSize() {
		return bidSize;
	}

	public void setBidSize(int bidSize) {
		this.bidSize = bidSize;
	}

	public LocalDate getQuoteDate() {
		return quoteDate;
	}

	public void setQuoteDate(LocalDate quoteDate) {
		this.quoteDate = quoteDate;
	}

	public LocalTime getQuoteTime() {
		return quoteTime;
	}

	public void setQuoteTime(LocalTime quoteTime) {
		this.quoteTime = quoteTime;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	

	
}
