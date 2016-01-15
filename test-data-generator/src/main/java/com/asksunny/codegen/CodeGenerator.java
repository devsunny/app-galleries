package com.asksunny.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.asksunny.codegen.CodeGenConfig.CodeOverwriteStrategy;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Schema;

public abstract class CodeGenerator {

	protected CodeGenConfig configuration;	
	protected Entity entity;
	protected Schema schema;
	public CodeGenerator(CodeGenConfig configuration, Entity entity) {
		super();
		this.configuration = configuration;
		this.entity = entity;
	}
	public CodeGenerator(CodeGenConfig configuration, Schema schema) {
		super();
		this.configuration = configuration;
		this.schema = schema;
	}
	
	
	
	
	protected void writeCode(CodeGenConfig config, File dir, String fileName,
			String code) throws IOException {
		File fj = new File(dir, fileName);
		if (configuration.getOverwriteStrategy() == CodeOverwriteStrategy.IGNORE
				&& fj.exists()) {
			return;
		} else if (configuration.getOverwriteStrategy() == CodeOverwriteStrategy.RENAME_EXISTING
				&& fj.exists()) {
			File newFile = null;
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				newFile = new File(dir, String.format("%s.%03d", fileName, i));
				if (!newFile.exists()) {
					break;
				}
			}
			if(!fj.renameTo(newFile)){
				throw new IOException("Failed to rename existing file");
			}			
		} else if (fj.exists()) {
			for (int i = 1; i < Integer.MAX_VALUE; i++) {
				fj = new File(dir, String.format("%s.%03d", fileName, i));
				if (!fj.exists()) {
					break;
				}
			}
		}
		FileWriter fw = new FileWriter(fj);
		try {
			fw.write(code);
			fw.flush();
		} finally {
			fw.close();
		}
	}

}
