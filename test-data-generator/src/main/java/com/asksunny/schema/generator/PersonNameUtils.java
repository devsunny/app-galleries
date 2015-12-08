package com.asksunny.schema.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class PersonNameUtils {

	private static PersonNameUtils instance = new PersonNameUtils();

	private List<String> lastNames = new ArrayList<String>();
	private List<String> femaleFirstName = new ArrayList<String>();
	private List<String> maleFirstName = new ArrayList<String>();
	private int maxLen = 100000;

	private PersonNameUtils() {
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load data files", e);
		}
		maxLen = lastNames.size() + femaleFirstName.size() + maleFirstName.size();
	}

	protected void init() throws IOException {
		collect(lastNames, "dist.all.last.txt", 0);
		collect(femaleFirstName, "dist.female.first.txt", 0);
		collect(maleFirstName, "dist.male.first.txt", 0);
	}

	public String getLastName() {
		int idx = RandomUtil.getInstance().getUnsignedInt(maxLen) % lastNames.size();
		return lastNames.get(idx);
	}

	public String getFirstName() {
		int idx = RandomUtil.getInstance().getUnsignedInt(maxLen);
		if (idx % 2 == 0) {
			return femaleFirstName.get(idx % femaleFirstName.size());
		} else {
			return maleFirstName.get(idx % maleFirstName.size());
		}

	}

	public String getMaleFirstName() {
		int idx = RandomUtil.getInstance().getUnsignedInt(maxLen);
		return maleFirstName.get(idx % maleFirstName.size());

	}

	public String getFemaleFirstName() {
		int idx = RandomUtil.getInstance().getUnsignedInt(maxLen);
		return femaleFirstName.get(idx % femaleFirstName.size());
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

	public static PersonNameUtils getInstance() {
		return instance;
	}
	
	public static void main(String[] args){
		System.out.println(PersonNameUtils.getInstance().getLastName());
		System.out.println(PersonNameUtils.getInstance().getFirstName());
		System.out.println(PersonNameUtils.getInstance().getFemaleFirstName());
		System.out.println(PersonNameUtils.getInstance().getMaleFirstName());
	}
	
	

}
