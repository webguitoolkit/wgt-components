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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.webguitoolkit.ui.controls.util.validation.IValidator;

/**
 * @author BK
 * 
 */
public class SelectionHelperModel {

	public static final int SELECTION_MODE_SINGLE = 0;
	public static final int SELECTION_MODE_MULTIPLE = 1;

	private String noSelectionErrorKey, tableWidth;
	private String tableConfig;
	private int selectionMode = 0; // default
	private String[] columns;
	private HashMap resourceKeys;
	private HashMap defaultSearchValues;
	private HashMap selectValues;
	private HashMap searchFieldValidators;
	private List additionalSearchFields;
	private List excludedSearchFields;
	private List data;
	private int rows = 10;
	private int sortByColumn = -1;
	private boolean searchEnabled = false;
	private String windowTitle;

	/**
	 * @return the rows
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * @param rows
	 *            the rows to set
	 */
	public void setRows(int rows) {
		this.rows = rows;
	}

	public void addResourceKey(String locator, String resourceKey) {
		if (resourceKeys == null)
			resourceKeys = new HashMap();

		resourceKeys.put(locator, resourceKey);
	}
	
	protected void addSearchFieldValidator(String locator, IValidator validator) {
		if (searchFieldValidators == null)
			searchFieldValidators = new HashMap();

		searchFieldValidators.put(locator, validator);
	}

	/**
	 * if you need a search field as select, please use this method
	 * Must be a collection of String[]{id,value}
	 * 
	 * @param locator
	 * @param newSelectValues
	 */
	public void addSelectValues(String locator, Collection newSelectValues) {
		if (selectValues == null)
			selectValues = new HashMap();

		selectValues.put(locator, newSelectValues);
	}
	
	/**
	 * @param locator
	 * @param defaultValue
	 */
	public void addDefaultSearchValue(String locator, String defaultValue) {
		if (defaultSearchValues == null)
			defaultSearchValues = new HashMap();

		defaultSearchValues.put(locator, defaultValue);
	}

	public void addAdditionalSearchFied(String locator, String resourceKey, String defaultValue) {
		if (additionalSearchFields == null)
			additionalSearchFields = new ArrayList();

		additionalSearchFields.add(locator);
		if (resourceKey != null)
			this.addResourceKey(locator, resourceKey);

		if (defaultValue != null)
			this.addDefaultSearchValue(locator, defaultValue);
	}
	
	public void addAdditionalSearchFied(String locator, String resourceKey, String defaultValue, IValidator validator) {
		addAdditionalSearchFied(locator, resourceKey, defaultValue);

		if (validator != null)
			this.addSearchFieldValidator(locator, validator);
	}

	protected String getDefaultSearchValue(String locator) {
		String result = null;
		if (defaultSearchValues != null && defaultSearchValues.get(locator) != null) {
			result = (String) defaultSearchValues.get(locator);
		}
		return result;
	}
	
	protected Collection getSelectValues(String locator) {
		Collection result = null;
		if (selectValues != null && selectValues.get(locator) != null) {
			result = (Collection) selectValues.get(locator);
		}
		return result;
	}

	public void excludeSearchfiled(String locator) {
		if (excludedSearchFields == null)
			excludedSearchFields = new ArrayList();
		excludedSearchFields.add(locator);
	}

	protected String getReourceKey(String locator) {
		String result = null;
		if (resourceKeys != null && resourceKeys.get(locator) != null) {
			result = (String) resourceKeys.get(locator);
		}
		return result;
	}

	public boolean isSearchable(String locator) {
		if (excludedSearchFields == null) {
			return true;
		} else {
			return !excludedSearchFields.contains(locator);
		}
	}

	public boolean isInColumns(String locator) {
		if (columns == null || locator == null) {
			return false;
		} else {
			for (int i = 0; i < columns.length; i++) {
				if (columns[i] != null && columns[i].equals(locator))
					return true;
			}
			return false;
		}
	}

	/**
	 * @return the noSelectionErrorKey
	 */
	public String getNoSelectionErrorKey() {

		if (noSelectionErrorKey != null) {
			if (noSelectionErrorKey.indexOf("@") == -1)
				this.noSelectionErrorKey += "@Please make a selecttion";
		} else {
			this.noSelectionErrorKey = "Please make a selecttion";
		}
		return noSelectionErrorKey;

	}

	/**
	 * set message key to show error message (by default an English message is
	 * shown)
	 * 
	 * @param noSelectionErrorKey
	 *            the noSelectionErrorKey to set
	 */
	public void setNoSelectionErrorKey(String noSelectionErrorKey) {
		this.noSelectionErrorKey = noSelectionErrorKey;
	}

	/**
	 * @return the columns
	 */
	public String[] getColumns() {
		return columns;
	}

	/**
	 * Specify the properties of data elements to show in the table
	 * 
	 * @param columns
	 *            the columns to set
	 */
	public void setColumns(String[] columns) {
		this.columns = columns;
	}

	/**
	 * @return the data
	 */
	public List getData() {
		return data;
	}

	/**
	 * @param data
	 *            the data to set
	 */
	public void setData(List data) {
		this.data = data;
	}

	public boolean isValid() {
		if (columns != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @return the tableWidth
	 */
	public String getTableWidth() {
		if (tableWidth == null) {
			return "100%"; // default
		}

		return tableWidth;
	}

	/**
	 * use css style i.e 120px
	 * 
	 * @param tableWidth
	 *            the tableWidth to set
	 */
	public void setTableWidth(String tableWidth) {
		this.tableWidth = tableWidth;
	}

	/**
	 * @return the searchEnabled
	 */
	public boolean isSearchEnabled() {
		return searchEnabled;
	}

	/**
	 * @param searchEnabled
	 *            the searchEnabled to set
	 */
	public void setSearchEnabled(boolean searchEnabled) {
		this.searchEnabled = searchEnabled;
	}

	/**
	 * @return the additionalSearchFields
	 */
	protected List getAdditionalSearchFields() {
		return additionalSearchFields;
	}

	/**
	 * @return the selectionMode
	 */
	public int getSelectionMode() {
		return selectionMode;
	}

	/**
	 * @param selectionMode the selectionMode to set
	 */
	public void setSelectionMode(int selectionMode) {
		this.selectionMode = selectionMode;
	}

	/**
	 * @return the sortByColumn
	 */
	public int getSortByColumn() {
		return sortByColumn;
	}

	/**
	 * @param sortByColumn the sortByColumn to set
	 */
	public void setSortByColumn(int sortByColumn) {
		this.sortByColumn = sortByColumn;
	}

	public void setResourceKeys(String[] columnTitles) {
		// TODO Auto-generated method stub
		
	}

	public String getWindowTitle() {
		return windowTitle;
	}

	public void setWindowTitle(String windowTitle) {
		this.windowTitle = windowTitle;
	}

	public HashMap getSearchFieldValidators() {
		return searchFieldValidators;
	}
	
	protected IValidator getSearchFieldValidator(String locator) {
		IValidator result = null;
		if (searchFieldValidators != null && searchFieldValidators.get(locator) != null) {
			result = (IValidator) searchFieldValidators.get(locator);
		}
		return result;
	}

	public void setTableConfig(String tableConfig) {
		this.tableConfig = tableConfig;
	}

	public String getTableConfig() {
		return tableConfig;
	}

}
