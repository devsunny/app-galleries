package com.asksunny.schema.parser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.asksunny.codegen.CodeGenAnnotation;
import com.asksunny.codegen.CodeGenType;
import com.asksunny.schema.Entity;
import com.asksunny.schema.Field;
import com.asksunny.schema.Schema;

@SuppressWarnings("unused")
public class SQLScriptParser {

	private final SQLScriptLookaheadTokenReader tokenReader;
	private boolean debug = false;
	private PrintWriter debugWriter = null;

	public void debug(Object... args) {
		if (this.debug) {
			if (debugWriter == null) {
				debugWriter = new PrintWriter(System.out);
			}
			for (int i = 0; i < args.length; i++) {
				if (args[i] instanceof Token) {
					Token t = (Token) args[i];
					debugWriter.println(String.format("%d:%d [%s] [%s] [%s]", t.getLine(), t.getColumn(), t.getImage(),
							t.getKind(), t.getKeyword()));
					debugWriter.flush();
				} else {
					if (args[i] instanceof String && args.length > 1) {
						debugWriter.print(args[i]);
						debugWriter.print(" ");
					} else {
						debugWriter.println(args[i]);
					}
					debugWriter.flush();
				}

			}
		}
	}

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
				debug(t);
				switch (t.getKind()) {
				case KEYWORD:
					debug("Parse satetement\n");
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

	protected void parseCreateTable(Schema schema) throws IOException {
		Token ct = consume();
		Token tb = consume();
		if (tb == null) {
			throw new InvalidSQLException("<table_name>", "null", ct.getLine(), ct.getColumn());
		}
		Entity entity = new Entity(tb.image);
		if (peekMatch(0, LexerTokenKind.ANNOTATION_COMMENT)) {
			Token cmt = consume();
			CodeGenAnnoParser parser = new CodeGenAnnoParser(
					new CodeGenAnnoLexer(new StringReader(cmt.getImage()), 0, 0));
			CodeGenAnnotation anno = parser.parseCodeAnnotation();
			entity.setAnnotation(anno);

		}
		debug("parse Table body", entity);
		parseCreateTableBody(entity);
		schema.put(entity.getName(), entity);
	}

	protected void parseUniqueIndex(Schema schema) throws IOException {
		Token idxName = consume();

		Token on = consume();
		Token tb = consume();
		Entity entity = schema.get(tb.getImage());
		List<Token> idxTokens = consumeItemList();
		for (Token token : idxTokens) {
			Field fd = entity.findField(token.getImage());
			fd.setUnique(true);
		}
	}

	protected void parseAlterTableStatement(Schema schema) throws IOException {

		Token table = consume();
		Token action = consume();
		Entity entity = schema.get(table.getImage());

		if (action.getKeyword() == Keyword.ADD) {
			Token actName = tokenReader.peek(0);
			switch (actName.getKeyword()) {
			case UNIQUE:
				parseUniqueConstraint(entity);
				break;
			case PRIMARY:
				parsePKConstraint(entity);
				break;
			case FOREIGN:
				parseFKConstraint(entity);
				break;
			case CONSTRAINT:
				drain(2);
				Token constx = tokenReader.peek(0);
				switch (constx.getKeyword()) {
				case UNIQUE:
					parseUniqueConstraint(entity);
					break;
				case PRIMARY:
					parsePKConstraint(entity);
					break;
				case FOREIGN:
					parseFKConstraint(entity);
					break;
				default:
					ignoreStatement();
				}
				break;
			default:
				ignoreStatement();
			}
		} else {
			ignoreStatement();
		}

	}

	protected void parseStatement(Schema schema, Token startToken) throws IOException {
		debug("----------------------parseStatement", startToken, tokenReader.peek(0));
		switch (startToken.getKeyword()) {
		case CREATE:
			Token nxtTok = tokenReader.peek(0);
			switch (nxtTok.getKeyword()) {
			case TABLE:
				parseCreateTable(schema);
				break;
			case UNIQUE:
				if (tokenReader.peek(1).getKeyword() == Keyword.INDEX) {
					drain(2);
					parseUniqueIndex(schema);
					ignoreStatement();
				} else {
					ignoreStatement();
				}
				break;
			case INDEX:
				ignoreStatement();
				break;
			default:
				ignoreStatement();
				break;
			}
			break;
		case ALTER:
			Token anxtTok = tokenReader.peek(0);
			switch (anxtTok.getKeyword()) {
			case TABLE:
				drain(1);
				parseAlterTableStatement(schema);
				break;
			default:
				ignoreStatement();
				break;
			}
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
		debug("All field parsed.");
		parseEntityClause(entity);

		if (peekMatch(0, LexerTokenKind.RPAREN)) {
			consume();
		}
		ignoreStatement();
	}

	protected void parseUniqueConstraint(Entity entity) throws IOException {
		consume();
		List<Token> utoks = consumeItemList();
		if (utoks != null) {
			for (Token token : utoks) {
				Field fd = entity.findField(token.image);
				if (fd != null) {
					fd.setUnique(true);
					debug(fd);
				}
			}
		}

	}

	protected void parsePKConstraint(Entity entity) throws IOException {
		drain(2);
		List<Token> toks = consumeItemList();
		if (toks != null) {
			for (Token token : toks) {
				Field fd = entity.findField(token.image);
				if (fd != null) {
					fd.setPrimaryKey(true);
				}
			}
		}
	}

	protected void parseFKConstraint(Entity entity) throws IOException {
		drain(2);
		List<Token> ftoks = consumeItemList();
		debug("Hello FK reference:", ftoks);
		Token reference = consume();
		debug("Hello FK reference:", reference);
		Token tb = consume(); // refrence table name
		debug("Hello FK reference:", tb);
		debug("before FK token:", tokenReader.peek(0));
		List<Token> rtoks = consumeItemList();
		int size = ftoks.size();
		debug("Hello FK reference:", rtoks);
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
		debug("after FK token:", tokenReader.peek(0));
	}

	protected void parseEntityClause(Entity entity) throws IOException {
		while (tokenReader.peek(0) != null) {
			Token kk = tokenReader.peek(0);
			debug(kk, kk.getKind());
			if (kk.getKind() == LexerTokenKind.KEYWORD) {
				switch (kk.getKeyword()) {
				case CONSTRAINT:
					drain(2);
					break;
				case PRIMARY:
					parsePKConstraint(entity);
					break;
				case FOREIGN:
					parseFKConstraint(entity);
					break;
				case UNIQUE:
					parseUniqueConstraint(entity);
					break;
				default:
					debug(kk.getKeyword());
					consume();
				}
			} else if (kk.getKind() == LexerTokenKind.COMMA) {
				consume();
			} else if (kk.getKind() == LexerTokenKind.RPAREN) {
				break;
			} else {
				Token tok = consume();
				debug("Not handled token:" + tok);
			}
		}

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
			ret.setDbTypeName(tname);
			if (peekMatch(0, LexerTokenKind.LPAREN)) {
				consume();
				Token num1 = consume();				
				if (num1.getKeyword() == Keyword.ASTERISK) {
					ret.setPrecision(16);
					ret.setDisplaySize(16);
				} else if (num1.getKind() != LexerTokenKind.NUMBER) {
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
				if (peekMatch(0, Keyword.BYTE)) {
					consume();
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
					} else if (att.getKeyword() == Keyword.UNIQUE) {
						ret.setUnique(true);
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
			boolean gotComma = false;
			if (peekMatch(0, LexerTokenKind.COMMA)) {
				consume();
				gotComma = true;
			}

			if (peekMatch(0, LexerTokenKind.ANNOTATION_COMMENT)) {
				Token anno = consume();
				parseAnnotationComment(ret, anno.getImage());

			}

			if (!gotComma && peekMatch(0, LexerTokenKind.COMMA)) {
				consume();
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
		try {
			CodeGenAnnoParser parser = new CodeGenAnnoParser(new CodeGenAnnoLexer(new StringReader(commentText), 0, 0));
			CodeGenAnnotation anno = parser.parseCodeAnnotation();
			field.setDataType(anno.getCodeGenType());
			if (anno.getRef() != null) {
				String[] refs = anno.getRef().split("\\.");
				if (refs.length != 2) {
					throw new InvalidSQLException(String.format("Invalid ref format[%s]", anno.getRef()));
				}
				Field xf = new Field();
				xf.setName(refs[1]);
				xf.setContainer(new Entity(refs[0]));
				field.setReference(xf);
			}
			field.setAnnotation(anno);
			parser.close();
		} catch (Exception ex) {
			throw new RuntimeException("Invalid Annotation:" + commentText, ex);
		}

	}

	protected Token consume() throws IOException {
		Token tt = tokenReader.read();
		if (tt == null) {
			throw new InvalidSQLException("Unexpected end of token stream");
		}
		return tt;
	}

	protected void drain(int number) throws IOException {
		int d = number;
		while (d > 0) {
			Token tt = tokenReader.read();
			if (tt == null) {
				throw new InvalidSQLException("Unexpected end of token stream");
			} else {
				d--;
			}
		}
	}

	protected List<Token> consumeItemList() throws IOException {
		List<Token> toks = new ArrayList<>();
		if (peekMatch(0, LexerTokenKind.LPAREN)) {
			consume();
			while (!peekMatch(0, LexerTokenKind.RPAREN)) {
				Token t = consume();
				debug("consumeItemList", t);
				if (t.kind != LexerTokenKind.COMMA) {
					toks.add(t);
				}
			}
			Token tt = consume();
			debug("RPAREN", tt);
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

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public PrintWriter getDebugWriter() {
		return debugWriter;
	}

	public void setDebugWriter(PrintWriter debugWriter) {
		this.debugWriter = debugWriter;
	}

}
