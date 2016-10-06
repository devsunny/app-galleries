package com.asksunny.batch.graph.transform;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.batch.graph.BatchFlowContext;
import com.asksunny.batch.graph.FlowTaskParameterType;
import com.asksunny.batch.graph.TextPreprocessor;

public class CsvFileRecordReader implements RecordReader {
	private static final Logger logger = LoggerFactory.getLogger(CsvFileRecordReader.class);
	private String csvFilePath;
	private TextPreprocessor filePathPreprocessor;
	private String filePathParameterName;
	private FlowTaskParameterType filePathParameterType;
	private BatchFlowContext flowContext;
	private LinkedList<File> csvFiles = new LinkedList<>();
	private String delmiter = ",";
	private BufferedReader fileReader = null;
	private String bufferedLine = null;

	public CsvFileRecordReader() {

	}

	@Override
	public void init(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
		String filePath = getCsvFilePath();
		if (getFilePathPreprocessor() != null) {
			filePath = getFilePathPreprocessor().preprocess(getCsvFilePath(),
					getParameter(filePathParameterType, filePathParameterName));
		}
		logger.info("Reading file:{}", filePath);
		File file = new File(filePath);
		if (file.isDirectory()) {
			File[] sffs = file.listFiles(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathname.isFile() && pathname.canRead();
				}
			});
			csvFiles.addAll(Arrays.asList(sffs));
		} else {
			csvFiles.add(file);
		}
	}

	protected String readLine() throws Exception {
		String line = fileReader == null ? null : fileReader.readLine();
		while (line == null && !csvFiles.isEmpty()) {
			if (fileReader != null) {
				fileReader.close();
			}
			fileReader = new BufferedReader(new FileReader(csvFiles.poll()));
			line = fileReader.readLine();
		}
		return line;
	}

	@Override
	public boolean next() throws Exception {
		this.bufferedLine = readLine();
		return this.bufferedLine != null;
	}

	@Override
	public Object getNext() throws Exception {
		return this.bufferedLine != null ? this.bufferedLine.split(delmiter) : null;
	}

	@Override
	public void close() {
		if (fileReader != null) {
			try {
				fileReader.close();
			} catch (IOException e) {
				;
			}
		}
	}

	public Object getParameter(FlowTaskParameterType pType, String parameterName) {
		switch (pType) {
		case CLIArgumentContext:
			return getFlowContext().getCliArgument();
		case BatchFlowContext:
			return getFlowContext();
		case CLIArgument:
			return getFlowContext().getCliArgument().get(parameterName);
		case BatchFlowContextObject:
			return getFlowContext().get(parameterName);
		case SystemProperties:
			return System.getProperties();
		case SystemEnvs:
			return System.getenv();
		case None:
			return null;
		default:
			return null;
		}
	}

	public String getCsvFilePath() {
		return csvFilePath;
	}

	public void setCsvFilePath(String csvFilePath) {
		this.csvFilePath = csvFilePath;
	}

	public TextPreprocessor getFilePathPreprocessor() {
		return filePathPreprocessor;
	}

	public void setFilePathPreprocessor(TextPreprocessor filePathPreprocessor) {
		this.filePathPreprocessor = filePathPreprocessor;
	}

	public String getFilePathParameterName() {
		return filePathParameterName;
	}

	public void setFilePathParameterName(String filePathParameterName) {
		this.filePathParameterName = filePathParameterName;
	}

	public BatchFlowContext getFlowContext() {
		return flowContext;
	}

	public void setFlowContext(BatchFlowContext flowContext) {
		this.flowContext = flowContext;
	}

	public FlowTaskParameterType getFilePathParameterType() {
		return filePathParameterType;
	}

	public void setFilePathParameterType(FlowTaskParameterType filePathParameterType) {
		this.filePathParameterType = filePathParameterType;
	}

}
