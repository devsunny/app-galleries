package com.asksunny.compress;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.anarres.lzo.LzopOutputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.xerial.snappy.SnappyOutputStream;

public class CLICompressor {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		if (args.length != 3) {
			System.out
					.println("Usage: CLICompressor <bz2|snappy|lzo> <in_file> <out_file>");
			System.exit(1);
		}
		
		long start = System.currentTimeMillis();
		FileOutputStream fout = new FileOutputStream(args[2]);
		FileInputStream fin = new FileInputStream(args[1]);
		CompressorStreamFactory factory = new CompressorStreamFactory();
		try {
			OutputStream out = null;
			String type = args[0];
			if (type.equalsIgnoreCase("bz2") || type.equalsIgnoreCase("bzip2")) {
				out = factory.createCompressorOutputStream(
						CompressorStreamFactory.BZIP2, fout);
			}else if (type.equalsIgnoreCase("default") || type.equalsIgnoreCase("deflate")) {
				org.apache.hadoop.io.compress.DefaultCodec codec = new org.apache.hadoop.io.compress.DefaultCodec();
				out = codec.createOutputStream(fout);
			} else if (type.equalsIgnoreCase("snappy")) {
				out = new SnappyOutputStream(fout);
			} else if (type.equalsIgnoreCase("lzo")) {
				out = new LzopOutputStream(
						fout,
						org.anarres.lzo.hadoop.codec.LzoCompressor.CompressionStrategy.LZO1X_1
								.newCompressor());
			}
			IOUtils.copy(fin, out);
			fout.flush();
			long duration = System.currentTimeMillis() -  start;
			System.out.println(String.format("It took %d milliseconds to complete operation.", duration));
		} finally {
			try {
				fin.close();
			} finally {
				fout.close();
			}
		}

	}

}
