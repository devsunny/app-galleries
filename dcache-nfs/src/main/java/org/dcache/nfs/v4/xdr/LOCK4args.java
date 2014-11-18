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
import org.dcache.xdr.*;
import java.io.IOException;

public class LOCK4args implements XdrAble {
    public int locktype;
    public boolean reclaim;
    public offset4 offset;
    public length4 length;
    public locker4 locker;

    public LOCK4args() {
    }

    public LOCK4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        xdr.xdrEncodeInt(locktype);
        xdr.xdrEncodeBoolean(reclaim);
        offset.xdrEncode(xdr);
        length.xdrEncode(xdr);
        locker.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        locktype = xdr.xdrDecodeInt();
        reclaim = xdr.xdrDecodeBoolean();
        offset = new offset4(xdr);
        length = new length4(xdr);
        locker = new locker4(xdr);
    }

}
// End of LOCK4args.java
