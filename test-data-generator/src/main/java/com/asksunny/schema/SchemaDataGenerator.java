package com.asksunny.schema;

import java.util.ArrayList;
import java.util.List;

import com.asksunny.schema.generator.AddressHolder;
import com.asksunny.schema.generator.CityGenerator;
import com.asksunny.schema.generator.DateGenerator;
import com.asksunny.schema.generator.FirstNameGenerator;
import com.asksunny.schema.generator.FormattedStringGenerator;
import com.asksunny.schema.generator.Generator;
import com.asksunny.schema.generator.LastNameGenerator;
import com.asksunny.schema.generator.SequenceGenerator;
import com.asksunny.schema.generator.StateGenerator;
import com.asksunny.schema.generator.StreetGenerator;
import com.asksunny.schema.generator.TextGenerator;
import com.asksunny.schema.generator.TimeGenerator;
import com.asksunny.schema.generator.TimestampGenerator;
import com.asksunny.schema.generator.UDoubleGenerator;
import com.asksunny.schema.generator.UIntegerGenerator;
import com.asksunny.schema.generator.ULongGenerator;
import com.asksunny.schema.generator.ZipGenerator;
import com.asksunny.schema.sample.SampleSchemaCreator;

public class SchemaDataGenerator {

	private SchemaOutputType outputType;
	private String outputUri;
	private String schemaUri;
	private Schema schema;
	private long numberOfRecords;

	protected void generateData() {
		List<Entity> entityes = schema.getIndependentEntities();
		for (Entity entity : entityes) {
			System.out.println(entity.getName());
			 generateData(entity, creatorFieldGenerator(entity));
		}

	}

	protected void generateData(Entity entity, List<Generator<?>> generators) {

		for (long i = 0; i < numberOfRecords; i++) {
			for (Generator<?> gen : generators) {
				System.out.print(gen.nextStringValue());
				System.out.print(",");
			}
			System.out.println();
		}

	}

	protected List<Generator<?>> creatorFieldGenerator(Entity entity) {

		List<Generator<?>> generators = new ArrayList<>();
		List<Field> fields = entity.getFields();
		for (Field field : fields) {
			generators.add(creatorFieldGenerator(field));
		}
		return generators;

	}

	protected Generator<?> creatorFieldGenerator(Field field) {
		Generator<?> gen = null;
		AddressHolder addressHolder = new AddressHolder();
		switch (field.getDataType()) {
		case SEQUENCE:
			int seqmin = field.getMinValue() == null ? 0 : Integer.valueOf(field.getMinValue());
			int seqstep = field.getStep() == null ? 1 : Integer.valueOf(field.getStep());
			gen = new SequenceGenerator(seqmin, seqstep);
			break;
		case FIRST_NAME:
			gen = new FirstNameGenerator(field.isNullable());
			break;
		case LAST_NAME:
			gen = new LastNameGenerator(field.isNullable());
			break;
		case DATE:
			gen = new DateGenerator(field.getMinValue(), field.getMaxValue(), field.getFormat());
			break;
		case TIME:
			gen = new TimeGenerator(field.getMinValue(), field.getMaxValue(), field.getFormat());
			break;
		case TIMESTAMP:
			gen = new TimestampGenerator(field.getMinValue(), field.getMaxValue(), field.getFormat());
			break;
		case FORMATTED_STRING:
			gen = new FormattedStringGenerator(field.getFormat(), field.isNullable());
			break;
		case CITY:
			gen = new CityGenerator(addressHolder);
			break;
		case STATE:
			gen = new StateGenerator(addressHolder);
			break;
		case ZIP_US:
			gen = new ZipGenerator(addressHolder);
			break;
		case STREET:
			gen = new StreetGenerator(addressHolder);
			break;
		case SSN:
			gen = new FormattedStringGenerator("DDD-DD-DDDD", field.isNullable());
			break;
		case UINT:
			int uimax = field.getMaxValue() == null ? 0 : Integer.valueOf(field.getMaxValue());
			int uiqmin = field.getMinValue() == null ? 0 : Integer.valueOf(field.getMinValue());
			gen = new UIntegerGenerator(uiqmin, uimax);
			break;
		case ULONG:
			long ulmax = field.getMaxValue() == null ? 0 : Long.valueOf(field.getMaxValue());
			long ulqmin = field.getMinValue() == null ? 0 : Long.valueOf(field.getMinValue());
			gen = new ULongGenerator(ulqmin, ulmax);
			break;
		case UDOUBLE:
			double udmax = field.getMaxValue() == null ? 0 : Double.valueOf(field.getMaxValue());
			double udmin = field.getMinValue() == null ? 0 : Double.valueOf(field.getMinValue());
			gen = new UDoubleGenerator(udmax, udmin);
			break;
		default:
			gen = new TextGenerator(field.getDisplaySize(), field.isNullable());

		}

		return gen;

	}

	public static void main(String[] args) {
		SchemaDataGenerator dg = new SchemaDataGenerator();
		dg.setSchema(SampleSchemaCreator.newSampleSchema());
		dg.setNumberOfRecords(10);
		dg.generateData();
	}

	public SchemaOutputType getOutputType() {
		return outputType;
	}

	public void setOutputType(SchemaOutputType outputType) {
		this.outputType = outputType;
	}

	public String getOutputUri() {
		return outputUri;
	}

	public void setOutputUri(String outputUri) {
		this.outputUri = outputUri;
	}

	public String getSchemaUri() {
		return schemaUri;
	}

	public void setSchemaUri(String schemaUri) {
		this.schemaUri = schemaUri;
	}

	public Schema getSchema() {
		return schema;
	}

	public void setSchema(Schema schema) {
		this.schema = schema;
	}

	public long getNumberOfRecords() {
		return numberOfRecords;
	}

	public void setNumberOfRecords(long numberOfRecords) {
		this.numberOfRecords = numberOfRecords;
	}
	
	

}
