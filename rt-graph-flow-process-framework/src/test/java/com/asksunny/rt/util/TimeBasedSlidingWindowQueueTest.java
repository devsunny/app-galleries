package com.asksunny.rt.util;

import static org.junit.Assert.*;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TimeBasedSlidingWindowQueueTest {

	@Test
	public void test() throws Exception {
		TimeBasedSlidingWindowQueue<TimeBasedObject> queue = new TimeBasedSlidingWindowQueue<>(30, TimeUnit.SECONDS);
		SecureRandom rand = new SecureRandom(UUID.randomUUID().toString().getBytes());
		TimeBasedObject lastOne = null;
		for(int i=0; i<100; i++){
			lastOne = new TimeBasedObject();
			lastOne.setMessageTime(LocalDateTime.now());			
			queue.add(lastOne);
			Thread.sleep(Math.abs(rand.nextInt(10)*1000));
			System.out.println("Queue Size:" + queue.size());
			System.out.println("Time Diff:" + Duration.between(queue.peek().getTimestamp(), lastOne.getTimestamp()).toNanos()/1000000);			
			assertTrue(Duration.between(queue.peek().getTimestamp(), lastOne.getTimestamp()).toNanos() <= TimeBasedSlidingWindowQueue.SECOND_IN_NANO*30);
		}
		
	}

}
