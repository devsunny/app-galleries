package com.asksunny.nfs.hadoop;



import java.io.IOException;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.dcache.nfs.ChimeraNFSException;
import org.dcache.nfs.nfsstat;
import org.dcache.nfs.status.InvalException;
import org.dcache.nfs.status.IsDirException;
import org.dcache.nfs.status.NfsIoException;
import org.dcache.nfs.v4.AbstractNFSv4Operation;
import org.dcache.nfs.v4.CompoundContext;
import org.dcache.nfs.v4.Stateids;
import org.dcache.nfs.v4.xdr.WRITE4res;
import org.dcache.nfs.v4.xdr.WRITE4resok;
import org.dcache.nfs.v4.xdr.count4;
import org.dcache.nfs.v4.xdr.nfs4_prot;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.dcache.nfs.v4.xdr.stable_how4;
import org.dcache.nfs.v4.xdr.verifier4;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.dcache.xdr.OncRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsOperationWRITE extends AbstractNFSv4Operation {

	private static final Logger LOG = LoggerFactory
			.getLogger(HdfsOperationWRITE.class);
	private final HdfsVfs _fsCache;

	public HdfsOperationWRITE(nfs_argop4 args, HdfsVfs fsCache) {
		super(args, nfs_opnum4.OP_WRITE);
		_fsCache = fsCache;
	}

	@Override
	public void process(CompoundContext context, nfs_resop4 result)
			throws ChimeraNFSException, IOException, OncRpcException {
    	
    	final WRITE4res res = result.opwrite;

        long offset = _args.opwrite.offset.value;

        Inode inode = context.currentInode();
        
        Stat stat = context.getFs().getattr(inode);

        if (stat.type() == Stat.Type.DIRECTORY) {
            throw new IsDirException("Can't WRITE into a directory inode");
        }

        if (stat.type() != Stat.Type.REGULAR) {
            throw new InvalException("Invalid object type");
        }

        if ((context.getMinorversion() == 0) && !Stateids.ZeroStateId().equalsWithSeq(_args.opwrite.stateid) && !Stateids.OneStateId().equalsWithSeq(_args.opwrite.stateid)) {
            /*
             *  The NFSv4.0 spec requires to update lease time as long as client
             * needs the file. This is done through READ, WRITE and RENEW
             * opertations. With introduction of sessions in v4.1 update of the
             * lease time done through SEQUENCE operation.
             */
            context.getStateHandler().updateClientLeaseTime(_args.opwrite.stateid);
        }
        
       

        FSDataOutputStream out = _fsCache.getFSDataOutputStream(inode);
        _args.opwrite.data.rewind();
        
        if (LOG.isInfoEnabled())
			LOG.info("create and write data size: {} at offset {} ", _args.opwrite.data.remaining(), offset );
        
        int bytesWritten =  _args.opwrite.data.remaining();
        byte[] b = new byte[bytesWritten];
        _args.opwrite.data.get(b);
         out.write(b);
         out.flush();
         
        if (bytesWritten < 0) {
            throw new NfsIoException("IO not allowd");
        }

        res.status = nfsstat.NFS_OK;
        res.resok4 = new WRITE4resok();
        res.resok4.count = new count4(bytesWritten);
        res.resok4.committed = _args.opwrite.stable;
        res.resok4.writeverf = new verifier4();
        res.resok4.writeverf.value = new byte[nfs4_prot.NFS4_VERIFIER_SIZE];

        if (_args.opwrite.stable != stable_how4.UNSTABLE4) {
            stat.setSize(out.size());
            context.getFs().setattr(context.currentInode(), stat);
        }
        LOG.debug("MOVER: {}@{} written, {} requested. New File size {}",
                bytesWritten, offset, _args.opwrite.data, out.size());
	}


}
