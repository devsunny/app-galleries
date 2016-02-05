package org.apache.solr.core;

import java.io.IOException;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockFactory;
import org.apache.solr.util.plugin.SolrCoreAware;

public class TfsDirectoryFactory extends CachingDirectoryFactory implements SolrCoreAware{

	@Override
	public void inform(SolrCore core) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected Directory create(String path, LockFactory lockFactory, DirContext dirContext) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected LockFactory createLockFactory(String rawLockType) throws IOException {
		
		
		
		return null;
	}

	@Override
	public boolean isPersistent() {
		
		return false;
	}

}
