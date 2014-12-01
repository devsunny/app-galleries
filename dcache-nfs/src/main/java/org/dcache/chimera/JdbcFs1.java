package org.dcache.chimera;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.StringTokenizer;

import javax.sql.DataSource;

import org.dcache.acl.ACE;
import org.dcache.chimera.posix.Stat;
import org.dcache.chimera.store.AccessLatency;
import org.dcache.chimera.store.InodeStorageInformation;
import org.dcache.chimera.store.RetentionPolicy;
import org.dcache.chimera.util.SqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jolbox.bonecp.BoneCPDataSource;

public class JdbcFs1 implements FileSystemProvider {
	private static final Logger _log = LoggerFactory
			.getLogger(JdbcFs1.class);

	private static final int LEVELS_NUMBER = 7;

	private final FsInode _rootInode;

	private final String _wormID;

	private static final int MIN_HANDLE_LEN = 4;

	private final FsSqlDriver1 _sqlDriver;

	private final DataSource _dbConnectionsPool;

	private final FsStatCache _fsStatCache;

	private final int _fsId;

	static final long AVAILABLE_SPACE = 1152921504606846976L;

	static final long TOTAL_FILES = 62914560L;

	private static final int MAX_NAME_LEN = 255;

	public JdbcFs1(DataSource dataSource, String dialect) {
		this(dataSource, dialect, 0);
	}

	public JdbcFs1(DataSource dataSource, String dialect, int id) {
		this._dbConnectionsPool = dataSource;
		this._fsId = id;

		this._sqlDriver = FsSqlDriver1.getDriverInstance(dialect);

		this._rootInode = new FsInode(this,
				"000000000000000000000000000000000000");

		String wormID = null;
		try {
			wormID = getWormID().toString();
		} catch (Exception e) {
		}
		this._wormID = wormID;
		this._fsStatCache = new FsStatCache(this);
	}

	private FsInode getWormID() throws ChimeraFsException {
		return path2inode("/admin/etc/config");
	}

	public FsInode createLink(String src, String dest)
			throws ChimeraFsException {
		File file = new File(src);
		return createLink(path2inode(file.getParent()),
				file.getName(), dest);
	}

	public FsInode createLink(FsInode parent, String name, String dest)
			throws ChimeraFsException {
		return createLink(parent, name, 0, 0, 420, dest.getBytes());
	}

	public FsInode createLink(FsInode parent, String name, int uid, int gid,
			int mode, byte[] dest) throws ChimeraFsException {
		checkNameLength(name);

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		FsInode inode;

		try {
			dbConnection.setAutoCommit(false);

			if ((parent.statCache().getMode() & 0x400) != 0) {
				gid = parent.statCache().getGid();
			}

			inode = this._sqlDriver.createFile(dbConnection, parent,
					name, uid, gid, mode, 40960);

			this._sqlDriver.setInodeIo(dbConnection, inode, true);
			this._sqlDriver.write(dbConnection, inode, 0, 0L, dest, 0,
					dest.length);

			dbConnection.commit();
		} catch (SQLException se) {
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("createLink rollback ", e);
			}

			String sqlState = se.getSQLState();
			if (this._sqlDriver.isDuplicatedKeyError(sqlState)) {
				throw new FileExistsChimeraFsException();
			}
			_log.error("createLink ", se);
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return inode;
	}

	public FsInode createHLink(FsInode parent, FsInode inode, String name)
			throws ChimeraFsException {
		checkNameLength(name);

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.createEntryInParent(dbConnection, parent,
					name, inode);
			this._sqlDriver.incNlink(dbConnection, inode);
			this._sqlDriver.incNlink(dbConnection, parent);

			dbConnection.commit();
		} catch (SQLException e) {
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("create hlink rollback ", e);
			}

			String sqlState = e.getSQLState();
			if (this._sqlDriver.isDuplicatedKeyError(sqlState)) {
				throw new FileExistsChimeraFsException();
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return inode;
	}

	public FsInode createFile(String path) throws ChimeraFsException {
		File file = new File(path);

		return createFile(path2inode(file.getParent()), file.getName());
	}

	public FsInode createFile(FsInode parent, String name)
			throws ChimeraFsException {
		return createFile(parent, name, 0, 0, 420);
	}

	public FsInode createFileLevel(FsInode inode, int level)
			throws ChimeraFsException {
		return createFileLevel(inode, 0, 0, 420, level);
	}

	public FsInode createFile(FsInode parent, String name, int owner,
			int group, int mode) throws ChimeraFsException {
		return createFile(parent, name, owner, group, mode, 32768);
	}

	public FsInode createFile(FsInode parent, String name, int owner,
			int group, int mode, int type) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		FsInode inode = null;

