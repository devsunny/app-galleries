package com.asksunny.odbc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.WithItem;

public class PgSystemFunctionSearcher extends ExpressionVisitorAdapter implements SelectItemVisitor, SelectVisitor
{

	private Set<String> functionNames = new HashSet<String>();
	private boolean foundMatch = false;

	public PgSystemFunctionSearcher(List<String> functNames) {
		for (String string : functNames) {
			functionNames.add(string.toUpperCase());
		}
	}
	
	public PgSystemFunctionSearcher(String functName) {
		functionNames.add(functName.toUpperCase());
	}
	
	
	public boolean search(Select select)
	{
		select.getSelectBody().accept(this);
		return this.foundMatch;
	}
	
	
	@Override
	public void visit(AllColumns allColumns) {			
		
	}

	@Override
	public void visit(AllTableColumns allTableColumns) {			
		
	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {
		selectExpressionItem.getExpression().accept(this);		
	}

	@Override
	public void visit(Function function) {
		String name = function.getName().toUpperCase();
		if(this.foundMatch==false) this.foundMatch = functionNames.contains(name);
	}
	

	public boolean hasFoundMatch() {
		return this.foundMatch;
	}

	@Override
	public void visit(PlainSelect plainSelect) {
		List<SelectItem> selectItems = plainSelect.getSelectItems();
		  for (SelectItem selectItem : selectItems) {
			  selectItem.accept(this);
		 }
	}

	@Override
	public void visit(SetOperationList setOpList) {
	}

	@Override
	public void visit(WithItem withItem) {				
	}

}
