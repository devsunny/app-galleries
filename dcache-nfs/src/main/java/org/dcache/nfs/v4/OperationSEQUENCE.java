/*
 * Copyright (c) 2009 - 2014 Deutsches Elektronen-Synchroton,
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
package org.dcache.nfs.v4;

import org.dcache.nfs.nfsstat;
import org.dcache.nfs.v4.xdr.sessionid4;
import org.dcache.nfs.v4.xdr.uint32_t;
import org.dcache.nfs.v4.xdr.slotid4;
import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.v4.xdr.SEQUENCE4res;
import org.dcache.nfs.v4.xdr.SEQUENCE4resok;
import org.dcache.nfs.ChimeraNFSException;
import org.dcache.nfs.status.BadSessionException;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class OperationSEQUENCE extends AbstractNFSv4Operation {

    private static final Logger _log = LoggerFactory.getLogger(OperationSEQUENCE.class);

    public OperationSEQUENCE(nfs_argop4 args) {
        super(args, nfs_opnum4.OP_SEQUENCE);
    }

    @Override
    public void process(CompoundContext context, nfs_resop4 result) throws ChimeraNFSException {
        final SEQUENCE4res res = result.opsequence;

        NFSv41Session session = context.getStateHandler().sessionById(_args.opsequence.sa_sessionid);

        if (session == null) {
            _log.debug("no session for id [{}]", _args.opsequence.sa_sessionid);
            throw new BadSessionException("session not found");
        }

        NFS4Client client = session.getClient();

        if (!client.hasSessions()) {
            _log.debug("no client for session for id [{}]", _args.opsequence.sa_sessionid);
            throw new BadSessionException("client not found");
        }

        int opCount = context.getTotalOperationCount();
        context.setCache(session.checkCacheSlot(_args.opsequence.sa_slotid.value,
                _args.opsequence.sa_sequenceid.value, opCount > 1));

        session.bindIfNeeded( new SessionConnection(
                context.getRpcCall().getTransport().getLocalSocketAddress(),
                context.getRpcCall().getTransport().getRemoteSocketAddress())
        );
        context.setCacheThis(_args.opsequence.sa_cachethis);
        client.updateLeaseTime();

        context.setSession(session);
        context.setSlotId(_args.opsequence.sa_slotid.value);

        res.sr_resok4 = new SEQUENCE4resok();

        res.sr_resok4.sr_highest_slotid = new slotid4(session.getHighestSlot());
        res.sr_resok4.sr_slotid = new slotid4(_args.opsequence.sa_slotid.value);
        res.sr_resok4.sr_target_highest_slotid = new slotid4(session.getHighestSlot());
        res.sr_resok4.sr_sessionid = new sessionid4(_args.opsequence.sa_sessionid.value);

        //res.sr_resok4.sr_sequenceid = new sequenceid4( new uint32_t( session.nextSequenceID()) );
        res.sr_resok4.sr_sequenceid = _args.opsequence.sa_sequenceid;
        res.sr_resok4.sr_status_flags = new uint32_t(0);

        res.sr_status = nfsstat.NFS_OK;
    }
}
