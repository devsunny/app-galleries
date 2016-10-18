package com.asksunny.rt;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MessageSubscriptionStore {

	private String subscriptionId;
	private Queue<MessageSubscriber> subscribers = new ConcurrentLinkedQueue<>();

	public MessageSubscriptionStore(String subscriptionId) {
		super();
		this.subscriptionId = subscriptionId;
	}

	/**
	 * At this point, subscribe has already been subscribed to MessageChannel
	 * @param id
	 * @param subscriber
	 */
	public void subscribe(String id, MessageSubscriber subscriber) 
	{
		if(subscriptionId.equalsIgnoreCase(id)){
			subscribers.add(subscriber);
		}else{
			
		}
	}

	public String getSubscriptionId() {
		return subscriptionId;
	}

	public void setSubscriptionId(String subscriptionId) {
		this.subscriptionId = subscriptionId;
	}

	public Queue<MessageSubscriber> getSubscribers() {
		return subscribers;
	}

	public void setSubscribers(List<MessageSubscriber> subscribers) {
		this.subscribers.addAll(subscribers);
	}

}
