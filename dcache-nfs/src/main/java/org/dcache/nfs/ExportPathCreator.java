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
package org.dcache.nfs;

import com.google.common.base.Splitter;
import java.io.IOException;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.dcache.nfs.vfs.VirtualFileSystem;

/**
 * Class to scan export file and create missing directories
 */
public class ExportPathCreator {

    private ExportFile exportFile;
    public VirtualFileSystem vfs;

    public void setVfs(VirtualFileSystem vfs) {
        this.vfs = vfs;
    }

    public void setExportFile(ExportFile exportFile) {
        this.exportFile = exportFile;
    }

    public void init()  throws IOException {
        Inode root = vfs.getRootInode();
        for (FsExport export : exportFile.getExports()) {
            String path = export.getPath();
            Splitter splitter = Splitter.on('/').omitEmptyStrings();
            Inode inode = root;
            for (String s : splitter.split(path)) {

                Inode child;
                try {
                    child = vfs.lookup(inode, s);
                } catch(ChimeraNFSException e) {
                    if (e.getStatus() == nfsstat.NFSERR_NOENT)
                        child = vfs.create(inode, Stat.Type.DIRECTORY, s, 0, 0, 0777);
                }
            }
        }
    }
}
