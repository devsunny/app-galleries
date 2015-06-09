package com.asksunny.netty.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandlerFactory;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

public class NestedJarClassloader extends URLClassLoader {

	private static String tmpDir = System.getProperty("java.io.tmpdir") ;
	
	public NestedJarClassloader(URL[] urls) {
		super(inspect(urls));
	}

	public NestedJarClassloader(URL[] urls, ClassLoader parent) {
		super(inspect(urls), parent);		
	}

	public NestedJarClassloader(URL[] urls, ClassLoader parent,
			URLStreamHandlerFactory factory) {
		super(inspect(urls), parent, factory);		
	}

	
	protected static URL[] inspect(URL[] urls) {		
		
		List<URL> ret  =new ArrayList<>();
		
		File dir = new File(tmpDir, ".lib");
		if(!dir.exists()){
			dir.mkdirs();
		}		
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			try {
				File f = new File(url.toURI());
				ZipFile zf = new ZipFile(f);
				Enumeration<? extends ZipEntry> entries = zf.entries();
				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();
					if (!entry.isDirectory()) {
						String name = entry.getName();
						if (name.endsWith(".jar")) {
							File jf = new File(dir, name);
							if(!jf.exists()){
								FileOutputStream fout = new FileOutputStream(jf);
								IOUtils.copy(zf.getInputStream(entry), fout);
								fout.flush();
								fout.close();
								System.out.println(jf);
								ret.add(jf.toURI().toURL());
							}
						}
					}
				}
				zf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}		
		URL[] urlsr = new URL[ret.size()];
		return ret.toArray(urlsr);
	}
	
	
	

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File f = new File("C:\\bigdata\\hadoop-2.6.0.2.2.0.0-2041\\share\\hadoop\\common\\test\\test.jar");		
		NestedJarClassloader cl = new NestedJarClassloader(new URL[]{f.toURI().toURL()}, NestedJarClassloader.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(cl);
		Class<?> t =  Class.forName("org.apache.avro.file.Codec", false, cl);		
		System.out.println(t.getClass().getName());
		
	}

}
