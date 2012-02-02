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
package org.webguitoolkit.components.xml;

import org.jdom.Element;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.layout.TableLayout;
import org.webguitoolkit.ui.controls.table.Table;
import org.webguitoolkit.ui.controls.table.TableColumn;


public class ElementView extends AbstractView {
	private Element element;

	public ElementView(WebGuiFactory factory, ICanvas viewConnector,
			Element element) {
		super(factory, viewConnector);
		this.element = element;
	}

	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		int chidren = 0;
		TableLayout layout = getFactory().newTableLayout(viewConnector);

		getFactory().newLabel(layout, "Name: ");
		getFactory().newLabel(layout, element.getName());
		
		layout.newLine();
		getFactory().newLabel(layout, "Namespace Prefix: ");
		getFactory().newLabel(layout, element.getNamespacePrefix());
		
		layout.newLine();
		getFactory().newLabel(layout, "Namespace URI: ");
		getFactory().newLabel(layout, element.getNamespaceURI());
		
		layout.newLine();
		getFactory().newLabel(layout, "Qualified Name: ");
		getFactory().newLabel(layout, element.getQualifiedName());
				
		layout.newLine();
		getFactory().newLabel(layout, "Value: ");
		getFactory().newLabel(layout, element.getTextTrim());

		if (element.getChildren() != null)
			chidren = element.getChildren().size();

		layout.newLine();
		getFactory().newLabel(layout, "Children: ");
		getFactory().newLabel(layout, String.valueOf(chidren));
		
		if (element.getAttributes() != null && !element.getAttributes().isEmpty()) {
			layout.newLine();
			Table attributes = getFactory().newTable(layout, "Attributes", 5);
			attributes.getStyle().addWidth("390px");
			TableColumn name = getFactory().newTableColumn(attributes, "Name",
					"name", true);
			TableColumn val = getFactory().newTableColumn(attributes, "Value",
					"value", true);
			attributes.getDefaultModel().setTableData(element.getAttributes());
			attributes.reload();
			layout.getCurrentCell().setColSpan(2);
		}



	}

}
