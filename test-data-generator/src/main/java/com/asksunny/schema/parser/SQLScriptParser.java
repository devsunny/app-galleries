package com.asksunny.schema.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.asksunny.schema.DataGenType;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;

public class SQLScriptParser {

	private final SQLScriptLookaheadTokenReader tokenReader;

	public SQLScriptParser(SQLScriptLexer sqlLexer) throws IOException {
		super();
		this.tokenReader = new SQLScriptLookaheadTokenReader(3, sqlLexer);
	}

	public SQLScriptParser(File sqlFile) throws IOException {
		this(new FileReader(sqlFile));
	}

	public SQLScriptParser(String sqlText) throws IOException {
		this(new StringReader(sqlText));
	}

	public SQLScriptParser(InputStream in, Charset encode) throws IOException {
		this(new InputStreamReader(in, encode));
	}

	public SQLScriptParser(Reader reader) throws IOException {
		super();
		this.tokenReader = new SQLScriptLookaheadTokenReader(3, new SQLScriptLexer(reader));
	}

	public Schema parseSql() throws IOException {
		Schema schema = new Schema();
		try {
			Token t = null;
			while ((t = tokenReader.read()) != null) {
				switch (t.getKind()) {
				case KEYWORD:
					parseStatement(schema, t);
					break;
				default:
					ignoreStatement();
					break;
				}
			}
		} finally {
			close();
		}
		return schema;
	}

	public void close() throws IOException {
		if (this.tokenReader != null) {
			this.tokenReader.close();
		}
	}

	protected void parseStatement(Schema schema, Token startToken) throws IOException {

		switch (startToken.getKeyword()) {
		case CREATE:
			if (peekMatch(0, Keyword.TABLE)) {
				Token ct = tokenReader.read();
				Token tb = tokenReader.read();
				if (tb == null) {
					throw new InvalidSQLException("<table_name>", "null", ct.getLine(), ct.getColumn());
				}
				Entity entity = new Entity(tb.image);
				parseCreateTableBody(entity);
				schema.put(entity.getName(), entity);
			} else {
				ignoreStatement();
			}
			break;
		case ALTER:
			if (peekMatch(0, Keyword.TABLE) && peekMatch(2, Keyword.ADD)) {
				tokenReader.read();
				Token table = tokenReader.read();
				tokenReader.read();
				if (peekMatch(0, Keyword.FOREIGN) && peekMatch(1, Keyword.KEY)) {
					tokenReader.read();
					tokenReader.read();
					List<Token> fkCOls = consumeItemList();
					tokenReader.read();
					Token reftable = tokenReader.read();					
					List<Token> refCOls = consumeItemList();					
					Entity fkTable = schema.get(table.getImage());
					//System.out.println(">>>>>>>>>>>>>>>>" + table.getImage());
					//System.out.println(fkTable);
					//System.out.println(fkCOls.get(0).image);
					Field fk = fkTable.findField(fkCOls.get(0).image);					
					//System.out.println(">>>>>>>>>>>>>>>>" + reftable.getImage());
					Entity refTable = schema.get(reftable.getImage());
					//System.out.println(">>>>>>>>>>>>>>>>");
					//System.out.println(refTable);
					//System.out.println("<<<<<<<<<<<<<<<<<<<<");
					Field rfd = refTable.findField(refCOls.get(0).getImage());
					rfd.setContainer(refTable);					
					fk.setReference(rfd);
					rfd.addReferencedBy(fk);
					
				} else if (peekMatch(0, Keyword.PRIMARY) && peekMatch(1, Keyword.KEY)) {
					tokenReader.read();
					tokenReader.read();
					List<Token> pkCOls = consumeItemList();
					Entity pkTable = schema.get(table.getImage());
					for (Token pkT : pkCOls) {
						Field fd = pkTable.findField(pkT.getImage());
						fd.setPrimaryKey(true);
					}
				}
			}
			ignoreStatement();
			break;
		default:
			ignoreStatement();
			break;
		}

	}

