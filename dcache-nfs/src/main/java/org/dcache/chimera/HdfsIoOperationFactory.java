package org.dcache.chimera;

import org.dcache.nfs.v4.AbstractNFSv4Operation;
import org.dcache.nfs.v4.MDSOperationFactory;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.vfs.FsCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsIoOperationFactory extends MDSOperationFactory {

	private static final Logger _log = LoggerFactory
			.getLogger(HdfsIoOperationFactory.class);
	private final FsCache _fs;
	public HdfsIoOperationFactory(FsCache fs) {
		_fs = fs;
	}

	@Override
	public AbstractNFSv4Operation getOperation(nfs_argop4 op) {
		if (_log.isInfoEnabled())
			_log.info("getOperation:{}", op.toString());
		switch (op.argop) {
		case nfs_opnum4.OP_READ:
			return new HdfsOperationREAD(op, _fs);
		case nfs_opnum4.OP_COMMIT:
			return new HdfsOperationCOMMIT(op, _fs);
		case nfs_opnum4.OP_WRITE:
			return new HdfsOperationWRITE(op, _fs);
		case nfs_opnum4.OP_CLOSE:
			return new HdfsOperationCLOSE(op, _fs);
		default:
			return super.getOperation(op);
		}
	}

}
