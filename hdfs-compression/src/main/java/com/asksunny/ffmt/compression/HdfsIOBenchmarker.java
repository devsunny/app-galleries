package com.asksunny.ffmt.compression;

public class HdfsIOBenchmarker {

	public static void main(String[] args) throws Exception {

		if (args.length < 2) {
			System.out.println("Usage: HdfsIOBenchmarker <csv|orc|parquet> <output_path> [gzip|snappy|lzo|none]");
			return;
		}
		if (!args[0].matches("^csv|orc|parquet$")) {
			System.out.println("Only csv, orc and  parquet are supported");
			System.out.println("Usage: HdfsIOBenchmarker <csv|orc|parquet> <output_path> [gzip|snappy|lzo|none]");
			return;
		}

		if (args.length > 2 && !args[2].matches("^gzip|snappy|lzo|none$")) {
			System.out.println("Only gzip, snappy, lzo and none are supported");
			System.out.println("Usage: HdfsIOBenchmarker <csv|orc|parquet> <output_path> [gzip|snappy|lzo|none]");
			return;
		}

		BenchmarkBase benchmarker = null;
		if (args[0].equals("csv")) {
			benchmarker = new CSVFileTest();
		} else if (args[0].equals("orc")) {
			benchmarker = new ORCFileTest();
		} else {
			benchmarker = new ParquetFileTest();
		}
		String codec = args.length > 2 ? args[2] : null;
		System.out.printf("start generating %d records in %s %s format\n", benchmarker.getNumberOfRecord(), args[0],
				codec);
		benchmarker.start();
		benchmarker.write(codec, args[1]);
		long d = benchmarker.stopInSeconds();
		System.out.printf("It took %d seconds to generate %d records in %s %s format\n", d,
				benchmarker.getNumberOfRecord(), args[0], codec);

	}

}
