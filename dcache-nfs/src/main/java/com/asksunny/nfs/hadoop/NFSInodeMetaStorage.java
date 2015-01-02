package com.asksunny.nfs.hadoop;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.hadoop.fs.Path;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NFSInodeMetaStorage implements NFSConstants {

	
	private final static Logger LOG = LoggerFactory.getLogger(NFSInodeMetaStorage.class);
	
	/**
	   	CREATE TABLE "NFS_INODES" 
		(	
		"INODE_ID" varchar2(36) not null primary key, 
		"PATH" varchar2(1024) not null, 
		"PARENT_ID" varchar(36) not null, 
		"ITYPE" NUMBER(10,0)  not null, 
		"IMODE" NUMBER(10,0)  not null, 
		"INLINK" NUMBER(10,0)  not null, 
		"IUID" NUMBER(10,0)  not null, 
		"IGID" NUMBER(10,0)  not null, 
		"ISIZE" NUMBER(38,0)  DEFAULT 0, 
		"IIO" NUMBER(10,0)  DEFAULT 0, 
		"CTIME" TIMESTAMP (6) not null, 
		"ATIME" TIMESTAMP (6)  not null, 
		"MTIME" TIMESTAMP (6)  not null, 
		"CRTIME" TIMESTAMP (6) not null, 
		"GENERATION" NUMBER(38,0) DEFAULT 0	        
		);	 
				
		CREATE INDEX IDX_PARENT_ID ON "NFS_INODES" (PARENT_ID);
	 **/
	
	private static final String SQL_ID2INODE = "SELECT INODE_ID,PATH,PARENT_ID,ITYPE,IMODE,INLINK,IUID,IGID,ISIZE,IIO,CTIME,ATIME,MTIME,CRTIME,GENERATION FROM NFS_INODES WHERE inode_id=?";
	private static final String SQL_PATH2INODE = "SELECT INODE_ID,PATH,PARENT_ID,ITYPE,IMODE,INLINK,IUID,IGID,ISIZE,IIO,CTIME,ATIME,MTIME,CRTIME,GENERATION FROM NFS_INODES WHERE path=?";
	private static final String SQL_ADDINODE = "INSERT INTO NFS_INODES(INODE_ID,PATH,PARENT_ID,ITYPE,IMODE,INLINK,IUID,IGID,CTIME,ATIME,MTIME,CRTIME,GENERATION) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
	
	private static final String SQL_LISTINODE = "SELECT INODE_ID,PATH,PARENT_ID,ITYPE,IMODE,INLINK,IUID,IGID,ISIZE,IIO,CTIME,ATIME,MTIME,CRTIME,GENERATION from NFS_INODES WHERE parent_id=?";
	private static final String SQL_PARENOF = "SELECT a.inode_id, a.path, a.parent_id, a.ITYPE, a.IMODE,a.INLINK,a.IUID,a.IGID,a.ISIZE,a.IIO,a.CTIME,a.ATIME,a.MTIME,a.CRTIME,a.GENERATION from NFS_INODES a JOIN NFS_INODES b on a.inode_id=b.parent_id WHERE b.inode_id=?";
	
	private static final String SQL_REMOVEINODE = "DELETE FROM NFS_INODES WHERE inode_id=?";

	
	private DataSource dataSource = null;

	public NFSInodeMetaStorage(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	
	public HdfsInode path2inode(Path path) throws SQLException 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Look up inode for path:{}", path.toString());
		}
		HdfsInode inode = null;		
		PreparedStatement stmt = null;
		Connection conn = null;			
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL_PATH2INODE);			
			stmt.setString(1, path.toString());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				inode = HdfsInode.forId(rs.getString(1));
				rs2Inode(rs, inode);
			}else{
				LOG.warn("Could not find inode for path:{}", path.toString());
			}
			rs.close();
		} finally {
			closeSilently(stmt);	
			closeSilently(conn);	
		}
		return inode;
	}
	
	
	public HdfsInode mkdir(HdfsInode parent , String name,
			int owner, int group, int mode) throws SQLException 
	{
		Path p = new Path(parent.getPath(), name);
		if(LOG.isDebugEnabled()) {
			LOG.debug("mkdir {}", p.toString());
		}		
		HdfsInode hinode = HdfsInode.newInode();		
		hinode.setPath(p.toString());
		hinode.setType(Stat.S_IFDIR);
		hinode.setParentId(parent.getIdString());
		long ts = System.currentTimeMillis();
		hinode.setAtime(ts);
		hinode.setCtime(ts);
		hinode.setMtime(ts);
		hinode.setNlink(0);
		hinode.setMode(mode);
		hinode.setUid(owner);
		hinode.setGid(group);			
		addInode(hinode);	
		return hinode;
	}
	
	
	
	public HdfsInode id2inode(String id) throws SQLException 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Look up inode for Id:{}", id);
		}
		HdfsInode inode = null;		
		PreparedStatement stmt = null;
		Connection conn = null;			
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL_ID2INODE);			
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				inode = HdfsInode.forId(rs.getString(1));
				rs2Inode(rs, inode);
			}
			rs.close();
		} finally {
			closeSilently(stmt);	
			closeSilently(conn);	
		}
		return inode;
	}
	
	
	private void rs2Inode(ResultSet rs, HdfsInode inode) throws SQLException
	{		
		inode.setPath(rs.getString(2));
		inode.setParentId(rs.getString(3));
		inode.setType(rs.getInt(4));
		inode.setMode(rs.getInt(5));
		inode.setNlink(rs.getInt(6));
		inode.setUid(rs.getInt(7));
		inode.setGid(rs.getInt(8));
		inode.setSize(rs.getLong(9));
		inode.setIo(rs.getInt(10));
		inode.setCtime(rs.getLong(11));
		inode.setAtime(rs.getLong(12));
		inode.setMtime(rs.getLong(13));
		inode.setCrtime(rs.getLong(14));
		inode.setGeneration(rs.getInt(15));
	}
	
	public void addInode(HdfsInode inode) throws SQLException {		
		Connection conn = null;	
		PreparedStatement stmt = null;		
		String path = inode.getPath();
		if(NFSConstants.NFS_PREFIX.equals(path)){
			return;
		}
		Path hpath = normalizePath(inode.getPath());
		//(INODE_ID,PATH,PARENT_ID,ITYPE,IMODE,INLINK,IUID,IGID,CTIME,ATIME,MTIME,CRTIME,GENERATION) 
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL_ADDINODE);
			stmt.setString(1, inode.getIdString());			
			stmt.setString(2, hpath.toString());
			stmt.setString(3, inode.getParentId());
			stmt.setInt(4, inode.getType());
			stmt.setInt(5, inode.getMode());
			stmt.setInt(6, inode.getNlink());
			stmt.setInt(7, inode.getUid());
			stmt.setInt(8, inode.getGid());
			stmt.setLong(9, inode.getCtime());
			stmt.setLong(10, inode.getAtime());
			stmt.setLong(11, inode.getMtime());
			stmt.setLong(12, inode.getCrtime());
			stmt.setLong(13, inode.getGeneration());
			int c = stmt.executeUpdate();
			if( c !=1){
				throw new SQLException(String.format("Should only have one row inserted, but got [%d].", c));
			}
		} finally {
			closeSilently(stmt);	
			closeSilently(conn);	
		}
		
	}
	
	public void removeInode(HdfsInode inode) throws SQLException {		
		Connection conn = null;	
		PreparedStatement stmt = null;
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL_REMOVEINODE);
			stmt.setString(1, inode.getIdString());			
			int c = stmt.executeUpdate();
			if( c !=1){
				throw new SQLException(String.format("Should only have one row inserted, but got [%d].", c));
			}
		} finally {
			closeSilently(stmt);	
			closeSilently(conn);	
		}
		
	}
	
	
	
	
	
	public HdfsInode parentOf(HdfsInode inode) throws SQLException 
	{
		if(LOG.isDebugEnabled()) {
			LOG.debug("Look up parentOf for path:{}", inode.getPath());
		}
		HdfsInode pinode = null;		
		PreparedStatement stmt = null;
		Connection conn = null;			
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL_PARENOF);			
			stmt.setString(1, inode.getIdString());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				pinode = HdfsInode.forId(rs.getString(1));
				pinode.setPath(rs.getString(2));
				pinode.setParentId(rs.getString(3));
				pinode.setType(rs.getInt(4));
			}
			rs.close();
		} finally {
			closeSilently(stmt);	
			closeSilently(conn);	
		}
		return inode;
	}
	
	
		
	public Map<String, HdfsInode> listDir(HdfsInode parent) throws SQLException
	{
		
		if(LOG.isDebugEnabled()) {
			LOG.debug("list inode for inode:{}/path:{}", parent.getIdString(), parent.getPath());
		}
		HdfsInode inode = null;		
		PreparedStatement stmt = null;
		Connection conn = null;			
		Map<String, HdfsInode> maps = new HashMap<String, HdfsInode>();
		try {
			conn = dataSource.getConnection();
			stmt = conn.prepareStatement(SQL_LISTINODE);			
			stmt.setString(1, parent.getIdString());
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				inode = HdfsInode.forId(rs.getString(1));
				inode.setPath(rs.getString(2));
				inode.setParentId(rs.getString(3));
				inode.setType(rs.getInt(4));
				maps.put(inode.getPath(), inode);
			}
			rs.close();
		} finally {
			closeSilently(stmt);	
			closeSilently(conn);	
		}
		return maps;
	}
	
	
		
		
	
	
	
	

	public String inode2path(HdfsInode inode) 
	{
		
		
		
		return "/";
	}
	
	private static Path normalizePath(String paths)
	{
		
		Path hpath = (paths.endsWith(NFS_PREFIX)) ? new Path(paths.substring(0,
				paths.length() - 1)) : new Path(paths);
		return hpath;
	}

	private static void closeSilently(Connection conn) {
		if (conn == null)
			return;
		try {
			conn.close();
		} catch (SQLException e) {
			;
		}
	}

	private static void closeSilently(Statement stmt) {
		if (stmt == null)
			return;
		try {
			stmt.close();
		} catch (SQLException e) {
			;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
