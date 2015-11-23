package com.asksunny.app.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ResourceSweepingService {

	
	@Scheduled(cron="*/5 * * * * MON-FRI")
	public void syncResource() {
		System.err.println("Every 5 seconds from cron trigger from CacheSyncService");
	}

		
	@Scheduled(fixedDelay=5000)
	public void sweepForOrphanResource() {
		System.err.println("Every 5 seconds fixedDelay sweep for orphan resource and may perform evict based on the rules");
	}
	
		
	@Scheduled(fixedDelay=5000)
	public void sweepJobStatus() {
		System.err.println("Every 5 seconds fixedDelay sweep for job does not report status back to WLM");
	}
	
}
