package com.asksunny.schema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Types;
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

	private PrintWriter out = null;

	private String insertTemplate = "";

	public EntityDataGenerator(SchemaDataGenerator schemaDataGenerator, Entity entity, List<Generator<?>> generators) {
		super();
		this.schemaDataGenerator = schemaDataGenerator;
		this.entity = entity;
		this.generators = generators;
	}

	public void open() {
		try {
			if (outputUri != null) {
				String fileName = String.format("%s.%s", entity.getName(), "csv");
				if (outputType == SchemaOutputType.INSERT) {
					fileName = String.format("%s.%s", entity.getName(), "sql");
					StringBuilder buf = new StringBuilder();
					buf.append("INSERT INTO ").append(entity.getName());
					buf.append(" (");
					int cnt = 0;
					int size = entity.getFields().size();
					for (Field fd : entity.getFields()) {
						cnt++;
						buf.append(fd.getName());
						if (cnt < size) {
							buf.append(",");
						}
					}
					buf.append(") VALUES (%s);");
					insertTemplate = buf.toString();
				}
				File fout = new File(outputUri, fileName);
				out = new PrintWriter(fout);
			} else {
				out = new PrintWriter(System.out);
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException("Failed to open output file");
		}

	}

	public void generateData() {
		List<Field> fields = entity.getFields();
		int size = fields.size();
		List<RefereeDataGenerator> subGens = new ArrayList<>();
		List<String> values = new ArrayList<>();
		for (long i = 0; i < sampleDataCount; i++) {
			for (int j = 0; j < size; j++) {
				Generator<?> gen = generators.get(j);
				String val = gen.nextStringValue();
				values.add(val);
				if (fields.get(j).getReferencedBy().size() > 0) {
					for (Field refBy : fields.get(j).getReferencedBy()) {
						RefereeDataGenerator refGen = this.schemaDataGenerator
								.creatorRefereeGenerator(refBy.getContainer(), refBy);
						refGen.setValue(val);
						subGens.add(refGen);
					}
				}
			}
			if (subGens.size() > 0) {
				for (RefereeDataGenerator refereeDataGenerator : subGens) {
					refereeDataGenerator.generateData();
				}
				subGens.clear();
			}
			doOutput(fields, values);
		}

	}

	protected void doOutput(List<Field> fields, List<String> values) {
		if (outputType == SchemaOutputType.INSERT) {
			doInsertOutput(fields, values);
		} else if (outputType == SchemaOutputType.CSV) {
			doCsvOutput(fields, values);
		}
		values.clear();
	}

	protected void doCsvOutput(List<Field> fields, List<String> values) {
		out.println(String.join(",", values));
		out.flush();
	}

	protected void doInsertOutput(List<Field> fields, List<String> values) {
		StringBuilder buf = new StringBuilder();
		int size = fields.size();
		int lastIdx = size-1;
		for (int i = 0; i < size; i++) {
			switch(fields.get(i).getJdbcType())
			{
			case Types.BIT:
			case Types.TINYINT:
			case Types.SMALLINT:
			case Types.INTEGER:
			case Types.BIGINT:
			case Types.FLOAT:
			case Types.REAL:
			case Types.DOUBLE:
			case Types.NUMERIC:
			case Types.DECIMAL:
				buf.append(values.get(i));
				break;
			default:
				String val = values.get(i);
				if(val==null){
					buf.append("null"); 
				}else{
					val = val.replaceAll("'", "''");					
					buf.append("'").append(val).append("'"); 
				}				
				break;
			}
			if(i<lastIdx){
				buf.append(",");
			}
		}
		out.println(String.format(insertTemplate, buf.toString()));
		out.flush();
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

	public void close() {
		if (this.outputUri != null && this.out != null) {
			this.out.close();
		}
	}
}
