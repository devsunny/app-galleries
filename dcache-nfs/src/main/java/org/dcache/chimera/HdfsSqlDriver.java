package org.dcache.chimera;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.dcache.acl.ACE;
import org.dcache.acl.enums.AceType;
import org.dcache.acl.enums.Who;
import org.dcache.chimera.posix.Stat;
import org.dcache.chimera.store.AccessLatency;
import org.dcache.chimera.store.InodeStorageInformation;
import org.dcache.chimera.store.RetentionPolicy;
import org.dcache.chimera.util.SqlHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

class HdfsSqlDriver {
	private static final Logger _log = LoggerFactory
			.getLogger(HdfsSqlDriver.class);

	private static final int IOMODE_ENABLE = 1;

	private static final int IOMODE_DISABLE = 0;
	private final int _ioMode;
	private static final String sqlGetFsStat = "SELECT count(ipnfsid) AS usedFiles, SUM(isize) AS usedSpace FROM t_inodes WHERE itype=32768";
	private static final String sqlListDir = "SELECT * FROM t_dirs WHERE iparent=?";
	private static final String sqlListDirFull = "SELECT t_inodes.ipnfsid, t_dirs.iname, t_inodes.isize,t_inodes.inlink,t_inodes.imode,t_inodes.itype,t_inodes.iuid,t_inodes.igid,t_inodes.iatime,t_inodes.ictime,t_inodes.imtime  FROM t_inodes, t_dirs WHERE iparent=? AND t_inodes.ipnfsid = t_dirs.ipnfsid";
	private static final String sqlStat = "SELECT isize,inlink,itype,imode,iuid,igid,iatime,ictime,imtime,icrtime,igeneration FROM t_inodes WHERE ipnfsid=?";
	private static final String sqlMove = "UPDATE t_dirs SET iparent=?, iname=? WHERE iparent=? AND iname=?";
	private static final String sqlSetParent = "UPDATE t_dirs SET ipnfsid=? WHERE iparent=? AND iname='..'";
	private static final String sqlInodeOf = "SELECT ipnfsid FROM t_dirs WHERE iname=? AND iparent=?";
	private static final String sqlInode2Path_name = "SELECT iname FROM t_dirs WHERE ipnfsid=? AND iparent=? and iname !='.' and iname != '..'";
	private static final String sqlInode2Path_inode = "SELECT iparent FROM t_dirs WHERE ipnfsid=?  and iname != '.' and iname != '..'";
	private static final String sqlCreateInode = "INSERT INTO t_inodes VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	private static final String sqlRemoveInode = "DELETE FROM t_inodes WHERE ipnfsid=? AND inlink = 0";
	private static final String sqlIncNlink = "UPDATE t_inodes SET inlink=inlink +?,imtime=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlDecNlink = "UPDATE t_inodes SET inlink=inlink -?,imtime=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlCreateEntryInParent = "INSERT INTO t_dirs VALUES(?,?,?)";
	private static final String sqlRemoveEntryInParentByID = "DELETE FROM t_dirs WHERE ipnfsid=? AND iparent=?";
	private static final String sqlRemoveEntryInParentByName = "DELETE FROM t_dirs WHERE iname=? AND iparent=?";
	private static final String sqlGetParentOf = "SELECT iparent FROM t_dirs WHERE ipnfsid=? AND iname != '.' and iname != '..'";
	private static final String sqlGetParentOfDirectory = "SELECT iparent FROM t_dirs WHERE ipnfsid=? AND iname!='..' AND iname !='.'";
	private static final String sqlGetNameOf = "SELECT iname FROM t_dirs WHERE ipnfsid=? AND iparent=?";
	private static final String sqlSetFileSize = "UPDATE t_inodes SET isize=?,imtime=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileOwner = "UPDATE t_inodes SET iuid=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileName = "UPDATE t_dirs SET iname=? WHERE iname=? AND iparent=?";
	private static final String sqlSetInodeAttributes = "UPDATE t_inodes SET iatime=?, imtime=?, ictime=?, isize=?, iuid=?, igid=?, imode=?, itype=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileATime = "UPDATE t_inodes SET iatime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileCTime = "UPDATE t_inodes SET ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileMTime = "UPDATE t_inodes SET imtime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileGroup = "UPDATE t_inodes SET igid=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlSetFileMode = "UPDATE t_inodes SET imode=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
	private static final String sqlIsIoEnabled = "SELECT iio FROM t_inodes WHERE ipnfsid=?";
	private static final String sqlSetInodeIo = "UPDATE t_inodes SET iio=? WHERE ipnfsid=?";
	private static final String sqlGetInodeLocations = "SELECT ilocation,ipriority,ictime,iatime  FROM t_locationinfo WHERE itype=? AND ipnfsid=? AND istate=1 ORDER BY ipriority DESC";
	private static final String sqlAddInodeLocation = "INSERT INTO t_locationinfo VALUES(?,?,?,?,?,?,?)";

	
	
	protected HdfsSqlDriver() {
		if (Boolean.valueOf(System.getProperty("chimera.inodeIoMode"))
				.booleanValue()) {
			this._ioMode = 1;
		} else {
			this._ioMode = 0;
		}
	}

	
	FsStat getFsStat(Connection dbConnection) throws SQLException {
		long usedFiles = 0L;
		long usedSpace = 0L;
		PreparedStatement stFsStat = null;
		ResultSet rs = null;
		try {
			stFsStat = dbConnection
					.prepareStatement("SELECT count(ipnfsid) AS usedFiles, SUM(isize) AS usedSpace FROM t_inodes WHERE itype=32768");
			rs = stFsStat.executeQuery();
			if (rs.next()) {
				usedFiles = rs.getLong("usedFiles");
				usedSpace = rs.getLong("usedSpace");
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stFsStat);
		}

		return new FsStat(1152921504606846976L, 62914560L, usedSpace,
				usedFiles);
	}

	
	FsInode createFile(Connection dbConnection, FsInode parent, String name,
			int owner, int group, int mode, int type) throws SQLException {
		FsInode inode = new FsInode(parent.getFs());
		createFileWithId(dbConnection, parent, inode, name, owner,
				group, mode, type);

		return inode;
	}

	FsInode createFileWithId(Connection dbConnection, FsInode parent,
			FsInode inode, String name, int owner, int group, int mode, int type)
			throws SQLException {
		createInode(dbConnection, inode, type, owner, group, mode, 1);
		createEntryInParent(dbConnection, parent, name, inode);
		incNlink(dbConnection, parent);

		return inode;
	}

	String[] listDir(Connection dbConnection, FsInode dir) throws SQLException {
		String[] list = null;
		ResultSet result = null;
		PreparedStatement stListDirectory = null;

		try {
			stListDirectory = dbConnection
					.prepareStatement("SELECT * FROM t_dirs WHERE iparent=?");
			stListDirectory.setString(1, dir.toString());
			stListDirectory.setFetchSize(1000);
			result = stListDirectory.executeQuery();

			List<String> directoryList = new ArrayList();
			while (result.next()) {
				directoryList.add(result.getString("iname"));
			}

			list = (String[]) directoryList
					.toArray(new String[directoryList.size()]);
		} finally {
			SqlHelper.tryToClose(result);
			SqlHelper.tryToClose(stListDirectory);
		}

		return list;
	}

	DirectoryStreamB<HimeraDirectoryEntry> newDirectoryStream(
			Connection dbConnection, FsInode dir) throws SQLException {
		PreparedStatement stListDirectoryFull = dbConnection
				.prepareStatement("SELECT t_inodes.ipnfsid, t_dirs.iname, t_inodes.isize,t_inodes.inlink,t_inodes.imode,t_inodes.itype,t_inodes.iuid,t_inodes.igid,t_inodes.iatime,t_inodes.ictime,t_inodes.imtime  FROM t_inodes, t_dirs WHERE iparent=? AND t_inodes.ipnfsid = t_dirs.ipnfsid");
		stListDirectoryFull.setFetchSize(50);
		stListDirectoryFull.setString(1, dir.toString());

		ResultSet result = stListDirectoryFull.executeQuery();
		return new DirectoryStreamImpl(dir, dbConnection,
				stListDirectoryFull, result);
	}

	void remove(Connection dbConnection, FsInode parent, String name)
			throws ChimeraFsException, SQLException {
		FsInode inode = inodeOf(dbConnection, parent, name);
		if ((inode == null) || (inode.type() != FsInodeType.INODE)) {
			throw new FileNotFoundHimeraFsException("Not a file.");
		}

		if (inode.isDirectory()) {
			removeDir(dbConnection, parent, inode, name);
		} else {
			removeFile(dbConnection, parent, inode, name);
		}
	}

