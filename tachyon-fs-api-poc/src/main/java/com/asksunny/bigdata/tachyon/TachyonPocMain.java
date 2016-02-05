package com.asksunny.bigdata.tachyon;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import tachyon.TachyonURI;
import tachyon.client.UnderStorageType;
import tachyon.client.file.FileInStream;
import tachyon.client.file.FileOutStream;
import tachyon.client.file.TachyonFileSystem;
import tachyon.client.file.TachyonFileSystem.TachyonFileSystemFactory;
import tachyon.client.file.options.MkdirOptions;
import tachyon.conf.TachyonConf;

public class TachyonPocMain {

	public static void main(String[] args) throws Exception {

		TachyonFileSystem tfs = TachyonFileSystemFactory.get();
		TachyonURI path = new TachyonURI("/poc/directory");

		TachyonConf config = new TachyonConf();
		MkdirOptions.Builder builder = new MkdirOptions.Builder(config);
		builder.setRecursive(true);
		builder.setUnderStorageType(UnderStorageType.SYNC_PERSIST);
		tfs.mkdir(path, builder.build());
		TachyonURI fpath = new TachyonURI("/poc/directory/poc_file2");

		FileOutStream out = tfs.getOutStream(fpath);
		SecureRandom random = new SecureRandom(UUID.randomUUID().toString().getBytes());
		try {
			for (int i = 0; i < 1000000; i++) {
				int offset = Math.abs(random.nextInt(26)) % 26;
				out.write('A' + offset);
				if (i % 100 == 0) {
					out.write('\n');
				} else if (i % 10 == 0) {
					out.write('|');
				}
			}
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			out.close();

		}

		FileInStream fin = tfs.getInStream(tfs.open(fpath));
		try {
			String text = IOUtils.toString(fin);
			System.out.println(text);
		} finally {
			fin.close();
		}

	}

}
