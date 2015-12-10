package com.asksunny.schema;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Types;
import java.util.ArrayList;
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

	private PrintWriter out = null;
	private String insertTemplate = "";

	public RefereeDataGenerator(Entity referee, Field referField, List<Generator<?>> refereeGenerators) {
		super();
		this.referee = referee;
		this.referField = referField;
		this.refereeGenerators = refereeGenerators;
	}

	public void setValue(String val) {
		((RefValueGenerator) refereeGenerators.get(referField.getFieldIndex())).setRefValue(val);
	}

	public void open() {
		try {
			if (outputUri != null) {
				String fileName = String.format("%s.%s", referee.getName(), "csv");
				if (outputType == SchemaOutputType.INSERT) {
					fileName = String.format("%s.%s", referee.getName(), "sql");
					StringBuilder buf = new StringBuilder();
					buf.append("INSERT INTO ").append(referee.getName());
					buf.append(" (");
					int cnt = 0;
					int size = referee.getFields().size();
					for (Field fd : referee.getFields()) {
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
		long minCount = referField.getMinValue() != null ? Long.valueOf(referField.getMinValue()) : 0;
		long maxCount = referField.getMaxValue() != null ? Long.valueOf(referField.getMaxValue()) : 5;
		generateData(referee, refereeGenerators, minCount, maxCount);

	}

	protected void generateData(Entity entity, List<Generator<?>> generators, long minCount, long maxCount) {
		List<Field> fields = entity.getFields();
		int size = fields.size();
		long count = RandomUtil.getInstance().getUnsignedLong(minCount, maxCount);
		List<String> values = new ArrayList<>();
		for (long i = 0; i < count; i++) {
			for (int j = 0; j < size; j++) {
				Generator<?> gen = generators.get(j);
				String val = gen.nextStringValue();
				values.add(val);
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
				if (val==null) {
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

	public void close() {
		if (this.outputUri != null && this.out != null) {
			this.out.close();
		}
	}

}
