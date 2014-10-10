package com.asksunny.compress;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.AlgorithmParameters;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.anarres.lzo.LzopOutputStream;
import org.anarres.lzo.hadoop.codec.LzopInputStream;
import org.apache.commons.compress.compressors.CompressorStreamFactory;
import org.apache.commons.io.IOUtils;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

import com.asksunny.sec.SymmetricKeyCreator;

public class StreamCompressor {

	public static enum Mode {
		COMPRESS, DECOMPRESS
	};

	public static enum Type {
		LZO, SNAPPY, BZIP2, GZIP
	};

	private Mode mode = Mode.COMPRESS;
	private String key = null;
	private String keyAlgo = "AES";
	private String cipherAlgo = "AES/CBC/PKCS5Padding";
	private Type compressType = Type.LZO;
	

	public StreamCompressor(Mode mode, Type compressType, String key) {
		super();
		this.mode = mode;
		this.compressType = compressType;
		this.key = key;
	}

	public void doCompressAction(InputStream in, OutputStream out)
			throws Exception {

		if (mode == Mode.COMPRESS) {
			compress( in,  out);
		} else {
			decompress( in,  out);
		}

	}

	public void compress(InputStream in, OutputStream out) throws Exception {
		OutputStream cout = out;

		CompressorStreamFactory factory = new CompressorStreamFactory();
		if (compressType == Type.BZIP2) {
			cout = factory.createCompressorOutputStream(
					CompressorStreamFactory.BZIP2, cout);
		} else if (compressType == Type.SNAPPY) {
			cout = new SnappyOutputStream(cout);
		} else if (compressType == Type.LZO) {
			cout = new LzopOutputStream(
					cout,
					org.anarres.lzo.hadoop.codec.LzoCompressor.CompressionStrategy.LZO1X_1
							.newCompressor());
		} else {
			cout = factory.createCompressorOutputStream(
					CompressorStreamFactory.GZIP, cout);
		}

		if (key != null) {
			SymmetricKeyCreator kbinc = new SymmetricKeyCreator();
			KeyHolder holder = kbinc.to128BitKeys(getKey());
			SecretKey key = new SecretKeySpec(holder.getKey(),
					keyAlgo);
			Cipher cipher = Cipher.getInstance(cipherAlgo);
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(holder.getIv()));
			cout = new CipherOutputStream(cout, cipher);
		}
		IOUtils.copy(in, cout);
		out.flush();
	}

	public void decompress(InputStream in, OutputStream out) throws Exception {
		InputStream cin = in;
		CompressorStreamFactory factory = new CompressorStreamFactory();
		if (compressType == Type.BZIP2) {
			cin = factory.createCompressorInputStream(
					CompressorStreamFactory.BZIP2, cin);
		} else if (compressType == Type.SNAPPY) {
			cin = new SnappyInputStream(cin);
		} else if (compressType == Type.LZO) {
			cin = new LzopInputStream(cin);
		} else {
			cin = factory.createCompressorInputStream(
					CompressorStreamFactory.GZIP, cin);
		}

		if (key != null) {
			SymmetricKeyCreator kbinc = new SymmetricKeyCreator();
			KeyHolder holder = kbinc.to128BitKeys(getKey());
			SecretKey key = new SecretKeySpec(holder.getKey(),
					keyAlgo);
			Cipher cipher = Cipher.getInstance(cipherAlgo);			
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(holder.getIv()));
			cin = new CipherInputStream(cin, cipher);
		}		
		IOUtils.copy(cin, out);
		out.flush();
	}

	public Mode getMode() {
		return mode;
	}

	public void setMode(Mode mode) {
		this.mode = mode;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Type getCompressType() {
		return compressType;
	}

	public void setCompressType(Type compressType) {
		this.compressType = compressType;
	}

	
	
}
