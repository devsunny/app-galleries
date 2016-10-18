package com.asksunny.rt.simple;

import java.time.LocalTime;

import com.asksunny.rt.FlowMessage;
import com.asksunny.rt.MessageDispatcher;

public class SimpleMessageDispatcher implements MessageDispatcher {

	private long count = 0L;
	
	public SimpleMessageDispatcher() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void dispatch(FlowMessage message) {
		
		count++;
		if(count%1000000L==0){
			System.out.println(count);
			System.out.println(LocalTime.now());
		}
	}

}
