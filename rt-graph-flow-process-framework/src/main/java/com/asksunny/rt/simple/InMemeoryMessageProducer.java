package com.asksunny.rt.simple;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.asksunny.rt.MessageDispatcher;

public class InMemeoryMessageProducer implements Runnable {

	private ConcurrentLinkedQueue<MessageDispatcher> dispatchers = new ConcurrentLinkedQueue<>();

	public InMemeoryMessageProducer(Collection<MessageDispatcher> dispatchers) {
		this.dispatchers.addAll(dispatchers);
	}

	public void run() {
		long max = 100_000_000_000L;
		System.out.println(LocalTime.now());
		LocalTime start = LocalTime.now();
		while (max > 0) {
			SimpleMessage message = SimpleMessageBuilder.newBuilder().channelId("TEST").symbol("IBM").askPrice(100.01)
					.askSize(200).bidPrice(102.12).bidSize(100).build();
			this.dispatchers.stream().forEach(d -> d.dispatch(message));
			max--;
		}
		LocalTime end = LocalTime.now();
		System.out.println(Duration.between(start, end).toMinutes());
		System.out.println(LocalTime.now());
	}

	public static void main(String[] args) {		
		
		DateTimeFormatter formater = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSSSSS");
		LocalDateTime dt1 = LocalDateTime.parse("2016-09-20 12:36:54.689820", formater);
		LocalDateTime dt2 = LocalDateTime.parse("2016-09-20 12:40:36.018716", formater);	
		
		System.out.println(dt1.isBefore(dt2));
		System.out.println(dt1.isAfter(dt2));
		
		if(Duration.between(dt1, dt2).isNegative()){
			System.out.println("Date 2 is later");
		}else{
			System.out.println("Date 2 is same or ealier");
		}
		
		
		
//		ExecutorService executor = Executors.newSingleThreadExecutor();
//		SimpleMessageDispatcher dispatcher = new SimpleMessageDispatcher();
//		executor.execute(new InMemeoryMessageProducer(Collections.singletonList(dispatcher)));
//		executor.shutdown();
	}

}
