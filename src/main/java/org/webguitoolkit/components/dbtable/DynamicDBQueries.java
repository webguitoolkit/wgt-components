package org.webguitoolkit.components.dbtable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.webguitoolkit.components.dbtable.model.DBColumn;
import org.webguitoolkit.components.dbtable.model.DynamicDBException;

/**
 * Database class for DynamicDBTableView.
 * Uses prepared statements to prevent sql-injections.
 * 
 * 
 * @author schweizerg
 */
public class DynamicDBQueries {

	private static final Logger logger = Logger.getLogger(DynamicDBQueries.class);
	private static final int DEFAULT_MAX_ROWS = 10000;
	
	private DynamicDBTableConnectionPool connectionPool;
	private List<DBColumn> columns;
	private String tableName;
	private String filterClause;
	private boolean manualPrimaryKey;
	private LinkedHashMap<String, String> columnMapping;
	private int maxRows = DEFAULT_MAX_ROWS;
	
	public DynamicDBQueries(DynamicDBTableConnectionPool connectionPool, String tableName, boolean manualPrimaryKey, LinkedHashMap<String, String> columnMapping, String filterClause) {
		this.connectionPool = connectionPool;
		this.tableName = tableName;
		this.manualPrimaryKey = manualPrimaryKey;
		this.columnMapping = columnMapping;
		this.filterClause = filterClause;
	}
	
	public void setManualPrimaryKey(boolean value) {
		manualPrimaryKey = value;
	}
	
	public void setMaxRows(int newRows) {
		maxRows = newRows;
	}
	
	public List<DBColumn> getColumns() {
		if(columns == null) {
			getTable();
		}
		return columns;
	}
	
	private List<DBColumn> getColumnNames(ResultSetMetaData metaData) throws SQLException {
		columns = new ArrayList<DBColumn>();
		int columnCount = metaData.getColumnCount();
		for(int i=1;i<=columnCount;i++) {
			String name = metaData.getColumnName(i);
			int type = metaData.getColumnType(i);
			int columnSize = metaData.getColumnDisplaySize(i);
			columns.add(new DBColumn(name, i, type,columnSize));
		}
		return columns;
	}
	
	private String getSelectColumns() {
		StringBuilder result = new StringBuilder("");
		for(Iterator<String> iter = columnMapping.keySet().iterator();iter.hasNext();) {
			String key = iter.next();
			//key = column name in the db
			result.append(key);
			if(iter.hasNext())
				result.append(",");
		}
		return result.toString();
	}
	
	public List<Map<DBColumn, String>>  getTable() {
		List<Map<DBColumn, String>> rows = new ArrayList<Map<DBColumn,String>>();
		Connection con = null;
		try {
			con = connectionPool.getConnection();
			String selector = "*";
			if(columnMapping != null) {
				selector = getSelectColumns();
			}
			String query = "SELECT "+selector+" FROM "+tableName;
			if(!StringUtils.isEmpty(filterClause)) {
				query += " WHERE "+filterClause;
			}
			Statement sqlStatement = con.createStatement();
			sqlStatement.setMaxRows(maxRows);
			logger.debug("getTable() query: "+query);
			ResultSet resultSet = sqlStatement.executeQuery(query);
			columns = getColumnNames(resultSet.getMetaData());
			while(resultSet.next()) {
				Map<DBColumn,String> row = new HashMap<DBColumn, String>();
				for(Iterator<DBColumn> iter = columns.iterator();iter.hasNext();) {
					DBColumn col = iter.next();
					String value = resultSet.getString(col.getName());
					row.put(col,value);
				}
				rows.add(row);
			}			
		} catch(SQLException e) {
			logger.error("db error: "+e);
		} finally {
			closeConnection(con, null);
		}		
		return rows;
	}

