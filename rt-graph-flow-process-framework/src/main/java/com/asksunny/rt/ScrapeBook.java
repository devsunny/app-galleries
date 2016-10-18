package com.asksunny.rt;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScrapeBook {

	public ScrapeBook() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception {
		LocalDateTime ldt = LocalDateTime.parse("2016-09-20 11:05:33.074215", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS"));
		LocalDateTime end = ldt.minusNanos(100000);
		LocalDateTime start = ldt.plusNanos(100000);
		System.out.println(end.toString());
		System.out.println(start.toString());
	}

}