	private void removeDir(Connection dbConnection, FsInode parent,
			FsInode inode, String name) throws ChimeraFsException, SQLException {
		Stat dirStat = inode.statCache();
		if (dirStat.getNlink() > 2) {
			throw new DirNotEmptyHimeraFsException(
					"directory is not empty");
		}

		removeEntryInParent(dbConnection, inode, ".");
		removeEntryInParent(dbConnection, inode, "..");

		decNlink(dbConnection, inode, 2);
		removeTag(dbConnection, inode);

		removeEntryInParent(dbConnection, parent, name);
		decNlink(dbConnection, parent);
		setFileMTime(dbConnection, parent, 0,
				System.currentTimeMillis());

		removeInode(dbConnection, inode);
	}

	private void removeFile(Connection dbConnection, FsInode parent,
			FsInode inode, String name) throws ChimeraFsException, SQLException {
		boolean isLast = inode.stat().getNlink() == 1;

		decNlink(dbConnection, inode);
		removeEntryInParent(dbConnection, parent, name);

		if (isLast) {
			removeInode(dbConnection, inode);
		}

		decNlink(dbConnection, parent);
	}

	void remove(Connection dbConnection, FsInode parent, FsInode inode)
			throws ChimeraFsException, SQLException {
		if (inode.isDirectory()) {
			Stat dirStat = inode.statCache();
			if (dirStat.getNlink() > 2) {
				throw new DirNotEmptyHimeraFsException(
						"directory is not empty");
			}
			removeEntryInParent(dbConnection, inode, ".");
			removeEntryInParent(dbConnection, inode, "..");
			decNlink(dbConnection, inode, 2);
			removeTag(dbConnection, inode);
		} else {
			decNlink(dbConnection, inode);
			for (int i = 1; i <= 7; i++) {
				removeInodeLevel(dbConnection, inode, i);
			}
		}

		removeEntryInParentByID(dbConnection, parent, inode);
		decNlink(dbConnection, parent);
		removeStorageInfo(dbConnection, inode);
		removeInode(dbConnection, inode);
	}

	public Stat stat(Connection dbConnection, FsInode inode)
			throws SQLException {
		return stat(dbConnection, inode, 0);
	}

	public Stat stat(Connection dbConnection, FsInode inode, int level)
			throws SQLException {
		Stat ret = null;
		PreparedStatement stStatInode = null;
		ResultSet statResult = null;
		try {
			if (level == 0) {
				stStatInode = dbConnection
						.prepareStatement("SELECT isize,inlink,itype,imode,iuid,igid,iatime,ictime,imtime,icrtime,igeneration FROM t_inodes WHERE ipnfsid=?");
			} else {
				stStatInode = dbConnection
						.prepareStatement("SELECT isize,inlink,imode,iuid,igid,iatime,ictime,imtime FROM t_level_"
								+ level + " WHERE ipnfsid=?");
			}

			stStatInode.setString(1, inode.toString());
			statResult = stStatInode.executeQuery();

			if (statResult.next()) {
				ret = new Stat();

				int inodeType;
				if (level == 0) {
					inodeType = statResult.getInt("itype");
					ret.setCrTime(statResult.getTimestamp("icrtime")
							.getTime());
					ret.setGeneration(statResult
							.getLong("igeneration"));
				} else {
					inodeType = 32768;
					ret.setCrTime(statResult.getTimestamp("imtime")
							.getTime());
					ret.setGeneration(0L);
				}

				ret.setSize(statResult.getLong("isize"));
				ret.setATime(statResult.getTimestamp("iatime")
						.getTime());
				ret.setCTime(statResult.getTimestamp("ictime")
						.getTime());
				ret.setMTime(statResult.getTimestamp("imtime")
						.getTime());
				ret.setUid(statResult.getInt("iuid"));
				ret.setGid(statResult.getInt("igid"));
				ret.setMode(statResult.getInt("imode") | inodeType);
				ret.setNlink(statResult.getInt("inlink"));
				ret.setIno((int) inode.id());
				ret.setDev(17);
			}
		} finally {
			SqlHelper.tryToClose(statResult);
			SqlHelper.tryToClose(stStatInode);
		}

		return ret;
	}

	FsInode mkdir(Connection dbConnection, FsInode parent, String name,
			int owner, int group, int mode) throws ChimeraFsException,
			SQLException {
		FsInode inode;
		if (parent.isDirectory()) {
			inode = new FsInode(parent.getFs());

			createInode(dbConnection, inode, 16384, owner, group,
					mode, 2);
			createEntryInParent(dbConnection, parent, name, inode);

			incNlink(dbConnection, parent);

			createEntryInParent(dbConnection, inode, ".", inode);
			createEntryInParent(dbConnection, inode, "..", parent);
		} else {
			throw new NotDirChimeraException(parent);
		}
		return inode;
	}

	void move(Connection dbConnection, FsInode srcDir, String source,
			FsInode destDir, String dest) throws SQLException,
			ChimeraFsException {
		PreparedStatement stMove = null;
		PreparedStatement stParentMove = null;

		try {
			FsInode srcInode = inodeOf(dbConnection, srcDir, source);
			stMove = dbConnection
					.prepareStatement("UPDATE t_dirs SET iparent=?, iname=? WHERE iparent=? AND iname=?");

			stMove.setString(1, destDir.toString());
			stMove.setString(2, dest);
			stMove.setString(3, srcDir.toString());
			stMove.setString(4, source);
			stMove.executeUpdate();

			Stat stat = stat(dbConnection, srcInode);
			if ((stat.getMode() & 0xF000) == 16384) {
				stParentMove = dbConnection
						.prepareStatement("UPDATE t_dirs SET ipnfsid=? WHERE iparent=? AND iname='..'");
				stParentMove.setString(1, destDir.toString());
				stParentMove.setString(2, srcInode.toString());
				stParentMove.executeUpdate();
			}
		} finally {
			SqlHelper.tryToClose(stMove);
			SqlHelper.tryToClose(stParentMove);
		}
	}

	FsInode inodeOf(Connection dbConnection, FsInode parent, String name)
			throws SQLException {
		
		//PathTraceUtility.trace();
		FsInode inode = null;
		String id = null;
		PreparedStatement stGetInodeByName = null;

		ResultSet result = null;
		try {
			stGetInodeByName = dbConnection
					.prepareStatement("SELECT ipnfsid FROM t_dirs WHERE iname=? AND iparent=?");
			stGetInodeByName.setString(1, name);
			stGetInodeByName.setString(2, parent.toString());

			result = stGetInodeByName.executeQuery();
			if(_log.isInfoEnabled()) _log.info("name:{}; parent:{}", name, parent.toString());
			if (result.next()) {
				id = result.getString("ipnfsid");
				if(_log.isInfoEnabled()) _log.info("ipnfsid id:{}", id);
			}
		} finally {
			SqlHelper.tryToClose(result);
			SqlHelper.tryToClose(stGetInodeByName);
		}

		if (id != null) {
			inode = new FsInode(parent.getFs(), id);
		}
		return inode;
	}

	String inode2path(Connection dbConnection, FsInode inode,
			FsInode startFrom, boolean inclusive) throws SQLException {
		String path = null;
		PreparedStatement ps = null;

		try {
			List<String> pList = new ArrayList();
			String parentId = getParentOf(dbConnection, inode)
					.toString();
			String elementId = inode.toString();

			boolean done = false;
			do {
				ps = dbConnection
						.prepareStatement("SELECT iname FROM t_dirs WHERE ipnfsid=? AND iparent=? and iname !='.' and iname != '..'");
				ps.setString(1, elementId);
				ps.setString(2, parentId);

				ResultSet pSearch = ps.executeQuery();
				if (pSearch.next()) {
					pList.add(pSearch.getString("iname"));
				}
				elementId = parentId;

				SqlHelper.tryToClose(ps);
				if ((inclusive)
						&& (elementId.equals(startFrom.toString()))) {
					done = true;
				}

				ps = dbConnection
						.prepareStatement("SELECT iparent FROM t_dirs WHERE ipnfsid=?  and iname != '.' and iname != '..'");
				ps.setString(1, parentId);

				pSearch = ps.executeQuery();

				if (pSearch.next()) {
					parentId = pSearch.getString("iparent");
				}
				ps.close();

				if ((!inclusive)
						&& (parentId.equals(startFrom.toString()))) {
					done = true;
				}
				} while (!done);

			StringBuilder sb = new StringBuilder();

			for (int i = pList.size(); i > 0; i--) {
				sb.append("/").append((String) pList.get(i - 1));
			}

			path = sb.toString();
		} finally {
			SqlHelper.tryToClose(ps);
		}

		return path;
	}

