package com.asksunny.nfs.hadoop;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.sql.DataSource;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.dcache.nfs.v4.SimpleIdMap;
import org.dcache.nfs.vfs.Inode;
import org.dcache.nfs.vfs.Stat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class HdfsVfsTest {

	private final static Logger LOG = LoggerFactory.getLogger(HdfsVfsTest.class);
	private HdfsVfs hdfsVfs = null;
	private ApplicationContext context = null;
	
	
	@Before
	public void setUp() throws Exception {
		context = new ClassPathXmlApplicationContext(
			"jdbc-context.xml");
		DataSource ds = context.getBean("dataSource", DataSource.class);
		NFSInodeMetaStorage driver = new NFSInodeMetaStorage(ds);
		String hadoopHome = System.getenv("HADOOP_HOME");
		Path p = new Path(hadoopHome, "etc/hadoop");
		Configuration conf = new Configuration();
		conf.addResource(new Path(p, "core-site.xml"));
		conf.addResource(new Path(p, "hdfs-site.xml"));
		conf.addResource(new Path(p, "yarn-site.xml"));
		hdfsVfs = new HdfsVfs(driver, new SimpleIdMap(), new Path(hadoopHome), new Path("/exports"), "exports");		
	}

	@After
	public void tearDown() throws Exception {
		hdfsVfs = null;		
	}

	@Test
	public void testGetRootInode() throws IOException {
		Inode inode = hdfsVfs.getRootInode();
		assertTrue(inode instanceof HdfsInode);
		HdfsInode hdfsInode = (HdfsInode)inode;
		assertEquals(NFSConstants.NFS_ROOT_INODE_ID, hdfsInode.getIdString());
	}
	
	
	@Test
	public void testCreate() throws IOException{
		//Inode inode = hdfsVfs.getRootInode();
		//hdfsVfs.create(inode, Stat.Type.fromMode(Stat.S_IFREG), "test.txt", uid, gid, mode);
		
	}

	
	@Test
	public void testMkdir() throws IOException{
		Inode pinode = hdfsVfs.getRootInode();
		Inode cnode = hdfsVfs.mkdir(pinode, "data", 100, 100, 744);
		assertNotNull(cnode);
		assertTrue(cnode instanceof HdfsInode);
		HdfsInode hdfsNode = (HdfsInode)cnode;
		LOG.info("testMkdir:{}", hdfsNode.getIdString());		
		
		hdfsVfs.create(cnode, Stat.Type.fromMode(Stat.S_IFREG), "test.txt", 500, 500, 755);
		HdfsInode hdfsNode1 = (HdfsInode) hdfsVfs.lookup(cnode, "test.txt");
		assertNotNull(hdfsNode1);
		assertEquals("/data/test.txt", hdfsNode1.getPath());	
		hdfsVfs.remove(cnode, "test.txt");
		
		
		HdfsInode hdfsNode2 = (HdfsInode) hdfsVfs.lookup(pinode, "data");
		assertNotNull(hdfsNode2);
		assertEquals("/data", hdfsNode2.getPath());		
		hdfsVfs.remove(pinode, "data");
		
	}
	
	
	
	@Test
	public void testCheckAcl() {
		fail("Not yet implemented");
	}

	@Test
	public void testAccess() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testGetFsStat() {
		fail("Not yet implemented");
	}

	
	@Test
	public void testLink() {
		fail("Not yet implemented");
	}

	@Test
	public void testList() {
		fail("Not yet implemented");
	}

	

	@Test
	public void testMove() {
		fail("Not yet implemented");
	}

	@Test
	public void testParentOf() {
		fail("Not yet implemented");
	}

	@Test
	public void testRead() {
		fail("Not yet implemented");
	}

	@Test
	public void testReadlink() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemove() {
		fail("Not yet implemented");
	}

	@Test
	public void testSymlink() {
		fail("Not yet implemented");
	}

	@Test
	public void testWrite() {
		fail("Not yet implemented");
	}

	@Test
	public void testCommit() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetattr() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetattr() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAcl() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetAcl() {
		fail("Not yet implemented");
	}

	@Test
	public void testHasIOLayout() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAclCheckable() {
		fail("Not yet implemented");
	}

}
