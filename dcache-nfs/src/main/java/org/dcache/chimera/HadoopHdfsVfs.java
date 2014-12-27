package org.dcache.chimera;

import static org.dcache.nfs.v4.xdr.nfs4_prot.ACCESS4_EXTEND;
import static org.dcache.nfs.v4.xdr.nfs4_prot.ACCESS4_MODIFY;
import static org.dcache.nfs.v4.xdr.nfs4_prot.ACE4_INHERIT_ONLY_ACE;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.security.auth.Subject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.dcache.acl.ACE;
import org.dcache.acl.enums.AceFlags;
import org.dcache.acl.enums.AceType;
import org.dcache.acl.enums.Who;
import org.dcache.auth.Subjects;
import org.dcache.nfs.status.ExistException;
import org.dcache.nfs.status.NfsIoException;
import org.dcache.nfs.status.NoEntException;
import org.dcache.nfs.status.NotDirException;
import org.dcache.nfs.status.NotEmptyException;
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
import org.dcache.nfs.vfs.VirtualFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.Lists;

public class HadoopHdfsVfs implements VirtualFileSystem, AclCheckable {

	private final static Logger LOG = LoggerFactory
			.getLogger(HadoopHdfsVfs.class);
	private Configuration hdfsConfig = null;
	private FileSystem hdfs = null;
	private LoadingCache<Inode, FSDataOutputStream> hdfsFSoutCache;

	private int maxCacheSize = 250;
	private int lastAccess = 30;
	private Path base;

	private final NfsIdMapping idMapping;
	private final HdfsJdbcFileSystemProvider jdbcFsProvider;

	public HadoopHdfsVfs(HdfsJdbcFileSystemProvider jdbcFsProvider,
			NfsIdMapping idMapping) throws IOException {
		this.jdbcFsProvider = jdbcFsProvider;
		this.idMapping = idMapping;
		init(null);
	}

	public HadoopHdfsVfs(HdfsJdbcFileSystemProvider jdbcFsProvider,
			NfsIdMapping idMapping, Path base) throws IOException {
		this.jdbcFsProvider = jdbcFsProvider;
		this.idMapping = idMapping;
		this.base = base;
		init(null);
	}

	public HadoopHdfsVfs(HdfsJdbcFileSystemProvider jdbcFsProvider,
			NfsIdMapping idMapping, Path base, Path configDir)
			throws IOException {
		this.jdbcFsProvider = jdbcFsProvider;
		this.idMapping = idMapping;
		this.base = base;
		init(configDir);
	}

	public HadoopHdfsVfs(HdfsJdbcFileSystemProvider jdbcFsProvider,
			NfsIdMapping idMapping, Path base, Path configDir,
			int maxCacheSize, int lastAccess) throws IOException {
		this.jdbcFsProvider = jdbcFsProvider;
		this.idMapping = idMapping;
		this.base = base;
		this.maxCacheSize = maxCacheSize;
		this.lastAccess = lastAccess;
		init(configDir);
	}

	protected void init(Path configDir) throws IOException {
		this.hdfsConfig = new Configuration();
		if (configDir != null) {
			this.hdfsConfig.addResource(new Path(configDir, "core-site.xml"));
			this.hdfsConfig.addResource(new Path(configDir, "hdfs-site.xml"));
			this.hdfsConfig.addResource(new Path(configDir, "yarn-site.xml"));
			this.hdfs = FileSystem.get(this.hdfsConfig);
		}
		this.hdfsFSoutCache = CacheBuilder.newBuilder()
				.maximumSize(this.maxCacheSize)
				.expireAfterAccess(this.lastAccess, TimeUnit.SECONDS)
				.removalListener(new InodeGarbageCollector())
				.build(new HdfsOutputStreamSupplier(this));
	}

	@Override
	public Inode getRootInode() throws IOException {
		return toInode(FsInode.getRoot(jdbcFsProvider));
	}

