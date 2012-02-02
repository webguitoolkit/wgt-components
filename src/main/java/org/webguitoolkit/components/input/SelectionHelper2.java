/*
Copyright 2008 Endress+Hauser Infoserve GmbH&Co KG 
Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied. See the License for the specific language governing permissions 
and limitations under the License.
*/ 
package org.webguitoolkit.components.input;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.webguitoolkit.ui.base.DataBag;
import org.webguitoolkit.ui.base.IDataBag;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractPopup;
import org.webguitoolkit.ui.controls.Page;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.form.Button;
import org.webguitoolkit.ui.controls.form.Compound;
import org.webguitoolkit.ui.controls.form.Label;
import org.webguitoolkit.ui.controls.form.Text;
import org.webguitoolkit.ui.controls.layout.TableLayout;
import org.webguitoolkit.ui.controls.table.AbstractTableListener;
import org.webguitoolkit.ui.controls.table.ITable;
import org.webguitoolkit.ui.controls.table.Table;
import org.webguitoolkit.ui.controls.util.TextService;

/**
 * replaces select-control for large selections. load this view into an
 * abstratcDialog
 * 
 * @author BK
 * 
 */
public class SelectionHelper2 extends AbstractPopup {

	private Table valueTable;
	private Button apply, cancel, search;
	private SelectionHelperModel model;
	private Compound searchCompound;

	public static final int SELECTION_EVENT = 8451;
	public static final int SEARCH_EVENT = 1548;
	public static final int TABLE_LAYOUT_EDIT_EVENT = 96485;
	public static final String SELECTION_EVENT_PARAMETER = "selectiondata";
	public static final String SEARCH_EVENT_PARAMETER_BAG = "searchbag";
	public static final String SEARCH_EVENT_PARAMETER_HELPER = "thisHelper";
	private static final String SELECTION_CHECKBOX_PROPERTY = "selchkbx";
	public static final String TABLE_LAYOUT_EDIT_EVENT_PARAMETER = "tbllytedtevtprm";
	private static final int DEFAULT_WIDTH = 480;
	private static final int DEFAULT_HEIGHT = 330;
	
