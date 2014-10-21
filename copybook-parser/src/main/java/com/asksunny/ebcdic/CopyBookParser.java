package com.asksunny.ebcdic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.asksunny.ebcdic.Entity.Type;

public class CopyBookParser {

	private boolean closeAfterParse;
	private PeekableBufferedReader copyBookReader;
	private final static Pattern LENGTH_PATTERN = Pattern.compile("\\((\\d+)\\)");
	

	public CopyBookParser(String pathTo) throws IOException {
		this(new FileReader(pathTo), true);
	}

	public CopyBookParser(Reader reader) {
		this(reader, false);
	}

	public CopyBookParser(Reader reader, boolean closeAfterParse) {
		this.copyBookReader = new PeekableBufferedReader(reader);
		this.closeAfterParse = closeAfterParse;
	}

	public List<Field> parse() throws IOException {
		List<Field> fields = new ArrayList<Field>();
		String line = null;
		try {			
			while ((line = this.copyBookReader.readLine()) != null) {
				line = line.trim();
				//System.out.println(line);
				if (line.startsWith("*") || line.trim().length() == 0) {
					// comment;
					continue;
				} else if (line.matches("^\\d{2,}\\s+.+\\.$")) {
					Entity entity = parseField(line);
					if(entity instanceof Field){
						fields.add((Field)entity);
						//System.out.println(entity);
					}
					
					
				} else {
					//System.out.println(line.matches("^\\d{2}\\s+.+\\.$"));
				}

			}
		} finally {
			if (this.copyBookReader != null && this.closeAfterParse) {
				this.copyBookReader.close();
			}
		}
		return fields;
	}

	protected Entity parseField(String fieldString) {
		Entity ret = null;
		String[] names = fieldString.split("\\s+|\\.$");
		// System.out.println(line);
		if (names.length == 2) {
			// System.out.println("REcord:" + names[1]);
			ret = new Entity();
			ret.setName(names[1]);
			ret.setRecordLevel(Integer.valueOf(names[0]));
		} else if (names.length > 3 && names[2].toUpperCase().equals("PIC")) {
			// System.out.println("Field:" + names[1]);
			Field field = new Field();			
			field.setName(names[1]);
			field.setRecordLevel(Integer.valueOf(names[0]));
			parseFieldDef(names[3], field);
			ret = field;
		} else if (names.length > 3 && names[2].toUpperCase().equals("REDEFINES")) {
			
			
		}else if (names.length > 3 && names[2].toUpperCase().equals("OCCURS")) {
			
		}
		return ret;
	}

	protected void parseFieldDef(String fielddef, Field field) {
		
		 char type = fielddef.charAt(0);
		 switch (type) {
			case 'X':
				field.setType(Type.STRING);
				Matcher smatcher = LENGTH_PATTERN.matcher(fielddef);
				if(smatcher.find()){
					field.setLength(Integer.valueOf(smatcher.group(1)));
				}else{
					field.setLength(fielddef.length());
				}
				break;
			case '9':
				if(fielddef.indexOf("X")!=-1){
					field.setLength(fielddef.length());
					field.setType(Type.STRING);
					return;
				}
				
				String[] dd = fielddef.split("V|\\.");
				//boolean hasDec = false;
				if(fielddef.indexOf(".")!=-1){
					field.setImplicitDecimal(false);
					field.setType(Type.DECIMAL);
				}else if(fielddef.indexOf("V")!=-1){
					field.setImplicitDecimal(true);
					field.setType(Type.DECIMAL);
				}else{
					field.setType(Type.INTEGER);
				}
				if(dd.length==1){
					parseNumberField(dd[0], field, false);
				}else{
					parseNumberField(dd[0], field, false);
					parseNumberField(dd[1], field, true);
				}				
				break;
			case 'S':
				field.setSinged(true);
				fielddef = fielddef.substring(1);
				String[] dd2 = fielddef.split("V|\\.");
				//boolean hasDec = false;
				if(fielddef.indexOf(".")!=-1){
					field.setImplicitDecimal(false);
					field.setType(Type.DECIMAL);
				}else if(fielddef.indexOf("V")!=-1){
					field.setImplicitDecimal(true);
					field.setType(Type.DECIMAL);
				}else{
					field.setType(Type.INTEGER);
				}
				if(dd2.length==1){
					parseNumberField(dd2[0], field, false);
				}else{
					parseNumberField(dd2[0], field, false);
					parseNumberField(dd2[1], field, true);
				}
				break;
			}		 
		 
		
	
	}
	
	protected int parseNumberField(String numberField, Field field, boolean dec)
	{		
		Matcher smatcher = LENGTH_PATTERN.matcher(numberField);
		if(smatcher.find()){
			if(dec){
				field.setDecimalLength(Integer.valueOf(smatcher.group(1)));
			}else{
				field.setLength(Integer.valueOf(smatcher.group(1)));
			}
		}else{
			if(dec){
				field.setDecimalLength(numberField.length());
			}else{
				field.setLength(numberField.length());
			}
		}
		return 0;
	}

}
