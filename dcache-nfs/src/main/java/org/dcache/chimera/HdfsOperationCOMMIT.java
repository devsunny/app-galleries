package org.dcache.chimera;

import java.io.IOException;
import java.nio.channels.FileChannel;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.dcache.nfs.ChimeraNFSException;
import org.dcache.nfs.nfsstat;
import org.dcache.nfs.status.InvalException;
import org.dcache.nfs.status.IsDirException;
import org.dcache.nfs.v4.AbstractNFSv4Operation;
import org.dcache.nfs.v4.CompoundContext;
import org.dcache.nfs.v4.xdr.COMMIT4res;
import org.dcache.nfs.v4.xdr.COMMIT4resok;
import org.dcache.nfs.v4.xdr.nfs4_prot;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.dcache.nfs.v4.xdr.verifier4;
import org.dcache.nfs.vfs.FsCache;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.dcache.xdr.OncRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsOperationCOMMIT extends AbstractNFSv4Operation {

	private static final Logger _log = LoggerFactory
			.getLogger(HdfsOperationCOMMIT.class);
	private final HadoopHdfsVfs _fsCache;

	public HdfsOperationCOMMIT(nfs_argop4 args, HadoopHdfsVfs fsCache) {
		super(args, nfs_opnum4.OP_COMMIT);
		_fsCache = fsCache;
	}

	@Override
	public void process(CompoundContext context, nfs_resop4 result)
			throws ChimeraNFSException, IOException, OncRpcException {
		// FIXME: sync the data

		final COMMIT4res res = result.opcommit;
		if (context.getFs() != null) {
			Inode inode = context.currentInode();
			Stat stat = context.getFs().getattr(inode);

			if (stat.type() == Stat.Type.DIRECTORY) {
				throw new IsDirException("Invalid can't commit a directory");
			}

			if (stat.type() != Stat.Type.REGULAR) {
				throw new InvalException("Invalid object type");
			}
			FSDataOutputStream out = _fsCache.getFSDataOutputStream(inode);	
			
			stat.setSize(out.size());
			context.getFs().setattr(context.currentInode(), stat);
		}

		res.status = nfsstat.NFS_OK;
		res.resok4 = new COMMIT4resok();
		res.resok4.writeverf = new verifier4();
		res.resok4.writeverf.value = new byte[nfs4_prot.NFS4_VERIFIER_SIZE];

	}

}