	public SelectionHelper2( WebGuiFactory factory, Page page, SelectionHelperModel model ) {
		super(factory, page, model.getWindowTitle(), DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.model = model;
	}


	private class SearchListener implements IActionListener {
		public void onAction(ClientEvent event) {
			if (event.getSource() == search) {
				// fire search event back to caller!
				ServerEvent serverevent = new ServerEvent(SelectionHelper2.SEARCH_EVENT);

				// find entered fields
				searchCompound.save();
				
				//check input
				if (searchCompound.hasErrors())
					return;
				
				IDataBag searchbag = searchCompound.getBag();
				searchbag.save();
				searchbag.addProperty(SEARCH_EVENT_PARAMETER_HELPER, SelectionHelper2.this);
				serverevent.putParameter(SelectionHelper2.SEARCH_EVENT_PARAMETER_BAG, searchbag);
				fireServerEvent(serverevent);
			}
		}
	}


	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		TableLayout grid = getFactory().newTableLayout(viewConnector);
		grid.getEcsTable().setStyle("width: 100%;");

		if (model != null && model.isSearchEnabled() && model.isValid()) {
			searchCompound = getFactory().newCompound(grid);
			{
				// render search
				IDataBag searchBag = new DataBag(null);
				TableLayout searchGrid = getFactory().newTableLayout(searchCompound);
				searchGrid.getEcsTable().setStyle("width: 100%;");
				for (int i = 0; i < model.getColumns().length; i++) {
					Label l = null;
					if (model.isSearchable(model.getColumns()[i])) {
						if (model.getReourceKey(model.getColumns()[i]) != null) {
							l = getFactory().newLabel(searchGrid, TextService.getString(model.getReourceKey(model.getColumns()[i])));
						} else {
							l = getFactory().newLabel(searchGrid, SelectionHelper2.createName(model.getColumns()[i]));
						}
						Text input = getFactory().newText(searchGrid, model.getColumns()[i], l);
						if(model.getSearchFieldValidator(model.getColumns()[i])!=null){
							input.addValidator(model.getSearchFieldValidator(model.getColumns()[i]));
						}
						searchBag.addProperty(model.getColumns()[i], model.getDefaultSearchValue(model.getColumns()[i]));
						searchGrid.newLine();
					}
				}
				// add possible additiona searchfields
				if (model.getAdditionalSearchFields() != null && !model.getAdditionalSearchFields().isEmpty()) {
					Iterator iAdd = model.getAdditionalSearchFields().iterator();
					while (iAdd.hasNext()) {
						String key = (String) iAdd.next();
						// check for searchable and prevent duplicates
						if (model.isSearchable(key) && !model.isInColumns(key)) {
							Label l = null;
							if (model.getReourceKey(key) != null) {
								l = getFactory().newLabel(searchGrid, TextService.getString(model.getReourceKey(key)));
							} else {
								l = getFactory().newLabel(searchGrid, SelectionHelper2.createName(key));
							}
							Text input = getFactory().newText(searchGrid, key, l);
							if(model.getSearchFieldValidator(key)!=null){
								input.addValidator(model.getSearchFieldValidator(key));
							}	
							searchBag.addProperty(key, model.getDefaultSearchValue(key));
							searchGrid.newLine();
						}
					}
				}

				String searchkey = model.getReourceKey(".search");
				String searchtt = model.getReourceKey(".search.tt");
				if (searchkey == null)
					searchkey = "Search";
				if (searchtt == null)
					searchtt = "Search values";

				search = getFactory().newButton(searchGrid, null, searchkey, searchtt, new SearchListener());
				searchGrid.getCurrentCell().setColSpan(2);

				searchCompound.setBag(searchBag);
				searchCompound.load();
			}
			grid.newLine();
		}

		if (model != null && model.isValid()) {
			valueTable = getFactory().newTable(grid, null, model.getRows());
			valueTable.setListener(new SelectionListener());
			valueTable.getStyle().addWidth(model.getTableWidth());

			if (model.getSelectionMode() == SelectionHelperModel.SELECTION_MODE_MULTIPLE)
				valueTable.addCheckboxColumn(SELECTION_CHECKBOX_PROPERTY, "");
			for (int i = 0; i < model.getColumns().length; i++) {
				if (model.getReourceKey(model.getColumns()[i]) != null) {
					getFactory().newTableColumn(valueTable, TextService.getString(model.getReourceKey(model.getColumns()[i])), model.getColumns()[i], !model.isSearchEnabled());
				} else {
					getFactory().newTableColumn(valueTable, SelectionHelper2.createName(model.getColumns()[i]), model.getColumns()[i], !model.isSearchEnabled());
				}
			}
			if(model.getTableConfig()!=null)
				valueTable.loadColumnConfig(model.getTableConfig());			
			this.reload();
			if (model.getSortByColumn() > -1 && model.getSortByColumn() < model.getColumns().length)
				valueTable.sort(model.getSortByColumn());

			if (model.getSelectionMode() == SelectionHelperModel.SELECTION_MODE_MULTIPLE) {
				grid.newLine();
				String aKey = model.getReourceKey(".apply");
				String aTt = model.getReourceKey(".applay.tt");
				if (aKey == null)
					aKey = "Apply";
				if (aTt == null)
					aTt = "Apply selection";

				apply = getFactory().newButton(grid, null, aKey, aTt, new SelectionListener());
			}
		} else {
			getFactory().newLabel(grid, "No data available");
		}
	}

	public void reload() {
		if (model.getData() != null) {
			valueTable.getDefaultModel().setTableData(wrapWithDataBag(model.getData()));
			valueTable.reload();
		}
	}

	private List wrapWithDataBag(List data) {
		List result = new ArrayList(data.size());
		for (Iterator it = data.iterator(); it.hasNext();) {
			Object o = it.next();
			if (!(o instanceof IDataBag))
				result.add(new DataBag(o));
			else
				result.add(o);
		}
		return result;
	}

