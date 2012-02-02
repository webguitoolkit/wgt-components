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

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.webguitoolkit.components.preview.AbstractPreviewView;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.container.Canvas;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.IServerEventListener;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.layout.TableLayout;

public class XMLViewer extends AbstractPreviewView {

	private Canvas tree, element;
	private String filename;
	private File file;
	private XMLTree treeView;
	private String xmlSource;

	public XMLViewer(String xmlSource,WebGuiFactory factory, ICanvas viewConnector) {
		super(factory, viewConnector);
		this.xmlSource = xmlSource;
	}
	public XMLViewer(WebGuiFactory factory, ICanvas viewConnector, String xmlFilePath) {
		super(factory, viewConnector);
		filename = xmlFilePath;
	}
	public XMLViewer(WebGuiFactory factory, ICanvas viewConnector, File xmlFile) {
		super(factory, viewConnector);
		file = xmlFile;
	}

	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		TableLayout layout = getFactory().newTableLayout(viewConnector);

		tree = getFactory().newCanvas(layout);
		layout.getCurrentCell().setVAlign("top");
		element  = getFactory().newCanvas(layout);
		layout.getCurrentCell().setVAlign("top");

		IServerEventListener listener = new IServerEventListener(){

			public void handle(ServerEvent event) {
				// open a window where we show the attributes of the element
				Element theElement = (Element) event.getParameter( XMLTree.EVENT_PRPOPERTY_ELEMENT );
				ElementView elementView = new ElementView( getFactory(), element, theElement );
				elementView.show();
			}

		};


		if(file!=null){
			treeView =	new XMLTree(getFactory(),tree,file);
		} else if(StringUtils.isNotBlank(filename)) {
			treeView =	new XMLTree(getFactory(),tree,filename);
		} else if(StringUtils.isNotBlank(xmlSource)) {
			treeView =	new XMLTree(xmlSource,getFactory(),tree);
		}
		treeView.registerListener( XMLTree.EVENT_ELEMENT_CLICKED, listener );
		treeView.show();



	}
}
