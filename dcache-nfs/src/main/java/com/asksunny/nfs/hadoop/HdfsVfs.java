package com.asksunny.nfs.hadoop;

import static org.dcache.nfs.v4.xdr.nfs4_prot.ACCESS4_EXTEND;
import static org.dcache.nfs.v4.xdr.nfs4_prot.ACCESS4_MODIFY;
import static org.dcache.nfs.v4.xdr.nfs4_prot.ACE4_INHERIT_ONLY_ACE;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FsStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.AclEntry;
import org.apache.hadoop.fs.permission.AclStatus;
import org.apache.hadoop.fs.permission.FsPermission;
import org.dcache.acl.ACE;
import org.dcache.acl.enums.AceFlags;
import org.dcache.acl.enums.AceType;
import org.dcache.acl.enums.Who;
import org.dcache.auth.Subjects;
import org.dcache.nfs.status.NfsIoException;
import org.dcache.nfs.status.NoEntException;
import org.dcache.nfs.status.NotEmptyException;
import org.dcache.nfs.status.PermException;
import org.dcache.nfs.v4.NfsIdMapping;
import org.dcache.nfs.v4.acl.Acls;
import org.dcache.nfs.v4.xdr.aceflag4;
import org.dcache.nfs.v4.xdr.acemask4;
import org.dcache.nfs.v4.xdr.acetype4;
import org.dcache.nfs.v4.xdr.nfsace4;
import org.dcache.nfs.v4.xdr.uint32_t;
import org.dcache.nfs.v4.xdr.utf8str_mixed;
import org.dcache.nfs.vfs.AclCheckable;
import org.dcache.nfs.vfs.DirectoryEntry;
import org.dcache.nfs.vfs.FsStat;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.dcache.nfs.vfs.Stat.Type;
import org.dcache.nfs.vfs.VirtualFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class HdfsVfs implements VirtualFileSystem, AclCheckable, NFSConstants {

	private static final Logger LOG = LoggerFactory.getLogger(HdfsVfs.class);
	private static final String METASTORE_ERR = "Failed to access to File System metastore";
	private static final long FS_DIR_SIZE = 512L;
	private final NFSInodeMetaStorage inodeMetaStorage;
	private final NfsIdMapping idMapping;
	private LoadingCache<Inode, FSDataOutputStream> hdfsFSoutCache;
	private FileSystem hdfs;
	private final Path HDFS_NFS_BASE;
	private final String nfsShareName;
	private int maxCacheSize = 250;
	private int lastAccess = 30;

	public HdfsVfs(NFSInodeMetaStorage inodeMappingDriver,
			NfsIdMapping idMapping, Path nfsBase, String nfsShareName)
			throws IOException {
		this.inodeMetaStorage = inodeMappingDriver;
		this.HDFS_NFS_BASE = nfsBase;
		this.idMapping = idMapping;
		this.nfsShareName = nfsShareName;
		init(null);
	}

	public HdfsVfs(NFSInodeMetaStorage inodeMappingDriver,
			NfsIdMapping idMapping, Path nfsBase, String nfsShareName,
			int maxCacheSize, int lastAccess) throws IOException {
		this.inodeMetaStorage = inodeMappingDriver;
		this.HDFS_NFS_BASE = nfsBase;
		this.idMapping = idMapping;
		this.nfsShareName = nfsShareName;
		this.maxCacheSize = maxCacheSize;
		this.lastAccess = lastAccess;
		init(null);
	}

	public HdfsVfs(NFSInodeMetaStorage inodeMappingDriver,
			NfsIdMapping idMapping, Path hadoopHome, Path nfsBase,
			String nfsShareName) throws IOException {
		this.inodeMetaStorage = inodeMappingDriver;
		this.idMapping = idMapping;
		this.nfsShareName = nfsShareName;
		this.HDFS_NFS_BASE = nfsBase;
		init(hadoopHome);
	}

	public HdfsVfs(NFSInodeMetaStorage inodeMappingDriver,
			NfsIdMapping idMapping, Path hadoopHome, Path nfsBase,
			String nfsShareName, int maxCacheSize, int lastAccess)
			throws IOException {
		this.inodeMetaStorage = inodeMappingDriver;
		this.idMapping = idMapping;
		this.nfsShareName = nfsShareName;
		this.HDFS_NFS_BASE = nfsBase;
		this.maxCacheSize = maxCacheSize;
		this.lastAccess = lastAccess;
		init(hadoopHome);
	}

	protected void init(Path hadoopHome) throws IOException {
		Configuration conf = new Configuration();
		if (hadoopHome != null) {
			conf.addResource(new Path(hadoopHome, "/etc/hadoop/core-site.xml"));
			conf.addResource(new Path(hadoopHome, "/etc/hadoop/hdfs-site.xml"));
			conf.addResource(new Path(hadoopHome, "/etc/hadoop/yarn-site.xml"));
		}
		this.hdfs = FileSystem.get(conf);
		this.hdfsFSoutCache = CacheBuilder.newBuilder()
				.maximumSize(this.maxCacheSize)
				.expireAfterAccess(this.lastAccess, TimeUnit.SECONDS)
				.removalListener(new InodeGarbageCollector())
				.build(new HdfsOutputStreamSupplier(this));

		Path p = new Path(NFS_PREFIX, this.nfsShareName);
		try {
			HdfsInode hinode = inodeMetaStorage.path2inode(p);
			if (hinode == null) {
				Path hdfsp = new Path(this.HDFS_NFS_BASE, this.nfsShareName);
				if (!this.hdfs.exists(hdfsp)) {
					if (!this.hdfs.mkdirs(hdfsp)) {
						LOG.error("HDFS Permission denied on creating dir:{}",
								hdfsp.toString());
						throw new PermException("HDFS Permission denied");
					}
				}
				hinode = inodeMetaStorage.mkdir((HdfsInode) getRootInode(),
						nfsShareName, 0, 0, 0777);
			}
		} catch (SQLException ex) {
			LOG.error(METASTORE_ERR, ex);
			throw new NfsIoException(METASTORE_ERR);
		}

	}

	@Override
	public Inode getRootInode() throws IOException {
		HdfsInode inode = HdfsInode.forId(NFS_ROOT_INODE_ID);
		inode.setPath(NFS_PREFIX);
		inode.setType(Stat.S_IFDIR);
		inode.setNlink(Short.MAX_VALUE);
		return inode;
	}

	@Override
	public Inode lookup(Inode parent, String path) throws IOException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Look up for:{} in {}", path,
					HdfsInode.toHexString(parent.getFileId()));
		}
		if (path.equals(NFS_PREFIX)) {
			return getRootInode();
		}
		HdfsInode pinode = toHdfsInode(parent);
		Path p = new Path(pinode.getPath(), path);
		Path hdfsPath = toHdfsPath(pinode.getPath(), path);
		if (!hdfs.exists(hdfsPath)) {
			throw new NoEntException(String.format("%s does not exist in HDFS",
					p));
		}
		HdfsInode inode = null;
		try {
			inode = inodeMetaStorage.path2inode(p);
		} catch (SQLException e) {
			LOG.error(METASTORE_ERR, e);
			throw new NfsIoException(METASTORE_ERR);
		}
		if (inode == null) {
			LOG.error(String.format("%s does not exist", p));
			throw new NoEntException(String.format("%s does not exist", p));
		}
		return inode;
	}

	@Override
	public void remove(Inode parent, String path) throws IOException {
		HdfsInode pinode = toHdfsInode(parent);
		Path hdfsPath = toHdfsPath(pinode.getPath(), path);
		HdfsInode cnode = (HdfsInode) lookup(pinode, path);
		if (this.hdfs.delete(hdfsPath, Boolean.FALSE)) {
			try {
				this.inodeMetaStorage.removeInode(cnode);
			} catch (SQLException e) {
				LOG.error(METASTORE_ERR, e);
				throw new NfsIoException(METASTORE_ERR);
			}
		} else {
			throw new NotEmptyException(String.format(
					"%s directory is empty or permission denied", hdfsPath));
		}
	}

	@Override
	public Inode mkdir(Inode parent, String path, int uid, int gid, int mode)
			throws IOException {
		HdfsInode pinode = toHdfsInode(parent);
		Path p = new Path(pinode.getPath(), path);
		Path hdfspath = toHdfsPath(pinode.getPath(), path);
		if (LOG.isInfoEnabled()) {
			LOG.info("HDFS mkdir:{}", hdfspath.toString());
		}
		try {
			if (this.hdfs.mkdirs(hdfspath)) {
				try {
					HdfsInode ninode = this.inodeMetaStorage.mkdir(pinode,
							path, uid, gid, 0777);
					return ninode;
				} catch (SQLException e) {
					throw new IOException(
							"Failed to access to File System metastore", e);
				}
			} else {
				throw new PermException(String.format(
						"Permission denied to create directory %s",
						p.toString()));
			}
		} catch (IOException ex) {
			LOG.error("HDFS ERROR", ex);
			throw ex;
		}
	}

	private Path toHdfsPath(String nfsPath) {
		Path base = new Path(NFS_PREFIX, nfsShareName);
		if (nfsPath.startsWith(base.toString())) {
			Path dir = new Path(this.HDFS_NFS_BASE, nfsPath.substring(1));
			return dir;
		} else {
			Path dir = new Path(this.HDFS_NFS_BASE, nfsPath);
			return dir;
		}
	}

	private Path toHdfsPath(String nfsParentPath, String name) {
		Path base = new Path(NFS_PREFIX, nfsShareName);
		if (nfsParentPath.startsWith(base.toString())) {
			Path dir = new Path(this.HDFS_NFS_BASE, nfsParentPath.substring(1));
			return new Path(dir, name);
		} else {
			Path dir = new Path(this.HDFS_NFS_BASE, nfsParentPath);
			return new Path(dir, name);
		}
	}

	@Override
	public Inode create(Inode parent, Type type, String path, int uid, int gid,
			int mode) throws IOException {
		HdfsInode pinode = toHdfsInode(parent);
		Path p = new Path(pinode.getPath(), path);
		Path hdfspath = toHdfsPath(pinode.getPath(), path);
		if (type == Type.DIRECTORY) {
			return mkdir(parent, path, uid, gid, mode);
		} else {
			if (this.hdfs.createNewFile(hdfspath)) {
				HdfsInode ninode = HdfsInode.newInode();
				ninode.setType(Stat.S_IFREG);
				ninode.setPath(p.toString());
				ninode.setParentId(pinode.getIdString());
				long ts = System.currentTimeMillis();
				ninode.setAtime(ts);
				ninode.setCtime(ts);
				ninode.setMtime(ts);
				ninode.setNlink(0);
				ninode.setMode(mode);
				ninode.setUid(uid);
				ninode.setGid(gid);
				try {
					this.inodeMetaStorage.addInode(ninode);
				} catch (SQLException e) {
					LOG.error(METASTORE_ERR, e);
					throw new NfsIoException(METASTORE_ERR);
				}
				return ninode;
			} else {
				throw new PermException(String.format(
						"Permission denied to create file %s",
						hdfspath.toString()));
			}
		}

	}

	protected HdfsInode toHdfsInode(Inode inode) throws IOException {

		String id = HdfsInode.toHexString(inode.getFileId());
		HdfsInode pinode = null;
		if (NFS_ROOT_INODE_ID.equals(id)) {
			pinode = HdfsInode.forId(NFS_ROOT_INODE_ID);
			pinode.setPath(NFS_PREFIX);
			pinode.setType(Stat.S_IFDIR);

		} else {
			try {
				pinode = this.inodeMetaStorage.id2inode(id);
			} catch (SQLException e) {
				throw metastoreAccessException(e);
			}
		}

		return pinode;

	}

	@Override
	public Access checkAcl(Subject subject, Inode inode, int access)
			throws IOException {
		// TODO:implement security check here
		return Access.ALLOW;
	}

	@Override
	public int access(Inode inode, int mode) throws IOException {
		int accessmask = mode;
		if ((mode & (ACCESS4_MODIFY | ACCESS4_EXTEND)) != 0) {
			HdfsInode pinode = toHdfsInode(inode);
			if (pinode.getType() == Stat.S_IFREG) {
				accessmask ^= (ACCESS4_MODIFY | ACCESS4_EXTEND);
			}
		}
		return accessmask;
	}

	@Override
	public FsStat getFsStat() throws IOException {
		FsStatus fsstatus = this.hdfs.getStatus();
		FsStat fsstat = new FsStat(fsstatus.getCapacity(), Integer.MAX_VALUE,
				fsstatus.getUsed(), 1);
		return fsstat;
	}

	@Override
	public Inode link(Inode parent, Inode link, String path, int uid, int gid)
			throws IOException {
		throw new IOException("Not supported");
	}

	@Override
	public List<DirectoryEntry> list(Inode parent) throws IOException {
		HdfsInode pinode = toHdfsInode(parent);
		if (LOG.isDebugEnabled())
			LOG.debug("Listing directory:{}", pinode.getPath());
		List<DirectoryEntry> entries = new ArrayList<DirectoryEntry>();
		try {
			Map<String, HdfsInode> cnodes = this.inodeMetaStorage
					.listDir(pinode);
			FileStatus[] filestats = this.hdfs.listStatus(new Path(pinode
					.getPath()));
			for (FileStatus fileStatus : filestats) {
				Path p = fileStatus.getPath();
				HdfsInode inode = cnodes.get(p.toString());
				if (inode != null) {
					Stat stat = new Stat();
					stat.setATime(fileStatus.getAccessTime());
					stat.setCTime(fileStatus.getModificationTime());
					stat.setMTime(fileStatus.getModificationTime());
					stat.setGid(fileStatus.getGroup().hashCode());
					stat.setUid(fileStatus.getOwner().hashCode());
					stat.setMode(fileStatus.getPermission().toShort());
					DirectoryEntry entry = new DirectoryEntry(p.getName(),
							inode, stat);
					entries.add(entry);
				}
			}
		} catch (SQLException sex) {
			throw metastoreAccessException(sex);
		} catch (IOException ioe) {
			LOG.error("Hadoop Cluster is not running", ioe);
			throw ioe;
		}
		return entries;
	}

	@Override
	public boolean move(Inode src, String oldName, Inode dest, String newName)
			throws IOException {
		HdfsInode odir = toHdfsInode(src);
		HdfsInode ndir = toHdfsInode(dest);
		Path op = toHdfsPath(odir.getPath(), oldName);
		Path np = toHdfsPath(ndir.getPath(), newName);
		return this.hdfs.rename(op, np);
	}

	@Override
	public Inode parentOf(Inode inode) throws IOException {
		Inode pinode = null;
		try {
			pinode = this.inodeMetaStorage.parentOf((HdfsInode) inode);
		} catch (SQLException e) {
			throw metastoreAccessException(e);
		}
		return pinode;
	}

	@Override
	public int read(Inode inode, byte[] data, long offset, int count)
			throws IOException {
		HdfsInode hinode = (HdfsInode) inode;
		FSDataInputStream fin = this.hdfs.open(new Path(hinode.getPath()));
		int ret = fin.read(offset, data, 0, count);
		fin.close();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Path, {}, readed {}, offset, {}, count, {}",
					hinode.getPath(), ret, offset, count);
		}
		return ret;
	}

	@Override
	public String readlink(Inode inode) throws IOException {

		throw new IOException("Not supported");
	}

	@Override
	public Inode symlink(Inode parent, String path, String link, int uid,
			int gid, int mode) throws IOException {
		throw new IOException("Not supported");
	}

	@Override
	public WriteResult write(Inode inode, byte[] data, long offset, int count,
			StabilityLevel stabilityLevel) throws IOException {
		FSDataOutputStream fout = this.hdfsFSoutCache.getUnchecked(inode);
		fout.write(data, (int) offset, count);
		int bytesWritten = count;
		WriteResult wr = new WriteResult(stabilityLevel, bytesWritten);
		return wr;
	}

	@Override
	public void commit(Inode inode, long offset, int count) throws IOException {
		FSDataOutputStream fout = this.hdfsFSoutCache.getUnchecked(inode);
		fout.flush();
	}

	@Override
	public Stat getattr(Inode inode) throws IOException {
		String id = HdfsInode.toHexString(inode.getFileId());
		if (LOG.isDebugEnabled()) {
			LOG.debug("getattr: {}", id);
		}
		HdfsInode hinode = null;
		if (NFS_ROOT_INODE_ID.equals(id)) {
			hinode = (HdfsInode) getRootInode();
		} else {
			try {
				hinode = this.inodeMetaStorage.id2inode(id);
			} catch (SQLException e) {
				throw metastoreAccessException(e);
			}
		}
		Stat stat = new Stat();
		stat.setATime(hinode.getAtime());
		stat.setCTime(hinode.getCtime());
		stat.setMTime(hinode.getMtime());
		stat.setGid(hinode.getGid());
		stat.setUid(hinode.getUid());
		stat.setDev(17); // Magic number
		stat.setIno((int) hinode.id());
		stat.setMode(hinode.getMode() | hinode.getType());
		stat.setNlink(hinode.getNlink());
		stat.setRdev(0);
		stat.setSize(hinode.getSize());
		stat.setFileid(hinode.id());
		stat.setGeneration(hinode.getGeneration());
		return stat;
	}

	@Override
	public void setattr(Inode inode, Stat stat) throws IOException 
	{
		HdfsInode hinode = toHdfsInode(inode);
		Path hdfsPath = toHdfsPath(hinode.getPath());
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("setattr path: {}", hinode.getPath());
			LOG.debug("setattr hdfsPath: {}", hdfsPath);
		}
	}

	@Override
	public nfsace4[] getAcl(Inode inode) throws IOException {
		HdfsInode hinode = toHdfsInode(inode);		
		Path hdfsPath = toHdfsPath(hinode.getPath());
		if (LOG.isDebugEnabled()) {
			LOG.debug("getAcl path: {}", hinode.getPath());
			LOG.debug("getAcl hdfsPath: {}", hdfsPath);
		}
		// AclStatus aclstat = this.hdfs.getAclStatus(hdfsPath);
		nfsace4[] hdfsAcl = Acls.of(0777, hinode.getType() == Stat.S_IFDIR);
		// nfsace4[] unixAcl = Acls.of(stat.getMode(), fsInode.isDirectory());
		return hdfsAcl;
	}

	@Override
	public void setAcl(Inode inode, nfsace4[] acl) throws IOException {
		HdfsInode hinode = toHdfsInode(inode);
		Path hdfsPath = toHdfsPath(hinode.getPath());
		if (LOG.isDebugEnabled()) {
			LOG.debug("setAcl path: {}", hinode.getPath());
			LOG.debug("setAcl hdfsPath: {}", hdfsPath);
		}
	}

	@Override
	public boolean hasIOLayout(Inode inode) throws IOException {

		return false;
	}

	@Override
	public AclCheckable getAclCheckable() {
		return this;
	}

	public FSDataOutputStream getFSDataOutputStream(Inode inode)
			throws IOException {
		return this.hdfsFSoutCache.getUnchecked(inode);
	}

	public void close(Inode inode) {
		if (this.hdfsFSoutCache.getIfPresent(inode) != null) {
			this.hdfsFSoutCache.invalidate(inode);
		}
	}

	protected FSDataOutputStream inode2FSDataOutputStream(Inode inode)
			throws IOException {
		FSDataOutputStream ret = null;
		HdfsInode hinode = toHdfsInode(inode);
		try {
			Path hdfsFile = new Path(hinode.getPath());
			if (LOG.isDebugEnabled())
				LOG.debug("Request file path :{}", hdfsFile);
			if (!this.hdfs.exists(hdfsFile.getParent())) {
				this.hdfs.mkdirs(hdfsFile.getParent());
			}
			ret = this.hdfs.create(hdfsFile, Boolean.TRUE);
		} catch (Exception e) {
			LOG.error("Failed to create FSDataOutputStream", e);
		}
		return ret;
	}

	private static IOException metastoreAccessException(SQLException sex) {
		LOG.error(METASTORE_ERR, sex);
		return new IOException(METASTORE_ERR, sex);
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
				if (LOG.isDebugEnabled())
					LOG.debug("FSDataOutputStream Closed.");
			} catch (IOException e) {
				LOG.error("Failed to close file channel of {} : {}",
						notification.getKey(), e.getMessage());
			}
		}
	}

	private static class HdfsOutputStreamSupplier extends
			CacheLoader<Inode, FSDataOutputStream> {

		private final HdfsVfs hdfsDriver;

		HdfsOutputStreamSupplier(HdfsVfs hdfsDriver) throws IOException {
			this.hdfsDriver = hdfsDriver;
		}

		@Override
		public FSDataOutputStream load(Inode inode) throws IOException {
			return this.hdfsDriver.inode2FSDataOutputStream(inode);
		}

	}

}