	protected void parseCreateTableBody(Entity entity) throws IOException {

		if (!peekMatch(0, LexerTokenKind.LPAREN)) {
			Token p = tokenReader.peek(0);
			throw new InvalidSQLException("<table_name>", p.getImage(), p.getLine(), p.getColumn());
		} else {
			consume();
		}
		Field field = null;
		while ((field = parseField(entity)) != null) {
			entity.addField(field);
		}
		while (tokenReader.peek(0) != null) {
			Token kk = tokenReader.peek(0);
			if (kk.getKind() == LexerTokenKind.KEYWORD) {
				if (kk.getKeyword() == Keyword.CONSTRAINT) {
					consume();
					consume();
				} else if (kk.getKeyword() == Keyword.PRIMARY) {
					consume();
					consume();
					List<Token> toks = consumeItemList();
					if (toks != null) {
						for (Token token : toks) {
							Field fd = entity.findField(token.image);
							if (fd != null) {
								fd.setPrimaryKey(true);
							}
						}
					}
				} else if (kk.getKeyword() == Keyword.FOREIGN) {
					consume();
					consume();
					List<Token> ftoks = consumeItemList();
					consume();
					Token tb = consume(); // refrence table name
					List<Token> rtoks = consumeItemList();
					int size = ftoks.size();

					for (int i = 0; i < size; i++) {
						Token fk = ftoks.get(i);
						Token rk = rtoks.get(i);
						Field fd = entity.findField(fk.image);
						Entity e = new Entity(tb.image);
						Field rd = new Field();
						rd.setContainer(e);
						rd.setName(rk.getImage());
						fd.setReference(rd);
					}
				}
			} else if (kk.getKind() == LexerTokenKind.COMMA) {
				consume();
			} else if (kk.getKind() == LexerTokenKind.RPAREN) {
				break;
			} else {
				consume();
			}
		}

		if (peekMatch(0, LexerTokenKind.RPAREN)) {
			consume();
		}
		ignoreStatement();
	}

	protected Field parseField(Entity entity) throws IOException {
		Field ret = null;
		
		if (peekMatch(0, LexerTokenKind.IDENTIFIER)) {
			ret = new Field();
			ret.setName(tokenReader.read().image);			
			String tname = consume().image;			
			// if(ret.getName().equals("house_number")){
			// System.out.println(JdbcSqlTypeMap.getInstance().findJdbcType(tname));
			// System.out.println(tname);
			// }
			ret.setJdbcType(JdbcSqlTypeMap.getInstance().findJdbcType(tname));
			if (peekMatch(0, LexerTokenKind.LPAREN)) {
				consume();
				Token num1 = consume();
				if (num1.getKind() != LexerTokenKind.NUMBER) {
					throw new InvalidSQLException("<NUMBER>", num1.image, num1.line, num1.column);
				} else {
					ret.setPrecision(Integer.valueOf(num1.image));
					ret.setDisplaySize(Integer.valueOf(num1.image));
				}
				Token num2 = null;
				if (peekMatch(0, LexerTokenKind.COMMA)) {
					consume();
					num2 = consume();
					if (num2.getKind() != LexerTokenKind.NUMBER) {
						throw new InvalidSQLException("<NUMBER>", num2.image, num2.line, num2.column);
					} else {
						ret.setScale(Integer.valueOf(num2.image));
					}
				}
				if (peekMatch(0, LexerTokenKind.RPAREN)) {
					consume();
				} else {
					Token p = tokenReader.peek(0);
					throw new InvalidSQLException(LexerTokenKind.RPAREN.name(), p.getImage(), p.getLine(),
							p.getColumn());
				}
			}
			while (tokenReader.peek(0) != null && !peekMatch(0, LexerTokenKind.COMMA)
					&& !peekMatch(0, LexerTokenKind.RPAREN) && !peekMatch(0, LexerTokenKind.ANNOTATION_COMMENT)) {
				Token att = consume();
				if (att.getKind() == LexerTokenKind.KEYWORD) {
					if (att.getKeyword() == Keyword.NOT) {
						if (!peekMatch(0, Keyword.NULL)) {
							Token p = tokenReader.peek(0);
							throw new InvalidSQLException(Keyword.NULL.name(), p.getImage(), p.getLine(),
									p.getColumn());
						} else {
							consume();
							ret.setNullable(false);
						}
					} else if (att.getKeyword() == Keyword.NULL) {
						ret.setNullable(true);
					} else if (att.getKeyword() == Keyword.PRIMARY) {
						if (!peekMatch(0, Keyword.KEY)) {
							Token p = tokenReader.peek(0);
							throw new InvalidSQLException(Keyword.KEY.name(), p.getImage(), p.getLine(), p.getColumn());
						} else {
							consume();
							ret.setPrimaryKey(true);
						}
					}

				}
			}
			if (peekMatch(0, LexerTokenKind.COMMA)) {
				consume();
			}

			if (peekMatch(0, LexerTokenKind.ANNOTATION_COMMENT)) {
				Token anno = consume();
				parseAnnotationComment(ret, anno.getImage());

			}
		} else if (peekMatch(0, LexerTokenKind.RPAREN) || peekMatch(0, LexerTokenKind.KEYWORD)) {
			return null;
		} else {
			Token p = tokenReader.peek(0);
			throw new InvalidSQLException(LexerTokenKind.IDENTIFIER.name(), p.getImage(), p.getLine(), p.getColumn());
		}
		return ret;
	}

