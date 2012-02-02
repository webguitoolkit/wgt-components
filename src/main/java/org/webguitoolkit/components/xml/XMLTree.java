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
import java.io.StringReader;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.ServerEvent;
import org.webguitoolkit.ui.controls.tree.AbstractTreeListener;
import org.webguitoolkit.ui.controls.tree.GenericTreeModel;
import org.webguitoolkit.ui.controls.tree.GenericTreeNode;
import org.webguitoolkit.ui.controls.tree.ITree;
import org.webguitoolkit.ui.controls.tree.ITreeListener;
import org.webguitoolkit.ui.controls.tree.Tree;
import org.webguitoolkit.ui.controls.tree.TreeNodeHandler;


/**
 * <pre>
 * Reusable component for displaying XML documents as a tree.
 * </pre>
 */
public class XMLTree extends AbstractView {

	public static final int EVENT_ELEMENT_CLICKED = 0;
	public static final String EVENT_PRPOPERTY_ELEMENT = "element";

	protected Document xml = null;
	protected Tree theTree = null;

	public XMLTree(WebGuiFactory factory, ICanvas viewConnector, String xmlFilePath) {
		super(factory, viewConnector);
		xml = readFile(xmlFilePath);
	}

	public XMLTree(String xmlSource, WebGuiFactory factory, ICanvas viewConnector) {
		super(factory, viewConnector);
		xml = readFileFromSource(xmlSource);
	}
	public XMLTree(WebGuiFactory factory, ICanvas viewConnector, File xmlFile) {
		super(factory, viewConnector);
		xml = readFile(xmlFile);
	}

	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		// create tree
		theTree = getFactory().newTree( viewConnector );

		GenericTreeModel model = new GenericTreeModel(true,true,false,false);

		// handler for document nodes
		TreeNodeHandler rootNodeHandler = new TreeNodeHandler( Document.class );
		rootNodeHandler.setChildSelectors( new String[]{ "rootElement" } );
		rootNodeHandler.setDisplayProperty( "baseURI" );

		// handler for element nodes
		ITreeListener listener = new TreeListener();
		TreeNodeHandler childNodeHandler = new TreeNodeHandler( Element.class );
		childNodeHandler.setChildSelectors( new String[]{ "children" } );
		childNodeHandler.setDisplayProperty( "name" );
		childNodeHandler.setListener(listener);

		model.addTreeNodeHandler( rootNodeHandler );
		model.addTreeNodeHandler( childNodeHandler );
		model.setRoot( xml );

		// add model and load data
		theTree.setModel( model );
		theTree.load();
	}

	public void setTreeData( String xmlFilePath ){
		xml = readFile(xmlFilePath);
		if( theTree!=null ){
			((GenericTreeModel)theTree.getModel()).clearLoaded();
			((GenericTreeModel)theTree.getModel()).setRoot(xml);
			theTree.load();
		}
	}
	public void setTreeData( File xmlFile ){
		xml = readFile( xmlFile );
		if( theTree!=null ){
			((GenericTreeModel)theTree.getModel()).clearLoaded();
			((GenericTreeModel)theTree.getModel()).setRoot(xml);
			theTree.load();
		}
	}

	private Document readFile(String in) {
		Document tld = null;
		try {
			SAXBuilder b = new SAXBuilder(false);
			tld = b.build(in);
		} catch (Exception j) {
			j.printStackTrace();
		}
		return tld;
	}
	private Document readFile(File in) {
		Document tld = null;
		try {
			SAXBuilder b = new SAXBuilder(false);
			tld = b.build(in);
		} catch (Exception j) {
			j.printStackTrace();
		}
		return tld;
	}

	private Document readFileFromSource(String in) {
		Document tld = null;
		try {
			SAXBuilder b = new SAXBuilder(false);
			tld = b.build(new StringReader(in));
		} catch (Exception j) {
			j.printStackTrace();
		}
		return tld;
	}

	public class TreeListener extends AbstractTreeListener{
		@Override
		public void onTreeNodeClicked(ITree tree, String nodeId) {
			ServerEvent event = new ServerEvent( EVENT_ELEMENT_CLICKED );
			GenericTreeNode treeNode = (GenericTreeNode) tree.getModel().getTreeNode( nodeId );
			event.putParameter( EVENT_PRPOPERTY_ELEMENT, treeNode.getDataObject().getDelegate() );
			fireServerEvent(event);
		}
	};

}