		try {
			if (name.startsWith(".(")) {
				String[] cmd = PnfsCommandProcessor.process(name);

				if ((name.startsWith(".(tag)(")) && (cmd.length == 2)) {
					createTag(parent, cmd[1], owner, group, 420);
					return new FsInode_TAG(this, parent.toString(),
							cmd[1]);
				}

				if ((name.startsWith(".(pset)("))
						|| (name.startsWith(".(fset)("))) {
					throw new ChimeraFsException("Not supported");
				}

				if ((name.startsWith(".(use)(")) && (cmd.length == 3)) {
					FsInode useInode = inodeOf(parent, cmd[2]);
					int level = Integer.parseInt(cmd[1]);

					try {
						dbConnection.setAutoCommit(false);

						inode = this._sqlDriver.createLevel(
								dbConnection, useInode, useInode.stat()
										.getUid(), useInode.stat().getGid(),
								useInode.stat().getMode(), level);

						dbConnection.commit();

					} catch (SQLException se) {

						if (se.getSQLState().startsWith("23")) {
							throw new FileExistsChimeraFsException(
									name);
						}
						_log.error("create File: ", se);
						try {
							dbConnection.rollback();
						} catch (SQLException e) {
							_log.error("create File rollback ", e);
						}
					}
				}
				FsInode accessInode;
				if ((name.startsWith(".(access)("))
						&& (cmd.length == 3)) {
					accessInode = new FsInode(this, cmd[1]);
					int accessLevel = Integer.parseInt(cmd[2]);
					if (accessLevel == 0) {
						inode = accessInode;
					} else {
						try {
							dbConnection.setAutoCommit(false);

							inode = this._sqlDriver.createLevel(
									dbConnection, accessInode, accessInode
											.stat().getUid(), accessInode
											.stat().getGid(), accessInode
											.stat().getMode(), accessLevel);

							dbConnection.commit();

						} catch (SQLException se) {
							if (se.getSQLState().startsWith("23")) {
								throw new FileExistsChimeraFsException(
										name);
							}
							_log.error("create File: ", se);
							try {
								dbConnection.rollback();
							} catch (SQLException e) {
								_log.error("create File rollback ", e);
							}
						}
					}
				}

				return inode;
			}

			try {
				checkNameLength(name);

				dbConnection.setAutoCommit(false);
				Stat parentStat = this._sqlDriver.stat(dbConnection,
						parent);
				if (parentStat == null) {
					throw new FileNotFoundHimeraFsException("parent="
							+ parent.toString());
				}

				if ((parentStat.getMode() & 0xF000) != 16384) {
					throw new NotDirChimeraException(parent);
				}

				if ((parentStat.getMode() & 0x400) != 0) {
					group = parent.statCache().getGid();
				}

				inode = this._sqlDriver.createFile(dbConnection,
						parent, name, owner, group, mode, type);
				dbConnection.commit();
			} catch (SQLException se) {
				try {
					dbConnection.rollback();
				} catch (SQLException e) {
					_log.error("create File rollback ", e);
				}

				if (se.getSQLState().startsWith("23")) {

					throw new FileExistsChimeraFsException();
				}
				_log.error("create File: ", se);
				throw new IOHimeraFsException(se.getMessage());
			}
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return inode;
	}

	public void createFileWithId(FsInode parent, FsInode inode, String name,
			int owner, int group, int mode, int type) throws ChimeraFsException {
		checkNameLength(name);

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			if (!parent.exists()) {
				throw new FileNotFoundHimeraFsException("parent="
						+ parent.toString());
			}

			if (parent.isDirectory()) {
				dbConnection.setAutoCommit(false);

				if ((parent.statCache().getMode() & 0x400) != 0) {
					group = parent.statCache().getGid();
				}

				inode = this._sqlDriver.createFileWithId(dbConnection,
						parent, inode, name, owner, group, mode, type);
				dbConnection.commit();
			} else {
				throw new NotDirChimeraException(parent);
			}
		} catch (SQLException se) {
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("create File rollback ", e);
			}

			if (se.getSQLState().startsWith("23")) {

				throw new FileExistsChimeraFsException();
			}
			_log.error("create File: ", se);
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	FsInode createFileLevel(FsInode inode, int owner, int group, int mode,
			int level) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		FsInode levelInode = null;

