package org.webguitoolkit.components.dbtable.model;

import java.sql.Types;

/**
 * This class represents a DBColumn and is used by the DynamicDBTableView
 * 
 * @author i01002534
 */
public class DBColumn {

	public static final int COL_NUMERIC = 0;
	public static final int COL_STRING = 1;
	
	private String name;
	private int nr;
	//java.sql.Type
	private int type;
	private int columnSize;
	
	/**
	 * Use this contructor
	 * 
	 * @param name database name of the column
	 * @param nr column number from database
	 * @param type type from DBColumn.COL_NUMERIC or DBColumn.COL_STRING
	 */
	public DBColumn(String name, int nr, int type) {
		super();
		this.name = name;
		this.nr = nr;
		this.type = getDBColumnType(type);
		this.columnSize = 0;
	}
	
	/**
	 * Use this contructor if the column has a maximum size
	 * 
	 * @param name database name of the column
	 * @param nr column number from database
	 * @param type type from DBColumn.COL_NUMERIC or DBColumn.COL_STRING
	 */
	public DBColumn(String name, int nr, int type, int columnSize) {
		super();
		this.name = name;
		this.nr = nr;
		this.type = getDBColumnType(type);
		this.columnSize = columnSize;
	}

	private int getDBColumnType(int i) {
		if(i == Types.BIGINT || i == Types.DECIMAL || i == Types.TINYINT || i == Types.INTEGER || i == Types.DOUBLE || i == Types.FLOAT|| i == Types.REAL || i == Types.SMALLINT) {
			return COL_NUMERIC;
		} else {
			return COL_STRING;
		}
	}
	
	public String getName() {
		return name;
	}

	public int getType() {
		return type;
	}

	public int getNr() {
		return nr;
	}
	
	public int getColumnSize() {
		return columnSize;
	}
	
}
