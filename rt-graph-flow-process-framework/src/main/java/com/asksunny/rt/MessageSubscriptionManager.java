package com.asksunny.rt;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MessageSubscriptionManager {

	private Map<MessageSubscription, MessageSubscriber> subscribers = new ConcurrentHashMap<>();
	
	
	public MessageSubscriptionManager() {
		super();
	}

}
