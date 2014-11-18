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

public class BACKCHANNEL_CTL4args implements XdrAble {
    public uint32_t bca_cb_program;
    public callback_sec_parms4 [] bca_sec_parms;

    public BACKCHANNEL_CTL4args() {
    }

    public BACKCHANNEL_CTL4args(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        bca_cb_program.xdrEncode(xdr);
        { int $size = bca_sec_parms.length; xdr.xdrEncodeInt($size); for ( int $idx = 0; $idx < $size; ++$idx ) { bca_sec_parms[$idx].xdrEncode(xdr); } }
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        bca_cb_program = new uint32_t(xdr);
        { int $size = xdr.xdrDecodeInt(); bca_sec_parms = new callback_sec_parms4[$size]; for ( int $idx = 0; $idx < $size; ++$idx ) { bca_sec_parms[$idx] = new callback_sec_parms4(xdr); } }
    }

}
// End of BACKCHANNEL_CTL4args.java
