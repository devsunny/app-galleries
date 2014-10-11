package com.asksunny.sql;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.set.Set;
import net.sf.jsqlparser.statement.show.Show;
import net.sf.jsqlparser.statement.update.Update;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.odbc.SQLCommandType;

public class SQLRewriteEngine 
{
	private static Logger logger = LoggerFactory
			.getLogger(SQLRewriteEngine.class);
	
	public static RewritedSqlStatement rewrite(String sql)
	{
		RewritedSqlStatement rewrited = new RewritedSqlStatement();
		rewrited.setSql(sql);
		try{
			Statement stmt = CCJSqlParserUtil.parse(sql);				
			if(stmt instanceof Select){
				rewrited.setType(SQLCommandType.SELECT);
			}else if(stmt instanceof Insert){
				rewrited.setType(SQLCommandType.INSERT);
			}else if(stmt instanceof Update){
				rewrited.setType(SQLCommandType.UPDATE);
			}else if(stmt instanceof Delete){
				rewrited.setType(SQLCommandType.DELETE);
			}else if(stmt instanceof Set){
				rewrited.setType(SQLCommandType.SET);
			}else if(stmt instanceof Show){
				rewrited.setType(SQLCommandType.SHOW);
			}else{
				rewrited.setType(SQLCommandType.OTHER);
			}
		}catch(Exception ex){
			logger.warn("Failed to rewrite SQL", ex);
			;
		}
		return rewrited;
		
	}
}
