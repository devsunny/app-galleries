package com.asksunny.sec;

import java.nio.charset.Charset;
import java.security.MessageDigest;

import com.asksunny.compress.KeyHolder;

public class SymmetricKeyCreator 
{
		
	
	
	    public KeyHolder to128BitKeys(String key) throws Exception
		{
	    	KeyHolder holder = new KeyHolder();
	    	MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashes = md.digest(key.getBytes(Charset.defaultCharset()));
			byte[] kbin = new byte[16];
			byte[] ivbin = new byte[16];
			System.arraycopy(hashes, 0, kbin, 0, kbin.length);
			holder.setKey(kbin);
			System.arraycopy(hashes, hashes.length-ivbin.length, ivbin, 0, ivbin.length);
			holder.setIv(ivbin);
			return holder;
		}
		
		public KeyHolder to256BitKeys(String key) throws Exception
		{
			KeyHolder holder = new KeyHolder();
	    	MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashes = md.digest(key.getBytes(Charset.defaultCharset()));
			byte[] kbin = new byte[32];
			byte[] ivbin = new byte[32];
			System.arraycopy(hashes, 0, kbin, 0, kbin.length);
			holder.setKey(kbin);
			System.arraycopy(hashes, hashes.length-ivbin.length, ivbin, 0, ivbin.length);
			holder.setIv(ivbin);
			return holder;
		}
		
		
}
