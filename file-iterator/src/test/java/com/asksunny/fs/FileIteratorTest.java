package com.asksunny.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class FileIteratorTest {

	@Test
	public void testNext() throws Exception {
		URL url = getClass().getResource("/test1.txt");
		String file = URLDecoder.decode(url.getFile(), "UTF8");
		File dir = new File(file).getParentFile();
		FileIterator iterator = FileIterator.createFileIterator(dir, new FileFilter() {			
			public boolean accept(File pathname) {				
				return pathname.toString().toLowerCase().endsWith("epub");
			}
		});
		
		List<String> names = new ArrayList<>();
		while(iterator.hasNext()){
			names.add(iterator.next().getName());		
		}
		assertEquals(2, names.size());
		assertTrue(names.contains("test.epub"));
		assertTrue(names.contains("test234.epub"));
	}

}
