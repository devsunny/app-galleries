package com.asksunny.rt.util;

import java.time.LocalDateTime;

public class TimeBasedObject implements TimeBased {

	
	private LocalDateTime messageTime;
	
	
	public TimeBasedObject() {
		
	}
	
	
	

	@Override
	public LocalDateTime getTimestamp() {
		
		return messageTime;
	}



	public LocalDateTime getMessageTime() {
		return messageTime;
	}



	public void setMessageTime(LocalDateTime messageTime) {
		this.messageTime = messageTime;
	}

}
