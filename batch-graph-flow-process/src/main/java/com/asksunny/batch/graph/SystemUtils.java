package com.asksunny.batch.graph;

public final class SystemUtils {

	public SystemUtils() {

	}

	public static int getSystemCores() {
		return Runtime.getRuntime().availableProcessors();
	}

	public static long getSystemFreeMemory() {
		return Runtime.getRuntime().freeMemory();
	}

}
