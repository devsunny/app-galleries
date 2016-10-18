package com.asksunny.rt;

import java.util.List;

public interface MessageSubscriptionResolver {
	List<MessageSubscription> resolve(String subscriptionPattern);

}
