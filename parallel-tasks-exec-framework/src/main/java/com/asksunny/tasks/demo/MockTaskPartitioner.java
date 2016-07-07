package com.asksunny.tasks.demo;

import java.util.ArrayList;
import java.util.List;

import com.asksunny.tasks.ParallePartitioner;

public class MockTaskPartitioner implements ParallePartitioner{

	public MockTaskPartitioner() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void init(String[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getTimeout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String[]> doPartition() {
		List<String[]> ret = new ArrayList<>();
		for (int i = 0; i < 20; i++) {
			ret.add(new String[]{Integer.toString(i)});			
		}
		return ret;
	}

}
