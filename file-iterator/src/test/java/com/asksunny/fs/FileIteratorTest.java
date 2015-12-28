package com.asksunny.fs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Base64InputStream;
import org.apache.commons.io.IOUtils;
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
		
		FileReader fin = new FileReader("c:/tmp/data.txt");		
		String text = IOUtils.toString(fin);
		FileOutputStream fout = new FileOutputStream("c:/tmp/data.jar");
		IOUtils.write(Base64.decodeBase64(text), fout);
		fout.flush();
		fout.close();
		fin.close();
	}

}
