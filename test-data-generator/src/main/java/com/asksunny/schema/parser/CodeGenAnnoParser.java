package com.asksunny.schema.parser;

import java.io.IOException;

import com.asksunny.codegen.CodeGenAnnotation;
import com.asksunny.codegen.CodeGenAttrName;
import com.asksunny.codegen.CodeGenTokenKind;
import com.asksunny.codegen.CodeGenType;
import com.asksunny.io.LHTokenReader;

public class CodeGenAnnoParser {

	private LHTokenReader peekableTokenReader;

	public CodeGenAnnoParser(CodeGenAnnoLexer lexer) throws IOException {
		peekableTokenReader = new LHTokenReader(4, lexer);
	}

	public CodeGenAnnotation parseCodeAnnotation() throws IOException {
		CodeGenAnnotation anno = new CodeGenAnnotation();
		CodeGenAnnoToken token = null;
		while ((token = peek(0)) != null) {
			switch (token.getKind()) {
			case IDENTIFIER:
				if (peek(1) == null || peek(1).getKind() == CodeGenTokenKind.COMMA) {
					CodeGenAnnoToken tok = consume();
					try {
						anno.setCodeGenType(CodeGenType.valueOf(tok.getImage().toUpperCase()));
					} catch (Exception e) {
						;
					}
				} else if (peek(1).getKind() == CodeGenTokenKind.ASSIGNMENT) {
					parseNvp(anno);
				} else {
					throw new RuntimeException("Unexpected token:" + peek(1).getImage());
				}
				break;
			case COMMA:
				drain(1);
				break;
			default:
				drain(1);
			}
		}
		return anno;
	}

	protected void parseNvp(CodeGenAnnotation anno) throws IOException {
		CodeGenAnnoToken name = consume();
		consume();
		if (peek(0) == null || peek(0).getKind() == CodeGenTokenKind.COMMA) {
			return;
		}
		CodeGenAnnoToken val = consume();

		CodeGenAttrName attName = CodeGenAttrName.valueOf(name.getImage().toUpperCase());

		switch (attName) {
		case FORMAT:
			anno.setFormat(val.getImage());
			break;
		case LABEL:
			anno.setLabel(val.getImage());
			break;
		case MAX:
			anno.setMaxValue(val.getImage());
			break;
		case MIN:
			anno.setMinValue(val.getImage());
			break;
		case STEP:
			anno.setStep(val.getImage());
			break;
		case VALUES:
			anno.setEnumValues(val.getImage());
			break;
		case VARNAME:
			anno.setVarname(val.getImage());
			break;
		case REF:
			anno.setRef(val.getImage());
			break;
		case UITYPE:
			anno.setUitype(val.getImage());
			break;
		case ORDER:
			anno.setOrder(val.getImage());
			break;
		case ORDERBY:
			anno.setOrderBy(val.getImage());
			break;
		case GROUPFUNCTION:
			anno.setGroupFunction(val.getImage());
			break;
		case AUTOGEN:
			anno.setAutogen(val.getImage());
			break;
		case GROUPLEVEL:
			anno.setGroupLevel(val.getImage());
			break;
		case GROUPVIEW:
			anno.setGroupView(val.getImage());
			break;
		case IGNOREREST:
			anno.setIgnoreRest(val.getImage());
			break;
		case IGNOREVIEW:
			anno.setIgnoreView(val.getImage());
			break;
		case ITEMSPERPAGE:
			anno.setItemsPerPage(val.getImage());
			break;
		case DRILLDOWN:
			anno.setDrillDown(val.getImage());
			break;
		}

	}

	CodeGenAnnoToken peek(int idx) throws IOException {
		return (CodeGenAnnoToken) peekableTokenReader.peek(idx);
	}

	CodeGenAnnoToken consume() throws IOException {
		return (CodeGenAnnoToken) peekableTokenReader.nextToken();
	}

	void drain(int num) throws IOException {
		for (int i = 0; i < num; i++) {
			if (peekableTokenReader.nextToken() == null) {
				break;
			}
		}
	}

	public void close() throws IOException {
		this.peekableTokenReader.close();
	}

}