	public boolean deleteRowWithPrimaryKey(String primaryKey) {
		DBColumn first = columns.get(0);
		String query = "DELETE FROM "+tableName+" WHERE "+first.getName()+" = ?";
		Connection con = null;
		PreparedStatement updateStatement = null;
		try {
			con = connectionPool.getConnection();
			con.setAutoCommit(false);
			updateStatement = con.prepareStatement(query);
			updateStatement.setString(1, primaryKey);
			//do it
			if(first.getType() == DBColumn.COL_STRING) {
				updateStatement.setString(1, primaryKey);
			}else if(first.getType() == DBColumn.COL_NUMERIC) {
				int value = Integer.parseInt(primaryKey);
				updateStatement.setInt(1, value);
			}
			logger.debug("query: "+query +" "+primaryKey);
			updateStatement.execute();
			con.commit();
		} catch (SQLException e) {
			logger.error("db error: "+e+" in "+query);
			rollbackConnection(con);
		} finally {
			closeConnection(con, null);
		}
		return true;
	}
	
	/**
	 * 
	 * @param data
	 * @return true if update succeeded, false if failed
	 */
	public boolean updateRow(List<String> data) {
		boolean noError = true;
		StringBuilder query = new StringBuilder("UPDATE "+tableName+" SET ");
		int size=data.size();
		int i=1;
		//from second col to end
		for(;i<size;i++) {
			DBColumn col = columns.get(i);
			query.append(col.getName()+" = ?");
			if(i!=size-1) {
				query.append(", ");
			}
		}
		DBColumn primaryKeyCol = columns.get(0);
		query.append(" WHERE "+primaryKeyCol.getName()+" = ?");
		logger.debug(query.toString());
		Connection con = null;
		PreparedStatement updateStatement = null;
		try {
			con = connectionPool.getConnection();
			con.setAutoCommit(false);
			updateStatement = con.prepareStatement(query.toString());
			i=1;
			//from second col to end
			for(;i<size;i++) {
				//realCol needs to be 1 when we start at 0 or 1
				DBColumn col = columns.get(i);
				if(col.getType() == DBColumn.COL_STRING) {
					updateStatement.setString(i, data.get(i));
				}else if(col.getType() == DBColumn.COL_NUMERIC) {
					int value = Integer.parseInt(data.get(i));
					updateStatement.setInt(i, value);
				}
			}
			//set primarykey as where clause in the end
			//do it by get the first col and its data (0) and add it to the last ? which is equal the size of data
			DBColumn col = columns.get(0);
			if(col.getType() == DBColumn.COL_STRING) {
				updateStatement.setString(size, data.get(0));
			}else if(col.getType() == DBColumn.COL_NUMERIC) {
				int value = Integer.parseInt(data.get(0));
				updateStatement.setInt(size, value);
			}
			//do it
			int rowsUpdated = updateStatement.executeUpdate();
			if(rowsUpdated == 0)
				noError=false;
			con.commit();
		} catch (SQLException e) {
			noError = false;
			logger.error("db error: "+e+" in "+query);
			rollbackConnection(con);
		} finally {
			closeConnection(con, updateStatement);
		}
		return noError;
	}
	
	private String getColumnNamesAsCSV() {
		StringBuilder cols = new StringBuilder();
		int i=getForEachStart();
		int size = columns.size();
		for(;i<size;i++) {
			cols.append(columns.get(i).getName());
			if(i!=size-1) {
				//not last element
				cols.append(",");
			}
		}
		return cols.toString();
	}
	
	private int getForEachStart() {
		if(manualPrimaryKey) {
			return 0;
		} else {
			return 1;
		}
	}
	
	/**
	 * Inserts a new Row in db + returns the new primary key of this row!
	 * uses prepared statement
	 * @param data
	 * @return The primary key of the row
	 */
	public String insertRow(List<String> data) throws DynamicDBException {
		if(manualPrimaryKey) {
			boolean primExists = isPrimaryKeyAlreadyInTable(data.get(0));
			if(primExists) {
				throw new DynamicDBException("Primary key ("+data.get(0)+") already in table");
			}
		}
		StringBuilder query = new StringBuilder("INSERT INTO "+tableName+" ");
		query.append("("+getColumnNamesAsCSV()+") VALUES (");
		int size=data.size();
		int i=getForEachStart();
		//from second col to end
		for(;i<size;i++) {
			query.append("?");
			if(i!=size-1) {
				query.append(",");
			}
		}
		query.append(")");
		logger.debug(query.toString());
		Connection con = null;
		PreparedStatement updateStatement = null;
		try {
			con = connectionPool.getConnection();
			con.setAutoCommit(false);
			updateStatement = con.prepareStatement(query.toString());
			i=getForEachStart();
			//from second col to end
			for(;i<size;i++) {
				//realCol needs to be 1 when we start at 0 or 1
				int realCol = i+1-getForEachStart();
				DBColumn col = columns.get(i);
				if(col.getType() == DBColumn.COL_STRING) {
					updateStatement.setString(realCol, data.get(i));
				}else if(col.getType() == DBColumn.COL_NUMERIC) {
					int value = Integer.parseInt(data.get(i));
					updateStatement.setInt(realCol, value);
				}
			}
			updateStatement.executeUpdate();
			con.commit();
		} catch (SQLException e) {
			logger.error("db error: "+e+" in "+query);
			rollbackConnection(con);
		} finally {
			closeConnection(con, updateStatement);
		}	
		
		//if the primarykey was manual its the first row in data
		if(manualPrimaryKey)
			return data.get(0);		
		
		return getPrimaryKeyExecute(data);
	}

