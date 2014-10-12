package com.asksunny.sql;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.asksunny.odbc.PostgreSqlProtocolHandler;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Modulo;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.ItemsList;
import net.sf.jsqlparser.expression.operators.relational.ItemsListVisitor;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.LateralSubSelect;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SetOperationList;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.ValuesList;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.set.Set;
import net.sf.jsqlparser.statement.show.Show;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;

public class PostgresIndexedParameterVisitor implements StatementVisitor,
		SelectVisitor, FromItemVisitor, ItemsListVisitor, SelectItemVisitor,
		ExpressionVisitor {
	private static Logger logger = LoggerFactory
			.getLogger(PostgresIndexedParameterVisitor.class);
	private static final String POSTGRES_PARAM_PATTERN = "^\\$\\d+$";
	@Override
	public void visit(NullValue nullValue) {

	}

	@Override
	public void visit(Function function) {

	}

	@Override
	public void visit(SignedExpression signedExpression) {

	}

	@Override
	public void visit(JdbcParameter jdbcParameter) {

	}

	@Override
	public void visit(JdbcNamedParameter jdbcNamedParameter) {

	}

	@Override
	public void visit(DoubleValue doubleValue) {

	}

	@Override
	public void visit(LongValue longValue) {

	}

	@Override
	public void visit(DateValue dateValue) {

	}

	@Override
	public void visit(TimeValue timeValue) {

	}

	@Override
	public void visit(TimestampValue timestampValue) {

	}

	@Override
	public void visit(Parenthesis parenthesis) {

	}

	@Override
	public void visit(StringValue stringValue) {

	}

	@Override
	public void visit(Addition addition) {

	}

	@Override
	public void visit(Division division) {

	}

	@Override
	public void visit(Multiplication multiplication) {

	}

	@Override
	public void visit(Subtraction subtraction) {

	}

	@Override
	public void visit(AndExpression andExpression) {
		if (logger.isDebugEnabled())
			logger.debug("visit(AndExpression andExpression): ");
		rewriteBinaryExpression(andExpression);
	}

	protected void rewriteBinaryExpression(BinaryExpression bexpr) {
		if (logger.isDebugEnabled())
			logger.debug("rewriteBinaryExpression(BinaryExpression bexpr)");
		Expression leftExpr = bexpr.getLeftExpression();
		Expression rightExpr = bexpr.getRightExpression();
		if (leftExpr != null) {
			if (leftExpr instanceof Column && ((Column)leftExpr).getColumnName().matches(POSTGRES_PARAM_PATTERN)) {
				bexpr.setLeftExpression(new JdbcParameter());
			}else{
				if (logger.isDebugEnabled())
					logger.debug("rewriteBinaryExpression left:{}", leftExpr.getClass().getName());
				leftExpr.accept(this);
			}
		}
		if (rightExpr != null) {
			if (rightExpr instanceof Column && ((Column)rightExpr).getColumnName().matches(POSTGRES_PARAM_PATTERN)) {
				bexpr.setRightExpression(new JdbcParameter());
			}else{
				if (logger.isDebugEnabled())
					logger.debug("rewriteBinaryExpression right:{}", rightExpr.getClass().getName());
				rightExpr.accept(this);
			}
		}

	}

	@Override
	public void visit(OrExpression orExpression) {
		if (logger.isDebugEnabled())
			logger.debug("visit(OrExpression orExpression) : ");
		rewriteBinaryExpression(orExpression);
	}

	@Override
	public void visit(Between between) {
		if (logger.isDebugEnabled())
			logger.debug("visit(Between between)");
		Expression leftExpr = between.getBetweenExpressionStart();
		Expression rightExpr = between.getBetweenExpressionEnd();
		if (leftExpr != null) {
			if (leftExpr instanceof Column && ((Column)leftExpr).getColumnName().trim().matches(POSTGRES_PARAM_PATTERN)) {
				between.setBetweenExpressionStart(new JdbcParameter());
			}else{
				if (logger.isDebugEnabled())
					logger.debug("rewriteBinaryExpression left:{}", leftExpr.getClass().getName());
				leftExpr.accept(this);
			}
		}
		if (rightExpr != null) {
			if (rightExpr instanceof Column && ((Column)rightExpr).getColumnName().trim().matches(POSTGRES_PARAM_PATTERN)) {
				between.setBetweenExpressionEnd(new JdbcParameter());
			}else{
				if (logger.isDebugEnabled())
					logger.debug("rewriteBinaryExpression right:{}", rightExpr.getClass().getName());
				rightExpr.accept(this);
			}
		}
	}

	@Override
	public void visit(EqualsTo equalsTo) {
		if (logger.isDebugEnabled())
			logger.debug("visit(EqualsTo equalsTo)");
		rewriteBinaryExpression(equalsTo);
	}

	@Override
	public void visit(GreaterThan greaterThan) {
		if (logger.isDebugEnabled())
			logger.debug("visit(GreaterThan greaterThan)");
		rewriteBinaryExpression(greaterThan);
	}

	@Override
	public void visit(GreaterThanEquals greaterThanEquals) {
		if (logger.isDebugEnabled())
			logger.debug("visit(GreaterThanEquals greaterThanEquals)");
		rewriteBinaryExpression(greaterThanEquals);
	}

	@Override
	public void visit(InExpression inExpression) {
		ItemsList leftList = inExpression.getLeftItemsList();
		if(leftList!=null) leftList.accept(this);
		
		ItemsList rightList = inExpression.getRightItemsList();
		if(rightList!=null) rightList.accept(this);
		
	}

	@Override
	public void visit(IsNullExpression isNullExpression) {

	}

	@Override
	public void visit(LikeExpression likeExpression) {

	}

	@Override
	public void visit(MinorThan minorThan) {
		if (logger.isDebugEnabled())
			logger.debug("visit(MinorThan minorThan)");
		rewriteBinaryExpression(minorThan);
	}

	@Override
	public void visit(MinorThanEquals minorThanEquals) {
		rewriteBinaryExpression(minorThanEquals);
	}

	@Override
	public void visit(NotEqualsTo notEqualsTo) {
		rewriteBinaryExpression(notEqualsTo);
	}

	@Override
	public void visit(Column tableColumn) {
		if (logger.isDebugEnabled())
			logger.debug("visit(Column tableColumn)");

	}

	@Override
	public void visit(SubSelect subSelect) {
		subSelect.getSelectBody().accept(this);
	}

	@Override
	public void visit(CaseExpression caseExpression) {

	}

	@Override
	public void visit(WhenClause whenClause) {

	}

	@Override
	public void visit(ExistsExpression existsExpression) {

	}

	@Override
	public void visit(AllComparisonExpression allComparisonExpression) {

	}

	@Override
	public void visit(AnyComparisonExpression anyComparisonExpression) {

	}

	@Override
	public void visit(Concat concat) {

	}

	@Override
	public void visit(Matches matches) {

	}

	@Override
	public void visit(BitwiseAnd bitwiseAnd) {

	}

	@Override
	public void visit(BitwiseOr bitwiseOr) {

	}

	@Override
	public void visit(BitwiseXor bitwiseXor) {

	}

	@Override
	public void visit(CastExpression cast) {

	}

	@Override
	public void visit(Modulo modulo) {

	}

	@Override
	public void visit(AnalyticExpression aexpr) {

	}

	@Override
	public void visit(ExtractExpression eexpr) {

	}

	@Override
	public void visit(IntervalExpression iexpr) {

	}

	@Override
	public void visit(OracleHierarchicalExpression oexpr) {

	}

	@Override
	public void visit(RegExpMatchOperator rexpr) {

	}

	@Override
	public void visit(AllColumns allColumns) {

	}

	@Override
	public void visit(AllTableColumns allTableColumns) {

	}

	@Override
	public void visit(SelectExpressionItem selectExpressionItem) {

	}

	@Override
	public void visit(PlainSelect plainSelect) {
		if (logger.isDebugEnabled())
			logger.debug("visit(PlainSelect plainSelect)");

		if (plainSelect.getJoins() != null) {
			List<Join> joins = plainSelect.getJoins();
			for (Join join : joins) {
				Expression expr = join.getOnExpression();
				expr.accept(this);
			}
		}

		if (plainSelect.getHaving() != null) {
			Expression expr = plainSelect.getHaving();
			expr.accept(this);
		}

		if (plainSelect.getWhere() != null) {
			if (logger.isDebugEnabled())
				logger.debug("visit where clause:{}", plainSelect.getWhere()
						.getClass().getName());
			plainSelect.getWhere().accept(this);
		}
	}

	@Override
	public void visit(SetOperationList setOpList) {

	}

	@Override
	public void visit(WithItem withItem) {

	}

	@Override
	public void visit(Select select) {
		if (logger.isDebugEnabled())
			logger.debug("visit(Select select)");
		select.getSelectBody().accept(this);
		List<WithItem> withItems = select.getWithItemsList();
		if (withItems != null) {
			for (WithItem withItem : withItems) {
				withItem.accept(this);
			}
		}
	}

	@Override
	public void visit(Delete delete) {
		Expression whereClause = delete.getWhere();
		if(whereClause!=null){
			whereClause.accept(this);
		}
	}

	@Override
	public void visit(Update update) 
	{
		List<Expression> exprs = update.getExpressions();
		for (Expression expression : exprs) {
			if(expression instanceof BinaryExpression){
				rewriteBinaryExpression((BinaryExpression)expression);
			}
		}
		Expression whereClause = update.getWhere();
		if(whereClause!=null){
			whereClause.accept(this);
		}
		
		if(update.getJoins()!=null){
			List<Join> joins = update.getJoins();
			for (Join join : joins) {
				Expression expr = join.getOnExpression();
				if(expr instanceof BinaryExpression){
					rewriteBinaryExpression((BinaryExpression)expr);
				}
			}
		}
		
	}

	@Override
	public void visit(Insert insert) {
		if (logger.isDebugEnabled())
			logger.debug("visit(Insert insert)");
		insert.getItemsList().accept(this);
	}

	@Override
	public void visit(Replace replace) {

	}

	@Override
	public void visit(Drop drop) {

	}

	@Override
	public void visit(Truncate truncate) {

	}

	@Override
	public void visit(CreateIndex createIndex) {

	}

	@Override
	public void visit(CreateTable createTable) {

	}

	@Override
	public void visit(CreateView createView) {

	}

	@Override
	public void visit(Alter alter) {

	}

	@Override
	public void visit(Statements stmts) {
		if (logger.isDebugEnabled())
			logger.debug("visit(Statements stmts)");

	}

	@Override
	public void visit(Set stmts) {

	}

	@Override
	public void visit(Show stmts) {

	}

	@Override
	public void visit(Table tableName) {

	}

	@Override
	public void visit(SubJoin subjoin) {

	}

	@Override
	public void visit(LateralSubSelect lateralSubSelect) {

	}

	@Override
	public void visit(ValuesList valuesList) {

	}

	@Override
	public void visit(ExpressionList expressionList) {
		if (logger.isDebugEnabled())
			logger.debug("visit(ExpressionList expressionList)");
		List<Expression> exprs = expressionList.getExpressions();
		List<Expression> tmp = new ArrayList<Expression>();
		for (Expression expression : exprs) {
			if (logger.isDebugEnabled())
				if (expression instanceof Column
						&& ((Column) expression).getColumnName().matches(
								"\\$\\d+")) {
					if (logger.isDebugEnabled())
						logger.debug("Found Postgres Parameters:{}", expression);
					JdbcParameter param = new JdbcParameter();
					tmp.add(param);
				} else {
					tmp.add(expression);
				}
			expressionList.setExpressions(tmp);
		}
	}

	@Override
	public void visit(MultiExpressionList multiExprList) {
		List<ExpressionList> lists = multiExprList.getExprList();
		for (ExpressionList expressionList : lists) {
			visit(expressionList);
		}
	}

}
