package com.asksunny.schema.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class TextUtils {

	private static TextUtils instance = new TextUtils();

	private List<String> words = new ArrayList<String>();
	private int maxLen = 100000000;

	private TextUtils() {
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load data files", e);
		}
		maxLen = words.size();
	}

	protected void init() throws IOException {
		collect(words, "data.adj", 4);
		collect(words, "data.adv", 4);
		collect(words, "data.noun", 4);
		collect(words, "data.verb", 4);
	}

	public String getText(int minSize, int maxSize) {
		StringBuilder buf = new StringBuilder();		
		int size = minSize + RandomUtil.getInstance().getUnsignedInt(maxSize - minSize);
		int len = 0;
		while (buf.length() < size) {
			if (buf.length() > 0) {
				len = buf.length();
				if (RandomUtil.getInstance().getUnsignedInt(maxLen) % 7 == 0) {
					buf.append(",");
				} else if (RandomUtil.getInstance().getUnsignedInt(maxLen) % 9 == 0) {
					buf.append(";");
				} else if (RandomUtil.getInstance().getUnsignedInt(maxLen) % 13 == 0) {
					buf.append(".");
				}
				buf.append(" ");
			}
			int index = RandomUtil.getInstance().getUnsignedInt(maxLen);
			buf.append(words.get(index));
		}
		if (buf.length() > maxSize) {
			buf.delete(len, buf.length() - 1);
		}
		return buf.toString();
	}

	private void collect(List<String> container, String inputName, int index) throws IOException {

		InputStream in = getClass().getResourceAsStream(String.format("/%s", inputName));
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] ps = line.split("\\s+");
					if (ps != null && ps.length > index) {
						container.add(ps[index]);
					}
				}
			} finally {
				in.close();
			}
		} else {
			throw new RuntimeException(String.format("%s file not founr", inputName));
		}

	}

	public static TextUtils getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		for (int i = 0; i < 200; i++)
			System.out.println(TextUtils.getInstance().getText(10, 200));
	}

}
