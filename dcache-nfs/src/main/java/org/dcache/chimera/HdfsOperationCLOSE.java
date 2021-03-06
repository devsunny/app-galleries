/*
 * Copyright (c) 2009 - 2012 Deutsches Elektronen-Synchroton,
 * Member of the Helmholtz Association, (DESY), HAMBURG, GERMANY
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Library General Public License as
 * published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public
 * License along with this program (see the file COPYING.LIB for more
 * details); if not, write to the Free Software Foundation, Inc.,
 * 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package org.dcache.chimera;

import java.io.IOException;

import org.dcache.nfs.ChimeraNFSException;
import org.dcache.nfs.nfsstat;
import org.dcache.nfs.v4.AbstractNFSv4Operation;
import org.dcache.nfs.v4.CompoundContext;
import org.dcache.nfs.v4.NFS4Client;
import org.dcache.nfs.v4.Stateids;
import org.dcache.nfs.v4.xdr.CLOSE4res;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.dcache.nfs.vfs.FsCache;
import org.dcache.nfs.vfs.Inode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HdfsOperationCLOSE extends AbstractNFSv4Operation {

	private static final Logger _log = LoggerFactory
			.getLogger(HdfsOperationCLOSE.class);

	private final HadoopHdfsVfs _fsCache;

	HdfsOperationCLOSE(nfs_argop4 args, HadoopHdfsVfs fsCache) {
		super(args, nfs_opnum4.OP_CLOSE);
		_fsCache = fsCache;
	}

	@Override
	public void process(CompoundContext context, nfs_resop4 result)
			throws ChimeraNFSException, IOException {
		final CLOSE4res res = result.opclose;

		Inode inode = context.currentInode();
		_fsCache.close(inode);
		
		NFS4Client client;	
		if (context.getMinorversion() > 0) {
			client = context.getSession().getClient();
		} else {
			client = context.getStateHandler().getClientIdByStateId(
					_args.opclose.open_stateid);
		}

		if (context.getMinorversion() > 0) {
			context.getDeviceManager().layoutReturn(context,
					_args.opclose.open_stateid);
		}

		client.releaseState(_args.opclose.open_stateid);
		client.updateLeaseTime();

		res.open_stateid = Stateids.invalidStateId();
		res.status = nfsstat.NFS_OK;
		
		if(_log.isDebugEnabled()) _log.debug("IO Closed.");

	}
}
