package org.webguitoolkit.components.assortmenttables;

import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.form.IButton;

/**
 * This is an extension to the AssortmentTablesWidget: The assigned columns are arbitrarily sortable and the sorting order can be
 * persisted.
 * 
 * @author i102487
 * 
 */
public class SortableAssortmentTablesWidget extends AssortmentTablesWidget {

	public static final String UP_BUTTON_ID = "UP_BUTTON_ID";
	public static final String DOWN_BUTTON_ID = "DOWN_BUTTON_ID";

	public static final int ASSORTMENT_ACTION_SORT_UP = 2;
	public static final int ASSORTMENT_ACTION_SORT_DOWN = 3;

	private String tooltipKeyDown, tooltipKeyUp;
	private IActionListener sortDownListener;
	private IActionListener sortUpListener;

	private IButton upButton, downButton;
	
	/**
	 * @param id the id for the component that operates as prefix for the ids of the single controls of the AssortmentTablesWidget
	 */
	public SortableAssortmentTablesWidget(WebGuiFactory factory, ICanvas viewConnector, String theAllTitle, String theAssignedTitle,
			String id, String tooltipKeyDown, String tooltipKeyUp) {
		super(factory, viewConnector, theAllTitle, theAssignedTitle, id);

		this.tooltipKeyDown = tooltipKeyDown;
		this.tooltipKeyUp = tooltipKeyUp;
	}

	/**
	 * 
	 * @param factory
	 * @param viewConnector
	 * @param id the id for the component that operates as prefix for the ids of the single controls of the AssortmentTablesWidget
	 */
	public SortableAssortmentTablesWidget(WebGuiFactory factory, ICanvas viewConnector, String id) {
		super(factory, viewConnector, id);
	}

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {
		super.createControls(factory, viewConnector);

		upButton = factory.createButton(assignedTable, "./images/components/assortmenttables/up.gif", null,
				tooltipKeyUp, getSortUpListener(), this.id + UP_BUTTON_ID);

		downButton = factory.createButton(assignedTable, "./images/components/assortmenttables/down.gif", null,
				tooltipKeyDown, getSortDownListener(), this.id + DOWN_BUTTON_ID);
	}

	public IActionListener getSortDownListener() {
		if (sortDownListener == null) {
			sortDownListener = new SortDownButtonListener(ASSORTMENT_ACTION_SORT_DOWN, this);
		}

		return sortDownListener;
	}

	public void setSortDownListener(IActionListener sortDownListener) {
		this.sortDownListener = sortDownListener;
	}

	public IActionListener getSortUpListener() {
		if (sortUpListener == null) {
			sortUpListener = new SortUpButtonListener(ASSORTMENT_ACTION_SORT_UP, this);
		}

		return sortUpListener;
	}

	public void setSortUpListener(IActionListener sortUpListener) {
		this.sortUpListener = sortUpListener;
	}

	public void disableButtons() {
		downButton.setDisabled(true);
		upButton.setDisabled(true);
		super.disableButtons();
	}

	public void enableButtons() {
		downButton.setDisabled(false);
		upButton.setDisabled(false);
		super.enableButtons();
	}
	
	public void setVisible(boolean isVisible) {
		upButton.setVisible(isVisible);
		downButton.setVisible(isVisible);
		super.setVisible(isVisible);
	}

	/*
	 * can be overridden - see setter
	 */
	private class SortUpButtonListener extends AbstractSortableAssortmentTablesListener {

		private SortUpButtonListener(int theActionType, SortableAssortmentTablesWidget theAtw) {
			super(theActionType, theAtw);
		}

		@Override
		public void addToModel(IDataBag bag, Object parent) {
		}

		@Override
		public void commit() {
		}

		@Override
		public Object determineAssociatedParent() {
			return null;
		}

		@Override
		public void removeFromModel(IDataBag bag, Object parent) {
		}

	}

	/*
	 * can be overridden - see setter
	 */
	private class SortDownButtonListener extends AbstractSortableAssortmentTablesListener {

		private SortDownButtonListener(int theActionType, SortableAssortmentTablesWidget theAtw) {
			super(theActionType, theAtw);
		}

		@Override
		public void addToModel(IDataBag bag, Object parent) {
		}

		@Override
		public void commit() {
		}

		@Override
		public Object determineAssociatedParent() {
			return null;
		}

		@Override
		public void removeFromModel(IDataBag bag, Object parent) {
		}

	}
}
