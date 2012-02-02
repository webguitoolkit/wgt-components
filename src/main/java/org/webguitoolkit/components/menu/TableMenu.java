package org.webguitoolkit.components.menu;

import java.util.HashMap;
import java.util.Map;

import org.webguitoolkit.ui.base.WGTException;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IActionListener;
import org.webguitoolkit.ui.controls.form.IButton;
import org.webguitoolkit.ui.controls.form.ILabel;
import org.webguitoolkit.ui.controls.layout.ITableLayout;

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

/**
 * View to render a menu in a wgt TableLayout. required css classes:
 * 
 * 
 * .wgtComponentMenuLayout { background-color: #cbd5e1; margin-top:0px; margin-left:0px; border-spacing: 0px; width: 100%; }
 * .wgtComponentMenuButton:hover { color:blue; text-decoration:underline; }
 * 
 * .wgtComponentMenuButton{ color:black; text-decoration:none; }
 * 
 * .wgtComponentMenuLayoutCell{ padding: 0px; margin: 0px; border-bottom: 1px solid #FFFFFF; } .wgtComponentMenuLayoutCell_over{
 * background-color: #C2D0E1; } .wgtComponentMenuLayoutCell_active{ background-color: #A1BEE1; }
 * 
 * @author BK
 * 
 */
public class TableMenu extends AbstractView {
	private TableMenuItemMap menuItems;
	private String activeCellId = null;
	private boolean mouseOver = true;
	private TableMenuGroup group = null;
	private Map<String, String> idMap = null;

	/**
	 * @author BK
	 * 
	 *         This listener listens to the menu changes and calls the user defined action
	 */
	private class MenuItemActionListener implements IActionListener {
		private IActionListener originalListener = null;

		public MenuItemActionListener(IActionListener originalListener) {
			this.originalListener = originalListener;
		}

		public void onAction(ClientEvent event) {
			// determine what cell must be updated
			String newActiveCellId = ((IButton)event.getSource()).getId() + "_td";

			changeSelection(newActiveCellId);

			// now call the actual listener.
			if (originalListener != null)
				originalListener.onAction(event);

		}
	}

	public void reset() {
		if (getActiveCellId() != null) {
			// remove active-class from previous selected entry and add to the new one.
			getPage().getContext().sendJavaScript(String.valueOf(this.hashCode()),
					"jQuery('#" + getActiveCellId() + "').removeClass('wgtComponentMenuLayoutCell_active');");
		}
	}

	public TableMenu(WebGuiFactory factory, ICanvas viewConnector) {
		super(factory, viewConnector);
		menuItems = new TableMenuItemMap();
	}

	public TableMenu(WebGuiFactory factory, ICanvas viewConnector, TableMenuGroup group) {
		this(factory, viewConnector);
		this.setGroup(group);
	}

	@Override
	protected void createControls(WebGuiFactory factory, ICanvas viewConnector) {

		// main layout for menu
		ITableLayout menu = factory.createTableLayout(viewConnector);
		menu.addCssClass("wgtComponentMenuLayout");
		menu.getEcsTable().setCellSpacing(0);

		// add items
		if (menuItems.size() > 0) {
			idMap = new HashMap<String, String>();
			for (String key : menuItems) {
				IActionListener l = menuItems.get(key);
				if (l != null) {
					IButton b = factory.createLinkButton(menu, null, key, "", new MenuItemActionListener(l));
					b.addCssClass("wgtComponentMenuButton");
					menu.getCurrentCell().setID(b.getId() + "_td");
					idMap.put(key, b.getId() + "_td");
					menu.getCurrentCell().setClass("wgtComponentMenuLayoutCell");
					if (mouseOver) {
						menu.getCurrentCell().setOnMouseOver("classAdd( this, '" + "wgtComponentMenuLayoutCell_over' );");
						menu.getCurrentCell().setOnMouseOut("classRemove( this, '" + "wgtComponentMenuLayoutCell_over' );");
					}
				}
				else {
					ILabel b = factory.createLabel(menu, key);
					b.addCssClass("wgtComponentMenuButton");
					menu.getCurrentCell().setID(b.getId() + "_td");
					idMap.put(key, b.getId() + "_td");
					menu.getCurrentCell().setClass("wgtComponentMenuLayoutCell");
					// if (mouseOver) {
					// menu.getCurrentCell().setOnMouseOver("classAdd( this, '" + "wgtComponentMenuLayoutCell_over' );");
					// menu.getCurrentCell().setOnMouseOut("classRemove( this, '" + "wgtComponentMenuLayoutCell_over' );");
					// }

				}
				menu.newRow();
			}
		}
	}

	/**
	 * add menu entry at the end. Must be before the view is rendered
	 * 
	 * @param labelKey text representation for the MenuItem
	 * @param listener listener for the menuItem
	 */
	public void addMenuItem(String labelKey, IActionListener listener) {
		menuItems.put(labelKey, listener);
	}

	/**
	 * add menu entry at a specified position. Must be before the view is rendered
	 * 
	 * @param label text representation for the MenuItem
	 * @param listener listener for the menuItem
	 * @param index
	 */
	public void addMenuItem(String labelKey, IActionListener listener, int index) {
		menuItems.put(labelKey, listener, index);
	}

	/**
	 * show mouser-over effect, default = true.
	 * 
	 * @param mouseOver
	 */
	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	protected void setGroup(TableMenuGroup group) {
		this.group = group;
	}

	protected TableMenuGroup getGroup() {
		return group;
	}

	private void setActiveCellId(String activeCellId) {
		if (getGroup() == null)
			this.activeCellId = activeCellId;
		else
			this.getGroup().setGroupActiveCellId(activeCellId);
	}

	private String getActiveCellId() {
		if (getGroup() == null)
			return activeCellId;
		else
			return this.getGroup().getGroupActiveCellId();
	}

	private void changeSelection(String newActiveCellId) {
		// switch style
		if (getActiveCellId() != null) {
			// remove active-class from previous selected entry and add to the new one.
			getPage().getContext().sendJavaScript(
					String.valueOf(this.getClass().hashCode()),
					"jQuery('#" + getActiveCellId() + "').removeClass('wgtComponentMenuLayoutCell_active');jQuery('#" + newActiveCellId
							+ "').addClass('wgtComponentMenuLayoutCell_active');");
		}
		else {
			// add active-class to the new entry.
			getPage().getContext().sendJavaScript(String.valueOf(this.getClass().hashCode()),
					"jQuery('#" + newActiveCellId + "').addClass('wgtComponentMenuLayoutCell_active');");
		}

		// remember active ID
		setActiveCellId(newActiveCellId);
	}

	/**
	 * Change menu from your programms logic. Keep in mind: if triggering the event (fireEvent==true), the event itself will be
	 * null in the onAction-Method of your listener.
	 * 
	 * @param menuItemKey
	 * @param fireEvent
	 */
	public void changeMenuItemSelection(String menuItemKey, boolean fireEvent) {
		String id = idMap.get(menuItemKey);
		if (id == null) {
			throw new WGTException("Unkown Menu Item key " + menuItemKey);
		}
		changeSelection(id);

		if (fireEvent) {
			IActionListener originalListener = menuItems.get(menuItemKey);
			if (originalListener != null) {
				originalListener.onAction(null);
			}
		}
	}
}
