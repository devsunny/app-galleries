package com.asksunny.data;

import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.Date;

public class TestDataGenerator {

	public static void generateDataFile(String outfile, int number, String unit)
			throws Exception {
		long unitValue = 1024L * 1024L * 1024L;
		if (unit == null) {
		} else if (unit.equalsIgnoreCase("M") || unit.equalsIgnoreCase("MB")) {
			unitValue = 1024L * 1024L;
		} else if (unit.equalsIgnoreCase("G") || unit.equalsIgnoreCase("GB")) {
			unitValue = 1024L * 1024L * 1024L;
		} else if (unit.equalsIgnoreCase("K") || unit.equalsIgnoreCase("KB")) {
			unitValue = 1024L;
		} else {
			unitValue = 1024L * 1024L * 1024L;
		}

		long today = new Date().getTime();
		SecureRandom random = new SecureRandom(new Date().toString().getBytes());
		FileOutputStream fout = new FileOutputStream(outfile);
		long goal = unitValue * number;
		long dlenght = 0;
		long start = System.currentTimeMillis();
		try {
			int id = 0;
			for (;;) {
				long gap = Math.abs(random.nextInt(2000)) * 24 * 60 * 60 * 100;
				long t = today - gap;
				Date date = new Date(t);
				int nextId = Math.abs(random.nextInt(1000000000)) + 1000000000;
				String data = String
						.format("%1$d|%2$tY-%2$tm-%2$td|%3$d|ABC FHTGER XYZ|TEST DATA|abcdefghijklmnopqrstuvwxyz|ABCDEFGHIJKLMNOPQRSTUVWXYZ|1234567890\n",
								++id, date, nextId);
				byte[] bytes = data.getBytes(Charset.defaultCharset());
				fout.write(bytes);
				dlenght += bytes.length;
				if (dlenght >= goal) {
					break;
				}
			}
			fout.flush();
			long duration = System.currentTimeMillis() -  start;
			System.out.println(String.format("It took %d milliseconds to complete operation.", duration));
		} finally {
			fout.flush();
			fout.close();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length < 2) {
			System.out
					.println("Usage: TestDataGenerator <out_file> <size> [size_unit_M|G|K]");
			System.exit(1);
		}
		String unit = args.length < 3 ? "M" : args[2];
		generateDataFile(args[0], Integer.valueOf(args[1]), unit);
	}

}
