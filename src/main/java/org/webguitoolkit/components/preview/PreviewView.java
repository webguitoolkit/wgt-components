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
package org.webguitoolkit.components.preview;

import org.apache.commons.lang.StringUtils;
import org.webguitoolkit.components.text.TextFileView;
import org.webguitoolkit.components.xml.XMLViewer;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.AbstractView;
import org.webguitoolkit.ui.controls.container.ICanvas;
import org.webguitoolkit.ui.controls.event.ClientEvent;
import org.webguitoolkit.ui.controls.event.IServerEventListener;
import org.webguitoolkit.ui.controls.event.ServerEvent;

/**
 * Shows the content of a file based on the contentType. If not content Type is
 * provided (null) guess the content type from the file extension.
 * 
 */
public class PreviewView extends AbstractView {

	public static final String TXT = "TXT";
	public static final String XML = "XML";
	public static final String IMG = "IMG";
	public static final String HTML = "HTML";
	public static final String PDF = "PDF";
	public static final String NONE = "NONE";

	private AbstractPreviewView delegate;

	public PreviewView(WebGuiFactory factory, ICanvas viewConnector, String fileName, String contentType) {
		super(factory, viewConnector);
		if (contentType == null) {
			contentType = getContentTypeFromExtension(fileName);
		}
		if (XML.equals(contentType))
			delegate = new XMLViewer(getFactory(), viewConnector, fileName);
		else if (PDF.equals(contentType))
			delegate = new TextFileView(getFactory(), viewConnector, fileName);
		else
			delegate = new NoPreviewAvailableView(getFactory(), viewConnector);
	}

	/**
	 * Guess file type from file extension. The following mapping are done <br>
	 * TO BE CONTINUED
	 * @param fileName
	 * @return
	 */
	private String getContentTypeFromExtension(String fileName) {
		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
		if (StringUtils.isNotEmpty(extension)) {
			if ("XML".equals(extension.toUpperCase()))
				return XML;
			else if ("TXT".equals(extension.toUpperCase()))
				return TXT;
			else if ("JPG".equals(extension.toUpperCase()))
				return IMG;
			else if ("JPEG".equals(extension.toUpperCase()))
				return IMG;
			else if ("BMP".equals(extension.toUpperCase()))
				return IMG;
			else if ("GIF".equals(extension.toUpperCase()))
				return IMG;
			else if ("PDF".equals(extension.toUpperCase()))
				return IMG;
			else if ("HTM".equals(extension.toUpperCase()))
				return HTML;
			else if ("HTML".equals(extension.toUpperCase()))
				return HTML;
		}
		return NONE;
	}

	// delegate method calls to concrete view

	public void clear() {
		delegate.clear();
	}

	public void fireServerEvent(ServerEvent event) {
		delegate.fireServerEvent(event);
	}

	public void onAction(ClientEvent event) {
		delegate.onAction(event);
	}

	public void registerListener(int eventtype, IServerEventListener liz) {
		delegate.registerListener(eventtype, liz);
	}

	public void removeListener(int eventtype, IServerEventListener liz) {
		delegate.removeListener(eventtype, liz);
	}

	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		delegate.createControls( factory, viewConnector );
	}
}
