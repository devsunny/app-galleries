package com.asksunny.batch.graph.transform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import com.asksunny.batch.graph.BatchFlowContext;
import com.asksunny.batch.graph.sql.AbstractQueryable;

public class JdbcRecordReader extends AbstractQueryable implements RecordReader {
	private static final Logger logger = LoggerFactory.getLogger(JdbcRecordReader.class);

	protected AtomicBoolean initialized = new AtomicBoolean(false);
	protected ResultSet resultSet = null;
	protected PreparedStatement preparedStatement = null;
	protected Connection connection = null;
	protected int columnCount = 1;
	protected List<Object> record = null;

	public JdbcRecordReader() {

	}

	@Override
	public void init(BatchFlowContext flowContext) {
		flowContext.submitTask();
		setFlowContext(flowContext);
	}

	@Override
	public boolean next() throws Exception {
		if (initialized.weakCompareAndSet(false, true)) {
			connection = getDatasource().getConnection();
			preparedStatement = prepareStatement(connection, getStatement());
			resultSet = preparedStatement.executeQuery();
			columnCount = resultSet.getMetaData().getColumnCount();
			record = new ArrayList<>(columnCount);
		}
		if (resultSet.isClosed() && resultSet == null) {
			return false;
		}
		if (!resultSet.next()) {
			close();
			return false;
		} else {
			return true;
		}

	}

	@Override
	public Object getNext() throws Exception {
		record.clear();
		for (int i = 0; i < columnCount; i++) {
			record.add(resultSet.getObject(i + 1));
		}
		return record;
	}

	protected void close(ResultSet rs) {
		try {
			rs.close();
		} catch (SQLException e) {
			;
		}
	}

	protected void close(Statement stmt) {
		try {
			stmt.close();
		} catch (SQLException e) {
			;
		}
	}

	protected void close(Connection conn) {
		close(null, conn);
	}

	protected void close(DataSource ds, Connection conn) {
		if (ds == null || (ds != null && ds instanceof SingleConnectionDataSource
				&& ((SingleConnectionDataSource) ds).shouldClose(conn))) {
			try {
				conn.close();
			} catch (SQLException e) {
				;
			}
		} else {
			logger.info("Single Connection DataSource, leave connection open util JVM destroyed");
		}

	}

	@Override
	public void close() {
		close(resultSet);
		close(preparedStatement);
		close(getDatasource(), connection);

	}
}
