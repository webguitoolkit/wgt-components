package org.webguitoolkit.components.assortmenttables;

import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.form.IButton;
import org.webguitoolkit.ui.controls.layout.ITableLayout;
import org.webguitoolkit.ui.controls.table.ITable;
import org.webguitoolkit.ui.controls.util.style.Style;

/**
 * Use this component to make a sub-selection out of a defined amount of data by moving elements from one table to another. Be
 * sure to implement an add- and a remove-listener by your own extending the given abstract listener.
 * 
 * @author i102487
 * 
 */
public class AssortmentTablesWidget extends AbstractView {

	// ids
	public static final String REMOVE_BUTTON_ID = "REMOVE_BUTTON_ID";
	public static final String ADD_BUTTON_ID = "ADD_BUTTON_ID";

	// constants
	public static final int ASSORTMENT_ACTION_ADD = 0;
	public static final int ASSORTMENT_ACTION_REMOVE = 1;
	public static final int ASSORTMENT_TABLE_WIDTH = 300;

	// variables
	protected String id;
	protected ITable assignedTable, allTable;
	protected String allTitleKey = "";
	protected String assignedTitleKey = "";
	protected IActionListener addListener;
	protected IActionListener removeListener;
	protected boolean tablesEditable = false;
	protected int tablesWidth = ASSORTMENT_TABLE_WIDTH;
	protected IButton addButton, removeButton;

	// constructors
	/**
	 * @param id the id for the component that operates as prefix for the ids of the single controls of the AssortmentTablesWidget
	 */
	public AssortmentTablesWidget(WebGuiFactory factory, ICanvas viewConnector, String theAllTitle, String theAssignedTitle, String id) {
		this(factory, viewConnector, id);
		this.allTitleKey = theAllTitle;
		this.assignedTitleKey = theAssignedTitle;
	}

	/**
	 * 
	 * @param factory
	 * @param viewConnector
	 * @param id the id for the component that operates as prefix for the ids of the single controls of the AssortmentTablesWidget
	 */
	public AssortmentTablesWidget(WebGuiFactory factory, ICanvas viewConnector, String id) {
		super(factory, viewConnector);
		this.id = id;
	}

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		ITableLayout layout = factory.createTableLayout(viewConnector);

		// left table
		allTable = factory.createTable(layout, this.allTitleKey, 5);
		allTable.getStyle().addWidth(this.getTablesWidth(), Style.PIXEL);
		allTable.setDisplayMode(ITable.DISPLAY_MODE_SCROLL_BUTTONS);
		allTable.setEditable(this.isTablesEditable());
		allTable.addCheckboxColumn("checked", "org.webguitoolkit.components.assortmenttables.assortmenttableswidget.checkbox.title@Select");

		// buttons in the middle
		ICanvas buttonCan = getFactory().createCanvas(layout);
		ITableLayout buttonLayout = getFactory().createTableLayout(buttonCan);

		addButton = factory.createButton(buttonLayout, "images/wgt/icons/doublearrow_right.gif", null,
				"com.endress.infoserve.userdata.page.widgets.assortmenttablewidget.button.add.tooltip@Add", addListener, this.id
						+ ADD_BUTTON_ID);
		buttonLayout.newRow().addEmptyCell().setHeight(5);
		buttonLayout.newRow();
		removeButton = factory.createButton(buttonLayout, "images/wgt/icons/doublearrow_left.gif", null,
				"com.endress.infoserve.userdata.page.widgets.assortmenttablewidget.button.remove.tooltip@Remove", removeListener, this.id
						+ REMOVE_BUTTON_ID);

		layout.getCurrentCell().setStyle("vertical-align: middle;");

		// right table
		assignedTable = factory.createTable(layout, this.assignedTitleKey, 5);
		assignedTable.getStyle().addWidth(this.getTablesWidth(), Style.PIXEL);
		assignedTable.setDisplayMode(ITable.DISPLAY_MODE_SCROLL_BUTTONS);
		assignedTable.setEditable(this.isTablesEditable());
		assignedTable.addCheckboxColumn("checked", "Select");
	}

	public void disableButtons() {
		addButton.setDisabled(true);
		removeButton.setDisabled(true);
	}

	public void enableButtons() {
		addButton.setDisabled(false);
		removeButton.setDisabled(false);
	}

	public void setVisible(boolean isVisible) {
		allTable.setVisible(isVisible);
		assignedTable.setVisible(isVisible);
		addButton.setVisible(isVisible);
		removeButton.setVisible(isVisible);
	}

	// getters and setters
	public ITable getAssignedTable() {
		return assignedTable;
	}

	public void setAssignedTable(ITable assignedTable) {
		this.assignedTable = assignedTable;
	}

	public ITable getAllTable() {
		return allTable;
	}

	public void setAllTable(ITable allTable) {
		this.allTable = allTable;
	}

	public String getAllTitleKey() {
		return allTitleKey;
	}

	public void setAllTitleKey(String allTitleKey) {
		this.allTitleKey = allTitleKey;
	}

	public String getAssignedTitleKey() {
		return assignedTitleKey;
	}

	public void setAssignedTitleKey(String assignedTitleKey) {
		this.assignedTitleKey = assignedTitleKey;
	}

	public void setTablesEditable(boolean tablesEditable) {
		this.tablesEditable = tablesEditable;
	}

	public boolean isTablesEditable() {
		return tablesEditable;
	}

	public int getTablesWidth() {
		return tablesWidth;
	}

	public void setTablesWidth(int tablesWidth) {
		this.tablesWidth = tablesWidth;
	}

	public IActionListener getAddListener() {
		return addListener;
	}

	public void setAddListener(IActionListener addListener) {
		this.addListener = addListener;
	}

	public IActionListener getRemoveListener() {
		return removeListener;
	}

	public void setRemoveListener(IActionListener removeListener) {
		this.removeListener = removeListener;
	}

	public IButton getAddButton() {
		return addButton;
	}

	public void setAddButton(IButton addButton) {
		this.addButton = addButton;
	}

	public IButton getRemoveButton() {
		return removeButton;
	}

	public void setRemoveButton(IButton removeButton) {
		this.removeButton = removeButton;
	}
}
