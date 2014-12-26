package org.dcache.chimera;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.dcache.nfs.vfs.Inode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class HadoopHdfsDriver {

	private static final Logger LOG = LoggerFactory
			.getLogger(HadoopHdfsDriver.class);

	private Configuration hdfsConfig = null;
	private FileSystem hdfs = null;
	private HadoopHdfsVfs hdfsVfs;
	private LoadingCache<Inode, FSDataOutputStream> cache;
	private LoadingCache<Inode, FSDataInputStream> readCache;

	private int maxCacheSize;
	private int lastAccess;
	private Path base;

	public HadoopHdfsDriver(HadoopHdfsVfs hdfsVfs) throws IOException {
		this.hdfsConfig = new Configuration();
		this.hdfs = FileSystem.get(this.hdfsConfig);
		this.hdfsVfs = hdfsVfs;
	}
	
	public HadoopHdfsDriver(HadoopHdfsVfs hdfsVfs, File pathToHdfsCfgDir) throws IOException {
		LOG.info("HadoopHdfsDriver created with:{}", pathToHdfsCfgDir);
		this.hdfsConfig = new Configuration();		
		this.hdfsConfig.addResource(new Path(pathToHdfsCfgDir.toString(), "core-site.xml"));
		this.hdfsConfig.addResource(new Path(pathToHdfsCfgDir.toString(), "hdfs-site.xml"));
		this.hdfsConfig.addResource(new Path(pathToHdfsCfgDir.toString(), "yarn-site.xml"));
		this.hdfs = FileSystem.get(this.hdfsConfig);
		this.hdfsVfs = hdfsVfs;
	}
	

	public Configuration getHdfsConfig() {
		return hdfsConfig;
	}

	public void setHdfsConfig(Configuration hdfsConfig) {
		this.hdfsConfig = hdfsConfig;
	}

	public FileSystem getHdfs() {
		return hdfs;
	}

	public void setHdfs(FileSystem hdfs) {
		this.hdfs = hdfs;
	}

	public HadoopHdfsVfs getHdfsVfs() {
		return hdfsVfs;
	}

	public void setHdfsVfs(HadoopHdfsVfs hdfsVfs) {
		this.hdfsVfs = hdfsVfs;
	}

	public LoadingCache<Inode, FSDataOutputStream> getCache() {
		return cache;
	}

	public void setCache(LoadingCache<Inode, FSDataOutputStream> cache) {
		this.cache = cache;
	}

	public int getMaxCacheSize() {
		return maxCacheSize;
	}

	public void setMaxCacheSize(int maxCacheSize) {
		this.maxCacheSize = maxCacheSize;
	}

	public int getLastAccess() {
		return lastAccess;
	}

	public void setLastAccess(int lastAccess) {
		this.lastAccess = lastAccess;
	}

	public Path getBase() {
		return base;
	}

	public void setBase(Path base) {
		this.base = base;
	}

	public FSDataOutputStream getFSDataOutputStream(Inode inode)
			throws IOException {
		return this.cache.getUnchecked(inode);
	}

	public FSDataInputStream getFSDataInputStream(Inode inode)
			throws IOException {
		throw new IOException("Read is not allowed");
	}

	public void close(Inode inode) 
	{
		this.cache.invalidate(inode);		
	}

	public Path inode2path(Inode inode) throws IOException {
		String path = null;
		FsInode fsInode = this.hdfsVfs.toFsInode(inode);
		path = fsInode.getFs().inode2path(fsInode);
		return new Path(path);
	}

	protected FSDataOutputStream inode2FSDataOutputStream(Inode inode)
			throws IOException {
		FSDataOutputStream ret  =null;
		try {
			Path path = inode2path(inode);
			if(LOG.isDebugEnabled()) LOG.debug("Request path:{}", path);			
			Path hdfsFile = new Path(this.base, path);
			if (LOG.isDebugEnabled()) LOG.debug("Request file path :{}", hdfsFile);
			if(!this.hdfs.exists(hdfsFile.getParent())){
				this.hdfs.mkdirs(hdfsFile.getParent());
			}
			ret  = this.hdfs.create(hdfsFile, Boolean.TRUE);
		} catch (Exception e) {
			LOG.error("Failed to create FSDataOutputStream", e);
		}		
		return ret;
	}

	protected FSDataInputStream inode2FSDataInputStream(Inode inode)
			throws IOException {
		Path path = inode2path(inode);
		return this.hdfs.open(new Path(this.base, path));
	}

	public boolean isDirectory(Path p) throws IOException {
		return this.hdfs.isDirectory(p);
	}

	public void init() throws IOException {
		this.cache = CacheBuilder.newBuilder().maximumSize(this.maxCacheSize)
				.expireAfterAccess(this.lastAccess, TimeUnit.SECONDS)
				.removalListener(new InodeGarbageCollector())
				.build(new HdfsOutputStreamSupplier(this));

		this.readCache = CacheBuilder.newBuilder()
				.maximumSize(this.maxCacheSize)
				.expireAfterAccess(this.lastAccess, TimeUnit.SECONDS)
				.removalListener(new InodeReadGarbageCollector())
				.build(new HdfsInputStreamSupplier(this));
	}

	private static class HdfsOutputStreamSupplier extends
			CacheLoader<Inode, FSDataOutputStream> {

		private final HadoopHdfsDriver hdfsDriver;

		HdfsOutputStreamSupplier(HadoopHdfsDriver hdfsDriver)
				throws IOException {
			this.hdfsDriver = hdfsDriver;
		}

		@Override
		public FSDataOutputStream load(Inode inode) throws IOException {
			return this.hdfsDriver.inode2FSDataOutputStream(inode);
		}

	}

	private static class HdfsInputStreamSupplier extends
			CacheLoader<Inode, FSDataInputStream> {

		private final HadoopHdfsDriver hdfsDriver;

		HdfsInputStreamSupplier(HadoopHdfsDriver hdfsDriver) throws IOException {
			this.hdfsDriver = hdfsDriver;
		}

		@Override
		public FSDataInputStream load(Inode inode) throws IOException {
			return this.hdfsDriver.inode2FSDataInputStream(inode);
		}

	}

	private static class InodeGarbageCollector implements
			RemovalListener<Inode, FSDataOutputStream> {

		@Override
		public void onRemoval(
				RemovalNotification<Inode, FSDataOutputStream> notification) {
			try {
				FSDataOutputStream fout = notification.getValue();
				fout.flush();
				fout.close();
				if(LOG.isDebugEnabled()) LOG.debug("FSDataOutputStream Closed.");
			} catch (IOException e) {
				LOG.error("Failed to close file channel of {} : {}",
						notification.getKey(), e.getMessage());
			}
		}
	}

	private static class InodeReadGarbageCollector implements
			RemovalListener<Inode, FSDataInputStream> {

		@Override
		public void onRemoval(
				RemovalNotification<Inode, FSDataInputStream> notification) {
			try {
				FSDataInputStream fin = notification.getValue();
				fin.close();
			} catch (IOException e) {
				LOG.error("Failed to close file channel of {} : {}",
						notification.getKey(), e.getMessage());
			}
		}
	}

}
