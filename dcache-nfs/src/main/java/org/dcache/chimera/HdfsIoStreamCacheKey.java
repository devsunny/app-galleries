package org.dcache.chimera;

import org.dcache.nfs.v4.NFS4Client;
import org.dcache.nfs.vfs.Inode;

public class HdfsIoStreamCacheKey {

	private final Inode inode;
	private final NFS4Client client;
	
	public HdfsIoStreamCacheKey(NFS4Client client, Inode inode) {
		this.client = client;
		this.inode =  inode;
	}

	public Inode getInode() {
		return inode;
	}

	public NFS4Client getClient() {
		return client;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : new Long(client.getId()).hashCode());
		result = prime * result + ((inode == null) ? 0 : inode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HdfsIoStreamCacheKey other = (HdfsIoStreamCacheKey) obj;
		if (client == null) {
			if (other.client != null)
				return false;
		} else if (client.getId() != other.client.getId())
			return false;
		if (inode == null) {
			if (other.inode != null)
				return false;
		} else if (!inode.equals(other.inode))
			return false;
		return true;
	}
	
	
	
	

}
