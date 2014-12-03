package org.dcache.chimera;

import java.io.IOException;

import java.util.List;

import javax.security.auth.Subject;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.dcache.nfs.ChimeraNFSException;
import org.dcache.nfs.v4.NfsIdMapping;
import org.dcache.nfs.v4.xdr.nfsace4;
import org.dcache.nfs.vfs.AclCheckable;
import org.dcache.nfs.vfs.DirectoryEntry;
import org.dcache.nfs.vfs.FsStat;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.dcache.nfs.vfs.Stat.Type;
import org.dcache.nfs.vfs.VirtualFileSystem;

public class HadoopFS implements VirtualFileSystem, AclCheckable {

	private final NfsIdMapping _idMapping;
	private Configuration hdfsCfg = null;

	
	
	public HadoopFS(NfsIdMapping idMapping, Configuration hdfsCfg) {	
		this._idMapping = idMapping;
		this.hdfsCfg = hdfsCfg;
	}

	@Override
	public Access checkAcl(Subject subject, Inode inode, int accessMask)
			throws ChimeraNFSException, IOException 
	{
		
		return null;
	}

	@Override
	public int access(Inode inode, int mode) throws IOException 
	{		
		return 0;
	}

	@Override
	public Inode create(Inode parent, Type type, String path, int uid, int gid,
			int mode) throws IOException {
	
		return null;
	}

	@Override
	public FsStat getFsStat() throws IOException {
		
		return null;
	}

	@Override
	public Inode getRootInode() throws IOException {
		
		return null;
	}

	@Override
	public Inode lookup(Inode parent, String path) throws IOException {
		
		return null;
	}

	@Override
	public Inode link(Inode parent, Inode link, String path, int uid, int gid)
			throws IOException {
		
		return null;
	}

	@Override
	public List<DirectoryEntry> list(Inode inode) throws IOException {
		
		return null;
	}

	@Override
	public Inode mkdir(Inode parent, String path, int uid, int gid, int mode)
			throws IOException {
		
		return null;
	}

	@Override
	public boolean move(Inode src, String oldName, Inode dest, String newName)
			throws IOException {
		
		return false;
	}

	@Override
	public Inode parentOf(Inode inode) throws IOException {
		
		return null;
	}

	@Override
	public int read(Inode inode, byte[] data, long offset, int count)
			throws IOException {
		
		return 0;
	}

	@Override
	public String readlink(Inode inode) throws IOException {
		
		return null;
	}

	@Override
	public void remove(Inode parent, String path) throws IOException {
		

	}

	@Override
	public Inode symlink(Inode parent, String path, String link, int uid,
			int gid, int mode) throws IOException 
	{
		
		return null;
	}

	@Override
	public WriteResult write(Inode inode, byte[] data, long offset, int count,
			StabilityLevel stabilityLevel) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void commit(Inode inode, long offset, int count) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public Stat getattr(Inode inode) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setattr(Inode inode, Stat stat) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public nfsace4[] getAcl(Inode inode) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAcl(Inode inode, nfsace4[] acl) throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean hasIOLayout(Inode inode) throws IOException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public AclCheckable getAclCheckable() {
		// TODO Auto-generated method stub
		return null;
	}

}
