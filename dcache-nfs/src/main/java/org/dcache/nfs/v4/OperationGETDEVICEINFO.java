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
package org.dcache.nfs.v4;

import org.dcache.nfs.v4.xdr.nfs_argop4;
import org.dcache.nfs.v4.xdr.nfs_opnum4;
import org.dcache.nfs.v4.xdr.layouttype4;
import org.dcache.nfs.v4.xdr.bitmap4;
import org.dcache.nfs.v4.xdr.nfs_resop4;
import org.dcache.nfs.v4.xdr.GETDEVICEINFO4res;
import org.dcache.nfs.v4.xdr.GETDEVICEINFO4resok;
import org.dcache.nfs.v4.xdr.deviceid4;
import org.dcache.nfs.v4.xdr.device_addr4;
import java.io.IOException;
import org.dcache.nfs.nfsstat;
import org.dcache.nfs.status.InvalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OperationGETDEVICEINFO extends AbstractNFSv4Operation {

    private static final Logger _log = LoggerFactory.getLogger(OperationGETDEVICEINFO.class);

    OperationGETDEVICEINFO(nfs_argop4 args) {
        super(args, nfs_opnum4.OP_GETDEVICEINFO);
    }

    @Override
    public void process(CompoundContext context, nfs_resop4 result) throws IOException {

        /*
         * GETDEVICEINFO. returns the mapping of device ID to storage device
         * address.
         */
        final GETDEVICEINFO4res res = result.opgetdeviceinfo;

        deviceid4 deviceId = _args.opgetdeviceinfo.gdia_device_id;

        _log.debug("             Info for #{}", deviceId);
        _log.debug("             type for #{}",
                _args.opgetdeviceinfo.gdia_layout_type);

        res.gdir_resok4 = new GETDEVICEINFO4resok();

        device_addr4 deviceInfo = context.getDeviceManager().getDeviceInfo(context, deviceId);

        if (deviceInfo == null) {
            throw new InvalException("invalid deviceInfo id");
        }

        res.gdir_resok4.gdir_device_addr = deviceInfo;
        res.gdir_resok4.gdir_device_addr.da_layout_type = layouttype4.LAYOUT4_NFSV4_1_FILES;
        res.gdir_resok4.gdir_notification = new bitmap4();
        res.gdir_resok4.gdir_notification.value = new int[] {0};

        res.gdir_status = nfsstat.NFS_OK;
    }
}
