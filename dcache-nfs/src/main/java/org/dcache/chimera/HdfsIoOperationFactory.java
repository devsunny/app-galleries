package org.dcache.chimera;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
	private final HadoopHdfsDriver _fs;
	
	public HdfsIoOperationFactory(HadoopHdfsDriver fs) {
		_fs = fs;
	}

	@Override
	public AbstractNFSv4Operation getOperation(nfs_argop4 op) {
		if (_log.isInfoEnabled())
			_log.info("getOperation:{}", getOpName(op.argop));
		AbstractNFSv4Operation operationImpl = null;
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
	
	private static String getOpName(int opv)
	{
		String name = "OP_Unknown";
		
		Field[] flds = nfs_opnum4.class.getFields();
		for (Field field : flds) {
			if((field.getModifiers() & Modifier.STATIC) >0 ){
				try {
					int val = field.getInt(null);
					if(val == opv){
						name = field.getName();
						return name;
					}
				} catch (IllegalArgumentException e) {					
					_log.warn("IllegalArgumentException", e);
				} catch (IllegalAccessException e) {
					_log.warn("IllegalAccessException", e);
				}
			}
		}
		return name;
	}
	

}
