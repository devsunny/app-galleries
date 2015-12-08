package com.asksunny.schema.sample;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.asksunny.schema.DataGenType;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;
import com.asksunny.schema.ValueRandomType;

public class SampleSchemaCreator {

	public static Schema newSampleSchema() {
		Schema sample = new Schema("sample");

		Entity account = new Entity("Account");
		account.addField(Field.newField(Types.INTEGER, 0, 16, 16, false, "id", DataGenType.SEQUENCE, "1", null, null,
				null, ValueRandomType.FALSE));

		account.addField(Field.newField(Types.VARCHAR, 0, 64, 64, false, "fname", DataGenType.FIRST_NAME, "1", null,
				null, null, ValueRandomType.FALSE));
		account.addField(Field.newField(Types.VARCHAR, 0, 64, 64, false, "lname", DataGenType.LAST_NAME, "1", null,
				null, null, ValueRandomType.FALSE));
		account.addField(Field.newField(Types.DATE, 0, 16, 16, false, "DOB", DataGenType.DATE, "1900-01-01",
				"2015-12-31", "yyyy-MM-dd", null, ValueRandomType.TRUE));
		account.addField(Field.newField(Types.INTEGER, 0, 16, 16, false, "house_number", DataGenType.UINT, "1", "1000",
				null, null, ValueRandomType.TRUE));
		account.addField(Field.newField(Types.VARCHAR, 0, 128, 128, false, "street", DataGenType.STREET, "1", "1000",
				null, null, ValueRandomType.TRUE));

		account.addField(Field.newField(Types.VARCHAR, 0, 64, 64, false, "city", DataGenType.CITY, "1", "1000", null,
				null, ValueRandomType.TRUE));

		account.addField(Field.newField(Types.VARCHAR, 0, 2, 2, false, "state", DataGenType.STATE, "1", "1000", null,
				null, ValueRandomType.TRUE));

		account.addField(Field.newField(Types.VARCHAR, 0, 10, 10, false, "zip", DataGenType.ZIP_US, "1", "1000", null,
				null, ValueRandomType.TRUE));

		account.addField(Field.newField(Types.VARCHAR, 0, 11, 11, false, "SSN", DataGenType.SSN, "1", "1000", null,
				null, ValueRandomType.TRUE));

		account.addField(Field.newField(Types.VARCHAR, 0, 11, 11, false, "SACCNT", DataGenType.FORMATTED_STRING, "1",
				"1000", "XDX-DDD-DD-DDDD", null, ValueRandomType.TRUE));

		Entity order = new Entity("Order");
		order.addField(Field.newField(Types.INTEGER, 0, 16, 16, false, "order_id", DataGenType.SEQUENCE, "1", null,
				null, null, ValueRandomType.FALSE));
		Field field = Field.newField(Types.INTEGER, 0, 16, 16, false, "account_id", DataGenType.REF, null, null, null,
				null, ValueRandomType.FALSE);
		field.setReference(account.getFields().get(0));
		order.addField(field);
		order.addField(Field.newField(Types.TIMESTAMP, 0, 16, 16, false, "created_date", DataGenType.DATE,
				"2000-01-01 00:00:00", "2015-12-31 23:59:59", "yyyy-MM-dd HH:mm:ss", null, ValueRandomType.TRUE));
		order.addField(Field.newField(Types.DOUBLE, 2, 10, 12, false, "total_price", DataGenType.UDOUBLE, "1", null,
				"yyyy-MM-dd HH:mm:ss", null, ValueRandomType.TRUE));
		sample.put(account.getName(), account);
		sample.put(order.getName(), order);

		return sample;

	}

	public static void main(String[] args) {

	}

}