	protected void parseAnnotationComment(Field field, String commentText) {
		String[] ps = commentText.split("\\s*[,]\\s*");
		if (ps.length > 0) {
			DataGenType genType = ps[0].length() == 0 ? DataGenType.OTHER : DataGenType.valueOf(ps[0].toUpperCase());
			field.setDataType(genType);
			if (ps.length > 1) {
				for (int i = 1; i < ps.length; i++) {
					String[] nvp = ps[i].split("\\s*[=]\\s*");
					if (nvp.length != 2)
						continue;
					if (nvp[0].equalsIgnoreCase("format")) {
						field.setFormat(nvp[1]);
					} else if (nvp[0].equalsIgnoreCase("min")) {
						field.setMinValue(nvp[1]);
					} else if (nvp[0].equalsIgnoreCase("max")) {
						field.setMaxValue(nvp[1]);
					} else if (nvp[0].equalsIgnoreCase("varname")) {
						field.setVarname(nvp[1]);
					} else if (nvp[0].equalsIgnoreCase("uiname")) {
						field.setUiname(nvp[1]);
					} else if (nvp[0].equalsIgnoreCase("ref")) {
						String[] refs = nvp[1].split("\\.");
						if (refs.length != 2) {
							throw new InvalidSQLException(String.format("Invalid ref format [%s]", nvp[1]));
						}
						Field xf = new Field();
						xf.setName(refs[1]);
						xf.setContainer(new Entity(refs[0]));
						field.setReference(xf);
					} else if (nvp[0].equalsIgnoreCase("values")) {
						field.setEnumValues(nvp[1]);
					} else if (nvp[0].equalsIgnoreCase("step")) {
						field.setStep(nvp[1]);
					}

				}
			}
		}

	}

	protected Token consume() throws IOException {
		Token tt = tokenReader.read();
		if (tt == null) {
			throw new InvalidSQLException("Unexpected end of token stream");
		}
		return tt;
	}

	protected List<Token> consumeItemList() throws IOException {
		List<Token> toks = new ArrayList<>();
		if (peekMatch(0, LexerTokenKind.LPAREN)) {
			consume();
			while (!peekMatch(0, LexerTokenKind.RPAREN)) {
				Token t = consume();
				if (t.kind != LexerTokenKind.COMMA) {
					toks.add(t);
				}
			}
			consume();
		}
		return toks;
	}

	protected void consumeTo(LexerTokenKind kind) throws IOException {
		while (!peekMatch(0, kind)) {
			consume();
		}
		consume();
	}

	protected void consumeTo(LexerTokenKind kind, LexerTokenKind... kinds) throws IOException {
		while (!peekMatch(0, kind)) {
			consume();
		}
		consume();
	}

	public boolean peekMatch(int idx, LexerTokenKind kind, LexerTokenKind... kinds) {
		Token p = tokenReader.peek(idx);
		if (p == null) {
			throw new InvalidSQLException("Unexpected end of token stream");
		}
		for (LexerTokenKind k : kinds) {
			if (k == p.getKind()) {
				return true;
			}
		}
		return (p.getKind() == kind);
	}

	public boolean peekMatch(int idx, LexerTokenKind kind) {
		Token p = tokenReader.peek(idx);
		if (p == null) {
			throw new InvalidSQLException("Unexpected end of token stream");
		}
		return (p.getKind() == kind);
	}

	public boolean peekMatch(int idx, Keyword keyword) {
		Token p = tokenReader.peek(idx);
		if (p == null) {
			throw new InvalidSQLException("Unexpected end of token stream");
		}
		return (p.getKind() == LexerTokenKind.KEYWORD && p.getKeyword() == keyword);

	}

	protected void ignoreStatement() throws IOException {
		Token t = null;
		while ((t = tokenReader.read()) != null) {
			if (t.getKind() == LexerTokenKind.SEMICOLON) {
				break;
			}
		}
	}

}
