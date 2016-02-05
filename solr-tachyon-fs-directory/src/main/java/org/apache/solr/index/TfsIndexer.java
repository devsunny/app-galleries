package org.apache.solr.index;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.solr.core.TfsLockFactory;
import org.apache.solr.store.tfs.TfsDirectory;

import tachyon.TachyonURI;
import tachyon.client.UnderStorageType;
import tachyon.client.file.TachyonFileSystem;
import tachyon.client.file.TachyonFileSystem.TachyonFileSystemFactory;
import tachyon.client.file.options.MkdirOptions;
import tachyon.conf.TachyonConf;

public class TfsIndexer {

	public TfsIndexer() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) throws Exception{
		Date start = new Date();
		try {
			
			Properties prop = System.getProperties();
			for(Object key: prop.keySet()){
				System.out.printf("%s = %s\n", key.toString(), prop.getProperty(key.toString()));
			}			
			System.out.println("started:" );
			TachyonFileSystem tfs = TachyonFileSystemFactory.get();
			System.out.println("Get SYstem:" );
			TachyonURI path = new TachyonURI("/solr_test/levelx/levelx");
			System.out.println("Start open:" );
			if (tfs.openIfExists(path) == null) {
				TachyonConf config = new TachyonConf();
				MkdirOptions.Builder builder = new MkdirOptions.Builder(config);
				builder.setRecursive(true);
				builder.setUnderStorageType(UnderStorageType.SYNC_PERSIST);
				
				System.out.println("openned:" );				
				tfs.mkdir(path, builder.build());
				System.out.println("Created Index dir:" );
			}
			System.out.println("INDEX DIR:" + path.getPath());
			Directory dir = new TfsDirectory(TachyonFileSystemFactory.get(), path, TfsLockFactory.INSTANCE);
			System.out.println("Index DIR poited:" + path.getPath());
			
			Analyzer analyzer = new StandardAnalyzer();
			IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
			iwc.setOpenMode(OpenMode.CREATE_OR_APPEND);
			iwc.setRAMBufferSizeMB(64.0);
			System.out.println("Index open poited:" + path.getPath());
			IndexWriter writer = new IndexWriter(dir, iwc);
			System.out.println("Index writer:" + path.getPath());
			
			// NOTE: if you want to maximize search performance,
			// you can optionally call forceMerge here. This can be
			// a terribly costly operation, so generally it's only
			// worth it when your index is relatively static (ie
			// you're done adding documents to it):
			//
			// writer.forceMerge(1);

			writer.close();
			System.out.println("Close:" + path.getPath());
			Date end = new Date();
			System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

}
