package com.asksunny.rt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageChannelSubscriptionManager {

	private Map<String, MessageSubscriptionManager> channelSubscribers = new ConcurrentHashMap<>();

	public MessageChannelSubscriptionManager() {
		super();		
	}
	
	
	

}
