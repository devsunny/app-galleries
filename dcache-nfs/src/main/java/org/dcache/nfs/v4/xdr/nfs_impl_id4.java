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

public class nfs_impl_id4 implements XdrAble {
    public utf8str_cis nii_domain;
    public utf8str_cs nii_name;
    public nfstime4 nii_date;

    public nfs_impl_id4() {
    }

    public nfs_impl_id4(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        xdrDecode(xdr);
    }

    public void xdrEncode(XdrEncodingStream xdr)
           throws OncRpcException, IOException {
        nii_domain.xdrEncode(xdr);
        nii_name.xdrEncode(xdr);
        nii_date.xdrEncode(xdr);
    }

    public void xdrDecode(XdrDecodingStream xdr)
           throws OncRpcException, IOException {
        nii_domain = new utf8str_cis(xdr);
        nii_name = new utf8str_cs(xdr);
        nii_date = new nfstime4(xdr);
    }

}
// End of nfs_impl_id4.java
