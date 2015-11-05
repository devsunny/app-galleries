package com.asksunny.app.domain;

import java.util.Comparator;

public class PriorityGroupPolicyComparator implements Comparator<PriorityGroupPolicy> {

	public PriorityGroupPolicyComparator() {
	}

	@Override
	public int compare(PriorityGroupPolicy arg0, PriorityGroupPolicy arg1) {
		return arg0.getPriority() - arg1.getPriority();
	}

}
