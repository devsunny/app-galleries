package com.asksunny.rt;

public class SimpleMessageSubscription implements MessageSubscription {

	private String name;
	
	
	public SimpleMessageSubscription() {
		
	}

	@Override
	public String getSubscriptionPattern() {
		
		return null;
	}

	@Override
	public String getSubscriptionChannel() {
		
		return null;
	}

	@Override
	public boolean accept(FlowMessage message) {
		
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SimpleMessageSubscription other = (SimpleMessageSubscription) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	

}
