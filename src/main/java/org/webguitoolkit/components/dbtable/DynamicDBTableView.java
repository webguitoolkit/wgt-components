package org.webguitoolkit.components.dbtable;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.webguitoolkit.components.dbtable.model.DBColumn;
import org.webguitoolkit.components.dbtable.model.DynamicDBException;
import org.webguitoolkit.ui.base.DataBag;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.form.AbstractButtonBarListener;
import org.webguitoolkit.ui.controls.form.FormControl;
import org.webguitoolkit.ui.controls.form.IButtonBar;
import org.webguitoolkit.ui.controls.form.ICompound;
import org.webguitoolkit.ui.controls.form.ILabel;
import org.webguitoolkit.ui.controls.form.IText;
import org.webguitoolkit.ui.controls.layout.SequentialTableLayout;
import org.webguitoolkit.ui.controls.tab.ITab;
import org.webguitoolkit.ui.controls.tab.ITabStrip;
import org.webguitoolkit.ui.controls.table.ITable;
import org.webguitoolkit.ui.controls.table.ITableColumn;
import org.webguitoolkit.ui.controls.table.renderer.TeaserTextRenderer;
import org.webguitoolkit.ui.controls.util.MasterDetailController;
import org.webguitoolkit.ui.controls.util.TextService;
import org.webguitoolkit.ui.controls.util.validation.ValidatorUtil;
import org.webguitoolkit.ui.util.export.TableExportOptions;


/**
 * View that provides a table with data from a given database table
 * 
 * The component needs a connection-pool and the table-name to display all table data.
 * several configurations to customize could be set before calling show()
 * 
 * defaults are:<br>
 * <code>
 * editable = false; <br>
 * width = 600; <br>
 * rows = 15; <br>
 * sortColumn = 0; <br>
 * tableTitle = tableName;<br>
 * columnMapping = null;<br>
 * textTeaserActivated = true;<br>
 * maxCharsPerCol = 20;<br>
 * rowCount = 10000;<br>
 * </code>
 * <br>
 * if a columnMapping is set, it is also used to filter columns that are displayed. Columns without a mapping from columnMapping will not be displayed.
 * 
 * @author schweizerg
 *
 */
