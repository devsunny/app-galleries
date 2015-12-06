package com.asksunny.ffmt.compression;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;

import parquet.example.data.Group;
import parquet.example.data.simple.SimpleGroup;
import parquet.hadoop.ParquetWriter;
import parquet.hadoop.metadata.CompressionCodecName;
import parquet.schema.PrimitiveType;
import parquet.schema.PrimitiveType.PrimitiveTypeName;
import parquet.schema.Type.Repetition;

public class ParquetFileTest extends BenchmarkBase {

	@Override
	public void write(String codecClassName, String outputPath) throws IOException {

		Configuration conf = new Configuration();
		FileSystem fs = LocalFileSystem.get(conf);
		Path outpath = new Path(outputPath);
		fs.delete(outpath, true);
		CompressionCodecName compress = CompressionCodecName.UNCOMPRESSED;
		if (codecClassName == null) {
			compress = CompressionCodecName.UNCOMPRESSED;
		} else if (codecClassName.equals("gzip")) {
			compress = CompressionCodecName.GZIP;
		} else if (codecClassName.equals("snappy")) {
			compress = CompressionCodecName.SNAPPY;
		} else if (codecClassName.equals("lzo")) {
			compress = CompressionCodecName.LZO;
		} else {
			compress = CompressionCodecName.UNCOMPRESSED;
		}
		final parquet.schema.MessageType schema = new parquet.schema.MessageType("TestObject",
				new parquet.schema.PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.INT32, "age"),
				new parquet.schema.PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.DOUBLE, "price"),
				new parquet.schema.PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.BINARY, "location"),
				new parquet.schema.PrimitiveType(Repetition.OPTIONAL, PrimitiveTypeName.BINARY, "description"),
				new PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.INT64, "timeinmilli"),
				new PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.INT64, "dob"),
				new PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.INT64, "doq"),
				new PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.DOUBLE, "value"),
				new PrimitiveType(Repetition.REQUIRED, PrimitiveTypeName.BINARY, "comment"));

		GroupWriteSupportI.setSchema(schema, conf);
		GroupWriteSupportI suppport = new GroupWriteSupportI();
		suppport.init(conf);
		ParquetWriter<Group> writer = new ParquetWriter<Group>(outpath, suppport, compress,
				ParquetWriter.DEFAULT_BLOCK_SIZE, ParquetWriter.DEFAULT_PAGE_SIZE, ParquetWriter.DEFAULT_PAGE_SIZE,
				ParquetWriter.DEFAULT_IS_DICTIONARY_ENABLED, ParquetWriter.DEFAULT_IS_VALIDATING_ENABLED,
				ParquetWriter.DEFAULT_WRITER_VERSION, conf);
		try {
			for (long i = 0; i < getNumberOfRecord(); i++) {
				int intI = (int) (i & 0x000000000FFFFFFFL);
				SimpleGroup group = new SimpleGroup(schema);
				group.add(0, 12 + intI);
				group.add(1, 34.5 * (i % 10));
				group.add(2, "This is location " + i);
				group.add(3, "Fake description " + i);
				group.add(4, System.currentTimeMillis());
				group.add(5, System.currentTimeMillis() - 1100000);
				group.add(6, System.currentTimeMillis() + 1100000);
				group.add(7, 23.0 * i);
				group.add(8, "Comment" + i);
				writer.write(group);
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