	public void createInode(Connection dbConnection, FsInode inode, int type,
			int uid, int gid, int mode, int nlink) throws SQLException {
		PreparedStatement stCreateInode = null;

		try {
			stCreateInode = dbConnection
					.prepareStatement("INSERT INTO t_inodes VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

			Timestamp now = new Timestamp(System.currentTimeMillis());

			stCreateInode.setString(1, inode.toString());
			stCreateInode.setInt(2, type);
			stCreateInode.setInt(3, mode & 0xFFF);
			stCreateInode.setInt(4, nlink);
			stCreateInode.setInt(5, uid);
			stCreateInode.setInt(6, gid);
			stCreateInode.setLong(7, type == 16384 ? 512L : 0L);
			stCreateInode.setInt(8, this._ioMode);
			stCreateInode.setTimestamp(9, now);
			stCreateInode.setTimestamp(10, now);
			stCreateInode.setTimestamp(11, now);
			stCreateInode.setTimestamp(12, now);
			stCreateInode.setLong(13, 0L);

			stCreateInode.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stCreateInode);
		}
	}

	FsInode createLevel(Connection dbConnection, FsInode inode, int uid,
			int gid, int mode, int level) throws SQLException {
		PreparedStatement stCreateInodeLevel = null;

		try {
			Timestamp now = new Timestamp(System.currentTimeMillis());
			stCreateInodeLevel = dbConnection
					.prepareStatement("INSERT INTO t_level_" + level
							+ " VALUES(?,?,1,?,?,0,?,?,?, NULL)");

			stCreateInodeLevel.setString(1, inode.toString());
			stCreateInodeLevel.setInt(2, mode);
			stCreateInodeLevel.setInt(3, uid);
			stCreateInodeLevel.setInt(4, gid);
			stCreateInodeLevel.setTimestamp(5, now);
			stCreateInodeLevel.setTimestamp(6, now);
			stCreateInodeLevel.setTimestamp(7, now);
			stCreateInodeLevel.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stCreateInodeLevel);
		}