		try {
			dbConnection.setAutoCommit(false);

			levelInode = this._sqlDriver.createLevel(dbConnection,
					inode, owner, group, mode | 0x8000, level);
			dbConnection.commit();
		} catch (SQLException se) {
			_log.error("create level: ", se);
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("create level rollback ", e);
			}
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return levelInode;
	}

	public String[] listDir(String dir) {
		String[] list = null;
		try {
			list = listDir(path2inode(dir));
		} catch (Exception e) {
		}

		return list;
	}

	public String[] listDir(FsInode dir) throws IOHimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		String[] list = null;

		try {
			dbConnection.setAutoCommit(true);

			list = this._sqlDriver.listDir(dbConnection, dir);
		} catch (SQLException se) {
			_log.error("list: ", se);
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return list;
	}

	public DirectoryStreamB<HimeraDirectoryEntry> newDirectoryStream(FsInode dir)
			throws IOHimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(true);

			return this._sqlDriver.newDirectoryStream(dbConnection,
					dir);
		} catch (SQLException se) {
			_log.error("list full: ", se);
			SqlHelper.tryToClose(dbConnection);
			throw new IOHimeraFsException(se.getMessage());
		}
	}

	public void remove(String path) throws ChimeraFsException {
		File filePath = new File(path);

		String parentPath = filePath.getParent();
		if (parentPath == null) {
			throw new ChimeraFsException(
					"Cannot delete file system root.");
		}

		FsInode parent = path2inode(parentPath);
		String name = filePath.getName();
		remove(parent, name);
	}

	public void remove(FsInode parent, String name) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.remove(dbConnection, parent, name);
			dbConnection.commit();
		} catch (ChimeraFsException hfe) {
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("delete rollback", e);
			}
			throw hfe;
		} catch (SQLException e) {
			_log.error("delete", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("delete rollback", e1);
			}
			throw new BackEndErrorHimeraFsException(e.getMessage(), e);
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void remove(FsInode inode) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			FsInode parent = this._sqlDriver.getParentOf(dbConnection,
					inode);
			if (parent == null) {
				throw new FileNotFoundHimeraFsException(
						"No such file.");
			}

			if (inode.type() != FsInodeType.INODE) {
				throw new FileNotFoundHimeraFsException("Not a file.");
			}

			this._sqlDriver.remove(dbConnection, parent, inode);
			dbConnection.commit();
		} catch (ChimeraFsException hfe) {
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("delete rollback", e);
			}
			throw hfe;
		} catch (SQLException e) {
			_log.error("delete", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("delete rollback", e1);
			}
			throw new BackEndErrorHimeraFsException(e.getMessage(), e);
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public Stat stat(String path) throws ChimeraFsException {
		return stat(path2inode(path));
	}

	public Stat stat(FsInode inode) throws ChimeraFsException {
		return stat(inode, 0);
	}

	public Stat stat(FsInode inode, int level) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		Stat stat = null;

		try {
			dbConnection.setAutoCommit(true);

			stat = this._sqlDriver.stat(dbConnection, inode, level);
		} catch (SQLException e) {
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		if (stat == null) {
			throw new FileNotFoundHimeraFsException(inode.toString());
		}

		return stat;
	}

	public FsInode mkdir(String path) throws ChimeraFsException {
		int li = path.lastIndexOf('/');
		String file = path.substring(li + 1);
		String dir;
		if (li > 1) {
			dir = path.substring(0, li);
		} else {
			dir = "/";
		}

		return mkdir(path2inode(dir), file);
	}

	public FsInode mkdir(FsInode parent, String name) throws ChimeraFsException {
		return mkdir(parent, name, 0, 0, 493);
	}

	public FsInode mkdir(FsInode parent, String name, int owner, int group,
			int mode) throws ChimeraFsException {
		checkNameLength(name);

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		FsInode inode = null;

		try {
			dbConnection.setAutoCommit(false);

			if ((parent.statCache().getMode() & 0x400) != 0) {
				group = parent.statCache().getGid();
				mode |= 0x400;
			}

			inode = this._sqlDriver.mkdir(dbConnection, parent, name,
					owner, group, mode);
			this._sqlDriver.copyTags(dbConnection, parent, inode);
			dbConnection.commit();
		} catch (SQLException se) {
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("mkdir", se);
			}

			if (se.getSQLState().startsWith("23")) {
				throw new FileExistsChimeraFsException(name);
			}
			_log.error("mkdir", se);
			throw new ChimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return inode;
	}

	public FsInode path2inode(String path) throws ChimeraFsException {
		if(_log.isInfoEnabled()) _log.info("PATH:{}", path);
		return path2inode(path, this._rootInode);
	}

	public FsInode path2inode(String path, FsInode startFrom)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		FsInode inode = null;

		try {
			dbConnection.setAutoCommit(true);
			inode = this._sqlDriver.path2inode(dbConnection,
					startFrom, path);

			if (inode == null) {
				throw new FileNotFoundHimeraFsException(path);
			}
		} catch (SQLException e) {
			_log.error("path2inode", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return inode;
	}

	public List<FsInode> path2inodes(String path) throws ChimeraFsException {
		return path2inodes(path, this._rootInode);
	}

	public List<FsInode> path2inodes(String path, FsInode startFrom)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		List<FsInode> inodes;
		try {
			dbConnection.setAutoCommit(true);
			inodes = this._sqlDriver.path2inodes(dbConnection,
					startFrom, path);

			if (inodes.isEmpty()) {
				throw new FileNotFoundHimeraFsException(path);
			}
		} catch (SQLException e) {
			_log.error("path2inode", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return inodes;
	}

	public FsInode inodeOf(FsInode parent, String name)
			throws ChimeraFsException {
		FsInode inode = null;

		if (name.startsWith(".(")) {
			if (name.startsWith(".(id)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				inode = inodeOf(parent, cmd[1]);

				return new FsInode_ID(this, inode.toString());
			}

			if (name.startsWith(".(use)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 3) {
					throw new FileNotFoundHimeraFsException(name);
				}
				try {
					int level = Integer.parseInt(cmd[1]);

					FsInode useInode = inodeOf(parent, cmd[2]);

					if (level <= LEVELS_NUMBER ) {
						stat(useInode, level);
						return new FsInode(this, useInode.toString(),
								level);
					}

				} catch (NumberFormatException nfe) {
				} catch (FileNotFoundHimeraFsException e) {
					throw new FileNotFoundHimeraFsException(name);
				}
			}

			if (name.startsWith(".(access)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if ((cmd.length < 2) || (cmd.length > 3)) {
					throw new FileNotFoundHimeraFsException(name);
				}
				try {
					int level = cmd.length == 2 ? 0 : Integer
							.parseInt(cmd[2]);

					FsInode useInode = new FsInode(this, cmd[1]);

					if (level <= LEVELS_NUMBER ) {
						stat(useInode, level);
						return new FsInode(this, useInode.toString(),
								level);
					}

				} catch (NumberFormatException nfe) {
				} catch (FileNotFoundHimeraFsException e) {
					throw new FileNotFoundHimeraFsException(name);
				}
			}

			if (name.startsWith(".(nameof)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				FsInode nameofInode = new FsInode_NAMEOF(this, cmd[1]);
				if (!nameofInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return nameofInode;
			}

			if (name.startsWith(".(const)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				FsInode constInode = new FsInode_CONST(this,
						parent.toString());
				if (!constInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return constInode;
			}

			if (name.startsWith(".(parent)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				FsInode parentInode = new FsInode_PARENT(this, cmd[1]);
				if (!parentInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return parentInode;
			}

			if (name.startsWith(".(pathof)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				FsInode pathofInode = new FsInode_PATHOF(this, cmd[1]);
				if (!pathofInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return pathofInode;
			}

			if (name.startsWith(".(tag)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				FsInode tagInode = new FsInode_TAG(this,
						parent.toString(), cmd[1]);
				if (!tagInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return tagInode;
			}

			if (name.equals(".(tags)()")) {
				return new FsInode_TAGS(this, parent.toString());
			}

			if (name.startsWith(".(pset)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length < 3) {
					throw new FileNotFoundHimeraFsException(name);
				}
				String[] args = new String[cmd.length - 2];
				System.arraycopy(cmd, 2, args, 0, args.length);
				FsInode psetInode = new FsInode_PSET(this, cmd[1],
						args);
				if (!psetInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return psetInode;
			}

			if (name.equals(".(get)(cursor)")) {
				FsInode pgetInode = new FsInode_PGET(this,
						parent.toString(), new String[0]);
				if (!pgetInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return pgetInode;
			}

			if (name.startsWith(".(get)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length < 3) {
					throw new FileNotFoundHimeraFsException(name);
				}

				String[] args = new String[cmd.length - 1];
				System.arraycopy(cmd, 1, args, 0, args.length);
				inode = getPGET(parent, args);
				if (!inode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return inode;
			}

			if (name.equals(".(config)")) {
				return new FsInode(this, this._wormID);
			}

			if (name.startsWith(".(config)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length != 2) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return inodeOf(new FsInode(this, this._wormID),
						cmd[1]);
			}

			if (name.startsWith(".(fset)(")) {
				String[] cmd = PnfsCommandProcessor.process(name);
				if (cmd.length < 3) {
					throw new FileNotFoundHimeraFsException(name);
				}
				String[] args = new String[cmd.length - 2];
				System.arraycopy(cmd, 2, args, 0, args.length);

				FsInode fsetInode = inodeOf(parent, cmd[1]);
				if (!fsetInode.exists()) {
					throw new FileNotFoundHimeraFsException(name);
				}
				return new FsInode_PSET(this, fsetInode.toString(),
						args);
			}
		}

		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(true);

			inode = this._sqlDriver.inodeOf(dbConnection, parent,
					name);

			if (inode == null) {
				throw new FileNotFoundHimeraFsException(name);
			}
		} catch (SQLException e) {
			_log.error("inodeOf", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		inode.setParent(parent);

		return inode;
	}

	public String inode2path(FsInode inode) throws ChimeraFsException {
		return inode2path(inode, this._rootInode, true);
	}

	public String inode2path(FsInode inode, FsInode startFrom, boolean inclusive)
			throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		String path = null;

		try {
			dbConnection.setAutoCommit(true);

			path = this._sqlDriver.inode2path(dbConnection, inode,
					startFrom, inclusive);
		} catch (SQLException e) {
			_log.error("inode2path", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return path;
	}

	public boolean removeFileMetadata(String path, int level)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		boolean rc = false;

		try {
			dbConnection.setAutoCommit(false);

			rc = this._sqlDriver.removeInodeLevel(dbConnection,
					path2inode(path), level);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("removeFileMetadata", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("removeFileMetadata rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return rc;
	}

	public FsInode getParentOf(FsInode inode) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		FsInode parent = null;

		try {
			dbConnection.setAutoCommit(true);

			if (inode.isDirectory()) {
				parent = this._sqlDriver.getParentOfDirectory(
						dbConnection, inode);
			} else {
				parent = this._sqlDriver.getParentOf(dbConnection,
						inode);
			}
		} catch (SQLException e) {
			_log.error("getPathOf", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return parent;
	}

	public void setFileSize(FsInode inode, long newSize)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileSize(dbConnection, inode, newSize);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileSize", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileSize rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileOwner(FsInode inode, int newOwner)
			throws ChimeraFsException {
		setFileOwner(inode, 0, newOwner);
	}

	public void setFileOwner(FsInode inode, int level, int newOwner)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileOwner(dbConnection, inode, level,
					newOwner);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileOwner", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileOwner rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileName(FsInode dir, String oldName, String newName)
			throws ChimeraFsException {
		move(dir, oldName, dir, newName);
	}

	public void setInodeAttributes(FsInode inode, int level, Stat stat)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			switch (inode.type()) {
			case INODE:
				this._sqlDriver.setInodeAttributes(dbConnection,
						inode, level, stat);
				break;
			case TAG:
				this._sqlDriver.setTagMode(dbConnection,
						(FsInode_TAG) inode, stat.getMode());
				this._sqlDriver.setTagOwner(dbConnection,
						(FsInode_TAG) inode, stat.getUid());
				this._sqlDriver.setTagOwnerGroup(dbConnection,
						(FsInode_TAG) inode, stat.getGid());
			}

			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setInodeAttributes", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setInodeAttributes rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileATime(FsInode inode, long atime)
			throws ChimeraFsException {
		setFileATime(inode, 0, atime);
	}

	public void setFileATime(FsInode inode, int level, long atime)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileATime(dbConnection, inode, level,
					atime);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileATime", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileATime rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileCTime(FsInode inode, long ctime)
			throws ChimeraFsException {
		setFileCTime(inode, 0, ctime);
	}

	public void setFileCTime(FsInode inode, int level, long ctime)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileCTime(dbConnection, inode, level,
					ctime);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileCTime", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileCTime rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileMTime(FsInode inode, long mtime)
			throws ChimeraFsException {
		setFileMTime(inode, 0, mtime);
	}

	public void setFileMTime(FsInode inode, int level, long mtime)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileMTime(dbConnection, inode, level,
					mtime);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileMTime", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileMTime rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileGroup(FsInode inode, int newGroup)
			throws ChimeraFsException {
		setFileGroup(inode, 0, newGroup);
	}

	public void setFileGroup(FsInode inode, int level, int newGroup)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileGroup(dbConnection, inode, level,
					newGroup);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileGroup", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileGroup rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setFileMode(FsInode inode, int newMode)
			throws ChimeraFsException {
		setFileMode(inode, 0, newMode);
	}

	public void setFileMode(FsInode inode, int level, int newMode)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setFileMode(dbConnection, inode, level,
					newMode);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setFileMode", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setFileMode rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public boolean isIoEnabled(FsInode inode) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		boolean ioEnabled = false;

		try {
			dbConnection.setAutoCommit(true);

			ioEnabled = this._sqlDriver.isIoEnabled(dbConnection,
					inode);
		} catch (SQLException e) {
			_log.error("isIoEnabled", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return ioEnabled;
	}

	public void setInodeIo(FsInode inode, boolean enable)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setInodeIo(dbConnection, inode, enable);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setInodeIo", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setInodeIo rollback", e1);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public int write(FsInode inode, long beginIndex, byte[] data, int offset,
			int len) throws ChimeraFsException {
		return write(inode, 0, beginIndex, data, offset, len);
	}

	public int write(FsInode inode, int level, long beginIndex, byte[] data,
			int offset, int len) throws ChimeraFsException {
		if ((level == 0) && (!inode.isIoEnabled())) {
			_log.debug(inode + ": IO (write) not allowd");
			return -1;
		}

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.write(dbConnection, inode, level,
					beginIndex, data, offset, len);
			dbConnection.commit();
		} catch (SQLException e) {
			String sqlState = e.getSQLState();
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("write rollback", e);
			}

			if (this._sqlDriver.isForeignKeyError(sqlState)) {
				throw new FileNotFoundHimeraFsException();
			}
			_log.error("write", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return len;
	}

	public int read(FsInode inode, long beginIndex, byte[] data, int offset,
			int len) throws ChimeraFsException {
		return read(inode, 0, beginIndex, data, offset, len);
	}

	public int read(FsInode inode, int level, long beginIndex, byte[] data,
			int offset, int len) throws ChimeraFsException {
		int count = -1;

		if ((level == 0) && (!inode.isIoEnabled())) {
			_log.debug(inode + ": IO(read) not allowd");
			return -1;
		}

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(true);

			count = this._sqlDriver.read(dbConnection, inode, level,
					beginIndex, data, offset, len);
		} catch (SQLException se) {
			_log.debug("read:", se);
			throw new IOHimeraFsException(se.getMessage());
		} catch (IOException e) {
			_log.debug("read IO:", e);
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return count;
	}

	public byte[] readLink(String path) throws ChimeraFsException {
		return readLink(path2inode(path));
	}

	public byte[] readLink(FsInode inode) throws ChimeraFsException {
		byte[] b = new byte[(int) inode.statCache().getSize()];

		int n = read(inode, 0L, b, 0, b.length);
		byte[] link;
		if (n >= 0) {
			link = b;
		} else {
			link = new byte[0];
		}

		return link;
	}

	public boolean move(String source, String dest) {
		boolean rc;
		try {
			File what = new File(source);
			File where = new File(dest);

			rc = move(path2inode(what.getParent()), what.getName(),
					path2inode(where.getParent()), where.getName());
		} catch (Exception e) {
			rc = false;
		}

		return rc;
	}

	public boolean move(FsInode srcDir, String source, FsInode destDir,
			String dest) throws ChimeraFsException {
		checkNameLength(dest);

		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		boolean rc = false;

		try {
			dbConnection.setAutoCommit(false);

			Stat destStat = this._sqlDriver.stat(dbConnection,
					destDir);
			if ((destStat.getMode() & 0xF000) != 16384) {
				throw new NotDirChimeraException();
			}

			FsInode destInode = this._sqlDriver.inodeOf(dbConnection,
					destDir, dest);
			FsInode srcInode = this._sqlDriver.inodeOf(dbConnection,
					srcDir, source);
			if (srcInode == null) {
				throw new FileNotFoundHimeraFsException();
			}

			if (destInode != null) {
				Stat statDest = this._sqlDriver.stat(dbConnection,
						destInode);
				Stat statSrc = this._sqlDriver.stat(dbConnection,
						srcInode);
				if (destInode.equals(srcInode)) {
					dbConnection.commit();
					return false;
				}

				if ((statSrc.getMode() & 0x3F000) != (statDest
						.getMode() & 0x3F000)) {
					throw new FileExistsChimeraFsException();
				}

				this._sqlDriver.remove(dbConnection, destDir, dest);
			}

			if (!srcDir.equals(destDir)) {
				this._sqlDriver.move(dbConnection, srcDir, source,
						destDir, dest);
				this._sqlDriver.incNlink(dbConnection, destDir);
				this._sqlDriver.decNlink(dbConnection, srcDir);
			} else {
				long now = System.currentTimeMillis();
				this._sqlDriver.setFileName(dbConnection, srcDir,
						source, dest);
				this._sqlDriver.setFileMTime(dbConnection, destDir,
						0, now);
				this._sqlDriver.setFileCTime(dbConnection, destDir,
						0, now);
			}

			dbConnection.commit();
			rc = true;
		} catch (SQLException e) {
			_log.error("move:", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("move rollback:", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return rc;
	}

	public List<StorageLocatable> getInodeLocations(FsInode inode, int type)
			throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		List<StorageLocatable> locations = null;

		try {
			dbConnection.setAutoCommit(true);

			locations = this._sqlDriver.getInodeLocations(
					dbConnection, inode, type);
		} catch (SQLException se) {
			_log.error("getInodeLocations", se);
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return locations;
	}

	public void addInodeLocation(FsInode inode, int type, String location)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.addInodeLocation(dbConnection, inode,
					type, location);
			dbConnection.commit();
		} catch (SQLException se) {
			String sqlState = se.getSQLState();
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("addInodeLocation rollback ", e);
			}

			if (this._sqlDriver.isForeignKeyError(sqlState)) {
				throw new FileNotFoundHimeraFsException();
			}

			if (!this._sqlDriver.isDuplicatedKeyError(sqlState)) {

				_log.error("addInodeLocation:  [" + sqlState + "]",
						se);
				throw new IOHimeraFsException(se.getMessage());
			}
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void clearInodeLocation(FsInode inode, int type, String location)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.clearInodeLocation(dbConnection, inode,
					type, location);
			dbConnection.commit();
		} catch (SQLException se) {
			_log.error("clearInodeLocation", se);
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("clearInodeLocation rollback ", se);
			}
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public String[] tags(FsInode inode) throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		String[] list = null;
		try {
			dbConnection.setAutoCommit(true);

			list = this._sqlDriver.tags(dbConnection, inode);
		} catch (SQLException se) {
			_log.error("tags", se);
			throw new IOHimeraFsException(se.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return list;
	}

	public void createTag(FsInode inode, String name) throws ChimeraFsException {
		createTag(inode, name, 0, 0, 420);
	}

	public void createTag(FsInode inode, String name, int uid, int gid, int mode)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.createTag(dbConnection, inode, name, uid,
					gid, mode);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("createTag", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("createTag rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public int setTag(FsInode inode, String tagName, byte[] data, int offset,
			int len) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setTag(dbConnection, inode, tagName,
					data, offset, len);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setTag", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setTag rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} catch (ChimeraFsException e) {
			_log.error("setTag", e);
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return len;
	}

	public void removeTag(FsInode dir, String tagName)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.removeTag(dbConnection, dir, tagName);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("removeTag", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("removeTag rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void removeTag(FsInode dir) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.removeTag(dbConnection, dir);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("removeTag", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("removeTag rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public int getTag(FsInode inode, String tagName, byte[] data, int offset,
			int len) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		int count = -1;

		try {
			dbConnection.setAutoCommit(true);

			count = this._sqlDriver.getTag(dbConnection, inode,
					tagName, data, offset, len);
		} catch (SQLException e) {
			_log.error("getTag", e);
			throw new IOHimeraFsException(e.getMessage());
		} catch (IOException e) {
			_log.error("getTag io", e);
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return count;
	}

	public Stat statTag(FsInode dir, String name) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		Stat ret = null;

		try {
			dbConnection.setAutoCommit(true);

			ret = this._sqlDriver.statTag(dbConnection, dir, name);
		} catch (SQLException e) {
			_log.error("statTag", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return ret;
	}

	public void setTagOwner(FsInode_TAG tagInode, String name, int owner)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver
					.setTagOwner(dbConnection, tagInode, owner);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setTagOwner", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setTagOwner rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setTagOwnerGroup(FsInode_TAG tagInode, String name, int owner)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setTagOwnerGroup(dbConnection, tagInode,
					owner);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setTagOwnerGroup", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setTagOwnerGroup rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setTagMode(FsInode_TAG tagInode, String name, int mode)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setTagMode(dbConnection, tagInode, mode);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("setTagMode", e);
			try {
				dbConnection.rollback();
			} catch (SQLException e1) {
				_log.error("setTagMode rollback", e);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public int getFsId() {
		return this._fsId;
	}

	public void setStorageInfo(FsInode inode,
			InodeStorageInformation storageInfo) throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setStorageInfo(dbConnection, inode,
					storageInfo);
			dbConnection.commit();
		} catch (SQLException se) {
			String sqlState = se.getSQLState();
			try {
				dbConnection.rollback();
			} catch (SQLException e) {
				_log.error("setStorageInfo rollback ", e);
			}

			if (this._sqlDriver.isForeignKeyError(sqlState)) {
				throw new FileNotFoundHimeraFsException();
			}

			if (!this._sqlDriver.isDuplicatedKeyError(sqlState)) {

				_log.error("setStorageInfo:  [" + sqlState + "]", se);
				throw new IOHimeraFsException(se.getMessage());
			}
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setAccessLatency(FsInode inode, AccessLatency accessLatency)
			throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setAccessLatency(dbConnection, inode,
					accessLatency);
			dbConnection.commit();
		} catch (SQLException e) {
			String sqlState = e.getSQLState();
			try {
				dbConnection.rollback();
			} catch (SQLException ee) {
				_log.error("setAccessLatensy rollback ", ee);
			}

			if (this._sqlDriver.isForeignKeyError(sqlState)) {
				throw new FileNotFoundHimeraFsException();
			}
			_log.error("setAccessLatency:  [" + sqlState + "]", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void setRetentionPolicy(FsInode inode,
			RetentionPolicy retentionPolicy) throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setRetentionPolicy(dbConnection, inode,
					retentionPolicy);
			dbConnection.commit();
		} catch (SQLException e) {
			String sqlState = e.getSQLState();
			try {
				dbConnection.rollback();
			} catch (SQLException ee) {
				_log.error("setRetentionPolicy rollback ", ee);
			}

			if (this._sqlDriver.isForeignKeyError(sqlState)) {
				throw new FileNotFoundHimeraFsException();
			}
			_log.error("setRetentionPolicy:  [" + sqlState + "]", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public InodeStorageInformation getStorageInfo(FsInode inode)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		InodeStorageInformation storageInfo = null;

		try {
			dbConnection.setAutoCommit(true);

			storageInfo = this._sqlDriver.getStorageInfo(
					dbConnection, inode);
		} catch (SQLException e) {
			_log.error("setSorageInfo", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return storageInfo;
	}

	public AccessLatency getAccessLatency(FsInode inode)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		AccessLatency accessLatency = null;

		try {
			dbConnection.setAutoCommit(true);

			accessLatency = this._sqlDriver.getAccessLatency(
					dbConnection, inode);
		} catch (SQLException e) {
			_log.error("setSorageInfo", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return accessLatency;
	}

	public RetentionPolicy getRetentionPolicy(FsInode inode)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		RetentionPolicy retentionPolicy = null;

		try {
			dbConnection.setAutoCommit(true);

			retentionPolicy = this._sqlDriver.getRetentionPolicy(
					dbConnection, inode);
		} catch (SQLException e) {
			_log.error("setSorageInfo", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return retentionPolicy;
	}

	public void setInodeChecksum(FsInode inode, int type, String checksum)
			throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}

		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setInodeChecksum(dbConnection, inode,
					type, checksum);

			dbConnection.commit();
		} catch (SQLException e) {
			String sqlState = e.getSQLState();
			try {
				dbConnection.rollback();
			} catch (SQLException ee) {
				_log.error("setInodeChecksum rollback ", ee);
			}

			if (this._sqlDriver.isForeignKeyError(sqlState)) {
				throw new FileNotFoundHimeraFsException();
			}

			if (!this._sqlDriver.isDuplicatedKeyError(sqlState)) {

				_log
						.error("setInodeChecksum:  [" + sqlState + "]", e);
				throw new IOHimeraFsException(e.getMessage());
			}
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public void removeInodeChecksum(FsInode inode, int type)
			throws ChimeraFsException {
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		try {
			dbConnection.setAutoCommit(true);

			this._sqlDriver.removeInodeChecksum(dbConnection, inode,
					type);
		} catch (SQLException e) {
			_log.error("removeInodeChecksum", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	public String getInodeChecksum(FsInode inode, int type)
			throws ChimeraFsException {
		String checkSum = null;
		Connection dbConnection;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		try {
			dbConnection.setAutoCommit(true);

			checkSum = this._sqlDriver.getInodeChecksum(dbConnection,
					inode, type);
		} catch (SQLException e) {
			_log.error("getInodeChecksum", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		return checkSum;
	}

	public List<ACE> getACL(FsInode inode) throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		List<ACE> acl;
		try {
			dbConnection.setAutoCommit(true);

			acl = this._sqlDriver.getACL(dbConnection, inode);
		} catch (SQLException e) {
			_log.error("Failed go getACL:", e);
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
		return acl;
	}

	public void setACL(FsInode inode, List<ACE> acl) throws ChimeraFsException {
		Connection dbConnection;

		try {
			dbConnection = this._dbConnectionsPool.getConnection();
		} catch (SQLException e) {
			throw new BackEndErrorHimeraFsException(e.getMessage());
		}
		try {
			dbConnection.setAutoCommit(false);

			this._sqlDriver.setACL(dbConnection, inode, acl);
			dbConnection.commit();
		} catch (SQLException e) {
			_log.error("Failed to set ACL: ", e);
			try {
				dbConnection.rollback();
			} catch (SQLException ee) {
				_log.error("setACL rollback ", ee);
			}
			throw new IOHimeraFsException(e.getMessage());
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}
	}

	private static void checkNameLength(String name)
			throws InvalidNameChimeraException {
		if (name.length() > MAX_NAME_LEN) {
			throw new InvalidNameChimeraException("Name too long");
		}
	}

	static class FsStatCache {
		private FsStat _fsStatCached;

		private long _fsStatLastUpdate;

		private long _fsStateLifetime = 3600000L;
		private final JdbcFs1 _fs;

		FsStatCache(JdbcFs1 fs) {
			this._fs = fs;
		}

		public synchronized FsStat getFsStat(DataSource dbConnectionsPool,
				FsSqlDriver1 driver) throws ChimeraFsException {
			if ((this._fsStatLastUpdate == 0L)
					|| (this._fsStatLastUpdate + this._fsStateLifetime < System
							.currentTimeMillis())) {
				Connection dbConnection = null;
				try {
					dbConnection = dbConnectionsPool.getConnection();
					this._fsStatCached = driver
							.getFsStat(dbConnection);
				} catch (SQLException e) {
					throw new IOHimeraFsException(e.getMessage());
				} finally {
					SqlHelper.tryToClose(dbConnection);
				}
				JdbcFs1._log.debug("updateing cached value of FsStat");
				this._fsStatLastUpdate = System.currentTimeMillis();
			} else {
				JdbcFs1._log.debug("using cached value of FsStat");
			}

			return this._fsStatCached;
		}

	}

	public FsStat getFsStat() throws ChimeraFsException {
		return this._fsStatCache.getFsStat(this._dbConnectionsPool,
				this._sqlDriver);
	}

	public String getInfo() {
		String databaseProductName = "Unknown";
		String databaseProductVersion = "Unknown";
		Connection dbConnection = null;
		try {
			dbConnection = this._dbConnectionsPool.getConnection();
			if (dbConnection != null) {
				databaseProductName = dbConnection.getMetaData()
						.getDatabaseProductName();
				databaseProductVersion = dbConnection.getMetaData()
						.getDatabaseProductVersion();
			}
		} catch (SQLException se) {
		} finally {
			SqlHelper.tryToClose(dbConnection);
		}

		StringBuilder sb = new StringBuilder();

		sb.append("DB        : ")
				.append(this._dbConnectionsPool.toString()).append("\n");
		sb.append("DB Engine : ").append(databaseProductName)
				.append(" ").append(databaseProductVersion).append("\n");
		sb.append("rootID    : ").append(this._rootInode.toString())
				.append("\n");
		sb.append("wormID    : ").append(this._wormID).append("\n");
		sb.append("FsId      : ").append(this._fsId).append("\n");
		return sb.toString();
	}

	public void close() throws IOException {
		if ((this._dbConnectionsPool instanceof BoneCPDataSource)) {
			((BoneCPDataSource) this._dbConnectionsPool).close();
			} else if ((this._dbConnectionsPool instanceof Closeable)) {
			((Closeable) this._dbConnectionsPool).close();
		}
	}

	private static final byte[] FH_V0_BIN = { 48, 48, 48, 48 };
	private static final byte[] FH_V0_REG = { 48, 58 };
	private static final byte[] FH_V0_PFS = { 50, 53, 53, 58 };

	private static boolean arrayStartsWith(byte[] a1, byte[] a2) {
		if (a1.length < a2.length) {
			return false;
		}
		for (int i = 0; i < a2.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	public FsInode inodeFromBytes(byte[] handle) throws ChimeraFsException {
		if ((arrayStartsWith(handle, FH_V0_REG))
				|| (arrayStartsWith(handle, FH_V0_PFS)))
			return inodeFromBytesOld(handle);
		if (arrayStartsWith(handle, FH_V0_BIN)) {
			return inodeFromBytesNew(InodeId
					.hexStringToByteArray(new String(handle)));
		}
		return inodeFromBytesNew(handle);
	}

	private static final char[] HEX = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	public static String toHexString(byte[] bytes) {
		char[] chars = new char[bytes.length * 2];
		int p = 0;
		for (byte b : bytes) {
			int i = b & 0xFF;
			chars[(p++)] = HEX[(i / 16)];
			chars[(p++)] = HEX[(i % 16)];
		}
		return new String(chars);
	}

	private String[] getArgs(byte[] bytes) {
		StringTokenizer st = new StringTokenizer(new String(bytes),
				"[:]");
		int argc = st.countTokens();
		String[] args = new String[argc];
		for (int i = 0; i < argc; i++) {
			args[i] = st.nextToken();
		}

		return args;
	}

	FsInode inodeFromBytesNew(byte[] handle) throws ChimeraFsException {
		if (handle.length < MIN_HANDLE_LEN) {
			throw new FileNotFoundHimeraFsException(
					"File handle too short");
		}

		ByteBuffer b = ByteBuffer.wrap(handle);
		int fsid = b.get();
		int type = b.get();
		int idLen = b.get();
		byte[] id = new byte[idLen];
		b.get(id);
		int opaqueLen = b.get();
		if (opaqueLen > b.remaining()) {
			throw new FileNotFoundHimeraFsException("Bad Opaque len");
		}

		byte[] opaque = new byte[opaqueLen];
		b.get(opaque);

		FsInodeType inodeType = FsInodeType.valueOf(type);
		String inodeId = toHexString(id);
		FsInode inode;
		switch (inodeType) {
		case INODE:
			int level = Integer.parseInt(new String(opaque));
			inode = new FsInode(this, inodeId, level);
			break;

		case ID:
			inode = new FsInode_ID(this, inodeId);
			break;

		case TAGS:
			inode = new FsInode_TAGS(this, inodeId);
			break;

		case TAG:
			String tag = new String(opaque);
			inode = new FsInode_TAG(this, inodeId, tag);
			break;

		case NAMEOF:
			inode = new FsInode_NAMEOF(this, inodeId);
			break;
		case PARENT:
			inode = new FsInode_PARENT(this, inodeId);
			break;

		case PATHOF:
			inode = new FsInode_PATHOF(this, inodeId);
			break;

		case CONST:
			inode = new FsInode_CONST(this, inodeId);
			break;

		case PSET:
			inode = new FsInode_PSET(this, inodeId, getArgs(opaque));
			break;

		case PGET:
			inode = getPGET(inodeId, getArgs(opaque));
			break;
		default:
			throw new FileNotFoundHimeraFsException(
					"Unsupported file handle type: " + inodeType);
		}
		return inode;
	}

	FsInode inodeFromBytesOld(byte[] handle) throws ChimeraFsException {
		FsInode inode = null;

		String strHandle = new String(handle);

		StringTokenizer st = new StringTokenizer(strHandle, "[:]");

		if (st.countTokens() < 3) {
			throw new IllegalArgumentException(
					"Invalid HimeraNFS handler.(" + strHandle + ")");
		}

		int fsId = Integer.parseInt(st.nextToken());
		String type = st.nextToken();

		try {
			FsInodeType inodeType = FsInodeType.valueOf(type);
			String id;
			int argc;
			String[] args;
			
			switch (inodeType) {
			case INODE:
				id = st.nextToken();
				int level = 0;
				if (st.countTokens() > 0) {
					level = Integer.parseInt(st.nextToken());
				}
				inode = new FsInode(this, id, level);
				break;

			case ID:
				id = st.nextToken();
				inode = new FsInode_ID(this, id);
				break;

			case TAGS:
				id = st.nextToken();
				inode = new FsInode_TAGS(this, id);
				break;

			case TAG:
				id = st.nextToken();
				String tag = st.nextToken();
				inode = new FsInode_TAG(this, id, tag);
				break;

			case NAMEOF:
				id = st.nextToken();
				inode = new FsInode_NAMEOF(this, id);
				break;
			case PARENT:
				id = st.nextToken();
				inode = new FsInode_PARENT(this, id);
				break;

			case PATHOF:
				id = st.nextToken();
				inode = new FsInode_PATHOF(this, id);
				break;

			case CONST:
				String cnst = st.nextToken();
				inode = new FsInode_CONST(this, cnst);
				break;

			case PSET:
				id = st.nextToken();
				argc = st.countTokens();
				args = new String[argc];
				for (int i = 0; i < argc; i++) {
					args[i] = st.nextToken();
				}
				inode = new FsInode_PSET(this, id, args);
				break;

			case PGET:
				id = st.nextToken();
				argc = st.countTokens();
				args = new String[argc];
				for (int i = 0; i < argc; i++) {
					args[i] = st.nextToken();
				}
				inode = getPGET(id, args);
			}
		} catch (IllegalArgumentException iae) {
			_log.info(
					"Failed to generate an inode from file handle : {} : {}",
					strHandle, iae);
			inode = null;
		}

		return inode;
	}

	public byte[] inodeToBytes(FsInode inode) throws ChimeraFsException {
		return inode.getIdentifier();
	}

	protected FsInode_PGET getPGET(String id, String[] args)
			throws ChimeraFsException {
		return new FsInode_PGET(this, id, args);
	}

	protected FsInode_PGET getPGET(FsInode parent, String[] args)
			throws ChimeraFsException {
		return new FsInode_PGET(this, parent.toString(), args);
	}

}
