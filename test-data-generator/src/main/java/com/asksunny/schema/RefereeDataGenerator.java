package com.asksunny.schema;

import java.util.List;

import com.asksunny.schema.generator.Generator;
import com.asksunny.schema.generator.RandomUtil;
import com.asksunny.schema.generator.RefValueGenerator;

public class RefereeDataGenerator {
	private Entity referee;
	private Field referField;
	private List<Generator<?>> refereeGenerators;
	private SchemaOutputType outputType;
	private String outputUri;

	public RefereeDataGenerator(Entity referee, Field referField, List<Generator<?>> refereeGenerators) {
		super();
		this.referee = referee;
		this.referField = referField;
		this.refereeGenerators = refereeGenerators;
	}

	public void setValue(String val) {
		((RefValueGenerator) refereeGenerators.get(referField.getFieldIndex())).setRefValue(val);
	}

	public void generateData() {
		long minCount = referField.getMinValue() != null ? Long.valueOf(referField.getMinValue()) : 0;
		long maxCount = referField.getMaxValue() != null ? Long.valueOf(referField.getMaxValue()) : 5;
		generateData(referee, refereeGenerators, minCount, maxCount);

	}

	protected void generateData(Entity entity, List<Generator<?>> generators, long minCount, long maxCount) {

		List<Field> fields = entity.getFields();
		int size = fields.size();
		long count = RandomUtil.getInstance().getUnsignedLong(minCount, maxCount);
		for (long i = 0; i < count; i++) {
			for (int j = 0; j < size; j++) {
				Generator<?> gen = generators.get(j);
				String val = gen.nextStringValue();
				System.out.print(val);
				System.out.print(",");
			}
			System.out.println();
		}

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

	public Entity getReferee() {
		return referee;
	}

	public void setReferee(Entity referee) {
		this.referee = referee;
	}

	public Field getReferField() {
		return referField;
	}

	public void setReferField(Field referField) {
		this.referField = referField;
	}

	public List<Generator<?>> getRefereeGenerators() {
		return refereeGenerators;
	}

	public void setRefereeGenerators(List<Generator<?>> refereeGenerators) {
		this.refereeGenerators = refereeGenerators;
	}

}
