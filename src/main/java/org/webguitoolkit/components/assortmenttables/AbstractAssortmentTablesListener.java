package org.webguitoolkit.components.assortmenttables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.webguitoolkit.components.assortmenttables.AssortmentTablesWidget;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.table.ITable;

public abstract class AbstractAssortmentTablesListener implements IActionListener {
	private AssortmentTablesWidget atw;
	private ITable allTable, assignedTable;
	protected int actiontype;
	public List<IDataBag> allBags, filteredBags, myBags;

	public AbstractAssortmentTablesListener(int actiontype, AssortmentTablesWidget atw) {
		setActiontype(actiontype);
		setAtw(atw);
	}

	public void onAction(ClientEvent event) {
		allTable = atw.getAllTable();
		assignedTable = atw.getAssignedTable();

		switch (actiontype) {
			case AssortmentTablesWidget.ASSORTMENT_ACTION_ADD:
				this.addAction();
				break;
			case AssortmentTablesWidget.ASSORTMENT_ACTION_REMOVE:
				this.removeAction();
				break;
		}

	}

	protected void addAction() {
		// get the associated parent object to that the data is saved to
		Object parent = determineAssociatedParent();
		if (parent != null) {
			allTable.save2Bag();
			myBags = assignedTable.getDefaultModel().getTableData();
			filteredBags = allTable.getDefaultModel().getFilteredList();
			allBags = new ArrayList(allTable.getDefaultModel().getTableData());
			Integer orderNumber = myBags.size();
			for (Iterator iter = filteredBags.iterator(); iter.hasNext();) {
				IDataBag bag = (IDataBag)iter.next();
				try {
					orderNumber++;
					bag.setObject("order", orderNumber);
					if (bag.getObject("checked").equals(Boolean.TRUE)) {
						bag.setObject("checked", new Boolean(false));
						myBags.add(bag);
						allBags.remove(bag);

						// save data to selected user
						addToModel(bag, parent);
					}
				}
				catch (NullPointerException e) {
					bag.setObject("checked", new Boolean(false));
				}
			}

			commit();

			allTable.getDefaultModel().setTableData(allBags);
			allTable.reload();
			assignedTable.reload();
		}
	}

	protected void removeAction() {
		Object parent = determineAssociatedParent();
		if (parent != null) {
			assignedTable.save2Bag();
			allBags = allTable.getDefaultModel().getTableData();
			filteredBags = assignedTable.getDefaultModel().getFilteredList();
			myBags = new ArrayList(assignedTable.getDefaultModel().getTableData());
			for (Iterator iter = filteredBags.iterator(); iter.hasNext();) {
				IDataBag bag = (IDataBag)iter.next();
				try {
					if (bag.getObject("checked").equals(Boolean.TRUE)) {
						bag.setObject("checked", new Boolean(false));
						allBags.add(bag);
						myBags.remove(bag);
						removeFromModel(bag, parent);
					}
				}
				catch (NullPointerException e) {
					bag.setObject("checked", new Boolean(false));
				}
			}

			commit();

			assignedTable.getDefaultModel().setTableData(myBags);
			assignedTable.reload();
			allTable.reload();
		}
	}

	// abstract declarations
	/**
	 * determine the object to that the displayed data belongs and has to be saved to
	 */
	public abstract Object determineAssociatedParent();

	/**
	 * add the data - defined by the data bag of the all-table - to the delegate object
	 * 
	 * @param bag the data bag
	 * @param parent the delegate object
	 */
	public abstract void addToModel(IDataBag bag, Object parent);

	/**
	 * remove the data - defined by the data bag of the assigned-table - from the delegate object
	 * 
	 * @param bag the data bag
	 * @param parent the delegate object
	 */
	public abstract void removeFromModel(IDataBag bag, Object parent);

	/**
	 * commit to persist; if you do not want to commit on every assortment you have to refresh the parent object!
	 */
	public abstract void commit();

	// Getters and Setters
	public int getActiontype() {
		return actiontype;
	}

	/**
	 * set the actiontype
	 * 
	 * @param actiontype the actiontype: AssortmentTablesWidget.ASSORTMENT_ACTION_...
	 */
	public void setActiontype(int actiontype) {
		this.actiontype = actiontype;
	}

	public void setAtw(AssortmentTablesWidget atw) {
		this.atw = atw;
	}

	public AssortmentTablesWidget getAtw() {
		return atw;
	}

	public List<IDataBag> getAllBags() {
		return allBags;
	}

	public void setAllBags(List<IDataBag> allBags) {
		this.allBags = allBags;
	}

	public List<IDataBag> getMyBags() {
		return myBags;
	}

	public void setMyBags(List<IDataBag> myBags) {
		this.myBags = myBags;
	}

	protected ITable getAssignedTable() {
		return assignedTable;
	}

	protected void setAssignedTable(ITable assignedTable) {
		this.assignedTable = assignedTable;
	}

	protected ITable getAllTable() {
		return allTable;
	}

	protected void setAllTable(ITable allTable) {
		this.allTable = allTable;
	}
}
