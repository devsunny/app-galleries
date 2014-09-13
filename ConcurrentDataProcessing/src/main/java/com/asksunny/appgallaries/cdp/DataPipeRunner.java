package com.asksunny.appgallaries.cdp;

public class DataPipeRunner {

	private static String[][] srcData = {
			{ "ID-101", "COL1-V1", "COL2-V1", "COL3-V1", "COL4-V1", "COL5-V1",
					"COL6-V1", "COL7-V1" },
			{ "ID-101", "COL1-V2", "COL2-V2", "COL3-V2", "COL4-V2", "COL5-V2",
					"COL6-V2", "COL7-V2" },
			{ "ID-101", "COL1-V3", "COL2-V3", "COL3-V3", "COL4-V3", "COL5-V3",
					"COL6-V3", "COL7-V3" },
			{ "ID-101", "COL1-V4", "COL2-V4", "COL3-V4", "COL4-V4", "COL5-V4",
					"COL6-V4", "COL7-V4" } };

	public DataPipeRunner() {

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ConcrrentForkJoinPipe fjpipe = new ConcrrentForkJoinPipe();
		ConcurrentPipeline pl1 = new ConcurrentPipeline();
		LoggingPipe lgp1 = new LoggingPipe();
		lgp1.setPipeName("LGP1");
		pl1.addPipe(lgp1);
		pl1.setDataSelection(new int[] { 1, 2 });
		fjpipe.addPipe(pl1);
		ConcurrentPipeline pl2 = new ConcurrentPipeline();
		LoggingPipe lgp2 = new LoggingPipe();
		lgp2.setPipeName("LGP2");
		pl2.addPipe(lgp2);
		pl2.setDataSelection(new int[] { 1, 3, 4, 6 });
		fjpipe.addPipe(pl2);
		fjpipe.init();
		for (int i = 0; i < srcData.length; i++) {
			fjpipe.process(srcData[i]);
		}
		fjpipe.endOfDataStream();
	}

}
