package org.dcache.chimera;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.dcache.nfs.ChimeraNFSException;
import org.dcache.nfs.nfsstat;
import org.dcache.nfs.status.InvalException;
import org.dcache.nfs.status.IsDirException;
import org.dcache.nfs.v4.AbstractNFSv4Operation;
import org.dcache.nfs.v4.CompoundContext;
import org.dcache.nfs.v4.Stateids;
import org.dcache.nfs.v4.xdr.READ4res;
import org.dcache.nfs.v4.xdr.READ4resok;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.dcache.nfs.vfs.FsCache;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.dcache.xdr.OncRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsOperationREAD extends AbstractNFSv4Operation{

	private static final Logger _log = LoggerFactory
			.getLogger(HdfsOperationREAD.class);
	private final FsCache _fsCache;
	public HdfsOperationREAD(nfs_argop4 args, FsCache fsCache) {
		 super(args, nfs_opnum4.OP_READ);
		 _fsCache = fsCache;
	}

	@Override
	public void process(CompoundContext context, nfs_resop4 result)
			throws ChimeraNFSException, IOException, OncRpcException {
		 final READ4res res = result.opread;

	        Inode inode = context.currentInode();
	        Stat stat = context.getFs().getattr(inode);

	        if (stat.type() == Stat.Type.DIRECTORY) {
	            throw new IsDirException("Can't READ a directory inode");
	        }

	        if (stat.type() != Stat.Type.REGULAR) {
	            throw new InvalException("Invalid object type");
	        }

	        if ((context.getMinorversion() == 0) && !Stateids.ZeroStateId().equalsWithSeq(_args.opread.stateid) && !Stateids.OneStateId().equalsWithSeq(_args.opread.stateid)) {
	            /*
	             *  The NFSv4.0 spec requires to update lease time as long as client
	             * needs the file. This is done through READ, WRITE and RENEW
	             * opertations. With introduction of sessions in v4.1 update of the
	             * lease time done through SEQUENCE operation.
	             */
	            context.getStateHandler().updateClientLeaseTime(_args.opread.stateid);
	        }

	        boolean eof = false;

	        long offset = _args.opread.offset.value;
	        int count = _args.opread.count.value;

	        ByteBuffer bb = ByteBuffer.allocateDirect(count);
	        FileChannel in = _fsCache.get(inode);

	        int bytesReaded = in.read(bb, offset);
	        if (bytesReaded < 0) {
	            eof = true;
	            bytesReaded = 0;
	        }

	        res.status = nfsstat.NFS_OK;
	        res.resok4 = new READ4resok();
	        res.resok4.data = bb;

	        if (offset + bytesReaded == stat.getSize()) {
	            eof = true;
	        }
	        res.resok4.eof = eof;

	        _log.debug("MOVER: {}@{} readed, {} requested.",
	                bytesReaded, offset, _args.opread.count.value);
		
	}

}