public class DynamicDBTableView extends AbstractView {


	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DynamicDBTableView.class);
	
	private DynamicDBQueries queries;
	
	private ICanvas mainCanvas;
	
	private int width;
	private int row;
	private boolean isEditable;
	private String tableName;
	private String tableTitle;
	private int sortColumn;
	private LinkedHashMap<String,String> columnNameMapping;
	private boolean manualPrimaryKey;
	private boolean textTeaserActivated;
	private int maxCharsPerCol;
	
	//not yet setable
	private String textFieldWidth = "250px";
	
	private ITable table;
	private List<DBColumn> columns;
	private ITabStrip tabs;
	private IText primaryKeyText;
	
	

	/**
	 * Constructor for the DynamicDBTableView 
	 * 
	 * @param factory
	 * @param viewConnector
	 * @param connectionPool pool where the components gets the connection from
	 * @param tableName the database tablename (e.g. spareparts.sparemapping)
	 * @param manualPrimaryKey true if the primary key is set by the user, false if set by the db
	 * @param columnMapping null, or a mapping for columns (this activates the filter!)
	 * @param filterClause null, or filter rows (e.g. "ID=100 and NAME='test'")
	 */
	public DynamicDBTableView(WebGuiFactory factory, ICanvas viewConnector, DynamicDBTableConnectionPool connectionPool, String tableName, boolean manualPrimaryKey, LinkedHashMap<String,String> columnMapping, String filterClause) {
		super(factory, viewConnector);
		this.columnNameMapping = columnMapping;
		this.manualPrimaryKey = manualPrimaryKey;
		queries = new DynamicDBQueries(connectionPool, tableName,manualPrimaryKey,columnMapping,filterClause);
		this.tableName = tableName;
		row = 15;
		width = 600;
		isEditable = false;
		sortColumn = 0;
		textTeaserActivated = true;
		maxCharsPerCol = 20;
	}

	/**
	 * default is 600
	 * @param width
	 */
	public void setTableWidth(int width) {
		this.width = width;		
	}

	/**
	 * default is false
	 * @param editable
	 */
	public void setEditable(boolean editable) {
		this.isEditable = editable;		
	}

	/**
	 * default is 15
	 * @param rows number of rows the table 
	 */
	public void setRowcount(int rows) {
		this.row = rows;		
	}
	
	/**
	 * default is 0
	 * @param i column number the table is sorted with
	 */
	public void setSortColumn(int i) {
		this.sortColumn = i;
	}
	
	/**
	 * default is the tablename from the database
	 * @param title
	 */
	public void setTableTitle(String title) {
		this.tableTitle = title;
	}
	
	/**
	 * has to be set in constructor
	 * @param value true = table has a manual primary key, false = table generates primary key
	 */
	public void setManualPrimaryKey(boolean value) {
		this.manualPrimaryKey = value;
	}
	
	/**
	 * default is 10000. If the number is to high (>50000) the heap could be to small
	 * @param maxRows sets the maximum number of rows that are queried from the database.
	 */
	public void setMaxRows(int maxRows) {
		queries.setMaxRows(maxRows);
	}
	
	/**
	 * Call this method to set the mapping. The mapping is used to display column names and labels for editing
	 * @param map key=database column name; value = text to display for the column
	 */
	public void setColumnNameMaping(LinkedHashMap<String,String> map) {
		this.columnNameMapping = map;
	}
	

	public boolean isTextTeaserActivated() {
		return textTeaserActivated;
	}

	/**
	 * If data in row are to big to display, dots are displayed. Default is activated.
	 * @param textTeaserActivated true = activated, false = deactivated;
	 */
	public void setTextTeaserActivated(boolean textTeaserActivated) {
		this.textTeaserActivated = textTeaserActivated;
	}


	public int getMaxCharsPerCol() {
		return maxCharsPerCol;
	}

	/**
	 * If the Teasertext is displayed, this changes the maximum chars that will be displayed before the dots. default is 20
	 * @param maxCharsPerCol new number of maximum chars
	 */
	public void setMaxCharsPerCol(int maxCharsPerCol) {
		this.maxCharsPerCol = maxCharsPerCol;
	}
	
	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		mainCanvas = getFactory().createCanvas(viewConnector);
		mainCanvas.setLayout(new SequentialTableLayout());
		
		List<Map<DBColumn, String>> data = queries.getTable();
		columns = queries.getColumns();

		if(StringUtils.isEmpty(tableTitle)) {
			tableTitle = tableName;
		}
		table = getFactory().createTable(mainCanvas, tableTitle, row);
		table.getStyle().addWidth(width+"px");
		table.setDisplayMode(ITable.DISPLAY_MODE_SCROLL_BUTTONS);

		
		for(Iterator<DBColumn> iter = columns.iterator();iter.hasNext();) {
			DBColumn col = iter.next();
			ITableColumn tableCol = getFactory().createTableColumn(table, "", col.getName(), true);
			tableCol.setTitle(getColumnTextFor(col.getName()));
			if(textTeaserActivated) {
				TeaserTextRenderer teaser = new TeaserTextRenderer(maxCharsPerCol);
				tableCol.setRenderer(teaser);
			}
		}
		//fill the table
		fillTableWithData(data);
		table.sort(sortColumn);		

		if(isEditable) {
			table.setLayoutData(SequentialTableLayout.getLastInRow());
			buildCompound();
			
		}
		
		table.setExportOptions(new TableExportOptions(false, true));

	}
	
	private void buildCompound() {
		//create components
		tabs = getFactory().createTabStrip(mainCanvas);
		tabs.addCssClass("dynamicTableTabPane");
	    ITab detailTab =  getFactory().createTab(tabs, "detail@Detail");
	    detailTab.addCssClass("dynamicTableTabPane");
	    ICompound detailCompound = getFactory().createCompound(detailTab);
	    //detailCompound.setBag(new DataBag(""));
		detailCompound.setLayout(new SequentialTableLayout());
	    IButtonBar detailButtons = getFactory().createButtonBar(detailCompound, "edit,delete,cancel,save,new",new ButtonBarListener(detailCompound));
	    detailButtons.setLayoutData(SequentialTableLayout.getLastInRow());
		
	    boolean first = true;
		//build labels + texts
		for(DBColumn col :columns) {

			ILabel colLabel = getFactory().createLabel(detailCompound, "");
			colLabel.setText(getColumnTextFor(col.getName()));
			if(!first) {
				IText colText = getFactory().createText(detailCompound, col.getName());
				colText.setDescribingLabel(colLabel);
				colText.setLayoutData(SequentialTableLayout.getLastInRow());
				colText.getStyle().addWidth(textFieldWidth);
				if(col.getType() == DBColumn.COL_NUMERIC) {
					colText.addValidator(ValidatorUtil.NUMBER_VALIDATOR);
				}
				if(col.getColumnSize() > 0) {
					colText.setMaxlength(col.getColumnSize());
				}
			}
			//first field must be set (is the primary key in table!)
			if(first) {
				primaryKeyText = getFactory().createText(detailCompound, col.getName());
				primaryKeyText.setDescribingLabel(colLabel);
				primaryKeyText.setFinal(true);
				primaryKeyText.getStyle().addWidth(textFieldWidth);
				primaryKeyText.setLayoutData(SequentialTableLayout.getLastInRow());
				if(manualPrimaryKey) {
					primaryKeyText.addValidator(ValidatorUtil.MANDATORY_VALIDATOR);
					if(col.getType() == DBColumn.COL_NUMERIC) {
						primaryKeyText.addValidator(ValidatorUtil.NUMBER_VALIDATOR);
					}
					if(col.getColumnSize() > 0) {
						primaryKeyText.setMaxlength(col.getColumnSize());
					}
				}
				first = false;
			}
		}
	    
		//create masterdetail
	    MasterDetailController mdc = new MasterDetailController();
		mdc.setMasterTable(table);
		mdc.setTabStrip(tabs);
		mdc.addMasterCompound(detailCompound, detailButtons);
	}

	private void fillTableWithData(List<Map<DBColumn, String>> data) {
		List<IDataBag> tableData = new ArrayList<IDataBag>();
		for(Iterator<Map<DBColumn,String>> iter = data.iterator();iter.hasNext();) {
			Map<DBColumn,String> row = iter.next();
			IDataBag rowDataBag = new DataBag(null);
			for(DBColumn col :columns) {
				String value = row.get(col);
				rowDataBag.addProperty(col.getName(), value);
			}
			tableData.add(rowDataBag);
		}
		table.getDefaultModel().setTableData(tableData);
		table.reload();
	}

	/**
	 * returns the mapped value for the column, or the database column if no mapping was found
	 * @param col The database column name
	 * @return
	 */
	private String getColumnTextFor(String col) {
		if(columnNameMapping == null) {
			return col;
		}
		String columnName = columnNameMapping.get(col);
		if(StringUtils.isEmpty(columnName))
			return col;
		return columnName;
	}
	
	@SuppressWarnings("serial")
	class ButtonBarListener extends AbstractButtonBarListener {
		
		private ICompound compound;

		ButtonBarListener(ICompound compound) {
			super(compound);
			this.compound = compound;
		}
		
		@Override
		public int persist() {
			logger.debug("persist()");
			IDataBag newBag = compound.getBag();
			List<String> data = getDataFromBag(newBag);
			if (compound.getMode() == ICompound.MODE_NEW) {
				String newPrimaryKey = "";
				try {
					newPrimaryKey = queries.insertRow(data);
				} catch (DynamicDBException dbe) {
					String msg = TextService.getString("dynamicdbtable.primkeyexists@Primary key already exists in the table");
					compound.addError(msg, ((FormControl)primaryKeyText).getProperty());
					return SAVE_ERROR;
				}
				newBag.addProperty(columns.get(0).getName(), newPrimaryKey);
				compound.setBag(newBag);
				primaryKeyText.setValue(newPrimaryKey);
			} else if (compound.getMode() == ICompound.MODE_EDIT) {
				boolean worked = queries.updateRow(data);
				if(!worked) {
					return SAVE_ERROR;
				}
			}
			return SAVE_OK;
		}
		
		@Override
		public boolean refresh(Object delegate) {
			logger.debug("refresh()");
			return true;
		}

		@Override
		public boolean delete(Object delegate) {
			if(table.getSelectedRowIndex() == -1) {
				return false;
			} 
			IDataBag bag = compound.getBag();
			DBColumn col = columns.get(0);
			return queries.deleteRowWithPrimaryKey(bag.getString(col.getName()));
		}

		@Override
		public Object newDelegate() {
			return compound.getBag();
		}
		
		@Override
		public void postSave() {
			logger.debug("postSave()");
			//nothing here yet
		}
		
		public void postNew() {
			if(!manualPrimaryKey)
				primaryKeyText.changeMode(ICompound.MODE_READONLY);
		}
		
		@Override
		public void postEdit() {
			logger.debug("postEdit()");
		
			//selected?
			if(table.getSelectedRowIndex() == -1) {
				compound.changeElementMode(ICompound.MODE_READONLY);
			} 
		}
		
		
		private List<String> getDataFromBag(IDataBag bag) {
			List<String> data = new ArrayList<String>();
			for(DBColumn col: columns) {
				String value = bag.getString(col.getName());
				if(value == null)
					data.add("");
				data.add(value);
			}
			return data;
		}
	}

}
