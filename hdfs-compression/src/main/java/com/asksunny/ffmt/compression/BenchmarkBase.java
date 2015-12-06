package com.asksunny.ffmt.compression;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.io.compress.SnappyCodec;

import com.hadoop.compression.lzo.LzopCodec;

public abstract class BenchmarkBase {
	protected long numberOfRecord = 300000000L;
	protected TestObject inputBaseObject = null;

	protected long startTime = 0;
	protected long endTime = 0;

	public BenchmarkBase() {
		inputBaseObject = new TestObject(12, 34.5, "This is location ", "Fake description ", System.currentTimeMillis(),
				System.currentTimeMillis() - 1100000, System.currentTimeMillis() + 1100000, 23.0, "This is  Comment ");
	}

	public void start() {
		startTime = System.currentTimeMillis();
	}

	public long stop() {
		endTime = System.currentTimeMillis();
		return endTime - startTime;
	}

	public long stopInSeconds() {
		endTime = System.currentTimeMillis();
		return (endTime - startTime) / 1000;
	}

	protected abstract void writeObjects(OutputStream fout) throws IOException;

	public void write(String codecName, String outputPath) throws IOException {

		OutputStream fout = null;
		String codecClassName = null;
		if (codecName == null) {
			codecClassName = null;
		} else if (codecName.equals("gzip")) {
			codecClassName = GzipCodec.class.getName();
		} else if (codecName.equals("snappy")) {
			codecClassName = SnappyCodec.class.getName();
		} else if (codecName.equals("lzo")) {
			codecClassName = LzopCodec.class.getName();
		} else {
			codecClassName = null;
		}
		if (codecClassName == null) {
			fout = new FileOutputStream(outputPath);
		} else {
			CompressionCodecFactory codecfactory = new CompressionCodecFactory(new Configuration());
			CompressionCodec codec = codecfactory.getCodecByClassName(codecClassName);
			fout = codec.createOutputStream(new FileOutputStream(outputPath));
		}
		try {
			writeObjects(fout);
			fout.flush();
		} finally {
			if (fout != null) {
				fout.close();
			}
		}

		File outFile = new File(outputPath);
		if (outFile.exists()) {
			System.out.printf("File %s created with Size of %d GB.\n", outputPath,
					outFile.length() / (1024L * 1024L * 1024L));
		}

	}

	public long getNumberOfRecord() {
		return numberOfRecord;
	}

	public void setNumberOfRecord(long numberOfRecord) {
		this.numberOfRecord = numberOfRecord;
	}

	public TestObject getInputBaseObject() {
		return inputBaseObject;
	}

	public void setInputBaseObject(TestObject inputBaseObject) {
		this.inputBaseObject = inputBaseObject;
	}

}