		return new FsInode(inode.getFs(), inode.toString(), level);
	}

	boolean removeInode(Connection dbConnection, FsInode inode)
			throws SQLException {
		int rc = 0;
		PreparedStatement stRemoveInode = null;

		try {
			stRemoveInode = dbConnection
					.prepareStatement("DELETE FROM t_inodes WHERE ipnfsid=? AND inlink = 0");

			stRemoveInode.setString(1, inode.toString());

			rc = stRemoveInode.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stRemoveInode);
		}

		return rc > 0;
	}

	boolean removeInodeLevel(Connection dbConnection, FsInode inode, int level)
			throws SQLException {
		int rc = 0;
		PreparedStatement stRemoveInodeLevel = null;
		try {
			stRemoveInodeLevel = dbConnection
					.prepareStatement("DELETE FROM t_level_" + level
							+ " WHERE ipnfsid=?");
			stRemoveInodeLevel.setString(1, inode.toString());
			rc = stRemoveInodeLevel.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stRemoveInodeLevel);
		}

		return rc > 0;
	}

	void incNlink(Connection dbConnection, FsInode inode) throws SQLException {
		incNlink(dbConnection, inode, 1);
	}

	void incNlink(Connection dbConnection, FsInode inode, int delta)
			throws SQLException {
		PreparedStatement stIncNlinkCount = null;
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try {
			stIncNlinkCount = dbConnection
					.prepareStatement("UPDATE t_inodes SET inlink=inlink +?,imtime=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?");

			stIncNlinkCount.setInt(1, delta);
			stIncNlinkCount.setTimestamp(2, now);
			stIncNlinkCount.setTimestamp(3, now);
			stIncNlinkCount.setString(4, inode.toString());

			stIncNlinkCount.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stIncNlinkCount);
		}
	}

	void decNlink(Connection dbConnection, FsInode inode) throws SQLException {
		decNlink(dbConnection, inode, 1);
	}

	void decNlink(Connection dbConnection, FsInode inode, int delta)
			throws SQLException {
		PreparedStatement stDecNlinkCount = null;
		Timestamp now = new Timestamp(System.currentTimeMillis());
		try {
			stDecNlinkCount = dbConnection
					.prepareStatement("UPDATE t_inodes SET inlink=inlink -?,imtime=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?");
			stDecNlinkCount.setInt(1, delta);
			stDecNlinkCount.setTimestamp(2, now);
			stDecNlinkCount.setTimestamp(3, now);
			stDecNlinkCount.setString(4, inode.toString());

			stDecNlinkCount.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stDecNlinkCount);
		}
	}

	void createEntryInParent(Connection dbConnection, FsInode parent,
			String name, FsInode inode) throws SQLException {
		PreparedStatement stInserIntoParent = null;
		try {
			stInserIntoParent = dbConnection
					.prepareStatement("INSERT INTO t_dirs VALUES(?,?,?)");
			stInserIntoParent.setString(1, parent.toString());
			stInserIntoParent.setString(2, name);
			stInserIntoParent.setString(3, inode.toString());
			stInserIntoParent.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stInserIntoParent);
		}
	}

	void removeEntryInParentByID(Connection dbConnection, FsInode parent,
			FsInode inode) throws SQLException {
		PreparedStatement stRemoveFromParentById = null;
		try {
			stRemoveFromParentById = dbConnection
					.prepareStatement("DELETE FROM t_dirs WHERE ipnfsid=? AND iparent=?");
			stRemoveFromParentById.setString(1, inode.toString());
			stRemoveFromParentById.setString(2, parent.toString());

			stRemoveFromParentById.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stRemoveFromParentById);
		}
	}

	void removeEntryInParent(Connection dbConnection, FsInode parent,
			String name) throws SQLException {
		PreparedStatement stRemoveFromParentByName = null;
		try {
			stRemoveFromParentByName = dbConnection
					.prepareStatement("DELETE FROM t_dirs WHERE iname=? AND iparent=?");
			stRemoveFromParentByName.setString(1, name);
			stRemoveFromParentByName.setString(2, parent.toString());

			stRemoveFromParentByName.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stRemoveFromParentByName);
		}
	}

	FsInode getParentOf(Connection dbConnection, FsInode inode)
			throws SQLException {
		FsInode parent = null;
		ResultSet result = null;
		PreparedStatement stGetParentId = null;
		try {
			stGetParentId = dbConnection
					.prepareStatement("SELECT iparent FROM t_dirs WHERE ipnfsid=? AND iname != '.' and iname != '..'");
			stGetParentId.setString(1, inode.toString());
			System.out.println(inode.toString());
			result = stGetParentId.executeQuery();

			if (result.next()) {
				parent = new FsInode(inode.getFs(),
						result.getString("iparent"));
			}
		} finally {
			SqlHelper.tryToClose(result);
			SqlHelper.tryToClose(stGetParentId);
		}

		return parent;
	}

	FsInode getParentOfDirectory(Connection dbConnection, FsInode inode)
			throws SQLException {
		FsInode parent = null;
		ResultSet result = null;
		PreparedStatement stGetParentId = null;
		try {
			stGetParentId = dbConnection
					.prepareStatement("SELECT iparent FROM t_dirs WHERE ipnfsid=? AND iname!='..' AND iname !='.'");
			stGetParentId.setString(1, inode.toString());

			result = stGetParentId.executeQuery();

			if (result.next()) {
				parent = new FsInode(inode.getFs(),
						result.getString("iparent"));
			}
		} finally {
			SqlHelper.tryToClose(result);
			SqlHelper.tryToClose(stGetParentId);
		}

		return parent;
	}

	String getNameOf(Connection dbConnection, FsInode parent, FsInode inode)
			throws SQLException {
		ResultSet result = null;
		PreparedStatement stGetName = null;
		String name = null;
		try {
			stGetName = dbConnection
					.prepareStatement("SELECT iname FROM t_dirs WHERE ipnfsid=? AND iparent=?");
			stGetName.setString(1, inode.toString());
			stGetName.setString(2, parent.toString());

			result = stGetName.executeQuery();

			if (result.next()) {
				name = result.getString("iname");
			}
		} finally {
			SqlHelper.tryToClose(result);
			SqlHelper.tryToClose(stGetName);
		}

		return name;
	}

	void setFileSize(Connection dbConnection, FsInode inode, long newSize)
			throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = dbConnection
					.prepareStatement("UPDATE t_inodes SET isize=?,imtime=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?");

			ps.setLong(1, newSize);
			ps.setTimestamp(2,
					new Timestamp(System.currentTimeMillis()));
			ps.setTimestamp(3,
					new Timestamp(System.currentTimeMillis()));
			ps.setString(4, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileOwner(Connection dbConnection, FsInode inode, int level,
			int newOwner) throws SQLException {
		PreparedStatement ps = null;

		try {
			String fileSetModeQuery;

			if (level == 0) {
				fileSetModeQuery = "UPDATE t_inodes SET iuid=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?";
			} else {
				fileSetModeQuery = "UPDATE t_level_" + level
						+ " SET iuid=?,ictime=? WHERE ipnfsid=?";
			}
			ps = dbConnection.prepareStatement(fileSetModeQuery);

			ps.setInt(1, newOwner);
			ps.setTimestamp(2,
					new Timestamp(System.currentTimeMillis()));
			ps.setString(3, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileName(Connection dbConnection, FsInode dir, String oldName,
			String newName) throws SQLException, ChimeraFsException {
		PreparedStatement ps = null;

		try {
			ps = dbConnection
					.prepareStatement("UPDATE t_dirs SET iname=? WHERE iname=? AND iparent=?");

			ps.setString(1, newName);
			ps.setString(2, oldName);
			ps.setString(3, dir.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setInodeAttributes(Connection dbConnection, FsInode inode, int level,
			Stat stat) throws SQLException {
		PreparedStatement ps = null;

		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("UPDATE t_inodes SET iatime=?, imtime=?, ictime=?, isize=?, iuid=?, igid=?, imode=?, itype=?,igeneration=igeneration+1 WHERE ipnfsid=?");

				ps.setTimestamp(1, new Timestamp(stat.getATime()));
				ps.setTimestamp(2, new Timestamp(stat.getMTime()));
				ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
				ps.setLong(4, stat.getSize());
				ps.setInt(5, stat.getUid());
				ps.setInt(6, stat.getGid());
				ps.setInt(7, stat.getMode() & 0xFFF);
				ps.setInt(8, stat.getMode() & 0x3F000);
				ps.setString(9, inode.toString());
			} else {
				String fileSetModeQuery = "UPDATE t_level_"
						+ level
						+ " SET iatime=?, imtime=?, iuid=?, igid=?, imode=? WHERE ipnfsid=?";

				ps = dbConnection.prepareStatement(fileSetModeQuery);

				ps.setTimestamp(1, new Timestamp(stat.getATime()));
				ps.setTimestamp(2, new Timestamp(stat.getMTime()));
				ps.setInt(3, stat.getUid());
				ps.setInt(4, stat.getGid());
				ps.setInt(5, stat.getMode());
				ps.setString(6, inode.toString());
			}

			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileATime(Connection dbConnection, FsInode inode, int level,
			long atime) throws SQLException {
		PreparedStatement ps = null;

		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("UPDATE t_inodes SET iatime=?,igeneration=igeneration+1 WHERE ipnfsid=?");
			} else {
				String fileSetModeQuery = "UPDATE t_level_" + level
						+ " SET iatime=? WHERE ipnfsid=?";
				ps = dbConnection.prepareStatement(fileSetModeQuery);
			}

			ps.setTimestamp(1, new Timestamp(atime));
			ps.setString(2, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileCTime(Connection dbConnection, FsInode inode, int level,
			long ctime) throws SQLException {
		PreparedStatement ps = null;

		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("UPDATE t_inodes SET ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?");
			} else {
				String fileSetModeQuery = "UPDATE t_level_" + level
						+ " SET ictime=? WHERE ipnfsid=?";
				ps = dbConnection.prepareStatement(fileSetModeQuery);
			}

			ps.setTimestamp(1, new Timestamp(ctime));
			ps.setString(2, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileMTime(Connection dbConnection, FsInode inode, int level,
			long mtime) throws SQLException {
		PreparedStatement ps = null;

		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("UPDATE t_inodes SET imtime=?,igeneration=igeneration+1 WHERE ipnfsid=?");
			} else {
				String fileSetModeQuery = "UPDATE t_level_" + level
						+ " SET imtime=? WHERE ipnfsid=?";
				ps = dbConnection.prepareStatement(fileSetModeQuery);
			}
			ps.setTimestamp(1, new Timestamp(mtime));
			ps.setString(2, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileGroup(Connection dbConnection, FsInode inode, int level,
			int newGroup) throws SQLException {
		PreparedStatement ps = null;
		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("UPDATE t_inodes SET igid=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?");
			} else {
				String fileSetModeQuery = "UPDATE t_level_" + level
						+ " SET igid=?,ictime=? WHERE ipnfsid=?";
				ps = dbConnection.prepareStatement(fileSetModeQuery);
			}
			ps.setInt(1, newGroup);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setFileMode(Connection dbConnection, FsInode inode, int level,
			int newMode) throws SQLException {
		PreparedStatement ps = null;
		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("UPDATE t_inodes SET imode=?,ictime=?,igeneration=igeneration+1 WHERE ipnfsid=?");
			} else {
				String fileSetModeQuery = "UPDATE t_level_" + level
						+ " SET imode=?,ictime=? WHERE ipnfsid=?";
				ps = dbConnection.prepareStatement(fileSetModeQuery);
			}
			ps.setInt(1, newMode & 0xFFF);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, inode.toString());
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	boolean isIoEnabled(Connection dbConnection, FsInode inode)
			throws SQLException {
		boolean ioEnabled = false;
		ResultSet rs = null;
		PreparedStatement stIsIoEnabled = null;
		try {
			stIsIoEnabled = dbConnection
					.prepareStatement("SELECT iio FROM t_inodes WHERE ipnfsid=?");
			stIsIoEnabled.setString(1, inode.toString());

			rs = stIsIoEnabled.executeQuery();
			if (rs.next()) {
				ioEnabled = rs.getInt("iio") == 1;
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stIsIoEnabled);
		}
		return ioEnabled;
	}

	void setInodeIo(Connection dbConnection, FsInode inode, boolean enable)
			throws SQLException {
		PreparedStatement ps = null;

		try {
			ps = dbConnection
					.prepareStatement("UPDATE t_inodes SET iio=? WHERE ipnfsid=?");
			ps.setInt(1, enable ? 1 : 0);
			ps.setString(2, inode.toString());

			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	int write(Connection dbConnection, FsInode inode, int level,
			long beginIndex, byte[] data, int offset, int len)
			throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			if (level == 0) {
				ps = dbConnection
						.prepareStatement("SELECT ipnfsid FROM t_inodes_data WHERE ipnfsid=?");
				ps.setString(1, inode.toString());

				rs = ps.executeQuery();
				boolean exist = rs.next();
				SqlHelper.tryToClose(rs);
				SqlHelper.tryToClose(ps);

				if (exist) {
					String writeStream = "UPDATE t_inodes_data SET ifiledata=? WHERE ipnfsid=?";

					ps = dbConnection.prepareStatement(writeStream);

					ps.setBinaryStream(1, new ByteArrayInputStream(data,
							offset, len), len);
					ps.setString(2, inode.toString());

					ps.executeUpdate();
					SqlHelper.tryToClose(ps);
				} else {
					String writeStream = "INSERT INTO t_inodes_data VALUES (?,?)";

					ps = dbConnection.prepareStatement(writeStream);

					ps.setString(1, inode.toString());
					ps.setBinaryStream(2, new ByteArrayInputStream(data,
							offset, len), len);

					ps.executeUpdate();
					SqlHelper.tryToClose(ps);
				}

				String writeStream = "UPDATE t_inodes SET isize=? WHERE ipnfsid=?";

				ps = dbConnection.prepareStatement(writeStream);

				ps.setLong(1, len);
				ps.setString(2, inode.toString());

				ps.executeUpdate();

			} else {

				if (stat(dbConnection, inode, level) == null) {
					createLevel(dbConnection, inode, 0, 0, 644, level);
				}

				String writeStream = "UPDATE t_level_" + level
						+ " SET ifiledata=?,isize=? WHERE ipnfsid=?";
				ps = dbConnection.prepareStatement(writeStream);

				ps.setBinaryStream(1, new ByteArrayInputStream(data, offset,
						len), len);
				ps.setLong(2, len);
				ps.setString(3, inode.toString());

				ps.executeUpdate();
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(ps);
		}

		return len;
	}

	int read(Connection dbConnection, FsInode inode, int level,
			long beginIndex, byte[] data, int offset, int len)
			throws SQLException, IOHimeraFsException {
		int count = 0;
		PreparedStatement stReadFromInode = null;
		ResultSet rs = null;

		try {
			if (level == 0) {
				stReadFromInode = dbConnection
						.prepareStatement("SELECT ifiledata FROM t_inodes_data WHERE ipnfsid=?");
			} else {
				stReadFromInode = dbConnection
						.prepareStatement("SELECT ifiledata FROM t_level_"
								+ level + " WHERE ipnfsid=?");
			}

			stReadFromInode.setString(1, inode.toString());
			rs = stReadFromInode.executeQuery();

			if (rs.next()) {
				InputStream in = rs.getBinaryStream(1);

				in.skip(beginIndex);
				int c;
				while (((c = in.read()) != -1) && (count < len)) {
					data[(offset + count)] = ((byte) c);
					count++;
				}

			}
		} catch (IOException e) {
			throw new IOHimeraFsException(e.toString());
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stReadFromInode);
		}

		return count;
	}

	private static final String sqlClearInodeLocation = "DELETE FROM t_locationinfo WHERE ipnfsid=? AND itype=? AND ilocation=?";
	private static final String sqlClearInodeLocations = "DELETE FROM t_locationinfo WHERE ipnfsid=?";
	private static final String sqlTags = "SELECT itagname FROM t_tags where ipnfsid=?";
	private static final String sqlGetTagId = "SELECT itagid FROM t_tags WHERE ipnfsid=? AND itagname=?";
	private static final String sqlCreateTagInode = "INSERT INTO t_tags_inodes VALUES(?,?,1,?,?,0,?,?,?,NULL)";
	private static final String sqlAssignTagToDir_update = "UPDATE t_tags SET itagid=?,isorign=? WHERE ipnfsid=? AND itagname=?";
	private static final String sqlAssignTagToDir_add = "INSERT INTO t_tags VALUES(?,?,?,1)";
	private static final String sqlSetTag = "UPDATE t_tags_inodes SET ivalue=?, isize=?, imtime=? WHERE itagid=?";
	private static final String sqlRemoveSingleTag = "DELETE FROM t_tags WHERE ipnfsid=? AND itagname=?";
	private static final String sqlRemoveTag = "DELETE FROM t_tags WHERE ipnfsid=?";
	private static final String sqlGetTag = "SELECT ivalue,isize FROM t_tags_inodes WHERE itagid=?";
	private static final String sqlStatTag = "SELECT isize,inlink,imode,iuid,igid,iatime,ictime,imtime FROM t_tags_inodes WHERE itagid=?";
	private static final String sqlIsTagOwner = "SELECT isorign FROM t_tags WHERE ipnfsid=? AND itagname=?";
	private static final String sqlCopyTag = "INSERT INTO t_tags ( SELECT ?, itagname, itagid, 0 from t_tags WHERE ipnfsid=?)";
	private static final String sqlSetTagOwner = "UPDATE t_tags_inodes SET iuid=?, ictime=? WHERE itagid=?";
	private static final String sqlSetTagOwnerGroup = "UPDATE t_tags_inodes SET igid=?, ictime=? WHERE itagid=?";
	private static final String sqlSetTagMode = "UPDATE t_tags_inodes SET imode=?, ictime=? WHERE itagid=?";

	List<StorageLocatable> getInodeLocations(Connection dbConnection,
			FsInode inode, int type) throws SQLException {
		List<StorageLocatable> locations = new ArrayList();
		ResultSet rs = null;
		PreparedStatement stGetInodeLocations = null;
		try {
			stGetInodeLocations = dbConnection
					.prepareStatement("SELECT ilocation,ipriority,ictime,iatime  FROM t_locationinfo WHERE itype=? AND ipnfsid=? AND istate=1 ORDER BY ipriority DESC");

			stGetInodeLocations.setInt(1, type);
			stGetInodeLocations.setString(2, inode.toString());

			rs = stGetInodeLocations.executeQuery();

			while (rs.next()) {
				long ctime = rs.getTimestamp("ictime").getTime();
				long atime = rs.getTimestamp("iatime").getTime();
				int priority = rs.getInt("ipriority");
				String location = rs.getString("ilocation");

				StorageLocatable inodeLocation = new StorageGenericLocation(
						type, priority, location, ctime, atime, true);
				locations.add(inodeLocation);
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stGetInodeLocations);
		}

		return locations;
	}

	void addInodeLocation(Connection dbConnection, FsInode inode, int type,
			String location) throws SQLException {
		PreparedStatement stAddInodeLocation = null;
		try {
			stAddInodeLocation = dbConnection
					.prepareStatement("INSERT INTO t_locationinfo VALUES(?,?,?,?,?,?,?)");

			Timestamp now = new Timestamp(System.currentTimeMillis());
			stAddInodeLocation.setString(1, inode.toString());
			stAddInodeLocation.setInt(2, type);
			stAddInodeLocation.setString(3, location);
			stAddInodeLocation.setInt(4, 10);
			stAddInodeLocation.setTimestamp(5, now);
			stAddInodeLocation.setTimestamp(6, now);
			stAddInodeLocation.setInt(7, 1);

			stAddInodeLocation.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stAddInodeLocation);
		}
	}

	void clearInodeLocation(Connection dbConnection, FsInode inode, int type,
			String location) throws SQLException {
		PreparedStatement stClearInodeLocation = null;
		try {
			stClearInodeLocation = dbConnection
					.prepareStatement("DELETE FROM t_locationinfo WHERE ipnfsid=? AND itype=? AND ilocation=?");
			stClearInodeLocation.setString(1, inode.toString());
			stClearInodeLocation.setInt(2, type);
			stClearInodeLocation.setString(3, location);

			stClearInodeLocation.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stClearInodeLocation);
		}
	}

	void clearInodeLocations(Connection dbConnection, FsInode inode)
			throws SQLException {
		PreparedStatement stClearInodeLocations = null;
		try {
			stClearInodeLocations = dbConnection
					.prepareStatement("DELETE FROM t_locationinfo WHERE ipnfsid=?");
			stClearInodeLocations.setString(1, inode.toString());

			stClearInodeLocations.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stClearInodeLocations);
		}
	}

	String[] tags(Connection dbConnection, FsInode inode) throws SQLException {
		String[] list = null;
		ResultSet rs = null;
		PreparedStatement stGetTags = null;
		try {
			stGetTags = dbConnection
					.prepareStatement("SELECT itagname FROM t_tags where ipnfsid=?");
			stGetTags.setString(1, inode.toString());
			rs = stGetTags.executeQuery();

			List<String> v = new ArrayList();

			while (rs.next()) {
				v.add(rs.getString("itagname"));
			}
			rs.close();

			list = (String[]) v.toArray(new String[v.size()]);
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stGetTags);
		}

		return list;
	}

	void createTag(Connection dbConnection, FsInode inode, String name,
			int uid, int gid, int mode) throws SQLException {
		String id = createTagInode(dbConnection, uid, gid, mode);
		assignTagToDir(dbConnection, id, name, inode, false, true);
	}

	String getTagId(Connection dbConnection, FsInode dir, String tag)
			throws SQLException {
		String tagId = null;
		ResultSet rs = null;
		PreparedStatement stGetTagId = null;
		try {
			stGetTagId = dbConnection
					.prepareStatement("SELECT itagid FROM t_tags WHERE ipnfsid=? AND itagname=?");

			stGetTagId.setString(1, dir.toString());
			stGetTagId.setString(2, tag);

			rs = stGetTagId.executeQuery();
			if (rs.next()) {
				tagId = rs.getString("itagid");
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stGetTagId);
		}
		return tagId;
	}

	String createTagInode(Connection dbConnection, int uid, int gid, int mode)
			throws SQLException {
		String id = UUID.randomUUID().toString().toUpperCase();
		PreparedStatement stCreateTagInode = null;
		try {
			stCreateTagInode = dbConnection
					.prepareStatement("INSERT INTO t_tags_inodes VALUES(?,?,1,?,?,0,?,?,?,NULL)");

			Timestamp now = new Timestamp(System.currentTimeMillis());

			stCreateTagInode.setString(1, id);
			stCreateTagInode.setInt(2, mode | 0x8000);
			stCreateTagInode.setInt(3, uid);
			stCreateTagInode.setInt(4, gid);
			stCreateTagInode.setTimestamp(5, now);
			stCreateTagInode.setTimestamp(6, now);
			stCreateTagInode.setTimestamp(7, now);

			stCreateTagInode.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stCreateTagInode);
		}
		return id;
	}

	void assignTagToDir(Connection dbConnection, String tagId, String tagName,
			FsInode dir, boolean isUpdate, boolean isOrign) throws SQLException {
		PreparedStatement ps = null;
		try {
			if (isUpdate) {
				ps = dbConnection
						.prepareStatement("UPDATE t_tags SET itagid=?,isorign=? WHERE ipnfsid=? AND itagname=?");

				ps.setString(1, tagId);
				ps.setInt(2, isOrign ? 1 : 0);
				ps.setString(3, dir.toString());
				ps.setString(4, tagName);
			} else {
				ps = dbConnection
						.prepareStatement("INSERT INTO t_tags VALUES(?,?,?,1)");

				ps.setString(1, dir.toString());
				ps.setString(2, tagName);
				ps.setString(3, tagId);
			}

			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	int setTag(Connection dbConnection, FsInode inode, String tagName,
			byte[] data, int offset, int len) throws SQLException,
			ChimeraFsException {
		PreparedStatement stSetTag = null;
		try {
			String tagId = getTagId(dbConnection, inode, tagName);

			if (!isTagOwner(dbConnection, inode, tagName)) {
				Stat tagStat = statTag(dbConnection, inode, tagName);

				tagId = createTagInode(dbConnection, tagStat.getUid(),
						tagStat.getGid(), tagStat.getMode());
				assignTagToDir(dbConnection, tagId, tagName, inode, true, true);
			}

			stSetTag = dbConnection
					.prepareStatement("UPDATE t_tags_inodes SET ivalue=?, isize=?, imtime=? WHERE itagid=?");
			stSetTag.setBinaryStream(1, new ByteArrayInputStream(data, offset,
					len), len);
			stSetTag.setLong(2, len);
			stSetTag.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
			stSetTag.setString(4, tagId);
			stSetTag.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stSetTag);
		}

		return len;
	}

	void removeTag(Connection dbConnection, FsInode dir, String tag)
			throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = dbConnection
					.prepareStatement("DELETE FROM t_tags WHERE ipnfsid=? AND itagname=?");
			ps.setString(1, dir.toString());
			ps.setString(2, tag);

			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void removeTag(Connection dbConnection, FsInode dir) throws SQLException {
		PreparedStatement ps = null;
		try {
			ps = dbConnection
					.prepareStatement("DELETE FROM t_tags WHERE ipnfsid=?");
			ps.setString(1, dir.toString());

			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	int getTag(Connection dbConnection, FsInode inode, String tagName,
			byte[] data, int offset, int len) throws SQLException, IOException {
		int count = 0;
		ResultSet rs = null;
		PreparedStatement stGetTag = null;
		try {
			String tagId = getTagId(dbConnection, inode, tagName);

			stGetTag = dbConnection
					.prepareStatement("SELECT ivalue,isize FROM t_tags_inodes WHERE itagid=?");
			stGetTag.setString(1, tagId);
			rs = stGetTag.executeQuery();

			if (rs.next()) {
				InputStream in = rs.getBinaryStream("ivalue");

				int size = Math.min(len, (int) rs.getLong("isize"));

				while (count < size) {
					int c = in.read();
					if (c == -1) {
						break;
					}

					data[(offset + count)] = ((byte) c);
					count++;
				}
				in.close();
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stGetTag);
		}

		return count;
	}

	Stat statTag(Connection dbConnection, FsInode dir, String name)
			throws ChimeraFsException, SQLException {
		Stat ret = new Stat();
		PreparedStatement stStatTag = null;
		try {
			String tagId = getTagId(dbConnection, dir, name);

			if (tagId == null) {
				throw new FileNotFoundHimeraFsException("tag do not exist");
			}

			stStatTag = dbConnection
					.prepareStatement("SELECT isize,inlink,imode,iuid,igid,iatime,ictime,imtime FROM t_tags_inodes WHERE itagid=?");
			stStatTag.setString(1, tagId);
			ResultSet statResult = stStatTag.executeQuery();

			if (statResult.next()) {
				ret.setSize(statResult.getLong("isize"));
				ret.setATime(statResult.getTimestamp("iatime").getTime());
				ret.setCTime(statResult.getTimestamp("ictime").getTime());
				ret.setMTime(statResult.getTimestamp("imtime").getTime());
				ret.setUid(statResult.getInt("iuid"));
				ret.setGid(statResult.getInt("igid"));
				ret.setMode(statResult.getInt("imode"));
				ret.setNlink(statResult.getInt("inlink"));
				ret.setIno((int) dir.id());
				ret.setDev(17);
			} else {
				throw new FileNotFoundHimeraFsException(name);
			}
		} finally {
			SqlHelper.tryToClose(stStatTag);
		}

		return ret;
	}

	boolean isTagOwner(Connection dbConnection, FsInode dir, String tagName)
			throws SQLException {
		boolean isOwner = false;
		PreparedStatement stTagOwner = null;
		ResultSet rs = null;

		try {
			stTagOwner = dbConnection
					.prepareStatement("SELECT isorign FROM t_tags WHERE ipnfsid=? AND itagname=?");
			stTagOwner.setString(1, dir.toString());
			stTagOwner.setString(2, tagName);

			rs = stTagOwner.executeQuery();
			if (rs.next()) {
				int rc = rs.getInt("isorign");
				if (rc == 1) {
					isOwner = true;
				}
			}
		} finally {
			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stTagOwner);
		}

		return isOwner;
	}

	void copyTags(Connection dbConnection, FsInode orign, FsInode destination)
			throws SQLException {
		PreparedStatement stCopyTags = null;
		try {
			stCopyTags = dbConnection
					.prepareStatement("INSERT INTO t_tags ( SELECT ?, itagname, itagid, 0 from t_tags WHERE ipnfsid=?)");
			stCopyTags.setString(1, destination.toString());
			stCopyTags.setString(2, orign.toString());
			stCopyTags.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stCopyTags);
		}
	}

	void setTagOwner(Connection dbConnection, FsInode_TAG tagInode, int newOwner)
			throws SQLException {
		PreparedStatement ps = null;
		String tagId = getTagId(dbConnection, tagInode, tagInode.tagName());

		try {
			ps = dbConnection
					.prepareStatement("UPDATE t_tags_inodes SET iuid=?, ictime=? WHERE itagid=?");

			ps.setInt(1, newOwner);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, tagId);
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setTagOwnerGroup(Connection dbConnection, FsInode_TAG tagInode,
			int newOwner) throws SQLException {
		PreparedStatement ps = null;
		String tagId = getTagId(dbConnection, tagInode, tagInode.tagName());

		try {
			ps = dbConnection
					.prepareStatement("UPDATE t_tags_inodes SET igid=?, ictime=? WHERE itagid=?");

			ps.setInt(1, newOwner);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, tagId);
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	void setTagMode(Connection dbConnection, FsInode_TAG tagInode, int mode)
			throws SQLException {
		PreparedStatement ps = null;
		String tagId = getTagId(dbConnection, tagInode, tagInode.tagName());

		try {
			ps = dbConnection
					.prepareStatement("UPDATE t_tags_inodes SET imode=?, ictime=? WHERE itagid=?");

			ps.setInt(1, mode & 0xFFF);
			ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			ps.setString(3, tagId);
			ps.executeUpdate();
		} finally {
			SqlHelper.tryToClose(ps);
		}
	}

	private static final String sqlSetStorageInfo = "INSERT INTO t_storageinfo VALUES(?,?,?,?)";
	private static final String sqlGetAccessLatency = "SELECT iaccessLatency FROM t_access_latency WHERE ipnfsid=?";
	private static final String sqlGetRetentionPolicy = "SELECT iretentionPolicy FROM t_retention_policy WHERE ipnfsid=?";
	private static final String sqlSetAccessLatency = "INSERT INTO t_access_latency VALUES(?,?)";
	private static final String sqlUpdateAccessLatency = "UPDATE t_access_latency SET iaccessLatency=? WHERE ipnfsid=?";
	private static final String sqlSetRetentionPolicy = "INSERT INTO t_retention_policy VALUES(?,?)";
	private static final String sqlUpdateRetentionPolicy = "UPDATE t_retention_policy SET iretentionPolicy=? WHERE ipnfsid=?";
	private static final String sqlRemoveStorageInfo = "DELETE FROM t_storageinfo WHERE ipnfsid=?";
	private static final String sqlGetStorageInfo = "SELECT ihsmName, istorageGroup, istorageSubGroup FROM t_storageinfo WHERE t_storageinfo.ipnfsid=?";
	private static final String sqlGetInodeFromCache = "SELECT ipnfsid FROM t_dir_cache WHERE ipath=?";
	private static final String sqlGetPathFromCache = "SELECT ipath FROM t_dir_cache WHERE ipnfsid=?";
	private static final String sqlSetInodeChecksum = "INSERT INTO t_inodes_checksum VALUES(?,?,?)";
	private static final String sqlGetInodeChecksum = "SELECT isum FROM t_inodes_checksum WHERE ipnfsid=? AND itype=?";
	private static final String sqlRemoveInodeChecksum = "DELETE FROM t_inodes_checksum WHERE ipnfsid=? AND itype=?";
	private static final String sqlRemoveInodeAllChecksum = "DELETE FROM t_inodes_checksum WHERE ipnfsid=?";
	private static final String sqlGetACL = "SELECT * FROM t_acl WHERE rs_id =  ? ORDER BY ace_order";
	private static final String sqlDeleteACL = "DELETE FROM t_acl WHERE rs_id = ?";
	private static final String sqlAddACL = "INSERT INTO t_acl VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

	void setStorageInfo(Connection dbConnection, FsInode inode,
			InodeStorageInformation storageInfo) throws SQLException {
		PreparedStatement stSetStorageInfo = null;

		try {
			stSetStorageInfo = dbConnection
					.prepareStatement("INSERT INTO t_storageinfo VALUES(?,?,?,?)");
			stSetStorageInfo.setString(1, inode.toString());
			stSetStorageInfo.setString(2, storageInfo.hsmName());
			stSetStorageInfo.setString(3, storageInfo.storageGroup());
			stSetStorageInfo.setString(4, storageInfo.storageSubGroup());

			stSetStorageInfo.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stSetStorageInfo);
		}
	}

	AccessLatency getAccessLatency(Connection dbConnection, FsInode inode)
			throws SQLException {
		AccessLatency accessLatency = null;
		PreparedStatement stGetAccessLatency = null;
		ResultSet alResultSet = null;

		try {
			stGetAccessLatency = dbConnection
					.prepareStatement("SELECT iaccessLatency FROM t_access_latency WHERE ipnfsid=?");
			stGetAccessLatency.setString(1, inode.toString());

			alResultSet = stGetAccessLatency.executeQuery();
			if (alResultSet.next()) {
				accessLatency = AccessLatency.valueOf(alResultSet
						.getInt("iaccessLatency"));
			}
		} finally {
			SqlHelper.tryToClose(alResultSet);
			SqlHelper.tryToClose(stGetAccessLatency);
		}

		return accessLatency;
	}

	RetentionPolicy getRetentionPolicy(Connection dbConnection, FsInode inode)
			throws SQLException {
		RetentionPolicy retentionPolicy = null;
		PreparedStatement stRetentionPolicy = null;
		ResultSet rpResultSet = null;

		try {
			stRetentionPolicy = dbConnection
					.prepareStatement("SELECT iretentionPolicy FROM t_retention_policy WHERE ipnfsid=?");
			stRetentionPolicy.setString(1, inode.toString());

			rpResultSet = stRetentionPolicy.executeQuery();
			if (rpResultSet.next()) {
				retentionPolicy = RetentionPolicy.valueOf(rpResultSet
						.getInt("iretentionPolicy"));
			}
		} finally {
			SqlHelper.tryToClose(rpResultSet);
			SqlHelper.tryToClose(stRetentionPolicy);
		}

		return retentionPolicy;
	}

	void setAccessLatency(Connection dbConnection, FsInode inode,
			AccessLatency accessLatency) throws SQLException {
		PreparedStatement stSetAccessLatency = null;
		PreparedStatement stUpdateAccessLatency = null;
		try {
			stUpdateAccessLatency = dbConnection
					.prepareStatement("UPDATE t_access_latency SET iaccessLatency=? WHERE ipnfsid=?");
			stUpdateAccessLatency.setInt(1, accessLatency.getId());
			stUpdateAccessLatency.setString(2, inode.toString());

			if (stUpdateAccessLatency.executeUpdate() == 0) {

				stSetAccessLatency = dbConnection
						.prepareStatement("INSERT INTO t_access_latency VALUES(?,?)");
				stSetAccessLatency.setString(1, inode.toString());
				stSetAccessLatency.setInt(2, accessLatency.getId());

				stSetAccessLatency.executeUpdate();
			}
		} finally {
			SqlHelper.tryToClose(stSetAccessLatency);
			SqlHelper.tryToClose(stUpdateAccessLatency);
		}
	}

	void setRetentionPolicy(Connection dbConnection, FsInode inode,
			RetentionPolicy accessLatency) throws SQLException {
		PreparedStatement stSetRetentionPolicy = null;
		PreparedStatement stUpdateRetentionPolicy = null;
		try {
			stUpdateRetentionPolicy = dbConnection
					.prepareStatement("UPDATE t_retention_policy SET iretentionPolicy=? WHERE ipnfsid=?");
			stUpdateRetentionPolicy.setInt(1, accessLatency.getId());
			stUpdateRetentionPolicy.setString(2, inode.toString());

			if (stUpdateRetentionPolicy.executeUpdate() == 0) {

				stSetRetentionPolicy = dbConnection
						.prepareStatement("INSERT INTO t_retention_policy VALUES(?,?)");
				stSetRetentionPolicy.setString(1, inode.toString());
				stSetRetentionPolicy.setInt(2, accessLatency.getId());

				stSetRetentionPolicy.executeUpdate();
			}
		} finally {
			SqlHelper.tryToClose(stSetRetentionPolicy);
			SqlHelper.tryToClose(stUpdateRetentionPolicy);
		}
	}

	void removeStorageInfo(Connection dbConnection, FsInode inode)
			throws SQLException {
		PreparedStatement stRemoveStorageInfo = null;
		try {
			stRemoveStorageInfo = dbConnection
					.prepareStatement("DELETE FROM t_storageinfo WHERE ipnfsid=?");
			stRemoveStorageInfo.setString(1, inode.toString());
			stRemoveStorageInfo.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stRemoveStorageInfo);
		}
	}

	InodeStorageInformation getStorageInfo(Connection dbConnection,
			FsInode inode) throws ChimeraFsException, SQLException {
		InodeStorageInformation storageInfo = null;

		ResultSet storageInfoResult = null;
		PreparedStatement stGetStorageInfo = null;
		try {
			stGetStorageInfo = dbConnection
					.prepareStatement("SELECT ihsmName, istorageGroup, istorageSubGroup FROM t_storageinfo WHERE t_storageinfo.ipnfsid=?");
			stGetStorageInfo.setString(1, inode.toString());
			storageInfoResult = stGetStorageInfo.executeQuery();

			if (storageInfoResult.next()) {
				String hsmName = storageInfoResult.getString("ihsmName");
				String storageGroup = storageInfoResult
						.getString("istoragegroup");
				String storageSubGroup = storageInfoResult
						.getString("istoragesubgroup");

				storageInfo = new InodeStorageInformation(inode, hsmName,
						storageGroup, storageSubGroup);
			} else {
				throw new FileNotFoundHimeraFsException(inode.toString());
			}
		} finally {
			SqlHelper.tryToClose(storageInfoResult);
			SqlHelper.tryToClose(stGetStorageInfo);
		}

		return storageInfo;
	}

	String getInodeFromCache(Connection dbConnection, String path)
			throws SQLException {
		String inodeString = null;
		PreparedStatement stGetInodeFromCache = null;
		ResultSet getInodeFromCacheResultSet = null;
		try {
			stGetInodeFromCache = dbConnection
					.prepareStatement("SELECT ipnfsid FROM t_dir_cache WHERE ipath=?");

			stGetInodeFromCache.setString(1, path);

			getInodeFromCacheResultSet = stGetInodeFromCache.executeQuery();
			if (getInodeFromCacheResultSet.next()) {
				inodeString = getInodeFromCacheResultSet.getString("ipnfsid");
			}
		} finally {
			SqlHelper.tryToClose(getInodeFromCacheResultSet);
			SqlHelper.tryToClose(stGetInodeFromCache);
		}

		return inodeString;
	}

	String getPathFromCache(Connection dbConnection, FsInode inode)
			throws SQLException {
		String path = null;
		PreparedStatement stGetPathFromCache = null;
		ResultSet getPathFromCacheResultSet = null;
		try {
			stGetPathFromCache = dbConnection
					.prepareStatement("SELECT ipath FROM t_dir_cache WHERE ipnfsid=?");

			stGetPathFromCache.setString(1, inode.toString());

			getPathFromCacheResultSet = stGetPathFromCache.executeQuery();
			if (getPathFromCacheResultSet.next()) {
				path = getPathFromCacheResultSet.getString("ipath");
			}
		} finally {
			SqlHelper.tryToClose(getPathFromCacheResultSet);
			SqlHelper.tryToClose(stGetPathFromCache);
		}

		return path;
	}

	void setInodeChecksum(Connection dbConnection, FsInode inode, int type,
			String value) throws SQLException {
		PreparedStatement stSetInodeChecksum = null;

		try {
			stSetInodeChecksum = dbConnection
					.prepareStatement("INSERT INTO t_inodes_checksum VALUES(?,?,?)");
			stSetInodeChecksum.setString(1, inode.toString());
			stSetInodeChecksum.setInt(2, type);
			stSetInodeChecksum.setString(3, value);

			stSetInodeChecksum.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stSetInodeChecksum);
		}
	}

	String getInodeChecksum(Connection dbConnection, FsInode inode, int type)
			throws SQLException {
		String checksum = null;

		PreparedStatement stGetInodeChecksum = null;
		ResultSet getGetInodeChecksumResultSet = null;

		try {
			stGetInodeChecksum = dbConnection
					.prepareStatement("SELECT isum FROM t_inodes_checksum WHERE ipnfsid=? AND itype=?");
			stGetInodeChecksum.setString(1, inode.toString());
			stGetInodeChecksum.setInt(2, type);

			getGetInodeChecksumResultSet = stGetInodeChecksum.executeQuery();

			if (getGetInodeChecksumResultSet.next()) {
				checksum = getGetInodeChecksumResultSet.getString("isum");
			}
		} finally {
			SqlHelper.tryToClose(getGetInodeChecksumResultSet);
			SqlHelper.tryToClose(stGetInodeChecksum);
		}

		return checksum;
	}

	void removeInodeChecksum(Connection dbConnection, FsInode inode, int type)
			throws SQLException {
		PreparedStatement stRemoveInodeChecksum = null;

		try {
			if (type >= 0) {
				stRemoveInodeChecksum = dbConnection
						.prepareStatement("DELETE FROM t_inodes_checksum WHERE ipnfsid=? AND itype=?");
				stRemoveInodeChecksum.setInt(2, type);
			} else {
				stRemoveInodeChecksum = dbConnection
						.prepareStatement("DELETE FROM t_inodes_checksum WHERE ipnfsid=?");
			}

			stRemoveInodeChecksum.setString(1, inode.toString());

			stRemoveInodeChecksum.executeUpdate();
		} finally {
			SqlHelper.tryToClose(stRemoveInodeChecksum);
		}
	}

	FsInode path2inode(Connection dbConnection, FsInode root, String path)
			throws SQLException, IOHimeraFsException {
		File pathFile = new File(path);
		
		if(_log.isInfoEnabled()) _log.info("path2inode PATH:{}", path);
		
		List<String> pathElemts = new ArrayList();
		do {
			String fileName = pathFile.getName();
			if (fileName.length() != 0) {

				pathElemts.add(pathFile.getName());
			}

			pathFile = pathFile.getParentFile();
		} while (pathFile != null);

		FsInode parentInode = root;
		FsInode inode = root;

		for (int i = pathElemts.size(); i > 0; i--) {
			String f = (String) pathElemts.get(i - 1);
			inode = inodeOf(dbConnection, parentInode, f);

			if (inode == null) {
				break;
			}

			Stat s = stat(dbConnection, inode);
			if (UnixPermission.getType(s.getMode()) == 40960) {
				byte[] b = new byte[(int) s.getSize()];
				int n = read(dbConnection, inode, 0, 0L, b, 0, b.length);
				String link = new String(b, 0, n);
				if (link.charAt(0) == File.separatorChar) {
					parentInode = new FsInode(parentInode.getFs(),
							"000000000000000000000000000000000000");
				}
				inode = path2inode(dbConnection, parentInode, link);
			}
			parentInode = inode;
		}
		if(_log.isInfoEnabled()) _log.info("inode :{}", inode.toString());
		return inode;
	}

	List<FsInode> path2inodes(Connection dbConnection, FsInode root, String path)
			throws SQLException, IOHimeraFsException {
		File pathFile = new File(path);
		List<String> pathElements = new ArrayList();
		do {
			String fileName = pathFile.getName();
			if (fileName.length() != 0) {

				pathElements.add(pathFile.getName());
			}
			pathFile = pathFile.getParentFile();
		} while (pathFile != null);

		FsInode parentInode = root;

		List<FsInode> inodes = new ArrayList(pathElements.size() + 1);
		inodes.add(root);

		for (String f : Lists.reverse(pathElements)) {
			FsInode inode = inodeOf(dbConnection, parentInode, f);

			if (inode == null) {
				return Collections.emptyList();
			}

			inodes.add(inode);

			Stat s = stat(dbConnection, inode);
			inode.setStatCache(s);
			if (UnixPermission.getType(s.getMode()) == 40960) {
				byte[] b = new byte[(int) s.getSize()];
				int n = read(dbConnection, inode, 0, 0L, b, 0, b.length);
				String link = new String(b, 0, n);
				if (link.charAt(0) == '/') {
					parentInode = new FsInode(parentInode.getFs(),
							"000000000000000000000000000000000000");
					inodes.add(parentInode);
				}
				List<FsInode> linkInodes = path2inodes(dbConnection,
						parentInode, link);

				if (linkInodes.isEmpty()) {
					return Collections.emptyList();
				}
				inodes.addAll(linkInodes.subList(1, linkInodes.size()));
				inode = (FsInode) linkInodes.get(linkInodes.size() - 1);
			}
			parentInode = inode;
		}

		return inodes;
	}

	List<ACE> getACL(Connection dbConnection, FsInode inode)
			throws SQLException {
		List<ACE> acl = new ArrayList();
		PreparedStatement stGetAcl = null;
		ResultSet rs = null;
		try {
			stGetAcl = dbConnection
					.prepareStatement("SELECT * FROM t_acl WHERE rs_id =  ? ORDER BY ace_order");
			stGetAcl.setString(1, inode.toString());

			rs = stGetAcl.executeQuery();
			while (rs.next()) {
				int type = rs.getInt("type");
				acl.add(new ACE(type == 0 ? AceType.ACCESS_ALLOWED_ACE_TYPE
						: AceType.ACCESS_DENIED_ACE_TYPE, rs.getInt("flags"),
						rs.getInt("access_msk"), Who.valueOf(rs.getInt("who")),
						rs.getInt("who_id"), rs.getString("address_msk")));

			}

		} finally {

			SqlHelper.tryToClose(rs);
			SqlHelper.tryToClose(stGetAcl);
		}
		return acl;
	}

	void setACL(Connection dbConnection, FsInode inode, List<ACE> acl)
			throws SQLException {
		PreparedStatement stDeleteACL = null;
		PreparedStatement stAddACL = null;
		try {
			stDeleteACL = dbConnection
					.prepareStatement("DELETE FROM t_acl WHERE rs_id = ?");
			stDeleteACL.setString(1, inode.toString());
			stDeleteACL.executeUpdate();

			if (acl.isEmpty()) {
				return;
			}
			stAddACL = dbConnection
					.prepareStatement("INSERT INTO t_acl VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");

			int type = inode.isDirectory() ? 0 : 1;
			int order = 0;
			for (ACE ace : acl) {
				stAddACL.setString(1, inode.toString());
				stAddACL.setInt(2, type);
				stAddACL.setInt(3, ace.getType().getValue());
				stAddACL.setInt(4, ace.getFlags());
				stAddACL.setInt(5, ace.getAccessMsk());
				stAddACL.setInt(6, ace.getWho().getValue());
				stAddACL.setInt(7, ace.getWhoID());
				stAddACL.setString(8, ace.getAddressMsk());
				stAddACL.setInt(9, order);

				stAddACL.addBatch();
				order++;
			}
			stAddACL.executeBatch();
			setFileCTime(dbConnection, inode, 0, System.currentTimeMillis());
		} finally {
			SqlHelper.tryToClose(stDeleteACL);
			SqlHelper.tryToClose(stAddACL);
		}
	}

	public boolean isDuplicatedKeyError(String sqlState) {
		return sqlState.equals("23505");
	}

	public boolean isForeignKeyError(String sqlState) {
		return sqlState.equals("23503");
	}

	static HdfsSqlDriver getDriverInstance(String dialect) {
		HdfsSqlDriver driver = null;

		String dialectDriverClass = "org.dcache.chimera." + dialect  + "FsSqlDriver1";
		try {
			driver = (HdfsSqlDriver) Class.forName(dialectDriverClass)
					.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
		} catch (ClassNotFoundException e) {
			_log.info(dialectDriverClass
					+ " not found, using default FsSqlDriver1.");
			driver = new HdfsSqlDriver();
		}

		return driver;
	}
}
