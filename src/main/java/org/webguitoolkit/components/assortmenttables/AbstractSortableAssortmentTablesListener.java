package org.webguitoolkit.components.assortmenttables;

import java.util.List;

import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.controls.event.ClientEvent;

public abstract class AbstractSortableAssortmentTablesListener extends AbstractAssortmentTablesListener {

	public AbstractSortableAssortmentTablesListener(int actiontype, SortableAssortmentTablesWidget atw) {
		super(actiontype, atw);
	}

	public void onAction(ClientEvent event) {
		setAllTable(getAtw().getAllTable());
		setAssignedTable(getAtw().getAssignedTable());

		switch (actiontype) {
			case AssortmentTablesWidget.ASSORTMENT_ACTION_ADD:
				super.addAction();
				break;
			case AssortmentTablesWidget.ASSORTMENT_ACTION_REMOVE:
				super.removeAction();
				break;
			case SortableAssortmentTablesWidget.ASSORTMENT_ACTION_SORT_UP:
				this.sortUp();
				break;
			case SortableAssortmentTablesWidget.ASSORTMENT_ACTION_SORT_DOWN:
				this.sortDown();
				break;
		}

	}

	private void sortUpAction() {
		IDataBag selected = getAssignedTable().getSelectedRow();
		List<IDataBag> assignedBags = getAssignedTable().getDefaultModel().getTableData();
		int currentPos = assignedBags.indexOf(selected);
		int newSelection = currentPos;

		if (currentPos > 0) {
			IDataBag db = assignedBags.remove(currentPos);
			assignedBags.add(currentPos - 1, db);
			newSelection = currentPos - 1;
		}

		getAssignedTable().reload();
		getAssignedTable().selectionChange(newSelection);
	}

	private void sortDownAction() {
		IDataBag selected = getAssignedTable().getSelectedRow();
		List<IDataBag> assignedBags = getAssignedTable().getDefaultModel().getTableData();
		int currentPos = assignedBags.indexOf(selected);
		int newSelection = currentPos;

		if (currentPos >= 0 && currentPos < assignedBags.size() - 1) {
			IDataBag db = assignedBags.remove(currentPos + 1);
			assignedBags.add(currentPos, db);
			newSelection = currentPos + 1;
		}

		getAssignedTable().reload();
		getAssignedTable().selectionChange(newSelection);
	}

	/**
	 * this has to be overridden if there should be executed an additional action on sorting the table rows
	 */
	public void sortUp() {
		sortUpAction();
	}

	/**
	 * this has to be overridden if there should be executed an additional action on sorting the table rows
	 */
	public void sortDown() {
		sortDownAction();
	}
}
