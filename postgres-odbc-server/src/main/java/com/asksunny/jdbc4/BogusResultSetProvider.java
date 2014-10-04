package com.asksunny.jdbc4;

import java.sql.ResultSet;
import java.sql.Types;

public class BogusResultSetProvider {

	public BogusResultSetProvider() {		
	}
	
	public static ResultSet newResultSet()
	{
		InternalResultSetMetaData rsmd = new InternalResultSetMetaData();
		InternalResult rs = new InternalResult(rsmd);
		rsmd.addColumnMetaData("ID", Types.INTEGER, 15);
		rsmd.addColumnMetaData("NAME", Types.VARCHAR, 64);
		rsmd.addColumnMetaData("DESCRIPTION", Types.VARCHAR, 256);
		rs.addRow(1, "name1", "description1");
		rs.addRow(2, "name2", "description2");
		rs.addRow(3, "name3", "description3");
		rs.addRow(4, "name4", "description4");
		rs.addRow(5, "name7", "description5");		
		return rs;
	}

}
