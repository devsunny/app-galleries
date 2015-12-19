package com.asksunny.schema.dg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.asksunny.codegen.FileNameGenerator;

public class FileGenerator {
	private static final SecureRandom random = new SecureRandom(UUID.randomUUID().toString().getBytes());
	private static final char[] TEXT_CHARS = "!@#$%^&*()_+1234567890-=qwertyuiop{}[]|\\asdfghjkl:;'\"zxcvbnm,<.>/?QWERTYUIOPASDFGHJKLZXCVBNM \n\t"
			.toCharArray();
	private long minSize = 0;
	private long maxSize = 1024 * 1024;
	private long fixedSize = 1024;
	private String[] exts = new String[] { "txt" };
	private long numOfFiles = 1;
	private String outDir = "generated";
	private String namePattern = null;

	public FileGenerator() {

	}

	public void setArgs(String outDir, String namePattern, String numOfFiless, String extss, String fixedSizes,
			String minSizes, String maxSizes) {
		if (outDir != null) {
			this.outDir = outDir;
		}
		if (namePattern != null) {
			this.namePattern = namePattern;
		}
		this.numOfFiles = numOfFiless == null ? 1 : Long.valueOf(numOfFiless);
		this.exts = extss == null ? this.exts : extss.split("\\s*[,;]\\s*");
		this.fixedSize = fixedSizes == null ? 1024L : Long.valueOf(fixedSizes);
		this.minSize = minSizes == null ? 0 : Long.valueOf(minSizes);
		this.maxSize = maxSizes == null ? 0 : Long.valueOf(maxSizes);
	}

	public void genFiles() throws IOException {

		for (long i = 0; i < numOfFiles; i++) {
			Map<String, String> params = new HashMap<String, String>();
			String ext = FileNameGenerator.genExt(exts);
			File f = null;
			do {
				String name = FileNameGenerator.genFileName(namePattern, params);
				f = new File(outDir, String.format("%s.%s", name, ext));
				if (!f.exists()) {
					break;
				} else if (params.get("SEQ") == null) {
					params.clear();
				}
			} while (true);
			genFile(f, ext);
		}

	}

	protected void genFile(File f, String ext) throws IOException {
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}

		long size = maxSize == 0 ? fixedSize : (Math.abs(random.nextLong()) % (maxSize - minSize)) + minSize;

		if (ext.equalsIgnoreCase("zip")) {
			genZip(f, size);
		} else if (ext.equalsIgnoreCase("gz")  || ext.equalsIgnoreCase("gzip")) {
			genGZip(f, size);
		} else if (ext.equalsIgnoreCase("tar.gz")) {
			genTGZ(f, size);
		} else if (ext.equalsIgnoreCase("bz2")) {
			//genBZ2(f, size);
		} else if (ext.equalsIgnoreCase("txt")) {
			genText(f, size);
		} else {
			genBinary(f, size);
		}

	}

	private long ZIP_SIZE_MAX = 1024L * 1024L * 10;

	protected void genZip(File f, long size) throws IOException {
		FileOutputStream fw = new FileOutputStream(f);
		ZipOutputStream zipOut = new ZipOutputStream(fw);
		long count = calnum(size);
		System.out.println("Creaing zip file with mumber of file:" + count);
		try {
			for (long j = 0; j < count; j++) {
				String fileName = String.format("test_%04d.txt", j);
				System.out.println("Zipping:" + fileName);
				ZipEntry ze = new ZipEntry(fileName);
				zipOut.putNextEntry(ze);				
				if (size < ZIP_SIZE_MAX) {
					StringBuilder buf = new StringBuilder();
					for (long k = 0; k < size; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");
					zipOut.write(buf.toString().getBytes());
					System.out.println("Writed to Zip");
				} else {
					StringBuilder buf = new StringBuilder();
					for (long k = 0; k < ZIP_SIZE_MAX; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");
					zipOut.write(buf.toString().getBytes());
					System.out.println("Writed to Zip");
				}				
				zipOut.closeEntry();
			}
			zipOut.flush();
		} finally {
			zipOut.close();
			fw.close();
		}
	}

	private long calnum(long size) {
		return (size / (ZIP_SIZE_MAX)) + 1;
	}

	protected void genGZip(File f, long size) throws IOException 
	{
		FileOutputStream fw = new FileOutputStream(f);
		GZIPOutputStream zipOut = new GZIPOutputStream(fw);
		long count = calnum(size);
		System.out.printf("Creaing gzip file  %s with size of %d:", f.getName(), size);
		try {
			for (long j = 0; j < count; j++) {
				if (size < ZIP_SIZE_MAX) {
					StringBuilder buf = new StringBuilder();
					for (long k = 0; k < size; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");
					zipOut.write(buf.toString().getBytes());
					System.out.println("Writed to GZip");
				} else {
					StringBuilder buf = new StringBuilder();
					for (long k = 0; k < ZIP_SIZE_MAX; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");
					zipOut.write(buf.toString().getBytes());
					System.out.println("Writed to GZip");
				}		
			}
			zipOut.flush();
		} finally {
			zipOut.close();
			fw.close();
		}
	}

	protected void genTGZ(File f, long size) throws IOException {

	}

	

	protected void genText(File f, long size) throws IOException {
		BufferedWriter fw = new BufferedWriter(new FileWriter(f));
		try {
			for (long i = 0; i < size; i++) {
				fw.write(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
			}
		} finally {
			fw.close();
		}

	}

	protected void genBinary(File f, long size) throws IOException {
		FileOutputStream fw = new FileOutputStream(f);
		try {
			for (long i = 0; i < size; i++) {
				fw.write((byte) random.nextInt(256));
			}
		} finally {
			fw.close();
		}
	}

	protected static void usage() {
		System.err.println("Desc : FileGenerator is used to generated meanless files for testing");
		System.err.println("       purpose of file handling\n");
		System.err.println("Usage: FileGenerator <options>...");
		System.err.println("                   -fixedSize  <integer_size> - fixed file size");
		System.err.println("                   -minSize  <integer_size> - for random size file, min filesize");
		System.err.println("                   -maxSize  <integer_size> - for random size file, max file size");
		System.err.println("                   -exts  <ext_csv> - required a list file extension without period '.'");
		System.err.println("                   -n  <integer_number> - number of file to be generated, default 1");
		System.err.println("                   -d <out_dir> - output directory default 'generated'");
		System.err.println(
				"                   -name  <naming_pattern> - required xxx_yyyy_#{DATE}_#{NNNNNN}  default random");
		System.err.println("                           DATE - YYYYMMDD or CCYYMMDD");
		System.err.println("                           TIME - HHMMSS");
		System.err.println("                           TIMESTAMP - YYYYMMDD_HHMMSS");
		System.err.println("                           NNNNN - SEQUENCE number, number of 'N' means number of digit");
	}

	public static void main(String[] args) throws Exception {
		CLIArguments cliArgs = new CLIArguments(args);
		if (cliArgs.getOption("exts") == null || cliArgs.getOption("name") == null) {
			usage();
			return;
		}
		FileGenerator fg = new FileGenerator();
		fg.setArgs(cliArgs.getOption("d"), cliArgs.getOption("name"), cliArgs.getOption("n"), cliArgs.getOption("exts"),
				cliArgs.getOption("fixedSize"), cliArgs.getOption("minSize"), cliArgs.getOption("maxSize"));
		fg.genFiles();
	}

}
