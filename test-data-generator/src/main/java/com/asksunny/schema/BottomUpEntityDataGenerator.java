package com.asksunny.schema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.asksunny.schema.generator.ForeignKeyFieldGenerator;
import com.asksunny.schema.generator.Generator;

public class BottomUpEntityDataGenerator implements IEntityDataGenerator {

	private Entity entity;
	private List<BottomUpEntityDataGenerator> parentEntityGenerators;
	protected static SecureRandom rand = new SecureRandom(UUID.randomUUID().toString().getBytes());
	protected static final int MAX_SET_SIZE = 24;

	private SchemaOutputType outputType;
	private String outputUri;
	private PrintWriter out = null;
	private String insertTemplate = "";
	private long totalRecordCount = 0;

	public List<List<String>> generateDataSet() {
		int size = Math.abs(rand.nextInt(MAX_SET_SIZE));
		List<List<String>> dataSet = new ArrayList<>();
		Map<String, List<List<String>>> parentDataSets = new HashMap<>();
		if (parentEntityGenerators != null && parentEntityGenerators.size() > 0) {
			for (BottomUpEntityDataGenerator egen : parentEntityGenerators) {
				parentDataSets.put(egen.getEntity().getName().toUpperCase(), egen.generateDataSet());
			}
			List<Generator<?>> generators = FieldGeneratorFactory.createFieldGenerator(entity);
			List<Field> fields = entity.getFields();
			for (int i = 0; i < generators.size(); i++) {
				if (generators.get(i) instanceof ForeignKeyFieldGenerator) {
					Field fd = fields.get(i);
					int refidx = fd.getReference().getFieldIndex();
					List<List<String>> pds = parentDataSets
							.get(fd.getReference().getContainer().getName().toUpperCase());
					List<String> pvalues = new ArrayList<>();
					for (List<String> record : pds) {
						pvalues.add(record.get(refidx));
					}
					((ForeignKeyFieldGenerator) generators.get(i)).setValues(pvalues);
				}
			}
		}
		for (int i = 0; i < size; i++) {
			dataSet.add(generateRecord());
		}
		totalRecordCount = totalRecordCount - size;
		return dataSet;
	}

	/**
	 * This way can limit the memory usage while generating hierarchical dataset
	 */
	public void generateFullDataSet() {
		while (this.totalRecordCount > 0) {
			generateDataSet();
		}
	}

	protected List<String> generateRecord() {
		List<Field> fields = entity.getFields();
		int size = fields.size();
		List<String> values = new ArrayList<>();
		List<Generator<?>> generators = FieldGeneratorFactory.createFieldGenerator(entity);
		for (int j = 0; j < size; j++) {
			Generator<?> gen = generators.get(j);
			String val = gen.nextStringValue();
			values.add(val);
		}
		doOutput(fields, values);
		return values;
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
		int lastIdx = size - 1;
		for (int i = 0; i < size; i++) {
			switch (fields.get(i).getJdbcType()) {
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
				if (val == null) {
					buf.append("null");
				} else {
					val = val.replaceAll("'", "''");
					buf.append("'").append(val).append("'");
				}
				break;
			}
			if (i < lastIdx) {
				buf.append(",");
			}
		}
		out.println(String.format(insertTemplate, buf.toString()));
		out.flush();
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public List<BottomUpEntityDataGenerator> getParentEntityGenerators() {
		return parentEntityGenerators;
	}

	public void setParentEntityGenerators(List<BottomUpEntityDataGenerator> parentEntityGenerators) {
		this.parentEntityGenerators = parentEntityGenerators;
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

	public void close() {
		if (this.outputUri != null && this.out != null) {
			this.out.close();
		}
	}

	public long getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(long totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
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

	public String getInsertTemplate() {
		return insertTemplate;
	}

	public void setInsertTemplate(String insertTemplate) {
		this.insertTemplate = insertTemplate;
	}

}