	private boolean isPrimaryKeyAlreadyInTable(String key) {
		boolean result = false;
		DBColumn primaryKeyCol = columns.get(0);
		String query = "SELECT * FROM "+tableName+" WHERE "+primaryKeyCol.getName()+" = ?";
		Connection con = null;
		PreparedStatement updateStatement = null;
		try {
			con = connectionPool.getConnection();
			con.setAutoCommit(false);
			updateStatement = con.prepareStatement(query);
			
			if(primaryKeyCol.getType() == DBColumn.COL_STRING) {
				updateStatement.setString(1, key);
			} else if(primaryKeyCol.getType() == DBColumn.COL_NUMERIC) {
				int value = Integer.parseInt(key);
				updateStatement.setInt(1, value);
			}

			ResultSet resultSet = updateStatement.executeQuery();
			while(resultSet.next()) {
				//take the last primarykey from resultset if there are more then 1 (which should not happen!)
				result = true;			
			}
		} catch (SQLException e) {
			logger.error("db error: "+e+" in "+query);
		} finally {
			closeConnection(con, updateStatement);
		}	
		
		return result;
	}

	private String getPrimaryKeyExecute(List<String> data) {
		String result = "";
		StringBuilder primQuery = new StringBuilder("SELECT "+columns.get(0).getName()+" FROM "+tableName+" WHERE ");
		int i=getForEachStart();
		int size=data.size();
		//from second col to end
		for(;i<size;i++) {
			DBColumn col = columns.get(i);
			primQuery.append(col.getName()+" = ?");
			if(i!=size-1) {
				primQuery.append(" and ");
			}
		}
		Connection con = null;
		PreparedStatement updateStatement = null;
		try {
			con = connectionPool.getConnection();
			con.setAutoCommit(false);
			updateStatement = con.prepareStatement(primQuery.toString());
			i=getForEachStart();
			//from second col to end
			for(;i<size;i++) {
				DBColumn col = columns.get(i);
				if(col.getType() == DBColumn.COL_STRING) {
					updateStatement.setString(i, data.get(i));
				}else if(col.getType() == DBColumn.COL_NUMERIC) {
					int value = Integer.parseInt(data.get(i));
					updateStatement.setInt(i, value);
				}
			}
			ResultSet resultSet = updateStatement.executeQuery();
			while(resultSet.next()) {
				//take the last primarykey from resultset if there are more then 1 (which should not happen!)
				result = resultSet.getString(1);
				
			}
			
		} catch (SQLException e) {
			logger.error("db error: "+e+" in "+primQuery.toString());
		} finally {
			closeConnection(con, updateStatement);
		}	
		return result;
	}
	
	private void closeConnection(Connection con, Statement stmnt) {
		try {
			if(con!=null) {
				con.close();
			}
		} catch(Exception e) {
			logger.error("cant close connection: "+e);
		}
		try {
			if(stmnt!=null) {
				stmnt.close();
			}
		} catch(Exception e) {
			logger.error("cant close connection: "+e);
		}
	}
	
	private void rollbackConnection(Connection con){
		try {
			if(con!=null) {
				con.rollback();
			}
		} catch (SQLException e1) {
			logger.error("cant rollback connection: "+e1);
		}
	}
}
