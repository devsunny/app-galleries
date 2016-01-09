package com.asksunny.codegen.data;

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
import java.util.concurrent.atomic.AtomicBoolean;

import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.generator.ForeignKeyFieldGenerator;
import com.asksunny.schema.generator.Generator;

public class BottomUpEntityDataGenerator implements IEntityDataGenerator {

	protected static SecureRandom rand = new SecureRandom(UUID.randomUUID().toString().getBytes());
	protected static final int MAX_SET_SIZE = 24;

	private Entity entity;
	private SchemaDataConfig config;
	private List<Generator<?>> fieldGenerators = null;
	private List<BottomUpEntityDataGenerator> parentEntityGenerators = new ArrayList<>();

	private String insertTemplate = "";
	private long totalRecordCount = 0;
	private PrintWriter out = null;
	private AtomicBoolean openned = new AtomicBoolean(false);

	public List<List<String>> generateDataSet() {
		int size = totalRecordCount <= MAX_SET_SIZE ? (int) totalRecordCount : Math.abs(rand.nextInt(MAX_SET_SIZE));

		Map<String, List<List<String>>> parentDataSets = new HashMap<>();
		if (parentEntityGenerators.size() > 0) {
			for (BottomUpEntityDataGenerator egen : parentEntityGenerators) {
				List<List<String>> refData = egen.generateDataSet();
				if (this.config.isDebug()) {
					System.out.println(
							String.format("FK reference %s values [%s] ", egen.getEntity().getName(), refData));
				}
				parentDataSets.put(egen.getEntity().getName().toUpperCase(), refData);
			}
			List<Field> fields = entity.getFields();
			for (int i = 0; i < fieldGenerators.size(); i++) {
				if (fieldGenerators.get(i) instanceof ForeignKeyFieldGenerator) {
					Field fd = fields.get(i);
					List<List<String>> pds = parentDataSets
							.get(fd.getReference().getContainer().getName().toUpperCase());
					int refidx = fd.getReference().getFieldIndex();
					List<String> pvalues = new ArrayList<>();
					for (List<String> record : pds) {
						if (record.size() > refidx) {
							pvalues.add(record.get(refidx));
						}
					}
					((ForeignKeyFieldGenerator) fieldGenerators.get(i)).setValues(pvalues);
				}
			}
		}

		List<List<String>> dataSet = new ArrayList<>();
		for (int i = 0; i < size; i++) {
			List<String> record = generateRecord();
			dataSet.add(record);
		}
		totalRecordCount = totalRecordCount - size;
		if (this.config.isDebug()) {
			System.out.println(String.format("%s dataset [%s] ", getEntity().getName(), dataSet));
		}
		return dataSet;
	}

	/**
	 * This way can limit the memory usage while generating hierarchical dataset
	 */
	public void generateFullDataSet() {
		while (this.totalRecordCount > 0) {
			if (this.config.isDebug()) {
				System.out.println(String.format("%s [%d]", this.entity.getName(), this.totalRecordCount));
			}
			generateDataSet();
		}
	}

	protected List<String> generateRecord() {
		if (this.config.isDebug()) {
			System.out.println(String.format("Generate a record for [%s]", this.entity.getName()));
		}
		List<Field> fields = entity.getFields();
		int size = fields.size();
		List<String> values = new ArrayList<>();
		List<Generator<?>> generators = FieldGeneratorFactory.createFieldGenerator(entity);
		for (int j = 0; j < size; j++) {
			Generator<?> gen = generators.get(j);
			String val = gen.nextStringValue();
			values.add(val);
		}
		if (this.config.isDebug()) {
			System.out.println(String.format("a %s record %s ", this.entity.getName(), values));
		}
		doOutput(fields, values);
		return values;
	}

	protected void doOutput(List<Field> fields, List<String> values) {

		if (this.getConfig().getOutputType() == SchemaOutputType.INSERT) {
			if (this.config.isDebug()) {
				System.out.println(String.format("Output a %s INSERT record %s ", this.entity.getName(), values));
			}
			doInsertOutput(fields, values);
		} else if (this.getConfig().getOutputType() == SchemaOutputType.CSV) {
			if (this.config.isDebug()) {
				System.out.println(String.format("Output a %s CSV record %s ", this.entity.getName(), values));
			}
			doCsvOutput(fields, values);
		}
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
		if (this.openned.compareAndSet(false, true)) {
			try {
				for (Field f : entity.getAllReferences()) {
					if (config.isDebug()) {
						System.out.println(String.format("Reference:%s.%s", f.getContainer().getName(), f.getName()));
					}
					BottomUpEntityDataGenerator buGen = EntityGeneratorFactory.createEntityGenerator(f.getContainer(),
							getConfig());
					parentEntityGenerators.add(buGen);
				}
				this.totalRecordCount = this.getConfig().getNumberOfRecords();
				if (this.getConfig().getOutputUri() != null) {
					String fileName = String.format("%s.%s", entity.getName(), "csv");
					if (this.getConfig().getOutputType() == SchemaOutputType.INSERT) {
						fileName = String.format("%s.%s", entity.getName(), "sql");
					}
					File fout = new File(this.getConfig().getOutputUri(), fileName);
					out = new PrintWriter(fout);
				} else {
					out = new PrintWriter(System.out);
				}

			} catch (FileNotFoundException e) {
				throw new RuntimeException("Failed to open output file");
			}

			if (this.getConfig().getOutputType() == SchemaOutputType.INSERT) {
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
				if (config.isDebug()) {
					System.out.println(String.format("SQL INSERT TEMPLATE :%s", insertTemplate));
				}
			}

		}
	}

	public BottomUpEntityDataGenerator() {
		super();
	}

	public BottomUpEntityDataGenerator(Entity entity) {
		super();
		this.entity = entity;
	}

	public void close() {
		if (this.openned.compareAndSet(true, false)) {
			if (this.getConfig().getOutputUri() != null && this.out != null) {
				this.out.close();
			}
		}
	}

	public long getTotalRecordCount() {
		return totalRecordCount;
	}

	public void setTotalRecordCount(long totalRecordCount) {
		this.totalRecordCount = totalRecordCount;
	}

	public String getInsertTemplate() {
		return insertTemplate;
	}

	public void setInsertTemplate(String insertTemplate) {
		this.insertTemplate = insertTemplate;
	}

	public SchemaDataConfig getConfig() {
		return config;
	}

	public void setConfig(SchemaDataConfig config) {
		this.config = config;
	}

	public List<Generator<?>> getFieldGenerators() {
		return fieldGenerators;
	}

	public void setFieldGenerators(List<Generator<?>> fieldGenerators) {
		this.fieldGenerators = fieldGenerators;
	}

}
