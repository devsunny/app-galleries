package com.asksunny.codegen.data;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.generator.AddressHolder;
import com.asksunny.schema.generator.BinaryGenerator;
import com.asksunny.schema.generator.CityGenerator;
import com.asksunny.schema.generator.DateGenerator;
import com.asksunny.schema.generator.DoubleGenerator;
import com.asksunny.schema.generator.EnumGenerator;
import com.asksunny.schema.generator.FirstNameGenerator;
import com.asksunny.schema.generator.ForeignKeyFieldGenerator;
import com.asksunny.schema.generator.FormattedStringGenerator;
import com.asksunny.schema.generator.Generator;
import com.asksunny.schema.generator.IntegerGenerator;
import com.asksunny.schema.generator.LastNameGenerator;
import com.asksunny.schema.generator.LuxuryPriceGenerator;
import com.asksunny.schema.generator.PriceGenerator;
import com.asksunny.schema.generator.RefValueGenerator;
import com.asksunny.schema.generator.SequenceGenerator;
import com.asksunny.schema.generator.SmallIntGenerator;
import com.asksunny.schema.generator.StateGenerator;
import com.asksunny.schema.generator.StreetGenerator;
import com.asksunny.schema.generator.TextGenerator;
import com.asksunny.schema.generator.TimeGenerator;
import com.asksunny.schema.generator.TimestampGenerator;
import com.asksunny.schema.generator.TinyIntGenerator;
import com.asksunny.schema.generator.UIntegerGenerator;
import com.asksunny.schema.generator.ZipGenerator;

public class FieldGeneratorFactory {
	private final static Map<String, List<Generator<?>>> cacheGenerators = new HashMap<>();

	public static synchronized List<Generator<?>> createFieldGenerator(Entity entity) {

		List<Generator<?>> generators = null;
		generators = cacheGenerators.get(entity.getName().toUpperCase());
		if (generators == null) {
			generators = new ArrayList<>();
			List<Field> fields = entity.getFields();
			for (Field field : fields) {
				Generator<?> gen = createFieldGenerator(field);
				generators.add(gen);
			}
			cacheGenerators.put(entity.getName().toUpperCase(), generators);
		}
		return generators;

	}

	public static synchronized Generator<?> createFieldGenerator(Field field) {			
		Generator<?> gen = createExtendFieldGenerator(field);
		if (gen == null) {
			gen = createJdbcFieldGenerator(field);
		}
		return gen;
	}

	public static synchronized Generator<?> createJdbcFieldGenerator(Field field) {
		Generator<?> gen = null;
		switch (field.getJdbcType()) {
		case Types.BIGINT:
		case Types.INTEGER:
			gen = new IntegerGenerator(field);
			break;
		case Types.SMALLINT:
			gen = new SmallIntGenerator();
			break;
		case Types.TINYINT:
			gen = new TinyIntGenerator();
			break;
		case Types.BIT:
			gen = new TinyIntGenerator();
			break;
		case Types.DOUBLE:
		case Types.FLOAT:
		case Types.DECIMAL:
		case Types.REAL:
		case Types.NUMERIC:
			gen = new DoubleGenerator(field);
			break;
		case Types.BINARY:
		case Types.BLOB:
		case Types.VARBINARY:
		case Types.LONGVARBINARY:
			gen = new BinaryGenerator(field);
			break;
		case Types.DATE:
			gen = new DateGenerator(field);
			break;
		case Types.TIME:
			gen = new TimeGenerator(field);
			break;
		case Types.TIMESTAMP:
			gen = new TimestampGenerator(field);
			break;
		default:
			gen = new TextGenerator(field);
			break;
		}

		return gen;
	}

	public static synchronized Generator<?> createExtendFieldGenerator(Field field) {
		Generator<?> gen = null;
		AddressHolder addressHolder = new AddressHolder();

		if (field.getReference() != null) {
			return new ForeignKeyFieldGenerator();
		} else if (field.getDataType() == null) {
			return null;
		}

		switch (field.getDataType()) {
		case SEQUENCE:
			int seqmin = field.getMinValue() == null ? 0 : Integer.valueOf(field.getMinValue());
			int seqstep = field.getStep() == null ? 1 : Integer.valueOf(field.getStep());
			gen = new SequenceGenerator(seqmin, seqstep);
			break;
		case FIRST_NAME:
			gen = new FirstNameGenerator(field);
			break;
		case LAST_NAME:
			gen = new LastNameGenerator(field);
			break;
		case DATE:
			gen = new DateGenerator(field);
			break;
		case TIME:
			gen = new TimeGenerator(field);
			break;
		case TIMESTAMP:
			gen = new TimestampGenerator(field);
			break;
		case FORMATTED_STRING:
			gen = new FormattedStringGenerator(field);
			break;
		case REF:
			gen = new RefValueGenerator(field);
			break;
		case CITY:
			gen = new CityGenerator(field, addressHolder);
			break;
		case STATE:
			gen = new StateGenerator(field, addressHolder);
			break;
		case ZIP:
			gen = new ZipGenerator(field, addressHolder);
			break;
		case STREET:
			gen = new StreetGenerator(field, addressHolder);
			break;
		case SSN:
			field.setFormat("DDD-DD-DDDD");
			gen = new FormattedStringGenerator(field);
			break;
		case UINT:
			gen = new UIntegerGenerator(field);
			break;
		case ULONG:
			gen = new IntegerGenerator(field);
			break;
		case PRICE:
			gen = new PriceGenerator();
			break;
		case LUXURY_PRICE:
			gen = new LuxuryPriceGenerator();
			break;
		case INT:
			gen = new IntegerGenerator(field);
			break;
		case BIGINTEGER:
			gen = new IntegerGenerator(field);
			break;
		case UDOUBLE:
			gen = new DoubleGenerator(field);
			break;
		case UFLOAT:
			gen = new DoubleGenerator(field);
			break;
		case DOUBLE:
			gen = new DoubleGenerator(field);
			break;
		case FLOAT:
			gen = new DoubleGenerator(field);
			break;
		case BINARY:
			gen = new BinaryGenerator(field);
			break;
		case ENUM:
			gen = new EnumGenerator(field);
			break;
		default:
			gen = null;
		}
		return gen;

	}

}
