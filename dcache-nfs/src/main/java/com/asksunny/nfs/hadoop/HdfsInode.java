package com.asksunny.nfs.hadoop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.UUID;

import org.dcache.chimera.FileNotFoundHimeraFsException;
import org.dcache.nfs.vfs.FileHandle;
import org.dcache.nfs.vfs.Inode;
import org.dcache.utils.Bytes;

public class HdfsInode extends Inode {

	public static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
	private static final byte[] FH_V0_BIN = { 48, 48, 48, 48 };
	private static final byte[] FH_V0_REG = { 48, 58 };
	private static final byte[] FH_V0_PFS = { 50, 53, 53, 58 };
	private static final int MIN_HANDLE_LEN = 4;

	public static final HashMap<Character, Integer> HEX_DIGITS_MAP = new HashMap<Character, Integer>();
	static {
		for (int i = 0; i < HEX_DIGITS.length; i++) {
			HEX_DIGITS_MAP.put(HEX_DIGITS[i], i);
		}
	}

	private String path;
	private String parentId;
	private int type;
	private int level;
	private int mode;
	private int nlink;
	private int uid;
	private int gid;
	private long size;
	private int io;
	private long ctime;
	private long mtime;
	private long atime;
	private long crtime;
	private int generation;

	public long getCtime() {
		return ctime;
	}

	public void setCtime(long ctime) {
		this.ctime = ctime;
	}

	public long getMtime() {
		return mtime;
	}

	public void setMtime(long mtime) {
		this.mtime = mtime;
	}

	public long getAtime() {
		return atime;
	}

	public long getCrtime() {
		return crtime;
	}

	public void setCrtime(long crtime) {
		this.crtime = crtime;
	}

	public void setAtime(long atime) {
		this.atime = atime;
	}

	public int getGeneration() {
		return generation;
	}

	public void setGeneration(int generation) {
		this.generation = generation;
	}