	@Override
	public Inode lookup(Inode parent, String path) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("lookup:{} in parant:{}", path, parent.toString());
		try {
			FsInode parentFsInode = toFsInode(parent);
			FsInode fsInode = parentFsInode.inodeOf(path);
			return toInode(fsInode);
		} catch (FileNotFoundHimeraFsException e) {
			throw new NoEntException("Path Do not exist.");
		}
	}

	@Override
	public Inode create(Inode parent, Stat.Type type, String path, int uid,
			int gid, int mode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("create:{}", path);
		try {
			FsInode parentFsInode = toFsInode(parent);
			if (LOG.isDebugEnabled())
				LOG.debug("ParentFNode:{}", parentFsInode.toString());
			FsInode fsInode = jdbcFsProvider.createFile(parentFsInode, path,
					uid, gid, mode | typeToChimera(type), typeToChimera(type));
			return toInode(fsInode);
		} catch (FileExistsChimeraFsException e) {
			throw new ExistException("path already exists");
		}
	}

	@Override
	public Inode mkdir(Inode parent, String path, int uid, int gid, int mode)
			throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("mkdir:{}", path);
		try {
			FsInode parentFsInode = toFsInode(parent);
			FsInode fsInode = parentFsInode.mkdir(path, uid, gid, mode);
			return toInode(fsInode);
		} catch (FileExistsChimeraFsException e) {
			throw new ExistException("path already exists");
		}
	}

	@Override
	public Inode link(Inode parent, Inode link, String path, int uid, int gid)
			throws IOException {
		if (LOG.isInfoEnabled())
			LOG.info("link");
		FsInode parentFsInode = toFsInode(parent);
		FsInode linkInode = toFsInode(link);
		try {
			FsInode fsInode = jdbcFsProvider.createHLink(parentFsInode,
					linkInode, path);
			return toInode(fsInode);
		} catch (NotDirChimeraException e) {
			throw new NotDirException("parent not a directory");
		} catch (FileExistsChimeraFsException e) {
			throw new ExistException("path already exists");
		}
	}

	@Override
	public Inode symlink(Inode parent, String path, String link, int uid,
			int gid, int mode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("symlink");
		try {
			FsInode parentFsInode = toFsInode(parent);
			FsInode fsInode = jdbcFsProvider.createLink(parentFsInode, path,
					uid, gid, mode, link.getBytes(StandardCharsets.UTF_8));
			return toInode(fsInode);
		} catch (FileExistsChimeraFsException e) {
			throw new ExistException("path already exists");
		}
	}

	@Override
	public int read(Inode inode, byte[] data, long offset, int count)
			throws IOException {
		Path path = inode2path(inode);
		FSDataInputStream fin = this.hdfs.open(new Path(this.base, path));
		int ret = fin.read(offset, data, 0, count);
		fin.close();
		if (LOG.isDebugEnabled()) {
			LOG.debug("Path, {}, readed {}, offset, {}, count, {}", path, ret,
					offset, count);
		}
		return ret;
	}

	@Override
	public boolean move(Inode src, String oldName, Inode dest, String newName)
			throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("move");
		FsInode from = toFsInode(src);
		FsInode to = toFsInode(dest);
		try {
			return jdbcFsProvider.move(from, oldName, to, newName);
		} catch (NotDirChimeraException e) {
			throw new NotDirException("not a directory");
		} catch (FileExistsChimeraFsException e) {
			throw new ExistException("destination exists");
		} catch (DirNotEmptyHimeraFsException e) {
			throw new NotEmptyException("directory exist and not empty");
		} catch (FileNotFoundHimeraFsException e) {
			throw new NoEntException("file not found");
		}
	}

	@Override
	public String readlink(Inode inode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("readlink");
		FsInode fsInode = toFsInode(inode);
		int count = (int) fsInode.statCache().getSize();
		byte[] data = new byte[count];
		int n = jdbcFsProvider.read(fsInode, 0, data, 0, count);
		if (n < 0) {
			throw new NfsIoException("Can't read symlink");
		}
		return new String(data, 0, n, StandardCharsets.UTF_8);
	}

	@Override
	public void remove(Inode parent, String path) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("remove");
		FsInode parentFsInode = toFsInode(parent);
		try {
			jdbcFsProvider.remove(parentFsInode, path);
		} catch (FileNotFoundHimeraFsException e) {
			throw new NoEntException("path not found");
		} catch (DirNotEmptyHimeraFsException e) {
			throw new NotEmptyException("directory not empty");
		}
	}

	@Override
	public WriteResult write(Inode inode, byte[] data, long offset, int count,
			StabilityLevel stabilityLevel) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("write:length[{}] offset[{}] count[{}]", data.length,
					offset, count);
		FsInode fsInode = toFsInode(inode);
		int bytesWritten = fsInode.write(offset, data, 0, count);
		return new WriteResult(StabilityLevel.FILE_SYNC, bytesWritten);
	}

	@Override
	public void commit(Inode inode, long offset, int count) throws IOException {
		// nop (all IO is FILE_SYNC so no commits expected)
	}

	public void close(Inode inode) {
		if (this.hdfsFSoutCache.getIfPresent(inode) != null) {
			this.hdfsFSoutCache.invalidate(inode);
		}
	}

	public FSDataOutputStream getFSDataOutputStream(Inode inode)
			throws IOException {
		FSDataOutputStream fout = null;
		try {
			fout = this.hdfsFSoutCache.get(inode);
		} catch (ExecutionException e) {
			throw new IOException("Failed to open HDFS output stream", e);
		}
		return fout;
	}

	@Override
	public List<DirectoryEntry> list(Inode inode) throws IOException {
		FsInode parentFsInode = toFsInode(inode);
		if (LOG.isDebugEnabled())
			LOG.debug("list inode:{}", parentFsInode.toString());
		List<HimeraDirectoryEntry> list = DirectoryStreamHelper
				.listOf(parentFsInode);
		return Lists.transform(list, new ChimeraDirectoryEntryToVfs());
	}

	@Override
	public Inode parentOf(Inode inode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("parentOf");
		FsInode parent = toFsInode(inode).getParent();
		if (parent == null) {
			throw new NoEntException("no parent");
		}
		return toInode(parent);
	}

	@Override
	public FsStat getFsStat() throws IOException {
		org.dcache.chimera.FsStat fsStat = jdbcFsProvider.getFsStat();
		return new FsStat(fsStat.getTotalSpace(), fsStat.getTotalFiles(),
				fsStat.getUsedSpace(), fsStat.getUsedFiles());
	}

	@Override
	public Stat getattr(Inode inode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("getattr");
		FsInode fsInode = toFsInode(inode);
		try {
			return fromChimeraStat(fsInode.stat(), fsInode.id());
		} catch (FileNotFoundHimeraFsException e) {
			throw new NoEntException("Path Do not exist.");
		}
	}

	@Override
	public void setattr(Inode inode, Stat stat) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("setattr");
		FsInode fsInode = toFsInode(inode);
		fsInode.setStat(toChimeraStat(stat));
	}

	@Override
	public nfsace4[] getAcl(Inode inode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("getAcl");
		FsInode fsInode = toFsInode(inode);
		nfsace4[] aces;
		List<ACE> dacl = jdbcFsProvider.getACL(fsInode);
		org.dcache.chimera.posix.Stat stat = fsInode.statCache();

		nfsace4[] unixAcl = Acls.of(stat.getMode(), fsInode.isDirectory());
		aces = new nfsace4[dacl.size() + unixAcl.length];
		int i = 0;
		for (ACE ace : dacl) {
			aces[i] = valueOf(ace, this.idMapping);
			i++;
		}
		System.arraycopy(unixAcl, 0, aces, i, unixAcl.length);
		return Acls.compact(aces);
	}

	@Override
	public void setAcl(Inode inode, nfsace4[] acl) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("setAcl");
		FsInode fsInode = toFsInode(inode);
		List<ACE> dacl = new ArrayList<>();
		for (nfsace4 ace : acl) {
			dacl.add(valueOf(ace, this.idMapping));
		}
		jdbcFsProvider.setACL(fsInode, dacl);
	}

	private static Stat fromChimeraStat(org.dcache.chimera.posix.Stat pStat,
			long fileid) {
		Stat stat = new Stat();
		stat.setATime(pStat.getATime());
		stat.setCTime(pStat.getCTime());
		stat.setMTime(pStat.getMTime());
		stat.setGid(pStat.getGid());
		stat.setUid(pStat.getUid());
		stat.setDev(pStat.getDev());
		stat.setIno(pStat.getIno());
		stat.setMode(pStat.getMode());
		stat.setNlink(pStat.getNlink());
		stat.setRdev(pStat.getRdev());
		stat.setSize(pStat.getSize());
		stat.setFileid(fileid);
		stat.setGeneration(pStat.getGeneration());
		return stat;
	}

	private static org.dcache.chimera.posix.Stat toChimeraStat(Stat stat) {

		org.dcache.chimera.posix.Stat pStat = new org.dcache.chimera.posix.Stat();
		pStat.setATime(stat.getATime());
		pStat.setCTime(stat.getCTime());
		pStat.setMTime(stat.getMTime());

		pStat.setGid(stat.getGid());
		pStat.setUid(stat.getUid());
		pStat.setDev(stat.getDev());
		pStat.setIno(stat.getIno());
		pStat.setMode(stat.getMode());
		pStat.setNlink(stat.getNlink());
		pStat.setRdev(stat.getRdev());
		pStat.setSize(stat.getSize());
		pStat.setGeneration(stat.getGeneration());
		return pStat;
	}

	@Override
	public int access(Inode inode, int mode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("access");
		int accessmask = mode;
		if ((mode & (ACCESS4_MODIFY | ACCESS4_EXTEND)) != 0) {

			FsInode fsInode = toFsInode(inode);
			if (shouldRejectUpdates(fsInode)) {
				accessmask ^= (ACCESS4_MODIFY | ACCESS4_EXTEND);
			}
		}

		return accessmask;
	}

	private boolean shouldRejectUpdates(FsInode fsInode)
			throws ChimeraFsException {
		if (LOG.isDebugEnabled())
			LOG.debug("shouldRejectUpdates");
		return fsInode.type() == FsInodeType.INODE
				&& fsInode.getLevel() == 0
				&& !fsInode.isDirectory()
				&& (!jdbcFsProvider.getInodeLocations(fsInode,
						StorageGenericLocation.TAPE).isEmpty() || !jdbcFsProvider
						.getInodeLocations(fsInode, StorageGenericLocation.DISK)
						.isEmpty());
	}

	@Override
	public boolean hasIOLayout(Inode inode) throws IOException {
		if (LOG.isDebugEnabled())
			LOG.debug("hasIOLayout");
		FsInode fsInode = toFsInode(inode);
		return fsInode.type() == FsInodeType.INODE && fsInode.getLevel() == 0;
	}

	@Override
	public AclCheckable getAclCheckable() {
		if (LOG.isDebugEnabled())
			LOG.debug("AclCheckable");
		return this;
	}

	protected Path inode2path(Inode inode) throws IOException {
		String path = null;
		FsInode fsInode = toFsInode(inode);
		path = fsInode.getFs().inode2path(fsInode);
		return new Path(path);
	}

	protected FSDataOutputStream inode2FSDataOutputStream(Inode inode)
			throws IOException {
		FSDataOutputStream ret = null;
		try {
			Path path = inode2path(inode);
			if (LOG.isDebugEnabled())
				LOG.debug("Request path:{}", path);
			Path hdfsFile = new Path(this.base, path);
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

	protected FSDataInputStream inode2FSDataInputStream(Inode inode)
			throws IOException {
		Path path = inode2path(inode);
		return this.hdfs.open(new Path(this.base, path));
	}

	protected FsInode toFsInode(Inode inode) throws IOException {
		return this.jdbcFsProvider.inodeFromBytes(inode.getFileId());
	}

	protected Inode toInode(final FsInode inode) {
		try {
			return Inode.forFile(this.jdbcFsProvider.inodeToBytes(inode));
		} catch (ChimeraFsException e) {
			throw new RuntimeException("bug found", e);
		}
	}

	private int typeToChimera(Stat.Type type) {
		switch (type) {
		case SYMLINK:
			return UnixPermission.S_IFLNK;
		case DIRECTORY:
			return UnixPermission.S_IFDIR;
		case SOCK:
			return UnixPermission.S_IFSOCK;
		case FIFO:
			return UnixPermission.S_IFIFO;
		case BLOCK:
			return UnixPermission.S_IFBLK;
		case CHAR:
			return UnixPermission.S_IFCHR;
		case REGULAR:
		default:
			return UnixPermission.S_IFREG;
		}
	}

	private static nfsace4 valueOf(ACE ace, NfsIdMapping idMapping) {

		String principal;
		switch (ace.getWho()) {
		case USER:
			principal = idMapping.uidToPrincipal(ace.getWhoID());
			break;
		case GROUP:
			principal = idMapping.gidToPrincipal(ace.getWhoID());
			break;
		default:
			principal = ace.getWho().getAbbreviation();
		}

		nfsace4 nfsace = new nfsace4();
		nfsace.access_mask = new acemask4(new uint32_t(ace.getAccessMsk()));
		nfsace.flag = new aceflag4(new uint32_t(ace.getFlags()));
		nfsace.type = new acetype4(new uint32_t(ace.getType().getValue()));
		nfsace.who = new utf8str_mixed(principal);
		return nfsace;
	}

	private static ACE valueOf(nfsace4 ace, NfsIdMapping idMapping) {
		String principal = ace.who.toString();
		int type = ace.type.value.value;
		int flags = ace.flag.value.value;
		int mask = ace.access_mask.value.value;

		int id = -1;
		Who who = Who.fromAbbreviation(principal);
		if (who == null) {
			// not a special pricipal
			boolean isGroup = AceFlags.IDENTIFIER_GROUP.matches(flags);
			if (isGroup) {
				who = Who.GROUP;
				id = idMapping.principalToGid(principal);
			} else {
				who = Who.USER;
				id = idMapping.principalToUid(principal);
			}
		}
		return new ACE(AceType.valueOf(type), flags, mask, who, id,
				ACE.DEFAULT_ADDRESS_MSK);
	}

	@Override
	public Access checkAcl(Subject subject, Inode inode, int access)
			throws IOException {
		FsInode fsInode = toFsInode(inode);
		List<ACE> acl = jdbcFsProvider.getACL(fsInode);
		org.dcache.chimera.posix.Stat stat = jdbcFsProvider.stat(fsInode);
		return checkAcl(subject, acl, stat.getUid(), stat.getGid(), access);
	}

	private Access checkAcl(Subject subject, List<ACE> acl, int owner,
			int group, int access) {

		for (ACE ace : acl) {

			int flag = ace.getFlags();
			if ((flag & ACE4_INHERIT_ONLY_ACE) != 0) {
				continue;
			}

			if ((ace.getType() != AceType.ACCESS_ALLOWED_ACE_TYPE)
					&& (ace.getType() != AceType.ACCESS_DENIED_ACE_TYPE)) {
				continue;
			}

			int ace_mask = ace.getAccessMsk();
			if ((ace_mask & access) == 0) {
				continue;
			}

			Who who = ace.getWho();

			if ((who == Who.EVERYONE)
					|| (who == Who.OWNER & Subjects.hasUid(subject, owner))
					|| (who == Who.OWNER_GROUP & Subjects
							.hasGid(subject, group))
					|| (who == Who.GROUP & Subjects.hasGid(subject,
							ace.getWhoID()))
					|| (who == Who.USER & Subjects.hasUid(subject,
							ace.getWhoID()))) {

				if (ace.getType() == AceType.ACCESS_DENIED_ACE_TYPE) {
					return Access.DENY;
				} else {
					return Access.ALLOW;
				}
			}
		}

		return Access.UNDEFINED;
	}

	private class ChimeraDirectoryEntryToVfs implements
			Function<HimeraDirectoryEntry, DirectoryEntry> {
		@Override
		public DirectoryEntry apply(HimeraDirectoryEntry e) {
			if (LOG.isDebugEnabled())
				LOG.debug(
						"transform HimeraDirectoryEntry to DirectoryEntry:{}",
						e.getName());
			return new DirectoryEntry(e.getName(), toInode(e.getInode()),
					fromChimeraStat(e.getStat(), e.getInode().id()));
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

		private final HadoopHdfsVfs hdfsDriver;

		HdfsOutputStreamSupplier(HadoopHdfsVfs hdfsDriver) throws IOException {
			this.hdfsDriver = hdfsDriver;
		}

		@Override
		public FSDataOutputStream load(Inode inode) throws IOException {
			return this.hdfsDriver.inode2FSDataOutputStream(inode);
		}

	}

}
