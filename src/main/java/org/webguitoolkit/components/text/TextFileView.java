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
package org.webguitoolkit.components.text;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.webguitoolkit.components.preview.AbstractPreviewView;
import org.webguitoolkit.ui.base.WebGuiFactory;
import org.webguitoolkit.ui.controls.container.Canvas;
import org.webguitoolkit.ui.controls.container.ICanvas;

public class TextFileView extends AbstractPreviewView {
	private String filename = null;

	public TextFileView(WebGuiFactory factory, ICanvas viewConnector,
			String newFilePath) {
		super(factory, viewConnector);
		this.filename = newFilePath;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void createControls( WebGuiFactory factory, ICanvas viewConnector ) {
		try {
			Canvas c = getFactory().newCanvas(viewConnector);
			getFactory().newLabel(c, readFile(this.filename));
			c.getStyle().add("overflow-y", "scroll");
			c.getStyle().add("overflow-x", "auto");
			c.getStyle().addHeight("95%");
		} catch (Exception e) {
			viewConnector.getPage().sendError(e.getMessage());
		}
	}

	private static String readFile(String filename) throws IOException {
		String lineSep = "<br />";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String nextLine = "";
		StringBuffer sb = new StringBuffer();
		while ((nextLine = br.readLine()) != null) {
			sb.append(nextLine);
			sb.append(lineSep);
		}
		return sb.toString();
	}

}
