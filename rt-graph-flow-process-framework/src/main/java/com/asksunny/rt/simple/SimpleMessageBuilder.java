package com.asksunny.rt.simple;

import java.time.LocalDate;
import java.time.LocalTime;

public class SimpleMessageBuilder {
	private String channelId;
	private String symbol;
	private double askPrice;
	private double bidPrice;
	private int askSize;
	private int bidSize;
	private LocalDate quoteDate;
	private LocalTime quoteTime;

	public static SimpleMessageBuilder newBuilder() {
		return new SimpleMessageBuilder();
	}

	public SimpleMessageBuilder channelId(String channelId) {
		this.channelId = channelId;
		return this;
	}

	public SimpleMessageBuilder symbol(String symbol) {
		this.symbol = symbol;
		return this;
	}

	public SimpleMessageBuilder askPrice(double askPrice) {
		this.askPrice = askPrice;
		return this;
	}

	public SimpleMessageBuilder bidPrice(double bidPrice) {
		this.bidPrice = bidPrice;
		return this;
	}

	public SimpleMessageBuilder askSize(int askSize) {
		this.askSize = askSize;
		return this;
	}

	public SimpleMessageBuilder bidSize(int bidSize) {
		this.bidSize = bidSize;
		return this;
	}

	public SimpleMessageBuilder quoteDate(LocalDate quoteDate) {
		this.quoteDate = quoteDate;
		return this;
	}

	public SimpleMessageBuilder quoteTime(LocalTime quoteTime) {
		this.quoteTime = quoteTime;
		return this;
	}

	public SimpleMessage build() {

		return new SimpleMessage(channelId, symbol, askPrice, bidPrice, askSize, bidSize, quoteDate, quoteTime);
	}

}