	public int getLevel() {
		return level;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public int getNlink() {
		return nlink;
	}

	public void setNlink(int nlink) {
		this.nlink = nlink;
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public int getGid() {
		return gid;
	}

	public void setGid(int gid) {
		this.gid = gid;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public int getIo() {
		return io;
	}

	public void setIo(int io) {
		this.io = io;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public HdfsInode(byte[] bytes) {
		super(bytes);
	}

	public HdfsInode(FileHandle h) {
		super(h);
	}

	public UUID getId() {
		return toUUID(toNfsHandle());
	}

	public String getIdString() {
		return toHexString(getFileId());
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public long id() 
	{		
		long id;
		if (getIdString().length() == 24) {
			id = buildPnfsInodeId();
		} else {
			id = buildDecodedAndXoredInodeId();
		}

		return id;
	}

	private long buildPnfsInodeId() {
		int database = Integer.parseInt(this.getIdString().substring(0, 4), 16);
		long high = Long.parseLong(this.getIdString().substring(8, 16), 16);
		long low = Long.parseLong(this.getIdString().substring(16), 16);
		long counter = low >> 3 | (high & 0x7) << 29;
		int reversedDatabase = Integer.reverse(database);
		long id = reversedDatabase ^ counter;
		return id & 0xFFFFFFFF;
	}

	private long buildDecodedAndXoredInodeId() {
		long inodeId = 0L;
		for (int index = 0; index < this.getIdString().length(); index += 8) {
			int endIndex = index + 8;
			if (endIndex > this.getIdString().length()) {
				endIndex = this.getIdString().length();
			}
			String idFragment = this.getIdString().substring(index, endIndex);
			long uint = Long.parseLong(idFragment, 16);
			inodeId ^= uint;
		}

		return inodeId;
	}

	
	public static HdfsInode inodeFromNfsHandle(byte[] handle)
			throws IOException {
		if ((arrayStartsWith(handle, FH_V0_REG))
				|| (arrayStartsWith(handle, FH_V0_PFS)))
			return inodeFromOldNfsHandle(handle);
		if (arrayStartsWith(handle, FH_V0_BIN)) {
			return HdfsInode.forFileHandle(handle);
		}
		return inodeFromNewNfsHandle(handle);
	}

	public static HdfsInode inodeFromNewNfsHandle(byte[] handle)
			throws IOException {
		if (handle.length < MIN_HANDLE_LEN) {
			throw new IOException("File handle too short");
		}
		HdfsInode hinode = null;
		ByteBuffer b = ByteBuffer.wrap(handle);
		@SuppressWarnings("unused")
		int fsid = b.get();
		int type = b.get();
		int idLen = b.get();
		byte[] id = new byte[idLen];
		b.get(id);

		int opaqueLen = b.get();
		if (opaqueLen > b.remaining()) {
			throw new FileNotFoundHimeraFsException("Bad Opaque len");
		}

		byte[] opaque = new byte[opaqueLen];
		b.get(opaque);
		InodeType inodeType = InodeType.valueOf(type);
		String inodeId = toHexString(id);
		switch (inodeType) {
		case INODE:
			int level = opaque.length==0?0:Integer.parseInt(new String(opaque));
			hinode = HdfsInode.forId(inodeId, level);
			break;
		default:
			throw new IOException("Not supported inode type");
		}

		return hinode;
	}

	public static HdfsInode inodeFromOldNfsHandle(byte[] handle)
			throws IOException {
		HdfsInode hinode = null;
		String strHandle = new String(handle);
		StringTokenizer st = new StringTokenizer(strHandle, "[:]");
		if (st.countTokens() < 3) {
			throw new IllegalArgumentException(String.format(
					"Invalid HimeraNFS handler.(%s", strHandle));
		}
		@SuppressWarnings("unused")
		int fsId = Integer.parseInt(st.nextToken());
		String type = st.nextToken();
		InodeType inodetype = InodeType.valueOf(type);
		switch (inodetype) {
		case INODE:
			String id = st.nextToken();
			int level = 0;
			if (st.countTokens() > 0) {
				level = Integer.parseInt(st.nextToken());
			}
			hinode = HdfsInode.forId(id, level);
			break;
		default:
			throw new IOException("Not supported");
		}
		return hinode;

	}

	public static String toHexString(byte[] bytes) {
		StringBuilder idstr = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			byte b = bytes[i];
			int fnibble = (b & 0xF0) >> 4;
			int lnibble = b & 0x0F;
			idstr.append(HEX_DIGITS[fnibble]);
			idstr.append(HEX_DIGITS[lnibble]);
		}
		return idstr.toString();
	}

	public static byte[] toBytes(String uuid) {
		int len = uuid.length();
		if (len % 2 != 0)
			throw new IllegalArgumentException(
					"Hex string has to be even number of characters");
		int alen = len / 2;
		byte[] bytes = new byte[alen];
		for (int i = 0; i < len; i = i + 2) {
			int fnibble = HEX_DIGITS_MAP.get(uuid.charAt(i));
			int lnibble = HEX_DIGITS_MAP.get(uuid.charAt(i + 1));
			bytes[i / 2] = (byte) ((fnibble << 4) | lnibble);
		}
		return bytes;
	}

	public static HdfsInode newInode() {
		return forFileHandle(toInode(UUID.randomUUID()));
	}

	public static HdfsInode id2Inode(UUID id) {
		return forFileHandle(toInode(id));
	}

	public static HdfsInode forId(String id) {
		return forFileHandle(toBytes(id));
	}

	public static HdfsInode forFileHandle(byte[] bytes) {
		return new HdfsInode(new FileHandle.FileHandleBuilder().build(bytes));
	}

	public static HdfsInode forId(String id, int level) {	
		HdfsInode inode = new HdfsInode(toBytes(id));
		inode.setLevel(level);
		return inode;
	}

	protected static byte[] toInode(UUID uuid) {
		byte[] fh = new byte[16];
		Bytes.putLong(fh, 0, uuid.getMostSignificantBits());
		Bytes.putLong(fh, 8, uuid.getLeastSignificantBits());
		return fh;
	}

	protected static UUID toUUID(byte[] data) {
		return new UUID(Bytes.getLong(data, 0), Bytes.getLong(data, 8));
	}

	private static boolean arrayStartsWith(byte[] a1, byte[] a2) {
		if (a1.length < a2.length) {
			return false;
		}
		for (int i = 0; i < a2.length; i++) {
			if (a1[i] != a2[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "HdfsInode [getIdString()=" + getIdString() + ", path=" + path
				+ ", parentId=" + parentId + ", type=" + type + "]";
	}
	
	

}
