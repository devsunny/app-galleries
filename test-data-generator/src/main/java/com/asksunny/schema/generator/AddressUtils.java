package com.asksunny.schema.generator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class AddressUtils {

	private static AddressUtils instance = new AddressUtils();

	private List<String> streets = new ArrayList<String>();
	private List<CityStateZip> cities = new ArrayList<CityStateZip>();

	private int maxLen = 100000;

	private AddressUtils() {
		try {
			init();
		} catch (Exception e) {
			throw new RuntimeException("Failed to load data files", e);
		}
		maxLen = streets.size() + cities.size();
	}

	protected void init() throws IOException {
		collectCSZ(cities, "zipcode-database.txt");
		collectStreet(streets, "StreetName.txt");
	}

	public Address getAddress() {

		CityStateZip city = getCity();
		return Address.newAddress(city.getCity(), city.getState(), city.getZip(), getHouseNumber(1, 1000),
				getStreet());

	}

	public int getHouseNumber(int min, int max) {
		int idx = min + RandomUtil.getInstance().getUnsignedInt(max - min);
		return idx;
	}

	public String getStreet() {
		int idx = RandomUtil.getInstance().getUnsignedInt(maxLen) % streets.size();
		return streets.get(idx);
	}

	public CityStateZip getCity() {
		int idx = RandomUtil.getInstance().getUnsignedInt(maxLen) % cities.size();
		return cities.get(idx);
	}

	private void collectStreet(List<String> container, String inputName) throws IOException {
		InputStream in = getClass().getResourceAsStream(String.format("/%s", inputName));
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
				br.readLine();
				String line = null;
				while ((line = br.readLine()) != null) {
					container.add(line);
				}
			} finally {
				in.close();
			}
		} else {
			throw new RuntimeException(String.format("%s file not founr", inputName));
		}

	}

	private void collectCSZ(List<CityStateZip> container, String inputName) throws IOException {

		InputStream in = getClass().getResourceAsStream(String.format("/%s", inputName));
		if (in != null) {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(in, Charset.defaultCharset()));
				br.readLine();
				String line = null;
				while ((line = br.readLine()) != null) {
					String[] ps = line.split("[,]");
					if (ps.length > 4) {
						cities.add(CityStateZip.newCityStateZip(ps[2], ps[3], ps[0], ps[1]));
					}
				}
			} finally {
				in.close();
			}
		} else {
			throw new RuntimeException(String.format("%s file not founr", inputName));
		}

	}

	public static AddressUtils getInstance() {
		return instance;
	}

	public static void main(String[] args) {
		System.out.println(AddressUtils.getInstance().getCity().getZip());
		for (int i = 0; i < 200; i++)
		System.out.println(AddressUtils.getInstance().getAddress());
	}

}
