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
package org.dcache.nfs.v4.xdr;
import org.dcache.nfs.nfsstat;
import org.dcache.xdr.*;
import java.io.IOException;

public class SET_SSV4res implements XdrAble {
    public int ssr_status;
    public SET_SSV4resok ssr_resok4;

    public SET_SSV4res() {
    }

    public SET_SSV4res(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(ssr_status);
        switch ( ssr_status ) {
        case nfsstat.NFS_OK:
            ssr_resok4.xdrEncode(xdr);
            break;
        default:
            break;
        }
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        ssr_status = xdr.xdrDecodeInt();
        switch ( ssr_status ) {
        case nfsstat.NFS_OK:
            ssr_resok4 = new SET_SSV4resok(xdr);
            break;
        default:
            break;
        }
    }

}
// End of SET_SSV4res.java
