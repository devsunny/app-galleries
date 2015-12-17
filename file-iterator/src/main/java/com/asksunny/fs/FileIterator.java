package com.asksunny.fs;

import java.io.File;
import java.io.FileFilter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This is hack to make FileIterator to work with unlimit sized directory;
 * 
 * @author SunnyLiu
 *
 */

public final class FileIterator implements Iterator<File>, UncaughtExceptionHandler {

	private final File dir;
	private final FileFilter filter;
	private final AtomicInteger counter = new AtomicInteger(0);
	private final AtomicBoolean endOfLoop = new AtomicBoolean(false);
	private final IteratorFileHolder fileHolder = new IteratorFileHolder();
	private Throwable throwable = null;
	private final AtomicBoolean interruptIterator = new AtomicBoolean(false);

	private FileIterator(File dirp, FileFilter filterp) {
		this.dir = dirp;
		this.filter = filterp;
		if (dir != null && dir.exists() && dir.isDirectory()) {
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						dir.listFiles(new FileFilter() {
							public boolean accept(File pathname) {
								if (interruptIterator.get()) {
									throw new IteratorAbortException();
								}
								if (filter.accept(pathname)) {
									fileHolder.setFile(pathname);
									counter.incrementAndGet();
									while (!(counter.get() == 0)) {
										try {
											if (interruptIterator.get()) {
												throw new IteratorAbortException();
											}
											Thread.sleep(50);
										} catch (InterruptedException e) {
											;
											;
										}
									}
								}
								return false;
							}
						});
					} catch (IteratorAbortException e) {
						;
					}
					endOfLoop.set(true);
				}
			});
			t.setName("FileIterator Internal Thread");
			t.setUncaughtExceptionHandler(this);
			t.start();
		} else {
			endOfLoop.set(true);
		}

	}

	public static FileIterator createFileIterator(File dir, FileFilter filter) {
		return new FileIterator(dir, filter);
	}

	public boolean hasNext() {
		if (this.throwable != null) {
			Throwable e = this.throwable;
			this.throwable = null;
			new RuntimeIOException("Failed to iterate through directory:" + this.dir.toString(), e);
		}
		if (!endOfLoop.get()) {
			while (!(counter.get() > 0)) {
				try {
					if (endOfLoop.get()) {
						break;
					}
					Thread.sleep(50);
				} catch (InterruptedException e) {
					;
				}
			}
		}
		return fileHolder.getFile() != null;
	}

	public File next() {
		File ret = fileHolder.getFile();
		fileHolder.setFile(null);
		counter.decrementAndGet();
		return ret;
	}

	public void abort() {
		this.interruptIterator.set(true);
	}

	public void uncaughtException(Thread t, Throwable e) {
		if (e != null) {
			this.throwable = e;
			;
		}
	}

	@Override
	protected void finalize() throws Throwable {
		interruptIterator.compareAndSet(false, true);
		super.finalize();
	}

	@Override
	public void remove() {
		throw new IllegalStateException("Remove Operation is not allowed for FileIterator");
	}

	public File getDir() {
		return dir;
	}

}
