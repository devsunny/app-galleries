package com.asksunny.ffmt.compression;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hive.ql.io.orc.CompressionKind;
import org.apache.hadoop.hive.ql.io.orc.OrcFile;
import org.apache.hadoop.hive.ql.io.orc.Writer;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.io.compress.GzipCodec;

public class ORCFileTest extends BenchmarkBase {

	@Override
	public void write(String codecClassName, String outputPath) throws IOException {

		Configuration conf = new Configuration();
		FileSystem fs = LocalFileSystem.get(conf);
		Path outpath = new Path(outputPath);
		fs.delete(outpath, true);
		CompressionKind compress = CompressionKind.NONE;
		if (codecClassName == null) {
			compress = CompressionKind.NONE;
		} else if (codecClassName.equals("gzip") ){
			compress = CompressionKind.ZLIB;
		} else if (codecClassName.equals("snappy")) {
			compress = CompressionKind.SNAPPY;
		} else if (codecClassName.equals("lzo")) {
			compress = CompressionKind.LZO;
		} else {
			compress = CompressionKind.NONE;
		}
		ObjectInspector inspector = ObjectInspectorFactory.getReflectionObjectInspector(TestObject.class,
				ObjectInspectorFactory.ObjectInspectorOptions.JAVA);
		Writer writer = OrcFile.createWriter(fs, outpath, conf, inspector, 64 * 1024, compress, 8 * 1024, 0);
		try {
			for (long i = 0; i < getNumberOfRecord(); i++) {
				int intI = (int) (i & 0x000000000FFFFFFFL);
				TestObject t = new TestObject(12 + intI, 34.5 * (i % 10), "This is location " + i,
						"Fake description " + i, System.currentTimeMillis(), System.currentTimeMillis() - 1100000,
						System.currentTimeMillis() + 1100000, 23.0 + i, "Comment" + i);
				writer.addRow(t);
			}
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
		File outFile = new File(outputPath);
		if (outFile.exists()) {
			System.out.printf("File %s created with Size of %d GB.\n", outputPath,
					outFile.length() / (1024L * 1024L * 1024L));
		}

	}

	@Override
	protected void writeObjects(OutputStream fout) throws IOException {

	}

}
