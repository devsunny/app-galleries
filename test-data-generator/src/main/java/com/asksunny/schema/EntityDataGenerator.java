package com.asksunny.schema;

import java.util.ArrayList;
import java.util.List;

import com.asksunny.schema.generator.Generator;

public class EntityDataGenerator {

	private Entity entity;
	private List<Generator<?>> generators;
	private SchemaOutputType outputType;
	private String outputUri;
	private long sampleDataCount = 10;
	private SchemaDataGenerator schemaDataGenerator;

	public EntityDataGenerator(SchemaDataGenerator schemaDataGenerator, Entity entity, List<Generator<?>> generators) {
		super();
		this.schemaDataGenerator = schemaDataGenerator;
		this.entity = entity;
		this.generators = generators;
	}

	public void generateData() {
		List<Field> fields = entity.getFields();
		int size = fields.size();
		List<RefereeDataGenerator> subGens = new ArrayList<>();
		for (long i = 0; i < sampleDataCount; i++) {
			for (int j = 0; j < size; j++) {
				Generator<?> gen = generators.get(j);
				String val = gen.nextStringValue();
				System.out.print(val);
				System.out.print(",");
				if (fields.get(j).getReferencedBy().size() > 0) {
					for (Field refBy : fields.get(j).getReferencedBy()) {
						RefereeDataGenerator refGen = this.schemaDataGenerator
								.creatorRefereeGenerator(refBy.getContainer(), refBy);
						refGen.setValue(val);
						subGens.add(refGen);
					}
				}
			}
			System.out.println();
			if (subGens.size() > 0) {
				for (RefereeDataGenerator refereeDataGenerator : subGens) {
					refereeDataGenerator.generateData();
				}
				subGens.clear();
			}

		}

	}

	public long getSampleDataCount() {
		return sampleDataCount;
	}

	public void setSampleDataCount(long sampleDataCount) {
		this.sampleDataCount = sampleDataCount;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<Generator<?>> getGenerators() {
		return generators;
	}

	public void setGenerators(List<Generator<?>> generators) {
		this.generators = generators;
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

}
