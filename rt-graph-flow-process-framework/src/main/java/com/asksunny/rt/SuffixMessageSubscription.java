package com.asksunny.rt;

public class SuffixMessageSubscription extends SimpleMessageSubscription {

	private String suffix;
	private String shortName;
	private String name;
	private boolean useShortName = false;

	public SuffixMessageSubscription() {

	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		String[] names = name.split(SUBSCRIPTION_DELIMITER);
		if(names.length>0){
			this.name = name;
			super.setName(this.name);
			this.useShortName = true;
		}else if(names.length>1){
			this.name = name;
			this.shortName = names[0];
			super.setName(this.shortName);
		}
		
		
	}

	public boolean isUseShortName() {
		return useShortName;
	}

	public void setUseShortName(boolean useShortName) {
		this.useShortName = useShortName;
	}

	@Override
	public int hashCode() {
		if (useShortName) {
			return super.hashCode();
		}
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (useShortName) {
			return super.equals(obj);
		}
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		SuffixMessageSubscription other = (SuffixMessageSubscription) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

}
