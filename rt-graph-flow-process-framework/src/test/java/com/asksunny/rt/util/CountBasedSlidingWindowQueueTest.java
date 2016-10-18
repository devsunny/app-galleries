package com.asksunny.rt.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class CountBasedSlidingWindowQueueTest {

	@Test
	public void test() {
		CountBasedSlidingWindowQueue<Object> objs = new CountBasedSlidingWindowQueue<>(10);
		for (int i = 0; i < 100; i++) {
			Object o = new Object();
			objs.add(o);
			assertTrue(objs.size()<=10);
		}
	}

}
