package com.asksunny.codegen.file;

import java.io.BufferedOutputStream;
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

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;

import com.asksunny.CLIArguments;
import com.asksunny.codegen.utils.FileNameGenerator;

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
			System.out.printf("File [%d] %s created\n", i+1, f.toString());
		}

	}

	protected void genFile(File f, String ext) throws IOException {
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		long size = maxSize == 0 ? fixedSize : (Math.abs(random.nextLong()) % (maxSize - minSize)) + minSize;
		if (ext.equalsIgnoreCase("zip")) {
			genZip(f, size);
		} else if (ext.equalsIgnoreCase("gz") || ext.equalsIgnoreCase("gzip")) {
			genGZip(f, size);
		} else if (ext.equalsIgnoreCase("tar.gz") || ext.equalsIgnoreCase("tgz")) {
			genTGZ(f, size);
		} else if (ext.equalsIgnoreCase("tar")) {
			genTAR(f, size);
		} else if (ext.equalsIgnoreCase("bz2")) {
			genBZ2(f, size);
		} else if (ext.equalsIgnoreCase("txt")) {
			genText(f, size);
		} else {
			genBinary(f, size);
		}

	}

	protected void genBZ2(File f, long size) throws IOException {
		FileOutputStream fw = new FileOutputStream(f);
		BufferedOutputStream bout = new BufferedOutputStream(fw);
		BZip2CompressorOutputStream bzout = new BZip2CompressorOutputStream(bout);
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
					bzout.write(buf.toString().getBytes());
					System.out.println("Writed to BZip2");
				} else {
					StringBuilder buf = new StringBuilder();
					for (long k = 0; k < ZIP_SIZE_MAX; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");
					bzout.write(buf.toString().getBytes());
					System.out.println("Writed to BZip2");
				}
			}
			bzout.flush();
			bout.flush();
		} finally {
			bzout.close();
			bout.close();
			fw.close();
		}

	}

	private long ZIP_SIZE_MAX = 1024L * 1024L * 10;

	private long calnum(long size) {
		return (size / (ZIP_SIZE_MAX)) + 1;
	}

	protected void genGZip(File f, long size) throws IOException {
		FileOutputStream fw = new FileOutputStream(f);
		BufferedOutputStream bout = new BufferedOutputStream(fw);
		GZIPOutputStream zipOut = new GZIPOutputStream(bout);
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
			bout.flush();
		} finally {
			zipOut.close();
			bout.close();
			fw.close();
		}
	}

	protected void genZip(File f, long size) throws IOException {
		FileOutputStream fw = new FileOutputStream(f);
		ZipOutputStream zipOut = new ZipOutputStream(fw);
		long count = calnum(size);
		System.out.println("Creaing zip file with mumber of file:" + count);
		try {
			for (long j = 0; j < count; j++) {
				String fileName = String.format("test_%04d.txt", j);
				System.out.printf("Add %s to Zip file %s\n", fileName, f.getAbsolutePath());
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

	protected void genTAR(File f, long size) throws IOException {
		FileOutputStream fw = new FileOutputStream(f);
		BufferedOutputStream bout = new BufferedOutputStream(fw);
		TarArchiveOutputStream tOut = new TarArchiveOutputStream(bout);
		long count = calnum(size);
		System.out.println("Creaing tar.gzip file with mumber of file:" + count);
		try {
			for (long j = 0; j < count; j++) {
				String fileName = String.format("test_%04d.txt", j);
				System.out.printf("Add %s to TAR file %s\n", fileName, f.getAbsolutePath());
				StringBuilder buf = new StringBuilder();
				if (size < ZIP_SIZE_MAX) {	
					for (long k = 0; k < size; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");
					tOut.write(buf.toString().getBytes());
					System.out.println("Writed to TAR file");
				} else {
					for (long k = 0; k < ZIP_SIZE_MAX; k++) {
						buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
					}
					System.out.println("Text created");					
				}
				TarArchiveEntry entry = new TarArchiveEntry(fileName);
				byte[] cnt = buf.toString().getBytes();
				entry.setSize(cnt.length);
				tOut.putArchiveEntry(entry);	
				tOut.write(cnt);
				System.out.println("Writed to TAR file");
				tOut.flush();
				tOut.closeArchiveEntry();
			}
			bout.flush();
		} finally {			
			tOut.close();
			bout.close();
			fw.close();
		}

	}

	protected void genTGZ(File f, long size) throws IOException {
		System.out.printf("Creaing tar.gzip %s file with size %d:\n", f, size);
		FileOutputStream fw = new FileOutputStream(f);
		BufferedOutputStream bout = new BufferedOutputStream(fw);
		GzipCompressorOutputStream gzOut = new GzipCompressorOutputStream(bout);
		TarArchiveOutputStream tOut = new TarArchiveOutputStream(gzOut);
		long count = calnum(size);
		System.out.println("Creaing tar.gzip file with mumber of file:" + count);
		try {
			for (long j = 0; j < count; j++) {
				String fileName = String.format("test_%04d.txt", j);
				System.out.printf("Add %s to TAR GZIP file %s\n", fileName, f.getAbsolutePath());
				StringBuilder buf = new StringBuilder();
				for (long k = 0; k < ZIP_SIZE_MAX; k++) {
					buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
				}
				System.out.println("Text created");		
				TarArchiveEntry entry = new TarArchiveEntry(fileName);
				byte[] cnt = buf.toString().getBytes();
				entry.setSize(cnt.length);
				tOut.putArchiveEntry(entry);	
				tOut.write(cnt);
				System.out.println("Writed to TGZip");
				tOut.flush();
				tOut.closeArchiveEntry();
			}
			gzOut.flush();
			bout.flush();
		} finally {			
			tOut.close();
			gzOut.close();
			bout.close();
			fw.close();
		}

	}

	protected void genText(File f, long size) throws IOException {
		System.out.printf("Creaing text %s file with size %d:\n", f, size);
		BufferedWriter fw = new BufferedWriter(new FileWriter(f));
		try {
			StringBuilder buf = new StringBuilder();
			long wlen = 0;
			for (long i = 0; i < size; i++) {
				buf.append(TEXT_CHARS[Math.abs(random.nextInt(Integer.MAX_VALUE)) % TEXT_CHARS.length]);
				wlen++;
				if(wlen%ZIP_SIZE_MAX==0){
					fw.write(buf.toString());
					buf.setLength(0);
					wlen = 0;
				}				
			}
			if(wlen>0){
				fw.write(buf.toString());
				buf.setLength(0);
				wlen = 0;
			}
			fw.flush();
		} finally {
			fw.close();
		}

	}

	protected void genBinary(File f, long size) throws IOException {
		System.out.printf("Creaing binary %s file with size %d:\n", f, size);
		FileOutputStream fw = new FileOutputStream(f);
		BufferedOutputStream bout = new BufferedOutputStream(fw);
		try {
			long count = calnum(size)-1;			
			for (long i = 0; i < count; i++) {
				byte[]  bar = new byte[(int)ZIP_SIZE_MAX];
				for (int k = 0; k < ZIP_SIZE_MAX ; k++) {
					bar[k] = (byte) random.nextInt(256);
				}
				bout.write(bar);
			}
			int left  = (int)(size % ZIP_SIZE_MAX);
			if(left >0)
			{
				byte[]  bar = new byte[left];
				for (int k = 0; k < left ; k++) {
					bar[k] = (byte) random.nextInt(256);
				}
				bout.write(bar);
			}			
			bout.flush();
		} finally {
			bout.close();
			fw.close();
		}
	}

	public static void usage() {
		System.err.println("Desc : FileGenerator is used to generated meanless files for testing");
		System.err.println("       purpose of file handling\n");
		System.err.println("Usage: FileGenerator <options>...");
		System.err.println("       Required:");		
		System.err.println("                   -exts  <ext_csv> - required a list file extension without period '.'");
		System.err.println("                           zip, gz, tgz, tar.gz, bz2, and txt are functionally supported.");
		System.err.println("                           Anything else is tream as binary format.");
		System.err.println("                   -name  <naming_pattern> - required xxx_yyyy_#{DATE}_#{NNNNNN}  default random");
		System.err.println("                           DATE - YYYYMMDD or CCYYMMDD");
		System.err.println("                           TIME - HHMMSS");
		System.err.println("                           TIMESTAMP - YYYYMMDD_HHMMSS");
		System.err.println("                           NNNNN - SEQUENCE number, number of 'N' means number of digit");
		System.err.println("       Optional:");			
		System.err.println("                   -n  <integer_number> - number of file to be generated, default 1");
		System.err.println("                   -fixedSize  <integer_size> - fixed file size");
		System.err.println("                   -minSize  <integer_size> - for random size file, min filesize");
		System.err.println("                   -maxSize  <integer_size> - for random size file, max file size");
		System.err.println("                   -d <out_dir> - output directory default 'generated'");
		System.err.println("examples:");		
		System.err.println("         FileGenerator -exts zip,tiff,tgz,bz2 -name myprefix_MB_loan_#{DATE}_#{NNN} -fixedSize 1000000");
		System.err.println("         FileGenerator -exts zip -name CCB_DMA_ICDW_#{TIMESTAMP} -fixedSize 1000000 -n 10000000");
		
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