	private static String createName(String locator) {
		String result = splitByCapitalLetters(locator.substring(0, 1).toUpperCase() + locator.substring(1));
		return result;
	}

	/**
	 * @return the model
	 */
	public SelectionHelperModel getModel() {
		return model;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(SelectionHelperModel model) {
		this.model = model;
	}

	public static String splitByCapitalLetters(String string) {
		StringBuffer result = null;
		if (string != null) {
			int l = string.length();
			for (int i = 0; i < l; i++) {
				if (i == 0) {
					//uppercase first letter in any case
					result = new StringBuffer();
					result.append(Character.toUpperCase(string.charAt(i)));
				} else {
					//insert blank if and lower case if
					//is upper case and previous is lower case orfollowing is lower case
					//this customerSAPNumber - > Customer SAP Number
					if (Character.isUpperCase(string.charAt(i))
							&& (Character.isLowerCase(string.charAt(i - 1)) || (((i + 1) < l) && Character
									.isLowerCase(string.charAt(i + 1))))) {
						result.append(" "
								+ Character.toLowerCase(string.charAt(i)));
					//insert blank before number
					//page123 -> Page 123
					} else if (Character.isDigit(string.charAt(i))
							&& !Character.isDigit(string.charAt(i - 1))) {
						result.append(" " + string.charAt(i));
					} else {
						result.append(string.charAt(i));
					}
				}
			}
			return result.toString();
		}
		return string;
	}
	
	private class SelectionListener extends AbstractTableListener implements IActionListener {
		public void onAction(ClientEvent event) {
			if (event.getSource() == cancel) {
				getViewConnector().getWindowActionListener().onClose(event);
			} else if (event.getSource() == apply && model != null && model.getSelectionMode() == SelectionHelperModel.SELECTION_MODE_MULTIPLE) {
				if (valueTable != null) {
					List selected = new ArrayList();
					valueTable.load();
					Iterator irows = valueTable.getDefaultModel().getTableData().iterator();
					while (irows.hasNext()) {
						IDataBag object = (IDataBag) irows.next();
						if (object != null && object.get(SELECTION_CHECKBOX_PROPERTY) != null && object.getBool(SELECTION_CHECKBOX_PROPERTY)) {
							selected.add(object);
						}
					}
					if (selected.isEmpty()) {
						getPage().sendWarn(model.getNoSelectionErrorKey());
					} else {
						ServerEvent serverevent = new ServerEvent(SelectionHelper2.SELECTION_EVENT);
						serverevent.putParameter(SelectionHelper2.SELECTION_EVENT_PARAMETER, selected);
						fireServerEvent(serverevent);
						getViewConnector().getWindowActionListener().onClose(event);
					}
				} else {
					getPage().sendWarn(model.getNoSelectionErrorKey());
				}
			}
		}

		@Override
		public void onRowSelection(ITable table, int row) {
			// TODO Auto-generated method stub
			if (model != null && model.getSelectionMode() == SelectionHelperModel.SELECTION_MODE_SINGLE) {
				if (((Table)table).getSelectedRowIndex() > -1) {
					IDataBag bag = (IDataBag) valueTable.getSelectedRow();
					ServerEvent serverevent = new ServerEvent(SelectionHelper2.SELECTION_EVENT);
					serverevent.putParameter(SelectionHelper2.SELECTION_EVENT_PARAMETER, bag);
					fireServerEvent(serverevent);
					getViewConnector().getWindowActionListener().onClose(event);
				} else {
					getPage().sendWarn(model.getNoSelectionErrorKey());
				}
			}
		}
		
		@Override
		public void onEditTableLayout(ClientEvent event, int rowCount, String tableSetting) {
			super.onEditTableLayout(event, rowCount, tableSetting);
			//throw event to handle changes.
			ServerEvent serverevent = new ServerEvent(SelectionHelper2.TABLE_LAYOUT_EDIT_EVENT);
			serverevent.putParameter(SelectionHelper2.TABLE_LAYOUT_EDIT_EVENT_PARAMETER, tableSetting);
			fireServerEvent(serverevent);			
		}
	}

}
