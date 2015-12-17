import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public final class MultiDirFileIterator implements Iterator<File> {

	private final List<FileIterator> fileIterators = (List<FileIterator>) Collections
			.synchronizedList(new ArrayList<FileIterator>());
	private final AtomicInteger currentPos = new AtomicInteger(0);
	private final AtomicInteger fileIteratorsSize = new AtomicInteger(0);
	private final FileFilter filter;
	private final Lock iteratorLock = new ReentrantLock();
	private final FileReferenceHolder fileHolder = new FileReferenceHolder();

	public ConcurrentMultiDirFileIterator(FileFilter filterp) {
		this.filter = filterp;
	}

	public synchronized void addDirectory(File dir) {
		if (dir == null || !dir.isDirectory()) {
			throw new IllegalArgumentException("Only aacept directory");
		}
		fileIterators.add(FileIterator.createFileIterator(dir, this.filter));
		fileIteratorsSize.incrementAndGet();
	}

	@Override
	public boolean hasNext() {
		fileHolder.setFile(getNext());
		return fileHolder.getFile() != null;
	}

	public void tryLock() {
		this.iteratorLock.lock();
	}

	protected File getNext() {
		File f = null;
		for (int i = 0; i < fileIteratorsSize.get(); i++) {
			FileIterator fiter = null;
			if (fileIteratorsSize.get() > 0) {
				currentPos.set(currentPos.get() % fileIteratorsSize.get());
			}
			if (currentPos.get() < fileIteratorsSize.get()) {
				fiter = fileIterators.get(currentPos.getAndIncrement());
			}
			if (fiter == null) {
				continue;
			}
			if (fiter.hasNext()) {
				f = fiter.next();
				break;
			} else {
				fiter = FileIterator.createFileIterator(fiter.getDir(),
						this.filter);
				fileIterators
						.set((currentPos.get() - 1) % fileIteratorsSize.get(),
								fiter);
			}
		}
		return f;
	}

	@Override
	public File next() {
		File f = fileHolder.getFile();
		return f;
	}

	public void unLock() {
		this.iteratorLock.unlock();
	}

	public void abort() {
		for (FileIterator fiter : this.fileIterators) {
			fiter.abort();
		}
		iteratorLock.unlock();
	}

	@Override
	public void remove() {
		throw new IllegalStateException(
				"Remove Operation is not allowed for FileIterator");
	}

}
